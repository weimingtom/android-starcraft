package hotheart.starcraft.units.target;

import hotheart.starcraft.units.Flingy;

public class FlingyTarget extends AbstractTarget {

	Flingy dest;
	
	public FlingyTarget(Flingy u)
	{
		dest = u;
	}
	
	@Override
	public int getDestinationX() {
		return dest.getPosX();
	}

	@Override
	public int getDestinationY() {
		return dest.getPosY();
	}

	@Override
	public int getDestinationRadius() {
		return 10;
	}

}
