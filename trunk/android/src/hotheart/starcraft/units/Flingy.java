package hotheart.starcraft.units;

import hotheart.starcraft.core.GameContext;
import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.graphics.Sprite;
import hotheart.starcraft.map.Map;
import hotheart.starcraft.units.target.AbstractTarget;
import hotheart.starcraft.units.target.FlingyTarget;
import hotheart.starcraft.units.target.StaticPointTarget;
import hotheart.starcraft.utils.MapNode;
import hotheart.starcraft.utils.AStarSearch;

import android.util.Log;

public class Flingy extends Sprite {

	public static final int FLINGY_DAT = 0;
	public static final int MIXED = 1;
	public static final int ISCRIPT_BIN = 2;

	public static class Factory {
		private static byte[] flingy;
		private static int count;

		public static void init(byte[] arr) {
			flingy = arr;
			count = arr.length / 15;
		}

		public static final Flingy getFlingy(int id, int teamColor) {
			final int spriteId = (flingy[id * 2] & 0xFF)
					+ ((flingy[id * 2 + 1] & 0xFF) << 8);

			final int speed = (flingy[id * 4 + count * 2] & 0xFF)
					+ ((flingy[id * 4 + count * 2 + 1] & 0xFF) << 8)
					+ ((flingy[id * 4 + count * 2 + 2] & 0xFF) << 16)
					+ ((flingy[id * 4 + count * 2 + 3] & 0xFF) << 24);

			final int accel = (flingy[id * 2 + count * 6] & 0xFF)
					+ ((flingy[id * 2 + count * 6 + 1] & 0xFF) << 8);

			final int haltDist = (flingy[id * 4 + count * 8] & 0xFF)
					+ ((flingy[id * 4 + count * 8 + 1] & 0xFF) << 8)
					+ ((flingy[id * 4 + count * 8 + 2] & 0xFF) << 16)
					+ ((flingy[id * 4 + count * 8 + 3] & 0xFF) << 24);

			final int turnRadius = (flingy[id + count * 12] & 0xFF);

			final int moveControl = (flingy[id + count * 14] & 0xFF);

			final Flingy res = new Flingy(Sprite.Factory.getSprite(spriteId, teamColor,
					0));

			res.topSpeed = speed / 120;
			res.acceleration = accel;
			res.haltDistantion = haltDist / 256;
			res.turnRadius = turnRadius;
			res.moveControl = moveControl;
			return res;
		}
	}

	public Flingy(Flingy src) {
		super(src);
		this.topSpeed = src.topSpeed;
		this.acceleration = src.acceleration;
		this.haltDistantion = src.haltDistantion;
		this.turnRadius = src.turnRadius;
		this.moveControl = src.moveControl;
		this.currentAttack = src.currentAttack;
		this.speed = src.speed;
		this.action = src.action;
		this.isAir = src.isAir;
		this.target = new FlingyTarget(this);
		
		this.aStarSearch = new AStarSearch();
		this.nextNode = new MapNode();
	}

	private Flingy(Sprite src) {
		super(src);
	}

	public static final int ATTACK_AIR = 2;
	public static final int ATTACK_GRND = 3;
	public static final int IDLE = 4;
	public static final int MOVING = 5;
	public static final int DEATH = 6;

	// public Sprite sprite;
	public int topSpeed;
	public int acceleration;
	public int haltDistantion;
	public int turnRadius;
	public int moveControl;
	public boolean isAir = true;

	public AbstractTarget target = new FlingyTarget(this);
	
	// A* search related
	public boolean searchStarted = false;
	public int searchMove;
	public int searchStep;

	public MapNode nextNode;
	public AStarSearch aStarSearch;
	public int remX = target.getDestinationX();
	public int remY = target.getDestinationY();
		
	private int currentAttack = ATTACK_GRND;

	int speed = 0;
	int action = IDLE;

	public final int getSqLenToTarget() {
		int destX = target.getDestinationX();
		int destY = target.getDestinationY();

		return (int) ((posX - destX) * (posX - destX) + (posY - destY)
				* (posY - destY));
	}

	public final void move(int d) {
		final float dx = (float) Math
				.cos(((imageState.angle - 90) / 180.0f) * 3.1415f)
				* d;
		final float dy = (float) Math
				.sin(((imageState.angle - 90) / 180.0f) * 3.1415f)
				* d;

		if (isAir) {
			posX += dx;
			posY += dy;
		} else 
		{
			if (StarcraftCore.context.map.isWalkable(posX + (int) dx, posY + (int) dy)) {
				posX += dx;
				posY += dy;
			} else {
				stop();
			}
		}
	}
	public void aStar() {	

		if (searchStarted == false)
		{
		
		//Log.i("hotheart.starcraft.system", "Start X Y " + posX/32 + " " + posY/32);
		//Log.i("hotheart.starcraft.system", "Goal X Y " + target.getDestinationX()/32 + " " 
			//	+ target.getDestinationY()/32);
		
		MapNode startNode = new MapNode (posX/32, posY/32);
		MapNode goalNode = new MapNode (target.getDestinationX()/32, target.getDestinationY()/32);
		
		int searchState;
		
		searchStep = 0;
		searchMove = 0;
		
		aStarSearch.freeAllNodes();
		
		aStarSearch = null;
		aStarSearch = new AStarSearch();
		
		remX = target.getDestinationX();
		remY = target.getDestinationY();
		
		nextNode = null;
		nextNode = new MapNode(posX/32, posY/32);
		
		
		
		aStarSearch.setStartAndGoal(startNode, goalNode);
		
		do
		{
			searchState = aStarSearch.searchStep();
			//Log.i("hotheart.starcraft.system", "Search State is " + searchState);
		}
		while ( searchState == AStarSearch.SEARCH_STATE_SEARCHING );
		
		if (searchState == AStarSearch.SEARCH_STATE_SUCCEEDED)
			{
				nextNode = aStarSearch.getSolutionStart().getClone();
				searchStarted = true;
				searchStep++;
			}
			
		if (searchState == AStarSearch.SEARCH_STATE_FAILED)
			stop();
		}
		else
		{
			//Log.i("hotheart.starcraft.system", "trying to continue sS sM " + searchStep + " " + searchMove);
			if (posX / 32 == nextNode.getX() && posY / 32 == nextNode.getY())
			{	
				//Log.i("hotheart.starcraft.system", "Got new NextNode");
				nextNode = aStarSearch.getSolutionNext().getClone();
				//searchStep ++;
			}
			//Log.i("hotheart.starcraft.system","nextNode X, Y " + nextNode.getX() + " "+ nextNode.getY());
		}
	}
	
