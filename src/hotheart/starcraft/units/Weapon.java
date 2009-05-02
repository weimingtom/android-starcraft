package hotheart.starcraft.units;

import hotheart.starcraft.graphics.TeamColors;

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
	public static final int B_FLY_TO_TARGET      = 1;
	
	public int damage;
	public int behaviour;
	public int flingyId;
	
	public int reloadTime;
	
	public int maxDistance;
	public int minDistance;
	
	private class Missle extends Flingy
	{
		Unit destUnit;
		public Missle(Unit dest, Flingy base)
		{
			super(base);
			destUnit = dest;
		}
		public void update()
		{
			int len_sq = (destUnit.flingy.getPosX() - this.posX)*(destUnit.flingy.getPosX() - this.posX)+
					  (destUnit.flingy.getPosY() - this.posY)*(destUnit.flingy.getPosY() - this.posY);
			if (len_sq<10)
				this.delete();
			else
				this.move(destUnit.flingy.getPosX(), destUnit.flingy.getPosY());
			super.update();
		}
	}
	
	public boolean attack(Unit srsUnit,  Unit targetUnit)
	{
		if (behaviour == B_APPEAR_ON_TARGET)
		{
			Flingy f = Flingy.getFlingy(flingyId, TeamColors.COLOR_DEFAULT);
			f.setPos(targetUnit.flingy.getPosX(), targetUnit.flingy.getPosY());
			f.imageState.angle = targetUnit.flingy.imageState.angle; 
			ObjectPool.addImage(f);
		}
		else if (behaviour == B_FLY_TO_TARGET)
		{
			Flingy f = Flingy.getFlingy(flingyId, TeamColors.COLOR_DEFAULT);
			f.setPos(srsUnit.flingy.getPosX(), srsUnit.flingy.getPosY());
			
			ObjectPool.addImage(new Missle(targetUnit, f));
		}
		
		targetUnit.hit(damage);
		
		if (targetUnit.health<=0)
		{
			targetUnit.kill();
			return true;
		}
		else
			return false;
	}
}
