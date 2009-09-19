package hotheart.starcraft.orders;

import hotheart.starcraft.files.DatFile;

import java.io.FileInputStream;
import java.io.IOException;

public class Order {
	
	private static final int COUNT = 189;
	
	public static int[] libLabelIds = null;
	public static byte[] libUseWeaponTargeting = null;
	public static byte[] libCanBeInterrupted = null;
	public static byte[] libCanBeQueued = null;
	
	public static byte[] libTargeting = null;
	public static byte[] libEnergy = null;
	public static byte[] libAnimation = null;
	public static byte[] libHighlight = null;
	public static byte[] libObscured = null;
	
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
		libHighlight = file.read1ByteData(COUNT);
		
		file.skip(COUNT * 2);
		
		libObscured = file.read1ByteData(COUNT);
	}

}
