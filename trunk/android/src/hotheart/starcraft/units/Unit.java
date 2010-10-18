package hotheart.starcraft.units;

import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.files.DatFile;
import hotheart.starcraft.graphics.Image;
import hotheart.starcraft.graphics.utils.SelectionCircles;
import hotheart.starcraft.orders.Order;
import hotheart.starcraft.orders.executers.AttackOrder;
import hotheart.starcraft.units.target.FlingyTarget;
import hotheart.starcraft.units.target.StaticPointTarget;
import hotheart.starcraft.units.target.UnitTarget;
import hotheart.starcraft.weapons.Weapon;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public final class Unit extends Flingy {
	
	public static class Factory
	{
		private static final int COUNT = 228;

		private static byte[] libFlingyId;
		private static int[] libSubUnit1;
		private static int[] libSubUnit2;
		private static int[] libHitPoints;
		private static byte[] libElevationLevel;
		private static byte[] libGroundWeapon;
		private static byte[] libAirWeapon;
		private static int[] libSpecialAbilityFlags;

		public static void init(byte[] arr) {

			DatFile file = new DatFile(new ByteArrayInputStream(arr));
			try {
				libFlingyId = file.read1ByteData(COUNT);
				libSubUnit1 = file.read2ByteData(COUNT);
				libSubUnit2 = file.read2ByteData(COUNT);

				file.skip((201 - 106 + 1)*2);
				file.skip(COUNT * 8);

				libHitPoints = file.read4ByteData2InnerBytes(COUNT);
				libElevationLevel = file.read1ByteData(COUNT);

				file.skip(COUNT * 7);

				libGroundWeapon = file.read1ByteData(COUNT);

				file.skip(COUNT);

				libAirWeapon = file.read1ByteData(COUNT);

				file.skip(COUNT * 2);

				libSpecialAbilityFlags = file.read4ByteData(COUNT);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public static Unit getUnit(int id, int teamColor) {
			int flingyId = libFlingyId[id]&0xFF;
			int subUnit1 = libSubUnit1[id];
			int subUnit2 = libSubUnit2[id];
			int hitPoints = libHitPoints[id];
			int elevationLevel = libElevationLevel[id]&0xFF;
			int groundWeapon = libGroundWeapon[id]&0xFF;
			int airWeapon = libAirWeapon[id]&0xFF;

			int specialAbilityFlags = libSpecialAbilityFlags[id];

			Unit res = new Unit(Flingy.Factory.getFlingy(flingyId, teamColor));

			res.unitId = id;
			res.teamColor = teamColor;
			res.hipPoints = hitPoints;
			res.maxHitPoints = hitPoints;
			res.elevationLevel = elevationLevel;
			res.specialAbilityFlags = specialAbilityFlags;

			if (elevationLevel >= 12) {
				res.isAir = true;
			} else {
				res.isAir = false;
			}
			
			if (groundWeapon != 130)
				res.groundWeapon = Weapon.Factory.getWeapon(groundWeapon);
			if (airWeapon != 130)
				res.airWeapon = Weapon.Factory.getWeapon(airWeapon); 

			if (subUnit1 != 228) {
				res.subunit1 = getUnit(subUnit1, teamColor);
				if (res.subunit1 != null)
					res.subunit1.parent = res;
			}

			if (subUnit2 != 228) {
				res.subunit2 = getUnit(subUnit2, teamColor);
				if (res.subunit2 != null)
					res.subunit2.parent = res;
			}
			
			if ((res.specialAbilityFlags & Unit.ABILITY_BUILDING) != 0)
			{
				res.buildQueue = new BuildingQueue(res);
			}
			
			res.controlPanel = new UnitOrders(res);

			return res;
		}
	}

	public static final int ABILITY_BUILDING = 1 << 0;
	public static final int ABILITY_WORKER = 1 << 3;
	public static final int ABILITY_SUBUNIT = 1 << 4;

	private Unit(Flingy src) {
		super(src);
	}

	public static final int MAX_GROUND_LEVEL = 11;
	
	// Library data

	public Weapon groundWeapon;
	public Weapon airWeapon;

	public Unit subunit1 = null;
	public Unit subunit2 = null;

	public Unit parent = null;

	public int maxHitPoints;
	public int hipPoints;
	public int elevationLevel;
	
	public int specialAbilityFlags = 0;
	
	public int unitId = 0;
	
	public BuildingQueue buildQueue = null;
	
	// Game data
	
	public int teamColor;
	
	public Order currentOrder = null;
	
	public UnitOrders controlPanel;
	
	public void buildTree() {
		if (parent!=null)
		{
			setPos(parent.getPosX(), parent.getPosY());
			rotateTo(parent.nextNode.getX()*32, parent.nextNode.getY()*32);
		}
		
		super.buildTree();
		
		if (subunit1 != null)
			subunit1.buildTree();
		if (subunit2 != null)
			subunit2.buildTree();
	}

	public final void drawSelection() {
		Image circ = SelectionCircles.selCircles[selCircle];
		circ.setPos(posX, posY + vertPos);
		circ.draw();
	}

	public void update() {
		super.update();

		if (subunit1 != null) {
			subunit1.update();
		}
		if (subunit2 != null)
			subunit2.update();
		
		if (currentOrder!=null)
			currentOrder.update();
	}
	
	public void kill() {

		StarcraftCore.context.removeUnit(this);
		StarcraftCore.context.addImage(this);

		super.kill();

		if (subunit1 != null)
			subunit1.kill();
		if (subunit2 != null)
			subunit2.kill();
	}

	public final void attack(Unit unit) {
		
		if (unit == this)
			return;
		
		Weapon selWeapon = this.airWeapon;
		if (!unit.isAir)
			selWeapon = this.groundWeapon;
		
		if (selWeapon != null)
		{
			currentOrder = new AttackOrder(this);
			currentOrder.execute(new UnitTarget(unit));
		}

		if (subunit1 != null)
			subunit1.attack(unit);
		if (subunit2 != null)
			subunit1.attack(unit);

	}
	
	public final void shootCallback(int type) {
		
		if (currentOrder instanceof AttackOrder)
			((AttackOrder)currentOrder).shoot(type);
	}

	public void repeatAttackCallback() {
		
		if (currentOrder instanceof AttackOrder)
			((AttackOrder)currentOrder).repeatAttack();
		
		super.repeatAttackCallback();
	}

	public final void hit(int points) {
		hipPoints -= points;
		if (hipPoints <= 0) {
			hipPoints = 0;
			kill();
		}
	}
}