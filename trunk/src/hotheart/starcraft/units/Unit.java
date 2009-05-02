package hotheart.starcraft.units;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import hotheart.starcraft.graphics.Image;
import hotheart.starcraft.graphics.utils.SelectionCircles;
import hotheart.starcraft.sounds.StarcraftSoundPool;

import java.util.Random;

public final class Unit {
	private static byte[] units;
	private static int count;
	private static Random rnd = new Random();

	public static void init(byte[] arr) {
		units = arr;
		count = 228;// ?
	}

	public static Unit getUnit(int id, int teamColor) {
		int flingyId = (units[id] & 0xFF);

		int subUnit1 = (units[id * 2 + count] & 0xFF)
				+ ((units[id * 2 + count + 1] & 0xFF) << 8);

		int subUnit2 = (units[id * 2 + count * 3] & 0xFF)
				+ ((units[id * 2 + count * 3 + 1] & 0xFF) << 8);

		int healthOffset = count * 13 + (201 - 106 + 1) * 2;

		int health = (units[id * 4 + healthOffset + 1] & 0xFF)
				+ ((units[id * 4 + healthOffset + 2] & 0xFF) << 8);

		int elevationLevelOffset = healthOffset + count * 4;
		int elevationLevel = (units[id + elevationLevelOffset] & 0xFF);

		int groundWeaponOffset = healthOffset + count * 12;
		int groundWeapon = (units[id + groundWeaponOffset] & 0xFF);

		int airWeaponOffset = groundWeaponOffset + 2 * count;
		int airWeapon = (units[id + airWeaponOffset] & 0xFF);

		int readySoundOffset = groundWeaponOffset + count * 15;
		// int readySound = (units[readySoundOffset + id*2]&0xFF) +
		// ((units[readySoundOffset + id*2 + 1]&0xFF)<<8);

		int whatSoundStartOffset = readySoundOffset + 106 * 2;
		int whatSoundEndOffset = whatSoundStartOffset + count * 2;

		int pissSoundStartOffset = whatSoundEndOffset + count * 2;
		int pissSoundEndOffset = pissSoundStartOffset + 106 * 2;

		int yesSoundStartOffset = pissSoundEndOffset + 106 * 2;
		int yesSoundEndOffset = yesSoundStartOffset + 106 * 2;

		int yesSound1 = (units[yesSoundStartOffset + id * 2] & 0xFF)
				+ ((units[yesSoundStartOffset + id * 2 + 1] & 0xFF) << 8);

		int yesSound2 = (units[yesSoundEndOffset + id * 2] & 0xFF)
				+ ((units[yesSoundEndOffset + id * 2 + 1] & 0xFF) << 8);

		int whatSound1 = (units[whatSoundStartOffset + id * 2] & 0xFF)
				+ ((units[whatSoundStartOffset + id * 2 + 1] & 0xFF) << 8);

		int whatSound2 = (units[whatSoundEndOffset + id * 2] & 0xFF)
				+ ((units[whatSoundEndOffset + id * 2 + 1] & 0xFF) << 8);

		Unit res = new Unit();
		res.flingy = Flingy.getFlingy(flingyId, teamColor);
		res.flingy.unit = res;
		res.teamColor = teamColor;
		res.health = health;
		res.maxHealth = health;
		res.elevationLevel = elevationLevel;
		if (id < 106) {
			res.YesSoundStart = yesSound1;
			res.YesSoundEnd = yesSound2;
		}

		res.WhatSoundStart = whatSound1;
		res.WhatSoundEnd = whatSound2;

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

	public Flingy flingy;
	public Weapon groundWeapon;
	public Weapon airWeapon;

	public Unit subunit1 = null;
	public Unit subunit2 = null;

	public Unit parent = null;

	public int teamColor;

	public int maxHealth;
	public int health;

	public int repeatTime = 0;

	public int elevationLevel;

	public int armor;
	public boolean selected = false;

	public int YesSoundStart = -1;
	public int YesSoundEnd = -1;

	public int WhatSoundStart = -1;
	public int WhatSoundEnd = -1;

	public int action = ACTION_IDLE;

	public final boolean isGround() {
		return elevationLevel <= MAX_GROUND_LEVEL;
	}

	public final void sayYes() {
		if (YesSoundStart > 0)
			StarcraftSoundPool.playSound(YesSoundStart
					+ rnd.nextInt(YesSoundEnd - YesSoundStart));
	}

	public final void sayWhat() {
		if (WhatSoundStart > 0)
			StarcraftSoundPool.playSound(WhatSoundStart
					+ rnd.nextInt(WhatSoundEnd - WhatSoundStart));
	}

	public final void preDraw() {
		flingy.preDraw();

		if (subunit1 != null) {
			subunit1.flingy.setPos(flingy.getPosX(), flingy.getPosY());
			subunit1.preDraw();
		}
		if (subunit2 != null) {
			subunit2.flingy.setPos(flingy.getPosX(), flingy.getPosY());
			subunit2.preDraw();
		}
	}

	public final void draw_selection(Canvas c) {
		if (selected) {
			Image circ = SelectionCircles.selCircles[flingy.selCircle];
			circ.setPos(flingy.getPosX(), flingy.getPosY() + flingy.vertPos);
			circ.draw(c);
		}
	}

	public final void draw(Canvas c) {
		if (flingy == null)
			return;

		if (selected) {
			Image circ = SelectionCircles.selCircles[flingy.selCircle];
			circ.setPos(flingy.getPosX(), flingy.getPosY() + flingy.vertPos);
			circ.draw(c);
		}

		flingy.draw(c);

		Paint p = new Paint();
		p.setColor(Color.GRAY);

		int yPos = flingy.getPosY()
				+ flingy.vertPos
				+ SelectionCircles.selCircleSize[flingy.selCircle]
				/ 2;

		c.drawRect(flingy.getPosX() - flingy.healthBar / 2, yPos, flingy.getPosX()
				+ flingy.healthBar / 2, yPos + 4, p);

		p.setColor(Color.GREEN);
		if (maxHealth > 0) {
			int len = (flingy.healthBar * health) / maxHealth;

			c.drawRect(flingy.getPosX() - flingy.healthBar / 2, yPos,
					flingy.getPosX() - flingy.healthBar / 2 + len, yPos + 4,
					p);
		}

		if (subunit1 != null) {
			subunit1.flingy.setPos(flingy.getPosX(), flingy.getPosY());
			subunit1.draw(c);
		}
		if (subunit2 != null) {
			subunit2.flingy.setPos(flingy.getPosX(), flingy.getPosY());
			subunit2.draw(c);
		}

	}

	public final void stop() {
		flingy.stop();
	}

	public int getLenSqToTarget() {
		int dposX = (int) flingy.destX;
		int dposY = (int) flingy.destX;

		if (action == ACTION_GRND_ATTACK)
			if (targetUnit != null)
				if (targetUnit.flingy != null) {
					dposX = (int) targetUnit.flingy.getPosX();
					dposY = (int) targetUnit.flingy.getPosY();
				}

		return (int) ((dposX - flingy.getPosX()) * (dposX - flingy.getPosX()) + (dposY - flingy.getPosY())
				* (dposY - flingy.getPosY()));
	}

	public void update() {
		if (flingy != null)
			flingy.update();
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
					int dposX = (int) targetUnit.flingy.getPosX();
					int dposY = (int) targetUnit.flingy.getPosY();
					flingy.rotateTo(dposX, dposY);

					int len_sq = (int) ((dposX - flingy.getPosX())
							* (dposX - flingy.getPosX()) + (dposY - flingy.getPosY())
							* (dposY - flingy.getPosY()));

					if (len_sq <= selWeapon.maxDistance * selWeapon.maxDistance) {
						if (action == ACTION_GRND_ATTACK)
							flingy.attack(Flingy.ATTACK_GRND);
						else
							flingy.attack(Flingy.ATTACK_AIR);
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

				flingy.repeatAttack();
			}
		}
	}

