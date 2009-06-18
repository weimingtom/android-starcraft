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
	
	SimpleRender render;
	
	Map map;
	int ofsX = 56 * 32, ofsY = 60 * 32;
	
	
	// FPS counting
	int frameCount = 0;
	int FPS = 0;
	long startTime;
	
	public SimpleView(Context context, SimpleRender r) {
		super(context);
		
		render = r;

		startTime = System.currentTimeMillis();

		setFocusableInTouchMode(true);
		requestFocus();
		
	}

	public void setMap(Map mp) {
		map = mp;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		render.canvas = canvas;
		render.begin();
		
		canvas.drawARGB(255, 67, 216, 248);

		canvas.save();
		
		Matrix transf = canvas.getMatrix();
		transf.postTranslate(-ofsX % TileLib.TILE_SIZE, -ofsY
				% TileLib.TILE_SIZE);
		canvas.setMatrix(transf);

		if (BuildParameters.LOAD_MAP)
			if (map != null)
				map.draw((int) ofsX / TileLib.TILE_SIZE, (int) ofsY
								/ TileLib.TILE_SIZE, (int) ofsX
								/ TileLib.TILE_SIZE + this.getWidth()
								/ TileLib.TILE_SIZE + 2, (int) ofsY / 32
								+ this.getHeight() / TileLib.TILE_SIZE + 2,
								canvas);

		transf.postTranslate(-ofsX + ofsX % TileLib.TILE_SIZE, -ofsY + ofsY
				% TileLib.TILE_SIZE);

		canvas.setMatrix(transf);

		ObjectPool.preDraw();
		ObjectPool.draw_fast();

		canvas.restore();
		
		render.end();

		ObjectPool.update();

		frameCount++;
		if (System.currentTimeMillis() - startTime > 1000) {
			FPS = frameCount;
			frameCount = 0;
			startTime = System.currentTimeMillis();
		}

		Paint p = new Paint();
		p.setColor(Color.RED);
		canvas.drawText("FPS: " + FPS, 10, 20, p);

		invalidate();
	}
}