package hotheart.starcraft.units;

public class UnitControlPanel {
	
	Unit unit;
	
	public UnitControlPanel(Unit u)
	{
		unit = u;
	}
	
	public int[] getButtons()
	{
		int[] res = new int[9];
		for(int i = 0; i < 9; i++)
			res[i] = -1;
		
		res[0] = 228;
		res[1] = 230;
		
		return res;
	}
}
