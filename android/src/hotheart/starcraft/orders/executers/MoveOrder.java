package hotheart.starcraft.orders.executers;

import hotheart.starcraft.orders.Order;
import hotheart.starcraft.units.Unit;
import hotheart.starcraft.units.target.AbstractTarget;

public class MoveOrder extends OrderExecutor {
	
	public MoveOrder(Unit u, AbstractTarget target) {
		super(u);
		
		u.target = target;
	}
}
