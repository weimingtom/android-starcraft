package hotheart.starcraft.units;

public class UnitControlPanel {
	
	Unit unit;
	
	public UnitControlPanel(Unit u)
	{
		unit = u;
	}
	
	public int[] getButtons()
	{
		return new int[9];
	}
}
