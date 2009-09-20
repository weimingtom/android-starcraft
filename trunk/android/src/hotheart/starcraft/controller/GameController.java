package hotheart.starcraft.controller;

import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.units.Unit;

public class GameController {
	public static final int ACTION_MOVE = 1;
	public static final int ACTION_SELECT = 2;
	
	private int currentAction = ACTION_MOVE;
	
	public void onClick(int mapX, int mapY)
	{
		switch(currentAction)
		{
			case ACTION_MOVE:
				StarcraftCore.context.moveSelected(mapX, mapY);
				break;
			case ACTION_SELECT:
				Unit u = StarcraftCore.context.PickUnit(mapX, mapY);
				if (u != null)
					StarcraftCore.context.selectUnit(u);
				break;
		}
	}
}
