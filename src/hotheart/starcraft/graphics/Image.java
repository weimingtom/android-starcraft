package hotheart.starcraft.graphics;

import hotheart.starcraft.configure.BuildParameters;
import hotheart.starcraft.units.ObjectPool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Canvas;
import android.graphics.Color;

public final class Image{
	public static final int COLOR_DEFAULT = 0;
	public static final int COLOR_GREEN   = 1;
	public static final int COLOR_RED     = 2;
	public static final int COLOR_BLUE    = 3;
	
	public static final int MAX_IMAGE_LAYER = 10000;
	public static final int MIN_IMAGE_LAYER = -10000;
	
	private static int count;
	private static byte[] data;

	private static int[] normalPalette;
	
	private static int[] redPalette;
	private static int[] greenPalette;
	private static int[] bluePalette;
	
	private static int[] selectPalette;
	
	private static int[] shadowPalette;
	private static int[] ofirePalette;
	private static int[] gfirePalette;
	private static int[] bfirePalette;
	private static int[] bexplPalette;
	
	private static final short[] indexes = { 8, 9, 10, 11, 12, 13, 14, 15};
	private static final float[] alpha = { 255.0f/255.0f, 
		222.0f/255.0f, 189.0f/255.0f, 156.0f/255.0f, 124.0f/255.0f, 
		91.0f/255.0f, 58.0f/255.0f, 25.0f/255.0f};

