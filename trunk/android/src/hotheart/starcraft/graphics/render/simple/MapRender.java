package hotheart.starcraft.graphics.render.simple;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

public class MapRender {
	// For 256x256 images

	public static final int TILE_SIDE = 256;

	public static final int WIDTH = 3;
	public static final int HEIGHT = 3;

	Bitmap[][] tilesWindow = new Bitmap[WIDTH][HEIGHT];
	boolean[][] loadedTiles = new boolean[WIDTH][HEIGHT];

	public int tilesOfsX = 0, tilesOfsY = 0;

	public MapRender() {
		for (int i = 0; i < WIDTH; i++)
			for (int j = 0; j < HEIGHT; j++) {
				loadedTiles[i][j] = false;
				tilesWindow[i][j] = null;
			}
	}

	void loadTile(int x, int y) {
		tilesWindow[x][y] = Bitmap.createBitmap(256, 256, Config.RGB_565);

		Canvas tmp = new Canvas(tilesWindow[x][y]);
		Paint p = new Paint();
		p.setTextSize(64);
		p.setColor(Color.GREEN);
		p.setAlpha(127);

		tmp.drawText(String.format("[%d | %d]", (Integer) (x + tilesOfsX),
				(Integer) (y + tilesOfsY)), 100, 100, p);

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

		Bitmap[][] newTilesWindow = new Bitmap[WIDTH][HEIGHT];
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

	public void drawMap(Canvas c, int ofsX, int ofsY, int width, int height) {
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

				c.drawBitmap(tilesWindow[i][j], -renderOfsX + TILE_SIDE
						* (i - startX), -renderOfsY + TILE_SIDE * (j - startY),
						new Paint());
			}
	}
}