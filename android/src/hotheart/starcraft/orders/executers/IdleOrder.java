package hotheart.starcraft.orders.executers;

import hotheart.starcraft.orders.Order;
import hotheart.starcraft.units.Unit;
import hotheart.starcraft.units.target.AbstractTarget;

public class IdleOrder extends Order {
	public IdleOrder(Unit u)
	{
		super(Order.Factory.getOrder(1, u));
	}
	
	protected boolean _execute(AbstractTarget target)
	{
		return true;
	}
}

