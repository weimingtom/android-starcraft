package hotheart.starcraft.system;

import java.util.Random;

import hotheart.starcraft.map.Map;
import hotheart.starcraft.units.ObjectPool;
import hotheart.starcraft.units.Unit;
import hotheart.starcraft.utils.FileSystemUtils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class UnitView extends View {
	
	Random rnd = new Random();
	Context cont;
	public int dx = 0;
	public int dy = 0;
	
	public Unit selUnit = null;
	
	public boolean fixed = false;
	public boolean selectingTarget = false;
	
	Map map;
	public UnitView(Context context) {
		super(context);
		cont = context;
		
		startTime = System.currentTimeMillis();
		frTime = System.currentTimeMillis();
		
		setFocusableInTouchMode(true);
		requestFocus();
		
		dx = this.getWidth()/2;
    	dy = this.getHeight()/2;
    	
    	map = new Map(FileSystemUtils.readAllBytes("/sdcard/starcraft/scenario.chk"));
	}
	int count = 0;
	int FPS = 0;
	long startTime;
	long frTime;
	
	boolean drawMap = true;
	
	int ofsX = 56*32, ofsY = 60*32;
	
    @Override protected void onDraw(Canvas canvas) {
    	
    	canvas.drawARGB(255, 255, 255, 255);
    	
    	
    	canvas.save();
    	Matrix transf = canvas.getMatrix();
    	transf.postTranslate(-ofsX%32, -ofsY%32);
    	canvas.setMatrix(transf);
    	
    	
    	if (drawMap)
    	{
    		map.draw(ofsX/32, ofsY/32, ofsX/32+ 1 + this.getWidth()/32, ofsY/32+this.getHeight()/32 + 1, canvas);
    	}
    	
    	transf.postTranslate( -ofsX + ofsX%32,-ofsY +  ofsY%32);
    	dx = -ofsX;dy = -ofsY;
    	
    	canvas.setMatrix(transf);
    	
    	//ObjectPool.draw(canvas);
    	
    	long preDrawStart = System.currentTimeMillis();
    	ObjectPool.preDraw();
    	long preDrawTime = System.currentTimeMillis() - preDrawStart;
    	
    	long drawStart = System.currentTimeMillis();
    	ObjectPool.draw_fast(canvas);
    	long drawTime = System.currentTimeMillis() - drawStart;

    	
    	
    	
//    	transf.postTranslate(- dx, - dy);
//    	canvas.setMatrix(transf);
//    	
//    	for(int i = 0; i<10; i++)
//    		for(int j = 0; j<10; j++)
//    		{
//    			TileLib.drawMegaTile(i*32, j*32, i+j*32, canvas);
//    		}
    	
    	canvas.restore();
    	
    	
    	
    	ObjectPool.update();
    	
    	count++;
    	if (System.currentTimeMillis() - startTime>1000)
    	{
    		FPS = count;
    		count = 0;
    		startTime = System.currentTimeMillis();
    	}
    	
    	Paint p = new Paint();
    	p.setColor(Color.RED);
    	canvas.drawText("FPS: " + FPS, 10, 20, p);
    	
    	Runtime rt = Runtime.getRuntime();
    	long total = rt.totalMemory(); /* Всего памяти, в обычной Java SE - это параметр -Xmx при старте приложения */
    	long free = rt.freeMemory(); /* Свободно памяти */
    	long used = total - free; /* Занято приложением */
    	 
    	canvas.drawText("Memory: " + used/(1024*1024.0f) +  " of " +total/(1024*1024.0f), 10, 20 + 16*2, p);
    	
    	canvas.drawText("Draw count:" + ObjectPool.drawCount, 10, 20 + 16, p);
    	
    	canvas.drawText("preDraw time:" + preDrawTime, 10, 20 + 16*3, p);
    	canvas.drawText("draw time:" + drawTime, 10, 20 + 16*4, p);
    	
    	//canvas.drawText("Memory: " + Debug.getThreadAllocSize()/(1024.0f*1024.0f), 10, 36, p);
    	if (selectingTarget)
    	{
    		canvas.drawText("Select target", 10, 20+16*5, p);
    	}
    	if (fixed)
    	{
    		p.setColor(Color.GREEN);
        	canvas.drawText("Fixed", 10, 20+16*6, p);
    	}
//    	if (System.currentTimeMillis() - frTime < 20)
//    	{
//    	try {
//    		long delta = 20 - (System.currentTimeMillis() - frTime);
//    		if (delta>0)
//    			Thread.sleep(delta);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	}
    	frTime = System.currentTimeMillis();
    	invalidate();
    }
    
    boolean mapMove = true;
    int oldX = -1;
    int oldY = -1;
    @Override
    public boolean onTouchEvent(MotionEvent event){
    	if (mapMove)
    	{
    		if (event.getAction() == MotionEvent.ACTION_DOWN)
    		{
    			oldX = (int)event.getX();
    			oldY = (int)event.getY();
    		}
    		else if (event.getAction() == MotionEvent.ACTION_MOVE)
    		{
    			try
    			{
    				int dx =(int)(oldX - event.getX());
    				int dy =(int)(oldY - event.getY());
    				
    				oldX = (int)event.getX();
        			oldY = (int)event.getY();
        			
    				ofsX+=dx;
    				ofsY+=dy;
    			}
    			catch(Exception e){}
    		}
    	}
    	else if (!fixed)
    	{
    		if (selUnit!=null)
				selUnit.selected = false;
    		
    		if (event.getAction() == MotionEvent.ACTION_UP)
    		{
				selUnit = ObjectPool.PickUnit((int)event.getX() - dx, (int)event.getY() - dy);
		
				if (selUnit!=null)
				{
					if (!selUnit.selected)
						selUnit.sayWhat();
					selUnit.selected = true;
				}
    		}			
    	}
    	else if (selectingTarget)
    	{
    		if (event.getAction() == MotionEvent.ACTION_UP)
    		{
    			Unit target = ObjectPool.PickUnit((int)event.getX() - dx, (int)event.getY() - dy);
    			if (selUnit!=null)
    			{
    				if (target!=null)
    				{
    					selUnit.sayYes();
    					selUnit.attack(target);
    				}
    			}
    		}
    	}
    	else
    	{
    		if (event.getAction() == MotionEvent.ACTION_UP)
    			if (selUnit!=null)
    			{
    				selUnit.sayYes();
    				selUnit.move((int)event.getX() - dx, (int)event.getY() - dy);
    			}
    	}
    	
    	return true;
	}
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	switch(keyCode)
    	{
    		case KeyEvent.KEYCODE_Q:
    			if (selUnit!=null)
    				selUnit.kill();
				break;
    		case KeyEvent.KEYCODE_H:
    			if (selUnit!=null)
    				selUnit.hit(10);
    			break;
    			
    		case KeyEvent.KEYCODE_L:
    			fixed = !fixed;
    			break;
    		case KeyEvent.KEYCODE_A:
    			selectingTarget = !selectingTarget;
    			break;
    		case KeyEvent.KEYCODE_M:
    			drawMap = !drawMap;
    			break;
    		case KeyEvent.KEYCODE_T:
    			mapMove = !mapMove;
    			break;
    	}
    	
		return true;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return false;
	}
    
}