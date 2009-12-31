package hotheart.starcraft.orders.executers;

import hotheart.starcraft.orders.Order;
import hotheart.starcraft.units.Unit;
import hotheart.starcraft.units.target.AbstractTarget;
import hotheart.starcraft.units.target.UnitTarget;

public class IdleOrder extends Order {
	public IdleOrder(Unit u)
	{
		super(Order.Factory.getOrder(1, u));
		this.isTargeting = false;
	}
	
	protected boolean _execute()
	{
		unit.target = new UnitTarget(unit);
		return true;
	}
}

