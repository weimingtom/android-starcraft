package hotheart.starcraft.orders.executers;

import hotheart.starcraft.orders.Order;
import hotheart.starcraft.units.Unit;
import hotheart.starcraft.units.target.AbstractTarget;

public class MoveOrder extends Order {
	
	public MoveOrder(Unit u)
	{
		super(Order.Factory.getOrder(6, u));
	}
	
	protected boolean _execute(AbstractTarget target)
	{
		unit.target = target;
		return true;
	}
}
