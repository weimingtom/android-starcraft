package hotheart.starcraft.graphics.script;

import hotheart.starcraft.configure.BuildParameters;
import hotheart.starcraft.core.GameContext;
import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.graphics.Image;
import hotheart.starcraft.graphics.Sprite;
import hotheart.starcraft.sounds.StarcraftSoundPool;
import hotheart.starcraft.units.Flingy;
import hotheart.starcraft.units.Unit;

import java.util.Random;

import android.util.Log;

public class ImageScriptEngine {

	static Random rnd = new Random();
	static byte[] script;
	static int basicOffset;

	public static void init(byte[] _script) {
		script = _script;
		basicOffset = (script[0] & 0xFF) + ((script[1] & 0xFF) << 8);
	}

	public static final ImageScriptHeader createHeader(int id) {

		int offset = basicOffset;

		while (true) {
			int num = (script[offset++] & 0xFF)
					+ ((script[offset++] & 0xFF) << 8);
			int dstOffset = (script[offset++] & 0xFF)
					+ ((script[offset++] & 0xFF) << 8);

			if (num == id) {
				ImageScriptHeader res = new ImageScriptHeader();
				res.id = id;
				res.headerOffset = dstOffset;
				res.type = script[dstOffset + 4] & 0xFF;
				return res;
			}

			if (num == 65535)
				break;

		}

		return null;
	}

	public static final void init(ImageState instance) {
		play(instance, 0);
	}

	private static final int OP_PLAY_FRAME_SET = 0x00;
	private static final int OP_PLAY_FRAME = 0x2b;
	private static final int OP_SHIFT_DOWN = 0x03;
	private static final int OP_SHIFT_LEFT = 0x02;
	private static final int OP_WAIT = 0x05;
	private static final int OP_WAIT_RAND = 0x06;
	private static final int OP_GOTO1 = 0x07;

	private static final int OP_ACT_OVERLAY = 0x08;
	private static final int OP_ACT_UNDERLAY = 0x09;

	private static final int OP_TURN_GRAPHICS_RAND = 0x22;
	private static final int OP_TURN_GRAPHICS_CW = 0x20;
	private static final int OP_TURN_GRAPHICS_CCW = 0x1F;

	private static final int OP_TURN_GRAPHICS_1CW = 0x21;

	private static final int OP_GOTO_RAND = 0x1E;
	private static final int OP_GOTO2 = 0x3F;
	private static final int OP_CALL = 0x35;
	private static final int OP_RETURN = 0x36;

	private static final int OP_FOLLOW = 0x1D;

	private static final int OP_ADD_SHADOW = 0x3d;
	private static final int OP_HIDE = 0x32;
	private static final int OP_SHOW = 0x33;

	private static final int OP_END = 0x16;
	private static final int OP_MOVE = 0x29;
	private static final int OP_SET_DIRECTION = 0x34;
	private static final int OP_SPRITE_OVERLAY = 0x13;
	private static final int OP_SPRITE_LOWEST = 0x11;
	private static final int OP_SPRITE_OVERLAY_LO = 0x15;

	private static final int OP_ATTACK_WITH_SOUND = 0x1c;
	private static final int OP_ATTACK_WITH = 0x25;
	private static final int OP_ATTACK = 0x26;
	private static final int OP_REPEAT_ATTACK = 0x2A;

	private static final int OP_NO_BREAK_START = 0x2e;
	private static final int OP_NO_BREAK_END = 0x2f;

	private static final int OP_STOP_ANIMATION = 0x30;

	private static final int OP_PLAY_RANDROM_SOUND = 0x19;
	private static final int OP_PLAY_SOUND = 0x18;
	private static final int OP_FOLLOW_PARENT_ANIM = 0x2c;
	private static final int OP_PLAY_SOUND_BETWEEN = 0x1a;

