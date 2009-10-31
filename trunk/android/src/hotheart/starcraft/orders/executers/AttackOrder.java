package hotheart.starcraft.orders.executers;

import hotheart.starcraft.orders.Order;
import hotheart.starcraft.units.Flingy;
import hotheart.starcraft.units.Unit;
import hotheart.starcraft.units.target.AbstractTarget;
import hotheart.starcraft.units.target.UnitTarget;
import hotheart.starcraft.weapons.Weapon;

public class AttackOrder extends Order {

	int reloadTime = 0;

	Unit destUnit;

	public AttackOrder(Unit u) {
		super(Order.Factory.getOrder(10, u));

	}

	protected boolean _execute(AbstractTarget target) {
		if (!(target instanceof UnitTarget))
			return false;

		Unit dUnit = ((UnitTarget) target).destUnit;

		reloadTime = 0;

		destUnit = dUnit;

		Weapon selWeapon = unit.airWeapon;
		if (!destUnit.isAir)
			selWeapon = unit.groundWeapon;

		unit.target = new UnitTarget(dUnit, selWeapon.maxDistance);
		if (unit.parent != null)
			unit.parent.target = unit.target;

		return true;
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

	// TODO must use attack type!
	public void shoot(int type) {

		Weapon selWeapon = unit.airWeapon;
		if (!destUnit.isAir)
			selWeapon = unit.groundWeapon;

		// Weapon selWeapon = unit.airWeapon;
		// if (type == 1)
		// selWeapon = unit.groundWeapon;
		//		
		// if (type == -1)
		// {
		// Weapon selWeapon = unit.airWeapon;
		// if (!destUnit.isAir)
		// selWeapon = unit.groundWeapon;
		// }

		if (selWeapon == null)
			return;

		if (selWeapon.attack(unit, destUnit)) {
			unit.target = null;
			unit.currentOrder = null;
			unit.finishAttack();
		}
	}

	public void repeatAttack() {
		reloadTime = 0;
	}
}