package hotheart.starcraft.orders.executers;

import hotheart.starcraft.orders.Order;
import hotheart.starcraft.units.Unit;

public class DieOrder extends OrderExecuter {

	public DieOrder(Unit u) {
		super(u);
		u.kill();
	}

}
