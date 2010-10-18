package hotheart.starcraft.map;

import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.graphics.render.RenderTile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;

public class MapRender {
	// For 256x256 images

	public static final int TILE_SIDE = 256;

	public static final int WIDTH = 3;
	public static final int HEIGHT = 3;

	RenderTile[][] tilesWindow = new RenderTile[WIDTH][HEIGHT];
	boolean[][] loadedTiles = new boolean[WIDTH][HEIGHT];

	public int tilesOfsX = 0, tilesOfsY = 0;

	public Map gameMap;

	public MapRender(Map map) {
		gameMap = map;

		for (int i = 0; i < WIDTH; i++)
			for (int j = 0; j < HEIGHT; j++) {
				loadedTiles[i][j] = false;
				tilesWindow[i][j] = null;
			}
	}

	int[] colors = new int[TILE_SIDE * TILE_SIDE];

	void loadTile(int x, int y) {

		int absX = x + tilesOfsX;
		int absY = y + tilesOfsY;

		String fileName = String.format(
				"/sdcard/starcraft/cache/%dx%d_map.cache", absX, absY);

		File img = new File(fileName);

		if (!img.exists()) {

			// Generate Tile
			int stride = TILE_SIDE / TileLib.TILE_SIZE;

			int startX = absX * stride;
			int startY = absY * stride;

			Bitmap tmpImage = Bitmap.createBitmap(256, 256, Config.RGB_565);

			for (int i = 0; i < stride; i++)
				for (int j = 0; j < stride; j++) 
				{
					//if (gameMap.height > (startX + i) && gameMap.width > (startY + j))
						TileLib.draw(i * TileLib.TILE_SIZE, j * TileLib.TILE_SIZE,
							gameMap.mapTiles[startX + i + (startY + j)
									* gameMap.width], colors, 256);
					//else
					//{
						//loadTile((gameMap.height - stride) / stride - tilesOfsX - 1,
							//	(gameMap.width - stride) / stride - tilesOfsY - 1);
						//bottom right corner
						//return;
					//}
				}

			tmpImage.setPixels(colors, 0, 256, 0, 0, 256, 256);

			// Save Tile
			try {
				FileOutputStream fis = new FileOutputStream(img);
				tmpImage.compress(CompressFormat.JPEG, 90, fis);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			tilesWindow[x][y] = StarcraftCore.render
					.createTileFromBitmap(tmpImage);

		} else {
			tilesWindow[x][y] = StarcraftCore.render
					.createTileFromBitmap(BitmapFactory.decodeFile(fileName));
		}

		loadedTiles[x][y] = true;
	}

	public boolean isTileInWindow(int tileX, int tileY) {

		if ((tileX >= tilesOfsX) && (tileY >= tilesOfsY)
				&& (tileX < tilesOfsX + WIDTH) && (tileY < tilesOfsY + HEIGHT))
			return true;
		else
			return false;
	}

	public boolean isInWindow(int pixX, int pixY) {
		int tileX = pixX / TILE_SIDE;
		int tileY = pixY / TILE_SIDE;

		return isTileInWindow(tileX, tileY);
	}

	public boolean needMoving(int pixOfsX, int pixOfsY, int pixW, int pixH) {
		return !(isInWindow(pixOfsX, pixOfsY) && isInWindow(pixOfsX + pixW,
				pixOfsY + pixH));
	}

	public void moveTileWindow(int pixOfsX, int pixOfsY) {
		int tileX = pixOfsX / TILE_SIDE;
		int tileY = pixOfsY / TILE_SIDE;

		RenderTile[][] newTilesWindow = new RenderTile[WIDTH][HEIGHT];
		boolean[][] newLoadedTiles = new boolean[WIDTH][HEIGHT];

		boolean[][] usedOldTiles = new boolean[WIDTH][HEIGHT];

		for (int i = 0; i < WIDTH; i++)
			for (int j = 0; j < HEIGHT; j++) {
				usedOldTiles[i][j] = false;
				newTilesWindow[i][j] = null;
				newLoadedTiles[i][j] = false;
			}

		for (int i = 0; i < WIDTH; i++)
			for (int j = 0; j < HEIGHT; j++) {
				if (isTileInWindow(tileX + i, tileY + j)) {
					int oldX = tileX + i - tilesOfsX;
					int oldY = tileY + j - tilesOfsY;

					newTilesWindow[i][j] = tilesWindow[oldX][oldY];
					newLoadedTiles[i][j] = loadedTiles[oldX][oldY];

					usedOldTiles[oldX][oldY] = true;
				} else {
					newTilesWindow[i][j] = null;
					newLoadedTiles[i][j] = false;
				}
			}

		for (int i = 0; i < WIDTH; i++)
			for (int j = 0; j < HEIGHT; j++) {
				if (loadedTiles[i][j] && (!usedOldTiles[i][j])) {
					if (tilesWindow[i][j] != null)
						tilesWindow[i][j].recycle();
				}
			}
		tilesWindow = newTilesWindow;
		loadedTiles = newLoadedTiles;

		tilesOfsX = tileX;
		tilesOfsY = tileY;
	}

	public void drawMap(int ofsX, int ofsY, int width, int height) {
		if (needMoving(ofsX, ofsY, width, height)) {
			moveTileWindow(ofsX, ofsY);
		}
		int renderOfsX = ofsX % TILE_SIDE;
		int renderOfsY = ofsY % TILE_SIDE;

		int startX = ofsX / TILE_SIDE - tilesOfsX;
		int startY = ofsY / TILE_SIDE - tilesOfsY;

		for (int i = startX; i < WIDTH; i++)
			for (int j = startY; j < HEIGHT; j++) {
				if (!loadedTiles[i][j])
					loadTile(i, j);

				if (tilesWindow[i][j].isRecycled())
					loadTile(i, j);

				tilesWindow[i][j].draw(-renderOfsX + TILE_SIDE * (i - startX),
						-renderOfsY + TILE_SIDE * (j - startY));

				// c.drawBitmap(tilesWindow[i][j], -renderOfsX + TILE_SIDE
				// * (i - startX), -renderOfsY + TILE_SIDE * (j - startY),
				// new Paint());
			}
	}
}