package hotheart.starcraft.utils;

import hotheart.starcraft.core.StarcraftCore;
import android.util.Log;

public class MapNode implements Comparable<MapNode>, Cloneable {

	private int x;
	private int y;
	private float h;
	private float g;
	private float f;
	private boolean parentNode;
	private boolean childNode;
	private MapNode parent;
	private MapNode child;
	
	public MapNode(int px, int py) {
		x = px;
		y = py;
		parentNode = false;
		childNode = false;
		parent = null;
		child = null;
	}
	
	public MapNode() {
		x = 0;
		y = 0;
		parentNode = false;
		childNode = false;
		parent = null;
		child = null;
	}
	
	public MapNode(MapNode rhs) {
		x = rhs.getX();
		y = rhs.getY();

		parentNode = rhs.hasParent();
		if (parentNode)
			parent = (MapNode) rhs.getParent().getClone();
		
		childNode = rhs.hasChild();
		if (childNode)
			child = (MapNode) rhs.getChild().getClone();			
	}

	public void setParent (MapNode nParent)
	{
		parent = nParent.getClone();
		parentNode = true;
	}
	
	public void setChild (MapNode nChild)
	{
		child = nChild.getClone();
		childNode = true;
	}
	
	public MapNode getParent()
	{
		return this.parent;
	}
	
	public MapNode getChild()
	{
		return this.child;
	}
	
	public boolean hasParent()
	{
		return this.parentNode;
	}
	
	public boolean hasChild()
	{
		return this.childNode;
	}
	
	public int getX()
	{
		return this.x;
	}
	
	public int getY()
	{
		return this.y;
	}
	
	public void setH (MapNode destination)
	{
		h = getHeuristic(destination);
	}
	
	public void setG (float val)
	{
		g = val;
	}
	
	public void setF()
	{
		f = h + g;
	}
	
	public float getF()
	{
		return f;
	}
	
	public float getG()
	{
		return g;
	}
	
	public boolean isGoal(MapNode goal)
	{
		return this.getX() == goal.getX() && this.getY() == goal.getY();
	}
	
	public boolean isWalkable(MapNode rhs) {
		return StarcraftCore.context.map.isWalkable(rhs.getX()*32, rhs.getY()*32);
	}

	public boolean isWalkable(int walkableX, int walkableY) {
		return StarcraftCore.context.map.isWalkable(walkableX*32, walkableY*32);
	}
	
	public boolean isSameState(MapNode rhs)
	{
		return this.getX() == rhs.getX() && this.getY() == rhs.getY();
	}
	
	public float getHeuristic(MapNode destinationNode) {
		return (float) (Math.abs((float) this.getX() - (float) destinationNode.getX())
				+ Math.abs((float) this.getY() - (float) destinationNode.getY()));
	}
	
	public int compareTo(MapNode compared)
	{
		return (int) (this.getF() - compared.getF());
	}
	
	public boolean getSuccesors(AStarSearch aStar, MapNode parentNode) {
		int parentX = -1;
		int parentY = -1;

		if ( parentNode != null)
		{
			parentX = parentNode.getX();
			parentY = parentNode.getY();
		}
		
		MapNode newNode;
		
		//Log.i("hotheart.starcraft.system", "Walkable X Y "+ x + " " + y);
		
		if (isWalkable(x+1, y) && parentX != x+1 && parentY != y)
		{
			newNode = new MapNode(x+1, y);
			aStar.addSuccessor(newNode);
		}
		
		if (isWalkable(x+1, y+1) && parentX != x+1 && parentY != y+1)
		{
			newNode = new MapNode(x+1, y+1);
			aStar.addSuccessor(newNode);
		}
		
		if (isWalkable(x, y+1) && parentX != x && parentY != y+1)
		{
			newNode = new MapNode(x, y+1);
			aStar.addSuccessor(newNode);
		}
		
		if (isWalkable(x-1, y+1) && parentX != x-1 && parentY != y+1)
		{
			newNode = new MapNode(x-1, y+1);
			aStar.addSuccessor(newNode);
		}
		if (isWalkable(x-1, y) && parentX != x-1 && parentY != y)
		{
			newNode = new MapNode(x-1, y);
			aStar.addSuccessor(newNode);
		}
		
		if (isWalkable(x-1, y-1) && parentX != x-1 && parentY != y-1)
		{
			newNode = new MapNode(x-1, y-1);
			aStar.addSuccessor(newNode);
		}
		
		if (isWalkable(x, y-1) && parentX != x && parentY != y-1)
		{
			newNode = new MapNode(x, y-1);
			aStar.addSuccessor(newNode);
		}
		
		if (isWalkable(x+1, y-1) && parentX != x+1 && parentY != y-1)
		{
			newNode = new MapNode(x+1, y-1);
			aStar.addSuccessor(newNode);
		}
		
		return true;
	}
	
	int getCost() {
		return 1;
	}
	
	public MapNode getClone()
	{
		try
		{
			return (MapNode) super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			return this;
		}
	}
	
	@Override
	public boolean equals(Object object)
	{
		return this.isSameState((MapNode) object);
	}
}

