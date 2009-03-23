package hotheart.starcraft.graphics;

public class LoFile {
	
	byte[] lo;
	int count;
	int perFrame;
	public LoFile(byte[] data)
	{
		lo = data;
		count = (lo[0]&0xFF) + ((lo[1]&0xFF)<<8)+
			    ((lo[2]&0xFF)<<16) + ((lo[3]&0xFF)<<24);
		
		perFrame= (lo[4]&0xFF) + ((lo[5]&0xFF)<<8)+
				 ((lo[6]&0xFF)<<16) + ((lo[7]&0xFF)<<24);
		
	}
	
	public void getOffsets(int layer, int frameNo, byte[] offs){
		int offset = (lo[8 + frameNo*4]&0xFF) + ((lo[9 + frameNo*4]&0xFF)<<8)+
			((lo[10 + frameNo*4]&0xFF)<<16) + ((lo[11 + frameNo*4]&0xFF)<<24);

		offs[0] = lo[offset + layer*2];
		offs[1] = lo[offset + layer*2 + 1];
	}
}
