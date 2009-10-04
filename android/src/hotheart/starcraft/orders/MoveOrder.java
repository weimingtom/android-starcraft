package hotheart.starcraft.orders;

import hotheart.starcraft.units.Unit;
import hotheart.starcraft.units.target.AbstractTarget;

public class MoveOrder extends Order {
	
	public MoveOrder(Unit u, AbstractTarget target) {
		super(u);
		
		u.target = target;
	}
}
