package hotheart.starcraft.files;

public class DatFile {
	
	byte[] data = null;
	int position = 0;
	
	public DatFile(byte[] pData)
	{
		data = pData;
		position = 0;
	}
	
	public final void reset()
	{
		position = 0;
	}
	
	public final void seek(int nPos)
	{
		position = nPos;
	}
	
	public final byte[] read1ByteData(int size, byte[] buff) {
		byte[] res = new byte[size];
		for (int i = 0; i < size; i++) {
			res[i] = buff[position++];
		}
		return res;
	}

	public final int[] read4ByteData(int size, byte[] buff) {
		int[] res = new int[size];

		for (int i = 0; i < size; i++) {
			res[i] = (buff[position++] & 0xFF)
					+ ((buff[position++] & 0xFF) << 8)
					+ ((buff[position++] & 0xFF) << 16)
					+ ((buff[position++] & 0xFF) << 24);
		}
		return res;
	}
}
