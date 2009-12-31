package hotheart.starcraft.units;

import hotheart.starcraft.orders.Order;
import hotheart.starcraft.orders.executers.AttackOrder;
import hotheart.starcraft.orders.executers.BuildUnitOrder;
import hotheart.starcraft.orders.executers.IdleOrder;
import hotheart.starcraft.orders.executers.MoveOrder;
import hotheart.starcraft.orders.executers.SCVBuildOrder;
import hotheart.starcraft.units.target.AbstractTarget;
import hotheart.starcraft.units.target.UnitTarget;

public class UnitOrders {

	Unit unit;
	public Order[] orders;

	public UnitOrders(Unit u) {
		unit = u;
		updateOrders();
	}

	private void updateOrders() {
		Order[] tmp = new Order[9];
		int count = 0;
		
		// MUST BE FIRST
		tmp[count++] = new IdleOrder(unit);
		
		//tmp[count++] = new BuildUnitOrder(unit);
		
		if (unit.unitId == 7)
			tmp[count++] = new SCVBuildOrder(unit);
		
		if ((unit.specialAbilityFlags & Unit.ABILITY_BUILDING) == 0)
			tmp[count++] = new MoveOrder(unit);

		if ((unit.airWeapon != null) || (unit.groundWeapon != null))
			tmp[count++] = new AttackOrder(unit);

		orders = new Order[count];
		for (int i = 0; i < count; i++)
			orders[i] = tmp[i];
	}

	public void executeButton(int id) {
		orders[id].execute();
		updateOrders();
	}

	public void executeButton(int id, AbstractTarget target) {
		orders[id].execute(target);
		updateOrders();
	}

	public int selectedButton = -1;
}
