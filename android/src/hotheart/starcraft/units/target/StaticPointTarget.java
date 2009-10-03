package hotheart.starcraft.units.target;

public class StaticPointTarget extends AbstractTarget {

	int destX, destY;
	public StaticPointTarget(int x, int y)
	{
		destX = x;
		destY = y;
	}
	@Override
	public int getDestinationRadius() {
		return 10;
	}

	@Override
	public int getDestinationX() {
		return destX;
	}

	@Override
	public int getDestinationY() {
		return destY;
	}

}
