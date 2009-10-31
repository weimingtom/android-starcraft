package hotheart.starcraft.units;

import hotheart.starcraft.orders.Order;
import hotheart.starcraft.units.target.AbstractTarget;
import hotheart.starcraft.units.target.UnitTarget;

public class UnitOrders {
	
	Unit unit;
	public Order[] orders;
	
	public UnitOrders(Unit u)
	{
		unit = u;
		updateOrders();
	}
	
	private void updateOrders()
	{
		Order[] tmp = new Order[9];
		int count = 0;
		
		if ((unit.specialAbilityFlags&Unit.ABILITY_BUILDING) == 0)
			tmp[count++] = Order.getOrder(Order.ORDER_STOP);
		
		if ((unit.specialAbilityFlags&Unit.ABILITY_BUILDING) == 0)
			tmp[count++] = Order.getOrder(Order.ORDER_MOVE);
		
		if ((unit.airWeapon!=null)||(unit.groundWeapon!=null))
			tmp[count++] = Order.getOrder(Order.ORDER_ATTACK);
		
		orders = new Order[count];
		for(int i = 0; i< count; i++)
			orders[i] = tmp[i];
	}
	
	public void executeButton(int id)
	{
		orders[id].execute(unit);
		updateOrders();
	}
	
	public void executeButton(int id, AbstractTarget target)
	{
		orders[id].execute(unit, target);
		updateOrders();
	}
	
	public int selectedButton = -1;
}
