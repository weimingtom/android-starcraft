package hotheart.starcraft.orders;

import hotheart.starcraft.units.Unit;

public class DieOrder extends Order {

	public DieOrder(Unit u) {
		super(u);
		u.kill();
	}

}
