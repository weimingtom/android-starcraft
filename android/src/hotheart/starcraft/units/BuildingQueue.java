package hotheart.starcraft.units;

import hotheart.starcraft.core.StarcraftCore;

public class BuildingQueue {
	public static final int QUEUE_MAX_LENGTH = 9;

	Unit unit;

	int[] queue = new int[QUEUE_MAX_LENGTH];
	int[] buildTime = new int[QUEUE_MAX_LENGTH];

	int count = 0;

	public BuildingQueue(Unit parent) {
		unit = parent;
	}

	public synchronized void update() {
		if (count > 0) {
			if (buildTime[0] <= 0) {
				onBuilded(queue[0]);
				remove(0);
			} else
				buildTime[0]--;
		}
	}

	public synchronized final int getQueueCount() {
		return count;
	}

	public synchronized void add(int unitId, int build) throws Exception {
		if (count == 9)
			throw new Exception("Queue is full");

		buildTime[count] = build;
		queue[count] = unitId;
		count++;
	}

	public synchronized void remove(int index) {
		if (count > 0) {
			for (int i = 1; i < count; i++) {
				queue[i - 1] = queue[i];
				buildTime[i - 1] = buildTime[i];
			}
			count--;
		}
	}

	protected void onBuilded(int unitId) {
		StarcraftCore.context.addUnit(Unit.Factory.getUnit(unitId,
				unit.teamColor), unit.getPosX(), unit.getPosY());
	}
}