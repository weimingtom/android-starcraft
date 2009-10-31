package hotheart.starcraft.graphics.render.simple;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import hotheart.starcraft.configure.BuildParameters;
import hotheart.starcraft.core.GameContext;
import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.map.Map;
import hotheart.starcraft.map.MapRender;
import hotheart.starcraft.map.TileLib;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.view.View;

public class SimpleView extends View {

	SimpleRender render;

	MapRender mapRend;

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
		mapRend = new MapRender(map);
		generateMapBitmap();
	}

	//private static final int WALKABLE_COLOR = Color.rgb(155, 248, 31);
	//private static final int NON_WALKABLE_COLOR = Color.rgb(249, 112, 30);
	
	private static final int WALKABLE_COLOR = Color.GREEN;
	private static final int NON_WALKABLE_COLOR = Color.RED;

	void generateMapBitmap() {

		mapBitmap = Bitmap.createBitmap(map.width * 4, map.height * 4,
				Config.RGB_565);
		
		int[] colors = new int[map.width * 4 * map.height * 4];

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

						int color = Color.BLACK;
						if (walkable) {
							color = WALKABLE_COLOR;
							//mapBitmap.setPixel(xPos, yPos, WALKABLE_COLOR);
						} else {
							color = NON_WALKABLE_COLOR;
							//mapBitmap.setPixel(xPos, yPos, NON_WALKABLE_COLOR);
						}
						
						colors[xPos + yPos*mapBitmap.getWidth()] = color;
					}
			}
		mapBitmap.setPixels(colors, 0, mapBitmap.getWidth(), 0, 0, mapBitmap.getWidth(), mapBitmap.getHeight());
		
		try {
			FileOutputStream fis = new FileOutputStream("/sdcard/map.png");
			mapBitmap.compress(CompressFormat.PNG, 90, fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
//		mapBitmap.c
	}

	@Override
	protected void onDraw(Canvas canvas) {

		Matrix transf;

		if (mapRend == null) {
			setMap(StarcraftCore.context.map);
		}

		render.canvas = canvas;

		int bkgColor = BuildParameters.BACKGROUND_COLOR;
		canvas.drawARGB(255, Color.red(bkgColor), Color.green(bkgColor), Color
				.blue(bkgColor));

		// ================================================
		// Drawing Map
		// ================================================

		if (mapRend != null)
			mapRend.drawMap(ofsX, ofsY, getWidth(), getHeight());

		// ================================================
		// Drawing Map Paths
		// ================================================

		canvas.save();

		transf = canvas.getMatrix();
		transf.preScale(8, 8);
		transf.postTranslate(-ofsX, -ofsY);
		canvas.setMatrix(transf);

		// Draw map path here

		Paint p = new Paint();
		p.setAlpha(127);

		canvas.drawBitmap(mapBitmap, 0, 0, p);

		canvas.restore();

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

		p = new Paint();
		p.setColor(Color.RED);
		canvas.drawText("FPS: " + FPS, 10, 20, p);

		invalidate();
	}
}