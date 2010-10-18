package hotheart.starcraft.utils;


import hotheart.starcraft.utils.MapNode;

import java.util.ArrayList;
import java.util.Collections;
import android.util.Log;

public class AStarSearch {
	
		public static final int SEARCH_STATE_NOT_INITIALISED = -1;
		public static final int SEARCH_STATE_SEARCHING = 0;
		public static final int SEARCH_STATE_SUCCEEDED = 1;
		public static final int SEARCH_STATE_FAILED = 3;
		public static final int SEARCH_STATE_OUT_OF_MEMORY = 4;
		public static final int SEARCH_STATE_INVALID = 5;
		
		private MapNode mStartNode;
		private MapNode mGoalNode;
		private MapNode mCurrentSolution;
		
		private ArrayList<MapNode> openList;
		private ArrayList<MapNode> closedList;
		private ArrayList<MapNode> successorList;
		
		private int searchState;
		private int searchSteps;
		
		private boolean cancelRequest = false;
		
		public AStarSearch() {
			openList = new ArrayList<MapNode>(1);
			closedList = new ArrayList<MapNode>(1);
			successorList = new ArrayList<MapNode>(1);
			
			mStartNode = new MapNode();
			mGoalNode = new MapNode();
			
			searchState = SEARCH_STATE_NOT_INITIALISED;
		}
		
	public void setStartAndGoal(MapNode startNode, MapNode goalNode)
	{
		mStartNode = startNode.getClone();
		mGoalNode = goalNode.getClone();
		
		searchState = SEARCH_STATE_SEARCHING;
		
		mStartNode.setH(mGoalNode);
		mStartNode.setG((float) 0);
		mStartNode.setF();
		
		openList.add(mStartNode);
		Collections.sort(openList);
		
		searchSteps = 0;
		//Log.i("hotheart.starcraft.system", "started A*");
	}
	
	public void cancelSearch()
	{
		cancelRequest = true;
	}
	
	public int searchStep()
	{
		assert (searchState > SEARCH_STATE_NOT_INITIALISED && searchState < SEARCH_STATE_INVALID);
		
		if (searchState == SEARCH_STATE_SUCCEEDED || searchState == SEARCH_STATE_FAILED)
		{
			//Log.i("hotheart.starcraft.system", "finished searching!");
			return searchState;
		}
		
		if (openList.isEmpty() || cancelRequest)
		{
			freeAllNodes();
			searchState = SEARCH_STATE_FAILED;
			//Log.i("hotheart.starcraft.system", "failed, empty oL " + openList.isEmpty());
			return searchState;
		}
		
		searchSteps++;
		MapNode n = openList.get(0);
		openList.remove(n);
		Collections.sort(openList);
		
		if (n.isGoal(mGoalNode))
		{
			//Log.i("hotheart.starcraft.system", "FOUND GOAL NODE in " + searchSteps + " steps");
			mGoalNode.setParent(n.getParent());
			
			if (false == n.isSameState(mStartNode))
				{
				MapNode nodeChild = new MapNode(mGoalNode);
				MapNode nodeParent = new MapNode(mGoalNode.getParent());
				int pass = 0;
				do
					{
						pass++;
						//Log.i("hotheart.starcraft.system", nodeChild.getX() + " " + nodeChild.getY());
						nodeParent.setChild(nodeChild);
						nodeChild = nodeParent.getClone();
						nodeParent = nodeParent.getParent().getClone();
					}
				while (!nodeParent.equals(mStartNode));
				
				mStartNode.setChild(nodeChild);
				//Log.i("hotheart.starcraft.system", "mStartNode has child : " 
						//+ (boolean)(mStartNode.hasChild()));

				}
			
			freeUnusedNodes();
			searchState = SEARCH_STATE_SUCCEEDED;
			//Log.i("hotheart.starcraft.system", "Finished searching, moving on!");
			return searchState;
		}
		else
		{
			for (int i = 0; i < successorList.size(); i++)
				freeNode(successorList.get(i));
			successorList.clear();
			boolean ret = n.getSuccesors(this, n.hasParent() ? n.getParent() : null);
			
			//Log.i("hotheart.starcraft.system", "SuccesorList: "+successorList);
			
			if (!ret)
			{
			freeAllNodes();
			searchState = SEARCH_STATE_OUT_OF_MEMORY;
			//Log.i("hotheart.starcraft.system", "Out of memory, aborting search");
			return searchState;
			}
			
			for (int i = 0; i< successorList.size(); i++)
			{
				MapNode successor = successorList.get(i);
				float newg = n.getG() + n.getCost();
				
				//Log.i("hotheart.starcraft.system", "OpenList: " + openList);
				
				if (openList.contains(successor))
				{
					if (openList.get(openList.indexOf(successor)).getG() <= newg)
					{
						freeNode(successor);
						continue;
					}
				}
				
				//Log.i("hotheart.starcraft.system", "ClosedList: "+closedList);
				
				if (closedList.contains(successor))
				{
					if (closedList.get(closedList.indexOf(successor)).getG() <= newg)
					{
						freeNode(successor);
						continue;
					}
				}
						
				successor.setParent(n);
				successor.setG(newg);
				successor.setH(mGoalNode);
				successor.setF();
				
				if (closedList.contains(successor))
				{
					freeNode (closedList.get(closedList.indexOf(successor)));
					closedList.remove(closedList.get(closedList.indexOf(successor)));
					Collections.sort(closedList);
				}
					
				if (openList.contains(successor))
				{
					freeNode (openList.get(openList.indexOf(successor)));
					openList.remove(openList.get(openList.indexOf(successor)));
					Collections.sort(openList);
				}
						
				openList.add(successor);
				Collections.sort(openList);
			}
			closedList.add(n);
			Collections.sort(closedList);
		}
		
		//Log.i("hotheart.starcraft.system", "Passed through a search step, continuing...");
	return searchState;
	}

