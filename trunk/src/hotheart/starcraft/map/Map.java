package hotheart.starcraft.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class Map {

	public int width;
	public int height;

	public int[] mapTiles;
	
	Loader thrd;
	
	public Map(byte[] mapData) {
		int p = 0;
		while (p < mapData.length) {
			long len = (mapData[p + 4] & 0xFF) + ((mapData[p + 5] & 0xFF) << 8)
					+ ((mapData[p + 6] & 0xFF) << 16)
					+ ((mapData[p + 7] & 0xFF) << 24);

			int dOffset = p + 8;

			if ((mapData[p] == 'E') & (mapData[p + 1] == 'R')
					& (mapData[p + 2] == 'A')) {
				
				int era = (mapData[dOffset] & 0xFF)
						+ ((mapData[dOffset + 1] & 0xFF) << 8);
				
				era = era%8;
				
				TileLib.init(era);
			}
			else if ((mapData[p] == 'D') & (mapData[p + 1] == 'I')
					& (mapData[p + 2] == 'M')) {
				width = (mapData[dOffset] & 0xFF)
						+ ((mapData[dOffset + 1] & 0xFF) << 8);
				height = (mapData[dOffset + 2] & 0xFF)
						+ ((mapData[dOffset + 3] & 0xFF) << 8);
			} else if ((mapData[p] == 'T') & (mapData[p + 1] == 'I')
					& (mapData[p + 2] == 'L') & (mapData[p + 3] == 'E')) {
				Log.v("Size:", width + "x" + height);
				mapTiles = new int[width * height];

				for (int i = 0; i < mapTiles.length; i++) {
					mapTiles[i] = (mapData[dOffset + i * 2] & 0xFF)
							+ ((mapData[dOffset + i * 2 + 1] & 0xFF) << 8);
				}
			}

			p += 8 + len;
		}

		for (int i = 0; i < cachedTiles.length; i++)
		{
			cachedTiles[i] = Bitmap.createBitmap(32, 32, Config.RGB_565);
			loaded[i] = true;
		}
		thrd = new Loader();
		thrd.start();
	}
	
	class Loader extends Thread
	{
		public void run() {
			
			while(true)
			{
				try {
					while(allIsLoaded)
						sleep(10);
				
				
					for (int i = 0; i < TILE_CACHE_SIZE; i++)
						for (int j = 0; j < TILE_CACHE_SIZE; j++)
						{
							if ((i + r_x1 >= 0) && (i + r_y1 >= 0)&& (i + r_x1 < width) && (j + r_y1 < height)&& (!loaded[i + j * TILE_CACHE_SIZE])) {
								TileLib.draw(0, 0, mapTiles[i + r_x1 + (j + r_y1)* width], buf, 32);
								cachedTiles[i + j * TILE_CACHE_SIZE].setPixels(buf,	0, 32, 0, 0, 32, 32);
								
								Bitmap img = cachedTiles[i + j * TILE_CACHE_SIZE];
								mapImageCanvas.drawBitmap(img, i << 5, j << 5,
												new Paint());

								loaded[i + j * TILE_CACHE_SIZE] = true;
							}
						}
					
					allIsLoaded = true;
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private Bitmap mapImage = Bitmap.createBitmap(920, 920, Config.RGB_565);
	private Canvas mapImageCanvas= new Canvas(mapImage);
	
	private static final int CACHE_SIZE_DELTA = 6;
	private static final int TILE_CACHE_SIZE = 25;

	private Bitmap[] cachedTiles = new Bitmap[TILE_CACHE_SIZE
			* TILE_CACHE_SIZE];

	private int[] buf = new int[32 * 32];
	
	private boolean allIsLoaded = false;
	
	private boolean[] loaded = new boolean[TILE_CACHE_SIZE
	                              			* TILE_CACHE_SIZE];
	
	private boolean[] loaded2 = new boolean[TILE_CACHE_SIZE
	                              			* TILE_CACHE_SIZE];
	
	private Bitmap[] cachedTiles2 = new Bitmap[TILE_CACHE_SIZE
			* TILE_CACHE_SIZE];
	
	private int r_x1 = -10000, r_x2 = -10000, r_y1 = -10000, r_y2 = -100000;
	
	private void redrawCache()
	{
		for (int i = r_x1; i <= r_x2; i++)
			for (int j = r_y1; j <= r_y2; j++) {
				if ((i - r_x1 >= 0) && (j - r_y1 >= 0)
						&& (i - r_x1 < TILE_CACHE_SIZE)
						&& (j - r_y1 < TILE_CACHE_SIZE)) {
					
					if (!loaded[(i - r_x1) + (j - r_y1)* TILE_CACHE_SIZE])
							allIsLoaded = false;
					else
					{
						Bitmap img = cachedTiles[(i - r_x1) + (j - r_y1)
						                         * TILE_CACHE_SIZE];
						mapImageCanvas.drawBitmap(img, (i - r_x1) << 5, (j - r_y1) << 5,
										new Paint());
					}
				}
			}
	}
	public void draw(int x1, int y1, int x2, int y2, Canvas c) {

		if (!((x1 >= r_x1) && (x2 <= r_x2) && (y1 >= r_y1) && (y2 <= r_y2))) {

			int old_r_x1 = r_x1;
			int old_r_y1 = r_y1;

			r_x1 = x1 - CACHE_SIZE_DELTA;
			r_x2 = x2 + CACHE_SIZE_DELTA;
			r_y1 = y1 - CACHE_SIZE_DELTA;
			r_y2 = y2 + CACHE_SIZE_DELTA;

			r_x1 = r_x1 < 0 ? 0 : r_x1;
			r_y1 = r_y1 < 0 ? 0 : r_y1;

			r_x1 = r_x1 >= width ? width - 1 : r_x1;
			r_y1 = r_y1 >= height ? height - 1 : r_y1;

			int dX = r_x1 - old_r_x1;
			int dY = r_y1 - old_r_y1;

			if ((Math.abs(dY) >= TILE_CACHE_SIZE)
				 	|| (Math.abs(dX) >= TILE_CACHE_SIZE)) {
				dX = 10000;
				dY = 10000;
			}
			
			Log.v("VERBOSE", "offset:" + dX + ":" + dY);

			for (int i = 0; i < TILE_CACHE_SIZE; i++)
				for (int j = 0; j < TILE_CACHE_SIZE; j++)
				{
					int dstIndex = i + j * TILE_CACHE_SIZE;
					int srsIndex = (i + dX + TILE_CACHE_SIZE)% TILE_CACHE_SIZE + ((j + dY + TILE_CACHE_SIZE) % TILE_CACHE_SIZE)* TILE_CACHE_SIZE;
					
					cachedTiles2[dstIndex] = cachedTiles[srsIndex];
					loaded2[dstIndex] = loaded[srsIndex];
				}

			Bitmap[] old = cachedTiles;
			cachedTiles = cachedTiles2;
			cachedTiles2 = old;
			
			boolean[] old_loaded = loaded;
			loaded = loaded2;
			loaded2 = old_loaded;

			for (int i = 0; i < TILE_CACHE_SIZE; i++)
				for (int j = 0; j < TILE_CACHE_SIZE; j++)
					if (!((i + dX >= 0) && (i + dX < TILE_CACHE_SIZE)
							&& (j + dY >= 0) && (j + dY < TILE_CACHE_SIZE))) {
						
						if ((i + r_x1 >= 0) && (i + r_y1 >= 0)&& (i + r_x1 < width) && (j + r_y1 < height)) {
							loaded[i + j * TILE_CACHE_SIZE] = false;
//							TileLib.draw(0, 0, mapTiles[i + r_x1 + (j + r_y1)* width], buf, 32);
//							cachedTiles[i + j * TILE_CACHE_SIZE].setPixels(buf,	0, 32, 0, 0, 32, 32);
						}
					}
			redrawCache();
			allIsLoaded = false;
		}
		for (int i = x1; i <= x2; i++)
			for (int j = y1; j <= y2; j++) {
				if ((i - r_x1 >= 0) && (j - r_y1 >= 0)
						&& (i - r_x1 < TILE_CACHE_SIZE)
						&& (j - r_y1 < TILE_CACHE_SIZE)) {
					
				 while (!loaded[(i - r_x1) + (j - r_y1)* TILE_CACHE_SIZE])
				 {
					allIsLoaded = false;
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 }
				}
			}
		//redrawCache();
		c.drawBitmap(mapImage, -(x1 - r_x1)*32, -(y1 - r_y1)*32, new Paint());
	}
}
