package hotheart.starcraft.graphics;

import hotheart.starcraft.configure.BuildParameters;
import hotheart.starcraft.files.LoFile;
import hotheart.starcraft.units.ObjectPool;
import hotheart.starcraft.utils.FileSystemUtils;

import java.io.ByteArrayInputStream;
import java.util.TreeMap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

public final class GRPImage{
	
	private static TreeMap<Integer, Object> resources = new TreeMap<Integer, Object>();
	
	static byte[] images;
	
	public final static void init(byte[] data)
	{
		images = data;
	}
	private final static String getFileName(int id)
	{
		ByteArrayInputStream is = new ByteArrayInputStream(images);
		
		is.skip(id*2);
			
		int offset = (is.read()&0xFF) + ((is.read()&0xFF)<<8);
			
		is.skip(offset - id*2 - 2);
		
		StringBuilder sb = new StringBuilder();
		int ch = is.read()&0xFF;
			
		while(ch != 0)
		{
			sb.append((char)ch);
			ch = is.read()&0xFF;
		}
		
		return sb.toString();
	}
	public final static LoFile getLoData(int id)
	{
		if (resources.containsKey((Integer)id))
			return (LoFile)resources.get((Integer)id);
		else
		{
			LoFile res = new LoFile(
					FileSystemUtils.readAllBytes("sdcard/starcraft/unit/" + getFileName(id).replace('\\', '/')));
			resources.put((Integer)id, res);
			return res;
		}
	}
	
	public final static GRPImage getGraphics(int id)
	{
		if (resources.containsKey((Integer)id))
			return (GRPImage)resources.get((Integer)id);
		else
		{
			GRPImage res = new GRPImage(
					FileSystemUtils.readAllBytes("sdcard/starcraft/unit/" + getFileName(id).replace('\\', '/')));
			resources.put((Integer)id, res);
			return res;
		}
	}

	
	public static int[] buffer = new int[512*512];
	//public static int[] buffer = new int[30*30];
	
	byte[] image;
	
	public int selectedFrame = 0;
	public int count;
	public int width, height;
	
	private int[] w, h, dataOffset, xOffset, yOffset;
	
	public GRPImage(byte[] _image)
	{
		image = _image;
		count =  (image[0]&0xFF) + ((image[1]&0xFF)<<8);
		width =  (image[2]&0xFF) + ((image[3]&0xFF)<<8);
		height =  (image[4]&0xFF) + ((image[5]&0xFF)<<8);
		w = new int[count];
		h = new int[count];
		dataOffset = new int[count];
		xOffset = new int[count];
		yOffset = new int[count];
		
		for(int i = 0; i < count; i++)
		{
			int frameOffset = i*8;
			
			w[i] = image[8+ frameOffset]&0xFF;
			h[i] = image[9+ frameOffset]&0xFF;
		
			dataOffset[i] = (image[10+ frameOffset]&0xFF) + ((image[11+ frameOffset]&0xFF)<<8) + 
				((image[12+ frameOffset]&0xFF)<<16) + ((image[13+ frameOffset]&0xFF)<<24);
			
			
			xOffset[i] = image[6 + frameOffset]&0xFF;
			yOffset[i] = image[7+ frameOffset]&0xFF;
		}
	}
	
	Bitmap[] bitmaps;
	public void makeCache(int[] palette)
	{
		if (bitmaps != null)
			return;
		bitmaps = new Bitmap[count];
		for(int i = 0; i < count; i++)
		{
			int[] pixels = createBitmap(i,w[i], h[i], dataOffset[i], palette);
			bitmaps[i] = Bitmap.createBitmap(pixels,w[i], h[i], Config.ARGB_4444);
		}
		image = null;
	}
	private int[] createBitmap(int id, int xW, int yH, int dataOffset, int[] palette)
	{
		int[] res = new int[xW*yH];
		int index;
		int j;
		for(int i = 0; i<yH; i++)
		{
			int curX = 0;
			int bufPos = dataOffset + (int)(image[dataOffset+i*2]&0xFF) + ((image[dataOffset+i*2 + 1]&0xFF)<<8);
			
			while(curX<xW)
			{
				int a = image[bufPos++]&0xFF;
				
				int bufferOffset = i*xW + curX;
                if (a >= 0x80)
                {
                	a -= 0x80;

                	curX += a;
                	for (j = 0; j < a; j++)
                    {
                		res[bufferOffset++] = 0;
                		
                    }
                }
                else if (a >= 0x40)
                {
                    a -= 0x40;
                    index = image[bufPos++]&0xFF;
                    int col = palette[index];
                    curX += a;
                    for (j = 0; j < a; j++)
                    	res[bufferOffset++] = col;
                }
                else
                {
                	curX+=a;

                    for (j = 0; j < a; j++)
                    {
                    	index = image[bufPos++]&0xFF;
                    	
                    	res[bufferOffset++] = palette[index];
                    }
                }
			}
		}

		return res;
	}
	
	public final void draw(Canvas c, int[] palette) {
		try
		{
			c.save();
			Matrix matr = c.getMatrix();
			matr.preTranslate(xOffset[selectedFrame], yOffset[selectedFrame]);
			c.setMatrix(matr);
		
			draw(dataOffset[selectedFrame], w[selectedFrame], h[selectedFrame], c, palette);
		
			c.restore();
		}
		catch(Exception e)
		{
			if (BuildParameters.DEBUG_GRP_RENDER_ERROR)
				e.printStackTrace();
		}
	}
	private final  void draw(int offset, int w, int h,Canvas c, int[] palette)
	{
		ObjectPool.drawCount++;
		
		if (bitmaps == null)
		{
		int index;
		int j;
		for(int i = 0; i<h; i++)
		{
			int curX = 0;
			int bufPos = offset + (int)(image[offset+i*2]&0xFF) + ((image[offset+i*2 + 1]&0xFF)<<8);
			
			while(curX<w)
			{
				int a = image[bufPos++]&0xFF;
				
				int bufferOffset = i*w + curX;
                if (a >= 0x80)
                {
                	a -= 0x80;

                	curX += a;
                	for (j = 0; j < a; j++)
                    {
                		buffer[bufferOffset++] = 0;
                		
                    }
                }
                else if (a >= 0x40)
                {
                    a -= 0x40;
                    index = image[bufPos++]&0xFF;
                    int col = palette[index];
                    curX += a;
                    for (j = 0; j < a; j++)
                    	buffer[bufferOffset++] = col;
                }
                else
                {
                	curX+=a;

                    for (j = 0; j < a; j++)
                    {
                    	index = image[bufPos++]&0xFF;
                    	
                    	buffer[bufferOffset++] = palette[index];
                    }
                }
			}
		}
		Paint p = new Paint();
		
		c.drawBitmap(buffer, 0, w, 0, 0, w, h, true, p);
		}
		else
		{
			Paint p = new Paint();
			
			c.drawBitmap(bitmaps[selectedFrame], 0, 0, p);
		}
	}
}
