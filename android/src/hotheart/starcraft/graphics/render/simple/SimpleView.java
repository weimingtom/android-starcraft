package hotheart.starcraft.graphics.render.simple;

import hotheart.starcraft.configure.BuildParameters;
import hotheart.starcraft.core.GameContext;
import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.map.Map;
import hotheart.starcraft.map.TileLib;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.view.View;

public class SimpleView extends View {

	SimpleRender render;
	
	MapRender mapRend = new MapRender();

	Map map;
	int ofsX = 56 * 32, ofsY = 60 * 32;

	// FPS counting
	int frameCount = 0;
	int FPS = 0;
	long startTime;
	Bitmap mapBitmap;

	public SimpleView(Context context, SimpleRender r) {
		super(context);

		render = r;

		startTime = System.currentTimeMillis();

		setFocusableInTouchMode(true);
		requestFocus();

	}

	public void setMap(Map mp) {
		map = mp;
		generateMapBitmap();
	}

	private static final int WALKABLE_COLOR = Color.rgb(155, 248, 31);
	private static final int NON_WALKABLE_COLOR = Color.rgb(249, 112, 30);

	void generateMapBitmap() {
		mapBitmap = Bitmap.createBitmap(map.width * 4, map.height * 4,
				Config.RGB_565);

		for (int x = 0; x < map.width; x++)
			for (int y = 0; y < map.height; y++) {

				// int[] tiles = TileLib.getTiles(map.mapTiles[x + y *
				// map.width]);

				int baseIndex = map.mapTiles[x + y * map.width];
				for (int i = 0; i < 4; i++)
					for (int j = 0; j < 4; j++) {
						boolean walkable = TileLib.haveFlagInstalled(baseIndex,
								i, j, TileLib.IS_WALKABLE);

						int xPos = x * 4 + i;
						int yPos = y * 4 + j;

						// boolean walkable = yPos % 2 == 0;

						if (walkable) {
							mapBitmap.setPixel(xPos, yPos, WALKABLE_COLOR);
						} else {
							mapBitmap.setPixel(xPos, yPos, NON_WALKABLE_COLOR);
						}
					}
			}
	}

	@Override
	protected void onDraw(Canvas canvas) {

		Matrix transf;

		if (mapBitmap == null) {
			setMap(GameContext.map);
		}

		render.canvas = canvas;

		int bkgColor = BuildParameters.BACKGROUND_COLOR;
		canvas.drawARGB(255, Color.red(bkgColor), Color.green(bkgColor), Color
				.blue(bkgColor));

		// ================================================
		// Drawing Map
		// ================================================

//		canvas.save();
//
//		transf = canvas.getMatrix();
//		transf.preScale(8, 8);
//		transf.postTranslate(-ofsX, -ofsY);
//		canvas.setMatrix(transf);
//		
//		// Draw map here
//
//		canvas.drawBitmap(mapBitmap, 0, 0, new Paint());
//
//		canvas.restore();
		
		mapRend.drawMap(canvas, ofsX, ofsY, getWidth(), getHeight());

		// ================================================
		// Drawing Units
		// ================================================

		canvas.save();

		transf = canvas.getMatrix();
		transf.postTranslate(-ofsX, -ofsY);

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