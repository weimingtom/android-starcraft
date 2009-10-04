package hotheart.starcraft.units.target;

import hotheart.starcraft.units.Unit;

public class UnitTarget extends FlingyTarget {

	public Unit destUnit;
	public UnitTarget(Unit u, int radius) 
	{
		super(u, radius);
		destUnit = u;
	}
	public UnitTarget(Unit u) {
		this(u, 10);
	}

}