	public static final void exec(ImageState instance) {
		if (instance.isPaused)
			return;

		if (!instance.isBlocked) {
			if (instance.gotoLine != -1) {
				instance.scriptPos = instance.gotoLine;
				instance.gotoLine = -1;
				instance.scriptWait = 0;
			}
		}

		if (instance.scriptWait > 0) {
			instance.scriptWait--;
			return;
		}

		int opcode = script[instance.scriptPos++] & 0xFF;
		if (BuildParameters.DEBUG_OPCODE)
			Log.d("EXEC", "opcode: 0x" + Integer.toHexString(opcode));
		switch (opcode) {
		// OK
		case OP_WAIT:
			instance.scriptWait = script[instance.scriptPos++] & 0xFF - 1;
			return;

			// OK
		case OP_WAIT_RAND:
			if (rnd.nextBoolean())
				instance.scriptWait = script[instance.scriptPos] & 0xFF - 1;
			else
				instance.scriptWait = script[instance.scriptPos + 1] & 0xFF - 1;

			instance.scriptPos += 2;
			return;

		case OP_PLAY_FRAME:
			instance.baseFrame = (script[instance.scriptPos++] & 0xFF);
			break;
		// OK
		case OP_PLAY_FRAME_SET:
			instance.baseFrame = (script[instance.scriptPos++] & 0xFF)
					+ ((script[instance.scriptPos++] & 0xFF) << 8);
			instance.visible = true;
			break;
		case OP_SHIFT_LEFT:
			instance.image.setOffsetX(script[instance.scriptPos++] & 0xFF);
			break;
		// OK
		case OP_SHIFT_DOWN:
			instance.image.setOffsetY(script[instance.scriptPos++] & 0xFF);
			break;

		// OK
		case OP_GOTO1:
			instance.scriptPos = (script[instance.scriptPos++] & 0xFF)
					+ ((script[instance.scriptPos++] & 0xFF) << 8);
			break;
		case 0x3a:
			int dist = (script[instance.scriptPos++] & 0xFF);
			instance.scriptPos++;
			int destPos = (script[instance.scriptPos++] & 0xFF)
					+ ((script[instance.scriptPos++] & 0xFF) << 8);

			if (instance.image instanceof Unit)
				if (((Unit) instance.image).getSqLenToTarget() <= (dist * dist))
					instance.scriptPos = destPos;

			break;
		// OK
		case OP_CALL:
			instance.returnLine = instance.scriptPos;
			instance.scriptPos = (script[instance.scriptPos++] & 0xFF)
					+ ((script[instance.scriptPos++] & 0xFF) << 8);
			break;

		// OK
		case OP_RETURN:
			instance.scriptPos = instance.returnLine;
			break;
		case 0x0d:
			// OK
		case OP_ACT_OVERLAY:

			int overlayId = (script[instance.scriptPos++] & 0xFF)
					+ ((script[instance.scriptPos++] & 0xFF) << 8);
			int ov_dx = script[instance.scriptPos++] & 0xFF;
			int ov_dy = script[instance.scriptPos++] & 0xFF;
			Image overlay = Image.getImage(overlayId,
					instance.image.foregroundColor,
					instance.image.currentImageLayer + 1);
			overlay.parentOverlay = instance.image;
			overlay.imageState.followParent = true;
			overlay.imageState.followParentAngle = true;
			// overlay.followParentAnim = true;
			// overlay.followParentAngle = true;
			overlay.setOffsetX(ov_dx);
			overlay.setOffsetY(ov_dy);

			// TODO Check this
			// overlay.sprite = instance.image.sprite;

			overlay.imageState.angle = instance.angle;
			instance.image.addChild(overlay);
			break;
		// OK
		case OP_ACT_UNDERLAY:
			int underlayId = (script[instance.scriptPos++] & 0xFF)
					+ ((script[instance.scriptPos++] & 0xFF) << 8);
			int un_dx = script[instance.scriptPos++] & 0xFF;
			int un_dy = script[instance.scriptPos++] & 0xFF;
			Image underlay = Image.getImage(underlayId,
					instance.image.foregroundColor,
					instance.image.currentImageLayer - 1);
			underlay.parentOverlay = instance.image;
			underlay.imageState.followParent = true;
			underlay.imageState.followParentAngle = true;
			// underlay.followParentAnim = true;
			// underlay.followParentAngle = true;

			// TODO: Check this
			// underlay.sprite = instance.image.sprite;

			underlay.setOffsetX(un_dx);
			underlay.setOffsetY(un_dy);
			underlay.imageState.angle = instance.angle;
			instance.image.addChild(underlay);
			break;

		// OK
		case OP_ADD_SHADOW:
			int shadowId = instance.image.imageId + 1;
			int sh_dx = script[instance.scriptPos++] & 0xFF;
			int sh_dy = script[instance.scriptPos++] & 0xFF;
			Image shadow = Image.getImage(shadowId,
					instance.image.foregroundColor,
					instance.image.currentImageLayer - 1);
			shadow.parentOverlay = instance.image;
			shadow.imageState.followParent = true;
			shadow.setOffsetX(sh_dx);
			shadow.setOffsetY(sh_dy);
			instance.image.addChild(shadow);
			break;

		// OK
		case OP_GOTO_RAND:
			int factor = script[instance.scriptPos++] & 0xFF;
			if (rnd.nextInt(256) > factor)
				instance.scriptPos = (script[instance.scriptPos++] & 0xFF)
						+ ((script[instance.scriptPos++] & 0xFF) << 8);
			else
				instance.scriptPos += 2;
			break;

		// OK
		case OP_TURN_GRAPHICS_CCW:
			int fr_count_ccw = script[instance.scriptPos++] & 0xFF;
			instance.angle -= (int) ((fr_count_ccw / 17.0) * 360);
			break;

		// OK
		case OP_TURN_GRAPHICS_CW:
			int fr_count_cw = script[instance.scriptPos++] & 0xFF;
			instance.angle += (int) ((fr_count_cw / 17.0) * 360);
			break;

		// OK
		case OP_TURN_GRAPHICS_RAND:
			int fr_count = script[instance.scriptPos++] & 0xFF;
			if (rnd.nextBoolean())
				instance.angle += (int) ((fr_count / 17.0) * 360);
			else
				instance.angle -= (int) ((fr_count / 17.0) * 360);
			break;

		// OK
		case OP_SPRITE_LOWEST:
			int sp_Id = (script[instance.scriptPos++] & 0xFF)
					+ ((script[instance.scriptPos++] & 0xFF) << 8);
			int sp_dx = script[instance.scriptPos++] & 0xFF;
			int sp_dy = script[instance.scriptPos++] & 0xFF;
			Sprite sp = Sprite.getSprite(sp_Id, instance.image.foregroundColor,
					Image.MIN_IMAGE_LAYER);

			sp.setPos(instance.image.getPosX() + sp_dx, instance.image
					.getPosY()
					+ sp_dy);

			sp.imageState.angle = instance.angle;

			// if (instance.image instanceof Sprite)
			// sp.parent = (Sprite) instance.image;

			StarcraftCore.context.addImage(sp);
			break;

		// Seems to be OK
		case OP_SPRITE_OVERLAY_LO:
			int x15_Id = (script[instance.scriptPos++] & 0xFF)
					+ ((script[instance.scriptPos++] & 0xFF) << 8);

			instance.scriptPos += 2;

			// int lo_Id = (script[instance.scriptPos ++ ]&0xFF) +
			// ((script[instance.scriptPos ++ ]&0xFF)<<8);

			Sprite lo_sprite = Sprite.getSprite(x15_Id,
					instance.image.foregroundColor,
					instance.image.currentImageLayer + 1);
			// LoFile lo = OldGrpImage.getLoData(lo_Id);
			// byte[] offsets = new byte[2];
			// lo.getOffsets(0, instance.baseFrame, offsets);
			lo_sprite.setPos(instance.image.getOffsetX(), instance.image
					.getOffsetY());
			lo_sprite.imageState.angle = instance.angle;

			// if (instance.image instanceof Sprite)
			// lo_sprite.parent = (Sprite) instance.image;

			StarcraftCore.context.addImage(lo_sprite);
			break;

		// case 0x15:
		case 0x0f:

			// OK
		case OP_SPRITE_OVERLAY:
			int Id = (script[instance.scriptPos++] & 0xFF)
					+ ((script[instance.scriptPos++] & 0xFF) << 8);
			int l_dx = script[instance.scriptPos++] & 0xFF;
			int l_dy = script[instance.scriptPos++] & 0xFF;
			Sprite l = Sprite.getSprite(Id, instance.image.foregroundColor,
					instance.image.currentImageLayer + 1);

			l.setPos(instance.image.getOffsetX() + l_dx, instance.image
					.getOffsetY()
					+ l_dy);
			l.imageState.angle = instance.angle;

			// if (instance.image instanceof Sprite)
			// l.parent = (Sprite) instance.image;

			StarcraftCore.context.addImage(l);
			break;

		// OK
		case OP_MOVE:
			if (instance.image instanceof Flingy)
				((Flingy) instance.image)
						.move(script[instance.scriptPos + 1] & 0xFF);

			instance.scriptPos++;
			break;

		// OK
		case OP_NO_BREAK_START:
			instance.isBlocked = true;
			break;
		// OK
		case OP_NO_BREAK_END:
			instance.isBlocked = false;
			break;
		// OK
		case OP_STOP_ANIMATION:
			instance.isPaused = true;
			instance.isBlocked = false;
			break;
		// OK
		case OP_FOLLOW_PARENT_ANIM:
			if ((script[instance.scriptPos++] & 0xFF) == 1)
				instance.followParentAnim = true;
			else
				instance.followParentAnim = false;
			break;
		// OK
		case OP_HIDE:
			instance.visible = false;
			break;
		// OK
		case OP_SHOW:
			instance.visible = true;
			break;

		// OK
		case OP_FOLLOW:
			instance.followParent = true;
			instance.followParentAngle = true;
			break;

		// OK
		case OP_TURN_GRAPHICS_1CW:
			instance.angle += (int) ((1.0 / 17.0) * 360);
			break;

		// OK
		case OP_END:
			instance.image.delete();
			break;

		// Not checked
		case OP_GOTO2:// Is lifted off
			instance.scriptPos += 2;
			break;

		case OP_SET_DIRECTION:
			int new_angle = script[instance.scriptPos++] & 0xFF;
			instance.angle = (int) ((new_angle / 17.0) * 360);
			break;

		// Attacking control
		case OP_REPEAT_ATTACK:
			if (instance.image instanceof Unit)
				((Unit) instance.image).repeatAttack();
			break;

		case 0x1b:
		case OP_ATTACK:
			if (instance.image instanceof Unit)
				((Unit) instance.image).attack(-1);

			break;

		case OP_ATTACK_WITH:
			if (instance.image instanceof Unit)
				((Unit) instance.image)
						.attack(script[instance.scriptPos + 1] & 0xFF);

			instance.scriptPos++;

			break;

		case OP_ATTACK_WITH_SOUND:
			if (instance.image instanceof Unit)
				((Unit) instance.image).attack(-1);

			// Sound control
		case OP_PLAY_RANDROM_SOUND:// Play random sound
			int count = script[instance.scriptPos++] & 0xFF;
			instance.scriptPos += count * 2;
			break;
		case OP_PLAY_SOUND:// Play sound
			int sound_id = (script[instance.scriptPos++] & 0xFF)
					+ ((script[instance.scriptPos++] & 0xFF) << 8);
			StarcraftSoundPool.playSound(sound_id);
			break;
		case OP_PLAY_SOUND_BETWEEN:
			int sound_start = (script[instance.scriptPos++] & 0xFF)
					+ ((script[instance.scriptPos++] & 0xFF) << 8);
			int sound_end = (script[instance.scriptPos++] & 0xFF)
					+ ((script[instance.scriptPos++] & 0xFF) << 8);

			StarcraftSoundPool.playSound(sound_start
					+ rnd.nextInt(sound_end - sound_start));
			break;

		// Unimplemented opcodes.

		// 4-byte commands
		case 0x10:
			instance.scriptPos += 3;

			// 1-byte commands
		case 0x24:
		case 0x31:
			instance.scriptPos++;

			// 0-byte commands
		case 0x27:
		default:
			Log.e("UNIMPLEMENTED", "Unimplemented opcode: 0x"
					+ Integer.toHexString(opcode));
			break;

		}
	}

	public static final void play(ImageState instance, int animId) {
		instance.isPaused = false;
		int offset = (script[instance.scriptHeader.headerOffset + 8 + animId
				* 2] & 0xFF)
				+ ((script[instance.scriptHeader.headerOffset + 9 + animId * 2] & 0xFF) << 8);

		if (offset != 0) {
			instance.gotoLine = offset;
		}
	}
}