	public final void stop() {
		if (action != MOVING || action != ATTACK_GRND || action != ATTACK_AIR)
			return;
		
		aStarSearch.cancelSearch();
		searchStarted = false;
		action = IDLE;
		speed = 0;
		searchMove = 0;
		
		play(12);

	}

	public final void rotateTo(int dx, int dy) {
		final int len_sq = (int) ((posX - dx) * (posX - dx) + (posY - dy)
				* (posY - dy));

		int current_angle = imageState.angle;

		int delta = (int) (18 * 3.1415 * turnRadius / (topSpeed));

		if (moveControl != Flingy.FLINGY_DAT)
			delta = 30;

		final float dot = (float) ((dx - posX)
				* (Math.sin(3.1415f * (current_angle / 180.0f))) - (dy - posY)
				* (Math.cos(3.1415f * (current_angle / 180.0f))));

		final float cross = (float) ((dx - posX)
				* (Math.cos(3.1415f * (current_angle / 180.0f))) + (dy - posY)
				* (Math.sin(3.1415f * (current_angle / 180.0f))));

		float alpha = (float) (dot / Math.sqrt(len_sq));
		alpha = (int) (Math.acos(alpha) * 180 / 3.1515f);

		delta = (int) Math.min(delta, alpha);

		if (cross < 0)
			current_angle = (current_angle - delta) % 360;
		else
			current_angle = (current_angle + delta) % 360;

		imageState.angle = current_angle;
	}

	public void update() {
		if (target != null) {
			int destR = target.getDestinationRadius();
			int destX = target.getDestinationX();
			int destY = target.getDestinationY();
			
			final int len_sq = (int) ((posX - destX) * (posX - destX) + (posY - destY)
					* (posY - destY));

			if (action != MOVING) {
				if (len_sq > destR * destR) {
					action = MOVING;
					play(11);
					speed = 0;
				}
			}
			
			if (len_sq > destR * destR && target.getDestinationX()/32 != nextNode.getX() &&
					target.getDestinationY()/32 != nextNode.getY())
			{
				if (StarcraftCore.context.map.isWalkable(target.getDestinationX(),
						target.getDestinationY()))
				{
					aStar();
					if ((StarcraftCore.context.map.isWalkable(nextNode.getX() * 32 + 31, 
							nextNode.getY() * 32 + 16)) && (target.getDestinationX() == remX &&
									target.getDestinationY() == remY))
					{
						destX = nextNode.getX() * 32 + 31;
						destY = nextNode.getY() * 32 + 16;
						//searchMove++;
					}
					else
					{
						searchStarted = false;
						aStar();
						destX = nextNode.getX() * 32 + 31;
						destY = nextNode.getY() * 32 + 16;
						//searchMove++;
					}
				}
			}

			//Log.i("hotheart.starcraft.system", "target X Y " + target.getDestinationX() + " " 
				//	+ target.getDestinationY());
			//Log.i("hotheart.starcraft.system", "dest X Y " + destX + " " + destY);
			//Log.i("hotheart.starcraft.system","nextNode X, Y " + nextNode.getX() * 32 
				//	+ " " + nextNode.getY() * 32);

			
			rotateTo(destX, destY);

			if (action == MOVING) {

				if ((moveControl == FLINGY_DAT) || (moveControl == MIXED)) {
					speed += acceleration;

					if (speed > topSpeed)
						speed = topSpeed;

					if (len_sq < (haltDistantion) * (haltDistantion)) {
						speed -= acceleration;
						if (speed <= 0) {
							speed += acceleration;

							speed -= acceleration / 10;

							if (speed * speed > len_sq) {
								speed = (int) Math.sqrt(len_sq) + 3;
							}

							// speed = (int) Math.sqrt(len_sq);
							// if (speed > acceleration)
							// speed = acceleration;
						}
					}
				}

				if (len_sq < destR * destR) {
					stop();
					searchStarted = false;
					aStarSearch.cancelSearch();
					return;
				}

				if ((moveControl == FLINGY_DAT) || (moveControl == MIXED)) {
					move(speed);
				}
			}
		}
		super.update();
	}

	public void startAttackAnimation(int attackType) {
		if (action == attackType)
			return;

		if (attackType == ATTACK_GRND) {
			currentAttack = action = ATTACK_GRND;
			play(2);
		} else {
			currentAttack = action = ATTACK_AIR;
			play(3);
		}
	}

	public void repeatAttackCallback() {
		action = currentAttack;
		play(5);
	}

	public void finishAttack() {
		action = IDLE;
		play(8);
	}

	public void kill() {
		if (action == DEATH)
			return;

		action = DEATH;

		play(1);

		// super.delete();
	}
}