	private final void moveUnit(int dx, int dy) {
		if (parent != null) {
			parent.moveUnit(dx, dy);
		}
		if (subunit1 != null) {
			if (subunit1.action != ACTION_GRND_ATTACK)
				subunit1.flingy.move(dx, dy);
		}
		if (flingy != null)
			flingy.move(dx, dy);
	}

	public final void move(int dx, int dy) {
		action = ACTION_MOVE;
		moveUnit(dx, dy);
	}

	public final void kill() {
		
		ObjectPool.removeUnit(this);
		ObjectPool.addImage(flingy);
		
		flingy.kill();

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
			flingy.attack(Flingy.ATTACK_GRND);
		else
			flingy.attack(Flingy.ATTACK_AIR);

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
				flingy.stop();

				if (subunit1 != null)
					subunit1.stop();
				if (subunit2 != null)
					subunit1.stop();
			}
		} else
			finishAttack();
	}

	public final void finishAttack() {
		flingy.finishAttack();
	}

	public final void repeatAttack() {
		repeatTime = 0;
		if (action == ACTION_GRND_ATTACK)
			action = ACTION_REPEAT_GRND_ATTACK;
		else
			action = ACTION_REPEAT_AIR_ATTACK;

	}

	public final void hit(int points) {
		health -= points;
		if (health <= 0) {
			health = 0;
			kill();
		}
	}
}