package hotheart.starcraft.graphics.render.simple;

import hotheart.starcraft.configure.BuildParameters;
import hotheart.starcraft.core.GameContext;
import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.map.Map;
import hotheart.starcraft.map.TileLib;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;

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

		StarcraftCore.context.draw();
		
		canvas.restore();
		
		

		StarcraftCore.context.update();

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