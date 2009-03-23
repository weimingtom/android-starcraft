package hotheart.starcraft.units;

import android.graphics.Canvas;
import hotheart.starcraft.graphics.Sprite;

public class Flingy {
	public static final int FLINGY_DAT = 0;
	public static final int MIXED = 1;
	public static final int ISCRIPT_BIN = 2;

	private static byte[] flingy;
	private static int count;

	public static final void init(byte[] arr) {
		flingy = arr;
		count = arr.length / 15;
	}

	public static final Flingy getFlingy(int id, int teamColor) {
		final int spriteId = (flingy[id * 2] & 0xFF)
				+ ((flingy[id * 2 + 1] & 0xFF) << 8);

		final int speed = (flingy[id * 4 + count * 2] & 0xFF)
				+ ((flingy[id * 4 + count * 2 + 1] & 0xFF) << 8)
				+ ((flingy[id * 4 + count * 2 + 2] & 0xFF) << 16)
				+ ((flingy[id * 4 + count * 2 + 3] & 0xFF) << 24);

		final int accel = (flingy[id * 2 + count * 6] & 0xFF)
				+ ((flingy[id * 2 + count * 6 + 1] & 0xFF) << 8);

		final int haltDist = (flingy[id * 4 + count * 8] & 0xFF)
				+ ((flingy[id * 4 + count * 8 + 1] & 0xFF) << 8)
				+ ((flingy[id * 4 + count * 8 + 2] & 0xFF) << 16)
				+ ((flingy[id * 4 + count * 8 + 3] & 0xFF) << 24);

		final int turnRadius = (flingy[id + count * 12] & 0xFF);

		final int moveControl = (flingy[id + count * 14] & 0xFF);

		final Flingy res = new Flingy();
		res.sprite = Sprite.getSprite(spriteId, teamColor, 0);
		res.sprite.flingy = res;
		res.topSpeed = speed/120;
		res.acceleration = accel;
		res.haltDistantion = haltDist/256;
		res.turnRadius = turnRadius;
		res.moveControl = moveControl;
		return res;
	}
	
	public static final int ATTACK_AIR  = 2;
	public static final int ATTACK_GRND = 3;
	public static final int IDLE        = 4;
	public static final int MOVING      = 5;
	public static final int DEATH       = 6;

	public Sprite sprite;
	public int topSpeed;
	public int acceleration;
	public int haltDistantion;
	public int turnRadius;
	public int moveControl;

	public int posX = 0;
	public int posY = 0;
	
	public Unit unit = null;

	public final void preDraw()
	{
		sprite.globalX = posX;
		sprite.globalY = posY;
		sprite.preDraw(posY);
	}
	public final void  draw(Canvas c) {
		sprite.globalX = (int) posX;
		sprite.globalY = (int) posY;
		sprite.draw(c);
	}

	public final void move(int d) {
		final float dx = (float) Math
				.cos(((sprite.image.angle - 90) / 180.0f) * 3.1415f)
				* d;
		final float dy = (float) Math
				.sin(((sprite.image.angle - 90) / 180.0f) * 3.1415f)
				* d;
		posX += dx;
		posY += dy;
	}

	int speed = 0;
	int action = IDLE;

	public int destX = 0, destY = 0;

	public final void move(int dx, int dy) {
		destX = dx;
		destY = dy;
		
		if (action == MOVING)
			return;
		action = MOVING;

		if (sprite!=null)
			sprite.image.play(11);

		speed = 0;
	}

	public final void stop() {
		if (action != MOVING)
			return;

		sprite.image.play(12);

		action = IDLE;
		speed = 0;
	}

	public final void rotateTo(int dx, int dy)
	{
		final int len_sq = (int) ((posX - dx) * (posX - dx) + (posY - dy)
				* (posY - dy));
		
		int current_angle = sprite.image.angle;

		int delta = (int) (18 * 3.1415 * turnRadius / (topSpeed));

		if (moveControl != Flingy.FLINGY_DAT)
			delta = 30;

		final float dot = (float) ((dx - posX)
				* (Math.sin(3.1415f * (current_angle / 180.0f))) - (dy - posY)
				* (Math.cos(3.1415f * (current_angle / 180.0f))));

		final float cross = (float) ((dx - posX)
				* (Math.cos(3.1415f * (current_angle / 180.0f))) + (dy - posY)
				* (Math.sin(3.1415f * (current_angle / 180.0f))));

		float alpha = (float)(dot/Math.sqrt(len_sq));
		alpha =(int)(Math.acos(alpha)*180/3.1515f);
		
		delta = (int)Math.min(delta, alpha);
	
		if (cross < 0)
			current_angle = (current_angle - delta) % 360;
		else
			current_angle = (current_angle + delta) % 360;
		
		sprite.image.angle = current_angle;
	}
	
	public void update() {
		if (action == MOVING)
		{
			final int len_sq = (int) ((posX - destX) * (posX - destX) + (posY - destY)
					* (posY - destY));
			
			if ((moveControl == FLINGY_DAT)
					|| (moveControl == MIXED))
			{
				speed += acceleration;

				if (speed > topSpeed)
					speed = topSpeed;
				
				if (len_sq< (haltDistantion)*(haltDistantion))
				{
					speed -= acceleration;
					if (speed<=0)
					{
						speed += acceleration;
						
						speed -= acceleration/10;
						
						if (speed*speed > len_sq)
						{
							speed = (int) Math.sqrt(len_sq) + 3;
						}
						
//						speed = (int) Math.sqrt(len_sq);
//						if (speed > acceleration)
//							speed = acceleration;
					}
				}
			}
		
			
			
			if (len_sq < 10) {
				stop();
				return;
			}
			
			
			
			rotateTo(destX, destY);
			
			if ((moveControl == FLINGY_DAT)|| (moveControl == MIXED))
			{
				move(speed);
			}
		}
		sprite.update();
	}

	private int currentAttack = ATTACK_GRND;
	
	public void attack(int attackType) {
		if (action == attackType)
			return;
		
		if (attackType == ATTACK_GRND)
		{
			currentAttack = action = ATTACK_GRND;
			sprite.image.play(2);
		}
		else
		{
			currentAttack = action = ATTACK_AIR;
			sprite.image.play(3);
		}
		
	}
	
	public void repeatAttack()
	{
		action = currentAttack;
		sprite.image.play(5);
	}

	public void finishAttack()
	{
		sprite.image.play(8);
	}
	
	public void kill() {
		if (action == DEATH)
			return;

		action = DEATH;

		sprite.image.play(1);
		
		sprite.globalX = (int)this.posX;
		sprite.globalY = (int)this.posY;
		ObjectPool.addSprite(sprite);
	}
}
