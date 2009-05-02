package hotheart.starcraft.files;

import java.io.FileInputStream;
import java.io.IOException;

public class DatFile {
	
	FileInputStream is;
	
	public DatFile(FileInputStream _is)
	{
		is = _is;
	}
	
	public final void skip(int offset) throws IOException
	{
		is.skip(offset);
	}
	
	public final byte[] read1ByteData(int size) throws IOException {
		byte[] res = new byte[size];
		for (int i = 0; i < size; i++) {
			res[i] = (byte)is.read();
		}
		return res;
	}

	public final int[] read4ByteData(int size) throws IOException {
		int[] res = new int[size];

		for (int i = 0; i < size; i++) {
			res[i] = (((byte)is.read()) & 0xFF)
					+ ((((byte)is.read()) & 0xFF) << 8)
					+ ((((byte)is.read()) & 0xFF) << 16)
					+ ((((byte)is.read()) & 0xFF) << 24);
		}
		return res;
	}
	
	public final int[] read2ByteData(int size) throws IOException {
		int[] res = new int[size];

		for (int i = 0; i < size; i++) {
			res[i] = (((byte)is.read()) & 0xFF)
					+ ((((byte)is.read()) & 0xFF) << 8);
		}
		return res;
	}
}