	private final static int[] createPalette(int color)
	{
		int[] res = new int[256];
		for(int i = 0; i< res.length; i++)
			res[i] = normalPalette[i];
		
		int dR = Color.red(color);
		int dG = Color.green(color);
		int dB = Color.blue(color);
		for(int i = 0; i< indexes.length; i++)
		{
			int R = (int)(dR*alpha[i]);
			int G = (int)(dG*alpha[i]);
			int B = (int)(dB*alpha[i]);
			res[indexes[i]] =(255<<24) + (R<<16) + (G<<8) + B; 
		}
		
		return res;
	}
	public final static void init(byte[] _data) {
		data = _data;
		count = data.length / 38;

		try {
			FileInputStream is = new FileInputStream(
					"/sdcard/starcraft/units.pal");
			byte[] tmp = new  byte[is.available()];
			is.read(tmp);
			is.close();
			
			normalPalette = new int[256];
			for(int i = 0; i< 256; i++)
			{
				normalPalette[i] =(255<<24) + (tmp[i*3]<<16) + (tmp[i*3 + 1]<<8) + tmp[i*3 + 2]; 
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		redPalette = createPalette(Color.RED);
		greenPalette = createPalette(Color.GREEN);
		bluePalette = createPalette(Color.BLUE);
		

		shadowPalette = new int[256];
		for (int i = 0; i < 256; i++)
		{
			shadowPalette[i] =(127<<24) + (127<<16) + (127<<8) + 127;
		}
		
		selectPalette = new int[256];
		ofirePalette = new int[256];
		gfirePalette = new int[256];
		bfirePalette = new int[256];
		bexplPalette = new int[256];
		
		for (int i = 0; i < 256; i++)
		{
			selectPalette[i] =(255<<24) + (0<<16) + (255<<8) + 0;
		}

		try {
			FileInputStream is = new FileInputStream(
					"/sdcard/starcraft/ofire.pal");
			byte[] tmp = new byte[is.available()];
			is.read(tmp);
			is.close();
			
			for(int i = 0; i< 256; i++)
			{
				ofirePalette[i] =(tmp[i*4]<<24) + (tmp[i*4 + 1]<<16) + (tmp[i*4 + 2]<<8) + tmp[i*3 + 3]; 
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileInputStream is = new FileInputStream(
					"/sdcard/starcraft/gfire.pal");
			byte[] tmp = new byte[is.available()];
			is.read(tmp);
			is.close();
			
			
			for(int i = 0; i< 256; i++)
			{
				gfirePalette[i] =(tmp[i*4]<<24) + (tmp[i*4 + 1]<<16) + (tmp[i*4 + 2]<<8) + tmp[i*3 + 3]; 
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileInputStream is = new FileInputStream(
					"/sdcard/starcraft/blue.pal");
			byte[] tmp = new byte[is.available()];
			is.read(tmp);
			is.close();
			
			for(int i = 0; i< 256; i++)
			{
				bfirePalette[i] =(tmp[i*4]<<24) + (tmp[i*4 + 1]<<16) + (tmp[i*4 + 2]<<8) + tmp[i*3 + 3]; 
			}	
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileInputStream is = new FileInputStream(
					"/sdcard/starcraft/bexpl.pal");
			byte[] tmp = new byte[is.available()];
			is.read(tmp);
			is.close();
			
			for(int i = 0; i< 256; i++)
			{
				bexplPalette[i] =(tmp[i*4]<<24) + (tmp[i*4 + 1]<<16) + (tmp[i*4 + 2]<<8) + tmp[i*3 + 3]; 
			}	

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final static Image getImage(int id, int color, int layer) {
		int grpId = (data[id * 4] & 0xFF) + ((data[id * 4 + 1] & 0xFF) << 8)
				+ ((data[id * 4 + 2] & 0xFF) << 16)
				+ ((data[id * 4 + 3] & 0xFF) << 24);

		int scriptId = (data[id * 4 + count * 10] & 0xFF)
				+ ((data[id * 4 + count * 10 + 1] & 0xFF) << 8)
				+ ((data[id * 4 + count * 10 + 2] & 0xFF) << 16)
				+ ((data[id * 4 + count * 10 + 3] & 0xFF) << 24);

		int align = (data[id + count * 4] & 0xFF);

		int functionId = (data[id + count * 8] & 0xFF);
		int remapping = (data[id + count * 9] & 0xFF);

		Image res = new Image(new GRPContainer(grpId), ImageScriptEngine
				.createHeader(scriptId), id, layer);
		
		int[] pal = normalPalette;
		//byte[] pal = redPalette;
		if (functionId == 10)
			pal = shadowPalette;
		else if (functionId == 9) {
			switch (remapping) {
			case 1:
				pal = ofirePalette;
				break;
			case 2:
				pal = gfirePalette;
				break;
			case 3:
				pal = bfirePalette;
				break;
			case 4:
				pal = bexplPalette;
				break;
			}
		}
		
		if (BuildParameters.CACHE_GRP)
			res.grp.image.makeCache(pal);
		res.align = align == 1;
		res.graphicsFuntion = functionId;
		res.remapping = remapping;
		res.foregroundColor = color;
		return res;
	}
	
	public int imageId;
	
	public int foregroundColor;
	
	public int currentImageLayer;

	public Image(GRPContainer ovGRP, ImageScriptHeader ovHeader, int id, int imageLayer) {
		imageId = id;
		this.grp = ovGRP;
		this.scriptHeader = ovHeader;
		ImageScriptEngine.init(this);
		currentImageLayer = imageLayer;
	}

	// Graphics data
	public int baseFrame = 0;
	public boolean align = true;
	public boolean visible = true;
	public GRPContainer grp;

	public int graphicsFuntion = 0;
	public int remapping = 0;

	public int offsetX = 0;
	public int offsetY = 0;

	
	public Image[] childs = new Image[20];

	public boolean followParent = false;
	public boolean followParentAnim = false;
	public boolean followParentAngle = false;

	// Graphics script data
	public int scriptPos = 0;
	public int scriptWait = 0;
	public ImageScriptHeader scriptHeader;
	public int gotoLine = -1;
	public boolean isBlocked = false;
	public boolean isPaused = false;
	public int returnLine = 0;

	// Game Data
	public Image parentOverlay = null;
	public int angle = 0;
	public Sprite sprite = null;

	public boolean deleted = false;
	
	public int childCount = 0;

	public final void delete() {
		
		deleted = true;
		
		if (childCount == 0)
		{
			if (sprite != null) {
				if (sprite.image == this)
					sprite.delete();
			} else if (parentOverlay != null) {
				parentOverlay.removeChild(this);
			}
		}
	}
	
	public final void addOverlay(Image img)
	{
		for(int i = 0; i<childs.length; i++)
			if (childs[i] == null)
			{
				childs[i]=img;
				childCount++;
				return;
			}

	}
	public final void addUnderlay(Image img)
	{
		for(int i = 0; i<childs.length; i++)
			if (childs[i] == null)
			{
				childCount++;
				childs[i]=img;
				return;
			}
	}

	public final void removeChild(Image img) {
		for(int i = 0; i<childs.length; i++)
			if (childs[i] == img)
			{
				childs[i]=null;
				childCount--;
				break;
			}
		if ((deleted)&(childCount == 0))
			delete();
	}

	public final void update() {
		if (!deleted)
			ImageScriptEngine.exec(this);

		for(int i = 0; i<childs.length; i++)
			if (childs[i]!=null)
				childs[i].update();
		
	}

	public final void play(int anim) {
		if (!deleted)
			ImageScriptEngine.play(this, anim);

		for(int i = 0; i<childs.length; i++)
			if (childs[i]!=null)
				if (childs[i].followParentAnim)
					childs[i].play(anim);
	}
	
	public int resX = 0, resY = 0;
	public int sortIndex = 0;
	public final void preDraw(int dX, int dY, int dSortIndex) {
		if (!this.visible)
			return;
		resX = dX;
		resY = dY;
		sortIndex = dSortIndex;
		ObjectPool.drawObjects.add(this);
		
		for(int i = 0; i<childs.length; i++)
			if (childs[i]!=null)
				childs[i].preDraw(dX, dY, dSortIndex);
	}
	public final void drawWithoutChilds(Canvas c)
	{
		if (!isBlocked)
			if (parentOverlay != null) {
				if (followParent)
					this.baseFrame = parentOverlay.baseFrame;
				if (followParentAngle)
					this.angle = parentOverlay.angle;
			}
		
		if (!deleted) {
			int[] pal = normalPalette;
			//byte[] pal = redPalette;
			if (graphicsFuntion == 10)
				pal = shadowPalette;
			else if (graphicsFuntion == 9) {
				switch (remapping) {
				case 1:
					pal = ofirePalette;
					break;
				case 2:
					pal = gfirePalette;
					break;
				case 3:
					pal = bfirePalette;
					break;
				case 4:
					pal = bexplPalette;
					break;
				}
			}
			else if (graphicsFuntion == 13)//WTF?! must be 11
				pal = selectPalette;
			else
				switch(foregroundColor)
				{
				case COLOR_RED:
					pal = redPalette;
					break;
				case COLOR_GREEN:
					pal = greenPalette;
					break;
				case COLOR_BLUE:
					pal = bluePalette;
					break;
				}
			
			grp.draw(c, this, pal, offsetX + resX, offsetY + resY);
		}
	}
	
	public final void draw(Canvas c, int dX, int dY) {
		if (!this.visible)
			return;

		if (!isBlocked)
			if (parentOverlay != null) {
				if (followParent)
					this.baseFrame = parentOverlay.baseFrame;
				if (followParentAngle)
					this.angle = parentOverlay.angle;
			}

		for(int i = 0; i<childs.length; i++)
			if (childs[i]!=null)
				childs[i].draw(c, dX, dY);
		
		resX = dX;
		resY = dY;
		drawWithoutChilds(c);
		
	}
}
