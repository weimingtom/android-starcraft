package hotheart.starcraft.units;

public final class Weapon {
	
	private static byte[] weapons;
	private static int count;
	public static void init(byte[] _weapons)
	{
		weapons = _weapons;
		count = 130;
	}
	
	public static Weapon getWeapon(int id)
	{
		Weapon res = new Weapon();
		int grOffset = count*2;
		res.flingyId =  (weapons[grOffset + id*4]&0xFF) +
						((weapons[grOffset + id*4 + 1]&0xFF)<<8) +
						((weapons[grOffset + id*4 + 2]&0xFF)<<16) +
						((weapons[grOffset + id*4 + 3]&0xFF)<<14);
		
		//9
		res.minDistance = (weapons[count*9 + id*4]&0xFF) +
						 ((weapons[count*9 + id*4 + 1]&0xFF)<<8) +
		                 ((weapons[count*9 + id*4 + 2]&0xFF)<<16) +
		                 ((weapons[count*9 + id*4 + 3]&0xFF)<<14);
		
		
		res.maxDistance = (weapons[count*13 + id*4]&0xFF) +
		 				 ((weapons[count*13 + id*4 + 1]&0xFF)<<8) +
		 				 ((weapons[count*13 + id*4 + 2]&0xFF)<<16) +
		 				 ((weapons[count*13 + id*4 + 3]&0xFF)<<14);
		

		int dmgOffset = count*28;
		res.damage =  (weapons[dmgOffset + id*2]&0xFF) +
					   ((weapons[dmgOffset + id*2 + 1]&0xFF)<<8);
		
		int bhvOffset = count*19;
		res.behaviour =  (weapons[bhvOffset + id]&0xFF);
		
		res.reloadTime = (weapons[count*32 + id]&0xFF);

		return res;
	}
	public static final int B_APPEAR_ON_ATTACKER = 5;
	public static final int B_APPEAR_ON_TARGET   = 2;
	
	public int damage;
	public int behaviour;
	public int flingyId;
	
	public int reloadTime;
	
	public int maxDistance;
	public int minDistance;
}
