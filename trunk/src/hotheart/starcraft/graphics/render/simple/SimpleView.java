package hotheart.starcraft.graphics.render.simple;

import java.io.FileNotFoundException;
import java.util.Random;

import hotheart.starcraft.configure.BuildParameters;
import hotheart.starcraft.map.Map;
import hotheart.starcraft.map.TileLib;
import hotheart.starcraft.system.ObjectPool;
import hotheart.starcraft.units.Unit;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

public class SimpleView extends View {

	Random rnd = new Random();
	Context cont;
	public int dx = 0;
	public int dy = 0;

	public Unit selUnit = null;

	public boolean fixed = false;
	public boolean selectingTarget = false;

	Map map;
	Scroller mScroller;


	public SimpleView(Context context) {
		super(context);

		cont = context;

		startTime = System.currentTimeMillis();
		frTime = System.currentTimeMillis();

		setFocusableInTouchMode(true);
		requestFocus();

		dx = this.getWidth() / 2;
		dy = this.getHeight() / 2;

		mScroller = new Scroller(context);
	}

	public void setMap(Map mp) {
		map = mp;
	}

	public void killSelectedUnit() {
		if (selUnit != null)
			selUnit.kill();
	}

	int count = 0;
	int FPS = 0;
	long startTime;
	long frTime;

	boolean drawMap = true;

	float ofsX = 56 * 32, ofsY = 60 * 32;

	@Override
	protected void onDraw(Canvas canvas) {

		// if (mapMove)
		// mScroller.computeScrollOffset();
		//    	
		// int ofsX = 56*32, ofsY = 60*32;
		//    	
		// if (ofsX<0)
		// ofsX = 0;
		// if (ofsY<0)
		// ofsY = 0;
		//    	
		// ofsX = mScroller.getCurrX();
		// ofsY = mScroller.getCurrY();

		canvas.drawARGB(255, 67, 216, 248);

		canvas.save();
		Matrix transf = canvas.getMatrix();
		transf.postTranslate(-ofsX % TileLib.TILE_SIZE, -ofsY
				% TileLib.TILE_SIZE);
		canvas.setMatrix(transf);

		if (BuildParameters.LOAD_MAP)
			if (drawMap) {
				if (map != null)
					map.draw((int)ofsX / TileLib.TILE_SIZE,
							(int)ofsY / TileLib.TILE_SIZE, (int)ofsX / TileLib.TILE_SIZE
									+ this.getWidth() / TileLib.TILE_SIZE + 2,
									(int)ofsY / 32 + this.getHeight() / TileLib.TILE_SIZE
									+ 2, canvas);
			}

		transf.postTranslate(-ofsX + ofsX % TileLib.TILE_SIZE, -ofsY + ofsY
				% TileLib.TILE_SIZE);
		dx = -(int)ofsX;
		dy = -(int)ofsY;

		canvas.setMatrix(transf);

		// ObjectPool.draw(canvas);

		long preDrawStart = System.currentTimeMillis();
		ObjectPool.preDraw();
		long preDrawTime = System.currentTimeMillis() - preDrawStart;

		long drawStart = System.currentTimeMillis();
		ObjectPool.draw_fast(canvas);
		long drawTime = System.currentTimeMillis() - drawStart;

		// transf.postTranslate(- dx, - dy);
		// canvas.setMatrix(transf);
		//    	
		// for(int i = 0; i<10; i++)
		// for(int j = 0; j<10; j++)
		// {
		// TileLib.drawMegaTile(i*32, j*32, i+j*32, canvas);
		// }

		canvas.restore();

		ObjectPool.update();

		count++;
		if (System.currentTimeMillis() - startTime > 1000) {
			FPS = count;
			count = 0;
			startTime = System.currentTimeMillis();
		}

		Paint p = new Paint();
		p.setColor(Color.RED);
		canvas.drawText("FPS: " + FPS, 10, 20, p);

		if (selectingTarget) {
			canvas.drawText("Select target", 10, 20 + 16 * 5, p);
		}
		if (fixed) {
			p.setColor(Color.GREEN);
			canvas.drawText("Fixed", 10, 20 + 16 * 6, p);
		}

		frTime = System.currentTimeMillis();
		invalidate();
	}

	int oldX = -1;
	int oldY = -1;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!fixed) {
			if (selUnit != null)
				selUnit.selected = false;

			if (event.getAction() == MotionEvent.ACTION_UP) {
				selUnit = ObjectPool.PickUnit((int) event.getX() - dx,
						(int) event.getY() - dy);

				if (selUnit != null) {
					if (!selUnit.selected)
						selUnit.sayWhat();
					selUnit.selected = true;
				}
			}
		} else if (selectingTarget) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				Unit target = ObjectPool.PickUnit((int) event.getX() - dx,
						(int) event.getY() - dy);
				if (selUnit != null) {
					if (target != null) {
						selUnit.sayYes();
						selUnit.attack(target);
					}
				}
			}
		} else {
			if (event.getAction() == MotionEvent.ACTION_UP)
				if (selUnit != null) {
					selUnit.sayYes();
					selUnit.move((int) event.getX() - dx, (int) event.getY()
							- dy);
				}
		}

		return true;
	}
//
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		switch (keyCode) {
//		case KeyEvent.KEYCODE_Q:
//			killSelectedUnit();
//			break;
//		case KeyEvent.KEYCODE_H:
//			if (selUnit != null)
//				selUnit.hit(10);
//			break;
//
//		case KeyEvent.KEYCODE_L:
//			fixed = !fixed;
//			break;
//		case KeyEvent.KEYCODE_A:
//			selectingTarget = !selectingTarget;
//			break;
//		case KeyEvent.KEYCODE_M:
//			drawMap = !drawMap;
//			break;
//		case KeyEvent.KEYCODE_T:
//			mapMove = !mapMove;
//			break;
//		}
//
//		return true;
//	}
//
//	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		return false;
//	}

}