	public void addSuccessor(MapNode newNode)
	{
		successorList.add(newNode);
	}
	
	public MapNode getSolutionStart()
	{
		mCurrentSolution = mStartNode;

		//Log.i("hotheart.starcraft.system", "mStartNode has child : " 
				//+ (boolean)(mStartNode.hasChild()));
		
		if (mStartNode != null)
		{
			return mStartNode;
		}
		else 
			return null;
	}
	
	public MapNode getSolutionNext()
	{
		//Log.i("hotheart.starcraft.system", "mCurrentSolution is null: "
				//+ (boolean)(mCurrentSolution == null));
		//Log.i("hotheart.starcraft.system", "mCurrentSolution has child: "
				//+ (boolean)(mCurrentSolution.hasChild()));
		if (!(mCurrentSolution == null))
		{
			if (mCurrentSolution.hasChild())
			{
				//Log.i("hotheart.starcraft.system", "methodNode X, Y "
						//+ mCurrentSolution.getChild().getX() + " "+mCurrentSolution.getChild().getY());
				
				MapNode child = new MapNode(mCurrentSolution.getChild().getClone());
				
				mCurrentSolution = mCurrentSolution.getChild().getClone();
				
				//Log.i("hotheart.starcraft.system", "child is null:" 
						//+ (boolean)(child == null));
				
				return child;
			}
		}
		return null;
	}
	
	private void freeNode (MapNode node)
	{
		node = null;
	}
	
	
	public void freeAllNodes()
	{
		for (int i = 0;i < openList.size(); i++)
			freeNode(openList.get(i));
		openList.clear();
		
		for (int i = 0; i < closedList.size(); i++)
			freeNode(closedList.get(i));
		closedList.clear();
		
		for (int i = 0; i < successorList.size(); i++)
			freeNode(successorList.get(i));
		successorList.clear();
			
		//Log.i("hotheart.starcraft.system", "oL "+ openList + " cl " + closedList + " sL "
			//	+ successorList);
	}
	
	private void freeUnusedNodes()
	{
		for (int i = 0; i < openList.size(); i++)
			if (!openList.get(i).hasChild())
				freeNode(openList.get(i));
		for (int i = 0; i < closedList.size(); i++)
			if (!closedList.get(i).hasChild())
				freeNode(closedList.get(i));
		openList.clear();
		closedList.clear();
	}
	
	

}