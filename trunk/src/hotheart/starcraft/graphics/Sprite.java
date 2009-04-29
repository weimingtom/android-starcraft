package hotheart.starcraft.graphics;

import hotheart.starcraft.units.Flingy;
import hotheart.starcraft.units.ObjectPool;
import android.graphics.Canvas;


public class Sprite extends Image {
	
	
	public Sprite(Image src) {
		super(src);
		// TODO Auto-generated constructor stub
	}

	private static byte[] sprites;
	private static int count;
	public static void init(byte[] arr)
	{
		sprites = arr;
		count = (sprites.length - 130*4)/7 + 130;
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
		
		Sprite res = new Sprite(Image.getImage(imageFile, color, layer));
		res.healthBar = healthBar;
		res.selCircle = selCircle;
		res.vertPos = vertOffset;
		res.currentImageLayer = layer;
		return res;
	}
	
	public int selCircle = 0;
	public int healthBar = 6;
	public int vertPos = 9;

	public int currentImageLayer;
	
	public boolean isVisible = true;
	public boolean isSelectable = true;
	
	public Sprite parent = null;
	
	public Flingy flingy = null;
	
	public void preDraw(int sortIndex)
	{
		super.preDraw(this.posX, this.posY, sortIndex); // From Image
	}
	
	public void draw(Canvas c)
	{
		super.draw(c, this.posX, this.posY);
	}
	public void update()
	{
		super.update();
	}
	
	public void delete()
	{
		if (flingy!= null)
			ObjectPool.removeFlingy(flingy);
		ObjectPool.removeSprite(this);
	}
}
