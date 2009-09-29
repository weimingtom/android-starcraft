package hotheart.starcraft.units;

import android.graphics.Canvas;
import hotheart.starcraft.core.GameContext;
import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.files.DatFile;
import hotheart.starcraft.graphics.Image;
import hotheart.starcraft.graphics.utils.SelectionCircles;
import hotheart.starcraft.weapons.Weapon;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;

public final class Unit extends Flingy {

	public static final int ABILITY_BUILDING = 1 << 0;
	public static final int ABILITY_WORKER = 1 << 3;
	public static final int ABILITY_SUBUNIT = 1 << 4;

	public Unit(Flingy src) {
		super(src);
	}

	private static final int COUNT = 228;

	private static byte[] libFlingyId;
	private static int[] libSubUnit1;
	private static int[] libSubUnit2;
	private static int[] libHitPoints;
	private static byte[] libElevationLevel;
	private static byte[] libGroundWeapon;
	private static byte[] libAirWeapon;
	private static int[] libSpecialAbilityFlags;

	public static void init(byte[] arr) {

		DatFile file = new DatFile(new ByteArrayInputStream(arr));
		try {
			libFlingyId = file.read1ByteData(COUNT);
			libSubUnit1 = file.read2ByteData(COUNT);
			libSubUnit2 = file.read2ByteData(COUNT);

			file.skip((201 - 106 + 1)*2);
			file.skip(COUNT * 8);

			libHitPoints = file.read4ByteData2LowestBytes(COUNT);
			libElevationLevel = file.read1ByteData(COUNT);

			file.skip(COUNT * 7);

			libGroundWeapon = file.read1ByteData(COUNT);

			file.skip(COUNT);

			libAirWeapon = file.read1ByteData(COUNT);

			file.skip(COUNT * 2);

			libSpecialAbilityFlags = file.read4ByteData(COUNT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Unit getUnit(int id, int teamColor) {
		int flingyId = libFlingyId[id]&0xFF;
		int subUnit1 = libSubUnit1[id];
		int subUnit2 = libSubUnit2[id];
		int hitPoints = libHitPoints[id];
		int elevationLevel = libElevationLevel[id]&0xFF;
		int groundWeapon = libGroundWeapon[id]&0xFF;
		int airWeapon = libAirWeapon[id]&0xFF;

		int specialAbilityFlags = libSpecialAbilityFlags[id];

		Unit res = new Unit(Flingy.getFlingy(flingyId, teamColor));

		res.teamColor = teamColor;
		res.hipPoints = hitPoints;
		res.maxHitPoints = hitPoints;
		res.elevationLevel = elevationLevel;
		res.specialAbilityFlags = specialAbilityFlags;

		if (elevationLevel >= 12) {
			res.isAir = true;
		} else {
			res.isAir = false;
		}
		
		if (groundWeapon != 130)
			res.groundWeapon = Weapon.getWeapon(groundWeapon);
		if (airWeapon != 130)
			res.airWeapon = Weapon.getWeapon(airWeapon);

		if (subUnit1 != 228) {
			res.subunit1 = getUnit(subUnit1, teamColor);
			if (res.subunit1 != null)
				res.subunit1.parent = res;
		}

		if (subUnit2 != 228) {
			res.subunit2 = getUnit(subUnit2, teamColor);
			if (res.subunit2 != null)
				res.subunit2.parent = res;
		}

		return res;
	}

	public static final int ACTION_IDLE = 0;
	public static final int ACTION_MOVE = 1;
	public static final int ACTION_GRND_ATTACK = 2;
	public static final int ACTION_REPEAT_GRND_ATTACK = 3;
	public static final int ACTION_AIR_ATTACK = 4;
	public static final int ACTION_REPEAT_AIR_ATTACK = 5;

	public static final int MAX_GROUND_LEVEL = 11;

	public Weapon groundWeapon;
	public Weapon airWeapon;

	public Unit subunit1 = null;
	public Unit subunit2 = null;

	public Unit parent = null;

	public int teamColor;

	public int maxHitPoints;
	public int hipPoints;

	public int repeatTime = 0;

	public int elevationLevel;

	public int armor;
	public boolean selected = false;

	public int specialAbilityFlags = 0;

	public int action = ACTION_IDLE;

	public final boolean isGround() {
		return elevationLevel <= MAX_GROUND_LEVEL;
	}

	public void buildTree() {
		super.buildTree();
		
		updateSubunits();

		if (subunit1 != null)
			subunit1.buildTree();
		if (subunit2 != null)
			subunit2.buildTree();
	}

	public final void draw_selection() {
		if (selected) {
			Image circ = SelectionCircles.selCircles[selCircle];
			circ.setPos(posX, posY + vertPos);
			circ.draw();
		}
	}

	public final void draw_healths() {
		// TODO: FIX Health bar

		// Paint p = new Paint();
		// p.setColor(Color.GRAY);
		//
		// int yPos = posY + vertPos + SelectionCircles.selCircleSize[selCircle]
		// / 2;
		//
		// c.drawRect(posX - healthBar / 2, yPos, posX + healthBar / 2, yPos +
		// 4,
		// p);
		//
		// p.setColor(Color.GREEN);
		// if (maxHealth > 0) {
		// int len = (healthBar * health) / maxHealth;
		//
		// c.drawRect(posX - healthBar / 2, yPos, posX - healthBar / 2 + len,
		// yPos + 4, p);
		// }

	}

	public void draw(Canvas c) {

		super.draw();

		draw_selection();
		draw_healths();
		
		updateSubunits();

		if (subunit1 != null)
			subunit1.draw(c);
			
		if (subunit2 != null)
			subunit2.draw(c);

	}

	public int getLenSqToTarget() {
		int dposX = (int) destX;
		int dposY = (int) destX;

		if (action == ACTION_GRND_ATTACK)
			if (targetUnit != null) {
				dposX = (int) targetUnit.getPosX();
				dposY = (int) targetUnit.getPosY();
			}

		return (int) ((dposX - posX) * (dposX - posX) + (dposY - posY)
				* (dposY - posY));
	}

	public void update() {
		super.update();

		if (subunit1 != null) {
			subunit1.update();
		}
		if (subunit2 != null)
			subunit2.update();

		if ((action == ACTION_GRND_ATTACK) || (action == ACTION_AIR_ATTACK)) {
			if (targetUnit != null) {
				Weapon selWeapon = airWeapon;
				if (action == ACTION_GRND_ATTACK)
					selWeapon = groundWeapon;

				if (selWeapon != null) {
					int dposX = (int) targetUnit.getPosX();
					int dposY = (int) targetUnit.getPosY();
					rotateTo(dposX, dposY);

					int len_sq = (int) ((dposX - posX) * (dposX - posX) + (dposY - posY)
							* (dposY - posY));

					if (len_sq <= selWeapon.maxDistance * selWeapon.maxDistance) {
						if (action == ACTION_GRND_ATTACK)
							super.attack(Flingy.ATTACK_GRND);
						else
							super.attack(Flingy.ATTACK_AIR);
						if (parent != null)
							parent.stop();
					} else {
						moveUnit(dposX, dposY);
					}
				}
			}
		} else if ((action == ACTION_REPEAT_GRND_ATTACK)
				|| (action == ACTION_REPEAT_AIR_ATTACK)) {
			repeatTime++;
			if (groundWeapon.reloadTime <= repeatTime) {
				repeatTime = 0;
				if (action == ACTION_REPEAT_GRND_ATTACK)
					action = ACTION_GRND_ATTACK;
				else
					action = ACTION_AIR_ATTACK;

				// Play repeat attack animation
				super.repeatAttack();
			}
		}
	}
	
	private void updateSubunits()
	{
		if (subunit1 != null) {
			subunit1.setPos(posX, posY);
			
			if (subunit1.action != ACTION_GRND_ATTACK)
				subunit1.rotateTo(destX, destY);
				
		}
		if (subunit2 != null) {
			subunit2.setPos(posX, posY);
			
			if (subunit2.action != ACTION_GRND_ATTACK)
				subunit2.rotateTo(destX, destY);
		}

	}

	private void moveUnit(int dx, int dy) {

		if ((specialAbilityFlags & ABILITY_BUILDING) != 0) {
			return;
		}

		if ((specialAbilityFlags & ABILITY_SUBUNIT) == 0) {
			updateSubunits();
		}
		
		super.move(dx, dy);
	}

	public void move(int dx, int dy) {
		action = ACTION_MOVE;
		moveUnit(dx, dy);
	}

	public void kill() {

		StarcraftCore.context.removeUnit(this);
		StarcraftCore.context.addImage(this);

		super.kill();

		if (subunit1 != null)
			subunit1.kill();
		if (subunit2 != null)
			subunit2.kill();
	}

	Unit targetUnit;

	public final void attack(Unit unit) {
		action = ACTION_GRND_ATTACK;
		if (!unit.isGround())
			action = ACTION_AIR_ATTACK;

		if (subunit1 != null)
			subunit1.attack(unit);
		if (subunit2 != null)
			subunit1.attack(unit);

		targetUnit = unit;

		if (action == ACTION_GRND_ATTACK)
			super.attack(Flingy.ATTACK_GRND);
		else
			super.attack(Flingy.ATTACK_AIR);

	}

	public final void attack(int type) {
		Weapon selWeapon = airWeapon;
		if (type == 1)
			selWeapon = groundWeapon;

		if (selWeapon == null)
			return;

		if (targetUnit != null) {
			if (selWeapon.attack(this, targetUnit)) {
				targetUnit = null;
				stop();

				if (subunit1 != null)
					subunit1.stop();
				if (subunit2 != null)
					subunit1.stop();
			}
		} else
			finishAttack();
	}

	public void repeatAttack() {
		repeatTime = 0;
		if (action == ACTION_GRND_ATTACK)
			action = ACTION_REPEAT_GRND_ATTACK;
		else
			action = ACTION_REPEAT_AIR_ATTACK;

		// super.repeatAttack();
	}

	public final void hit(int points) {
		hipPoints -= points;
		if (hipPoints <= 0) {
			hipPoints = 0;
			kill();
		}
	}
}