package hotheart.starcraft.orders;

import hotheart.starcraft.files.DatFile;
import hotheart.starcraft.orders.executers.*;
import hotheart.starcraft.units.Unit;
import hotheart.starcraft.units.target.*;

import java.io.FileInputStream;
import java.io.IOException;

public class Order {

	public static final int ORDER_DIE = 0;
	public static final int ORDER_STOP = 1;
	public static final int ORDER_MOVE = 6;
	public static final int ORDER_ATTACK = 10;

	public static class Factory {

		private static final int COUNT = 189;

		private static int[] libLabelIds = null;
		private static byte[] libUseWeaponTargeting = null;
		private static byte[] libCanBeInterrupted = null;
		private static byte[] libCanBeQueued = null;

		private static byte[] libTargeting = null;
		private static byte[] libEnergy = null;
		private static byte[] libAnimation = null;
		private static int[] libHighlight = null;
		private static byte[] libObscured = null;

		public static void initOrders(FileInputStream _is) throws IOException {
			initBuffers(_is);
		}

		private static void initBuffers(FileInputStream _is) throws IOException {
			DatFile file = new DatFile(_is);

			libLabelIds = file.read2ByteData(COUNT);

			libUseWeaponTargeting = file.read1ByteData(COUNT);

			file.skip(COUNT * 4);

			libCanBeInterrupted = file.read1ByteData(COUNT);

			file.skip(COUNT);

			libCanBeQueued = file.read1ByteData(COUNT);

			file.skip(COUNT * 4);

			libTargeting = file.read1ByteData(COUNT);

			libEnergy = file.read1ByteData(COUNT);
			libAnimation = file.read1ByteData(COUNT);
			libHighlight = file.read2ByteData(COUNT);

			file.skip(COUNT * 2);

			libObscured = file.read1ByteData(COUNT);
		}

		public static final Order getOrder(int id) {
			Order res = new Order();
			res.id = id;
			res.iconId = libHighlight[id];
			res.isTargeting = libTargeting[id] != 0;

			return res;
		}
	}

	private Order() {
	}

	public int id = 0;
	public int iconId = -1;

	public boolean isTargeting = false;

	public OrderExecutor execute(Unit u) {
		switch (id) {
		case ORDER_DIE:
			return new DieOrder(u);
		default:
			throw new UnsupportedOperationException();
		}
	}

	public OrderExecutor execute(Unit u, AbstractTarget target) {
		switch (id) {
		case ORDER_MOVE:
			return new MoveOrder(u, target);
		case ORDER_ATTACK:
			if (target instanceof UnitTarget)
				return new AttackOrder(u, ((UnitTarget) target).destUnit);
			else
				throw new UnsupportedOperationException();
		default:
			throw new UnsupportedOperationException();
		}
	}
}
