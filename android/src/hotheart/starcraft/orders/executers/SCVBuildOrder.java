package hotheart.starcraft.orders.executers;

import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.graphics.TeamColors;
import hotheart.starcraft.orders.Order;
import hotheart.starcraft.units.Flingy;
import hotheart.starcraft.units.Unit;
import hotheart.starcraft.units.target.AbstractTarget;
import hotheart.starcraft.units.target.UnitTarget;
import hotheart.starcraft.weapons.Weapon;

public class SCVBuildOrder extends Order {
	int reloadTime = 0;

	Unit destUnit;

	public SCVBuildOrder(Unit u) {
		super(Order.Factory.getOrder(30, u));
		this.iconId = 232;
	}

	protected boolean _execute(AbstractTarget target) {
		unit.currentOrder = new IdleOrder(unit);

		StarcraftCore.context.addUnit(Unit.Factory.getUnit(106,
				unit.teamColor), target.getDestinationX(), target
				.getDestinationY());
		
		// StarcraftCore.context.units.add(object)

		return true;
		// if (!(target instanceof UnitTarget))
		// return false;
		//
		// Unit dUnit = ((UnitTarget) target).destUnit;
		//
		// reloadTime = 0;
		//
		// destUnit = dUnit;
		//
		// Weapon selWeapon = unit.airWeapon;
		// if (!destUnit.isAir)
		// selWeapon = unit.groundWeapon;
		//
		// unit.target = new UnitTarget(dUnit, selWeapon.maxDistance);
		// if (unit.parent != null)
		// unit.parent.target = unit.target;
		//
		// return true;
	}

	public void update() {

		// Weapon selWeapon = unit.airWeapon;
		// if (!destUnit.isAir)
		// selWeapon = unit.groundWeapon;
		//
		// if (selWeapon == null)
		// return;
		//
		// if (selWeapon.reloadTime > reloadTime) {
		// reloadTime++;
		// return;
		// } else if (selWeapon.reloadTime == reloadTime) {
		// reloadTime++;
		// if (unit.getSqLenToTarget() <= selWeapon.maxDistance
		// * selWeapon.maxDistance) {
		// if (destUnit.isAir)
		// unit.startAttackAnimation(Flingy.ATTACK_AIR);
		// else
		// unit.startAttackAnimation(Flingy.ATTACK_GRND);
		// }
		// }
	}
}
