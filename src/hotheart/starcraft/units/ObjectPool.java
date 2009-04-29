package hotheart.starcraft.units;

import hotheart.starcraft.graphics.Image;
import hotheart.starcraft.graphics.SelectionCircles;
import hotheart.starcraft.graphics.Sprite;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeSet;
import android.graphics.Canvas;

public final class ObjectPool {
	public static Random rnd;
	public static int drawCount;
	public static void init()
	{
		rnd = new Random();
		drawCount = 0;
		units = new ArrayList<Unit>();
		drawObjects = new TreeSet<Image>(new Comparator<Image>()
				{
			
			public int compare(Image arg0, Image arg1) {
				if (arg0 == arg1)
					return 0;
				if (arg0.currentImageLayer == Image.MIN_IMAGE_LAYER)
					return -1;
				
				if (arg1.currentImageLayer == Image.MIN_IMAGE_LAYER)
					return 1;
				
				if (arg0.sortIndex < arg1.sortIndex)
					return 1;
				else if (arg0.sortIndex > arg1.sortIndex)
					return -1;
				
				if (arg0.currentImageLayer>arg1.currentImageLayer)
					return 1;
				else
					return -1;
			}
		});
	}
		
	public static ArrayList<Unit> units;
	
	public static void addUnit(Unit u, int x, int y)
	{
		int R = 0;
		//int objR = Sprite.selCircleSize[u.flingy.sprite.selCircle];
		while(true)
		{
			for(int i = 0; i < R; i++)
			{
				int nx = x - R;
				int ny=y - R/2 + i;
//				if ((nx < objR)||(ny < objR))
//					continue;
				
				if (PickUnit(nx, ny) == null)
				{
					u.flingy.posX = nx;
					u.flingy.posY = ny;

					addUnit(u);
					return;
				}
			}
			for(int i = 0; i < R; i++)
			{
				int nx = x + R;
				int ny=y - R/2 + i;
//				if ((nx < objR)||(ny < objR))
//					continue;
				
				if (PickUnit(nx, ny) == null)
				{
					u.flingy.posX = nx;
					u.flingy.posY = ny;

					addUnit(u);
					return;

				}
			}
			
			for(int i = 0; i < R; i++)
			{
				int ny = y - R;
				int nx = x - R/2 + i;
				
//				if ((nx < objR)||(ny < objR))
//					continue;
				if (PickUnit(nx, ny) == null)
				{
					u.flingy.posX = nx;
					u.flingy.posY = ny;

					addUnit(u);
					return;

				}
			}
			
			for(int i = 0; i < R; i++)
			{
				int ny = y + R;
				int nx = x - R/2 + i;
//				if ((nx < objR)||(ny < objR))
//					continue;
				if (PickUnit(nx, ny) == null)
				{
					u.flingy.posX = nx;
					u.flingy.posY = ny;

					addUnit(u);
					return;
				}
			}
			
			R+=10;
		}
	}
	
	public static Unit PickUnit(int x, int y)
	{
		for(Unit a : units)
		{
			if ( (x-a.flingy.posX)*(x-a.flingy.posX) + (y-a.flingy.posY)*(y-a.flingy.posY) 
					< 
					SelectionCircles.selCircleSize[a.flingy.sprite.selCircle]
					                     *
					                     SelectionCircles.selCircleSize[a.flingy.sprite.selCircle]/4)
			{
				return a;
			}
		}
		return null;
	}
	
	public static void addUnit(Unit u)
	{
		units.add(u);
	}
	
	public static void removeUnit(Unit u)
	{
		units.remove(u);
	}

	public static ArrayList<Sprite> sprites = new ArrayList<Sprite>();
	public static void addSprite(Sprite s)
	{
		sprites.add(s);
	}
	public static void removeSprite(Sprite s)
	{
		sprites.remove(s);
	}
	
	public static ArrayList<Flingy> flingys = new ArrayList<Flingy>(); 
	public static void addFlingy(Flingy f)
	{
		flingys.add(f);
	}
	public static void removeFlingy(Flingy f)
	{
		flingys.remove(f);
	}
	
	public static TreeSet<Image> drawObjects = new TreeSet<Image>();
	
	public static void preDraw()
	{
		drawObjects.clear();
		
		for(Flingy f: ObjectPool.flingys)
    		f.preDraw();
		
		for(Sprite s: ObjectPool.sprites)
    		s.preDraw(s.getOffsetY());
   	
    	for(Unit u: units)
    		u.preDraw();
	}
	
	public static void draw_fast(Canvas c)
	{
		drawCount = 0;
		
		for(Image i: ObjectPool.drawObjects)
    		i.drawWithoutChilds(c);
		
    	for(Unit u: units)
    		u.draw_selection(c);
	}

	public static void update()
	{
		for(Object s: ObjectPool.sprites.toArray())
    	{
    		((Sprite)s).update();
    	}
		
		for(Object u: ObjectPool.flingys.toArray())
    	{
    		((Flingy)u).update();
    	}
    	
    	for(Object u: ObjectPool.units.toArray())
    	{
    		((Unit)u).update();
    	}
	}

	
}

