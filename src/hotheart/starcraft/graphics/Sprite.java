package hotheart.starcraft.graphics;

import hotheart.starcraft.units.Flingy;
import hotheart.starcraft.units.ObjectPool;
import android.graphics.Canvas;


public class Sprite {
	public static int[] selCircleSize = new int[]
	{
		22, 32, 46, 62, 72, 94, 110, 122, 146, 224,
		22, 32, 46, 62, 72, 94, 110, 122, 146, 224
	};
	private static final int FIRST_CIRCLE_IMAGE = 561;
	public static final int DEFAULT_LAYER = 15;
	
	public static Image[] selCircles;
	
	private static byte[] sprites;
	private static int count;
	public static final void init(byte[] arr)
	{
		sprites = arr;
		count = (sprites.length - 130*4)/7 + 130;
	}
	public static final void initCircles()
	{
		selCircles = new Image[20];
		for(int i = 0; i < 20; i++)
		{
			selCircles[i] = Image.getImage(i + FIRST_CIRCLE_IMAGE, Image.COLOR_GREEN, Image.MAX_IMAGE_LAYER);
		}
	}
	//TODO: Add isVisible and isSelectable
	public static final Sprite getSprite(int id, int color, int layer)
	{
		int imageFile = (sprites[id*2]&0xFF) + 
					   ((sprites[id*2 + 1]&0xFF)<<8);
		
		int healthBar = 0;
		if (id>=130)
			healthBar = (sprites[(id - 130) + count*2]&0xFF);
		
		int selCircle = 0;
		if (id>=130)
			selCircle = (sprites[(id - 130) + count*4 + (count-130)*1]&0xFF);
		
		int vertOffset = 0;
		if (id>=130)
			vertOffset = (sprites[(id - 130) + count*4 + (count-130)*2]&0xFF);
		
		Sprite res = new Sprite();
		res.image = Image.getImage(imageFile, color, layer);
		res.image.sprite = res;
		res.healthBar = healthBar;
		res.selCircle = selCircle;
		res.vertPos = vertOffset;
		res.currentImageLayer = layer;
		return res;
	}
	
	public Image image = null;
	
	public int selCircle = 0;
	public int healthBar = 6;
	public int vertPos = 9;
	
	public int globalX = 0;
	public int globalY = 0;
	
	public int currentImageLayer;
	
	public boolean isVisible = true;
	public boolean isSelectable = true;
	
	public Sprite parent = null;
	
	public Flingy flingy = null;
	
	public final void preDraw(int sortIndex)
	{
    	if (image!=null)
        	image.preDraw(globalX, globalY, sortIndex);
	}
	
	public final void draw(Canvas c)
	{
    	if (image!=null)
    	{
        	image.draw(c, globalX, globalY);
    	}
	}
	public final void update()
	{
		if (image!=null)
			image.update();
	}
	
	public void delete()
	{
		if (flingy!= null)
			ObjectPool.removeFlingy(flingy);
		ObjectPool.removeSprite(this);
	}
}
