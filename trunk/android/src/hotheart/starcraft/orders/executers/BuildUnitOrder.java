package hotheart.starcraft.orders.executers;

import hotheart.starcraft.orders.Order;
import hotheart.starcraft.units.Unit;
import hotheart.starcraft.units.target.AbstractTarget;
import hotheart.starcraft.utils.UIUtils;

public class BuildUnitOrder extends Order {
	public BuildUnitOrder(Unit u)
	{
		super(Order.Factory.getOrder(30, u));
		this.isTargeting = false;
	}
	
	protected boolean _execute()
	{
		UIUtils.showSelectionMessageBox(new String[] {"A", "B", "C", "D" });
		return true;
	}
}
