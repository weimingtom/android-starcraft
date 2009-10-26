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
		
		int curPos = 0;
		
		if ((unit.specialAbilityFlags&Unit.ABILITY_BUILDING) == 0)
			res[curPos++] = 228;
		
		if ((unit.airWeapon!=null)||(unit.groundWeapon!=null))
			res[curPos++] = 230;
		
		return res;
	}
}