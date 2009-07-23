package hotheart.starcraft.controller;

import hotheart.starcraft.core.StarcraftCore;

public class GameController {
	private static final int MOVE_ACTION = 1;
	private static final int SELECT_ACTION = 2;
	
	private int currentAction = MOVE_ACTION;
	
	public void onClick(int mapX, int mapY)
	{
		switch(currentAction)
		{
			case MOVE_ACTION:
				StarcraftCore.context.moveSelection(mapX, mapY);
				break;
			case SELECT_ACTION:
				break;
		}
	}
}
