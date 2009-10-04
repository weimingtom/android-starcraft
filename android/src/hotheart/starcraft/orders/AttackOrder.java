package hotheart.starcraft.orders;

import hotheart.starcraft.units.Flingy;
import hotheart.starcraft.units.Unit;
import hotheart.starcraft.units.target.UnitTarget;
import hotheart.starcraft.weapons.Weapon;

public class AttackOrder extends Order {

	int reloadTime = 0;

	Unit destUnit;

	public AttackOrder(Unit u, Unit dUnit) {
		super(u);
		destUnit = dUnit;
		
		Weapon selWeapon = unit.airWeapon;
		if (!destUnit.isAir)
			selWeapon = unit.groundWeapon;
		
		u.target = new UnitTarget(dUnit, selWeapon.maxDistance);
	}

	public void update() {

		Weapon selWeapon = unit.airWeapon;
		if (!destUnit.isAir)
			selWeapon = unit.groundWeapon;

		if (selWeapon == null)
			return;

		if (selWeapon.reloadTime > reloadTime) {
			reloadTime++;
			return;
		} else if (selWeapon.reloadTime == reloadTime) {
			reloadTime++;
			if (unit.getSqLenToTarget() <= selWeapon.maxDistance
					* selWeapon.maxDistance) {
				if (destUnit.isAir)
					unit.startAttackAnimation(Flingy.ATTACK_AIR);
				else
					unit.startAttackAnimation(Flingy.ATTACK_GRND);
			}
		}
	}

	public void shoot(int type) {

		Weapon selWeapon = unit.airWeapon;
		if (type == 1)
			selWeapon = unit.groundWeapon;
		
		if (type == -1)
		{
			selWeapon = unit.airWeapon;
			if (!destUnit.isAir)
				selWeapon = unit.groundWeapon;
		}

		if (selWeapon == null)
			return;
		
		if (selWeapon.attack(unit, destUnit))
		{
			unit.target = null;
			unit.currentOrder = null;
			unit.finishAttack();
		}
	}

	public void repeatAttack() {
		reloadTime = 0;
	}
}
