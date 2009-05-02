/*
 * Module for loading and drawing map tiles
 * for Starcraft Android version
 * 
 * by Korshakov Stepan(c)
 * korshakov.stepan@gmail.com
 * 2009 year
 */
package hotheart.starcraft.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.Log;
import hotheart.starcraft.utils.FileSystemUtils;

public class TileLib {
	public static int[] palette;// Palette - generated from WPE file
	public static byte[] CV5; // Descriptions of sprites
	//public static byte[] VR4; // Tiles(8x8)
	public static int[] VX4Indexes; // Mega-tiles(32x32)
	public static Bitmap[] miniTiles; // Tiles(8x8)

	public static final void init(int type) {
		String filePrefix = "";

		// This ids used in scenario.chk
		switch (type & 0x7) {
		case 0:
			filePrefix = "badlands";
			break;
		case 1:
			filePrefix = "platform";
			break;
		case 2:
			filePrefix = "install";
			break;
		case 3:
			filePrefix = "ashworld";
			break;
		case 4:
			filePrefix = "jungle";
			break;
		case 5:
			filePrefix = "desert";
			break;
		case 6:
			filePrefix = "ice";
			break;
		case 7:
			filePrefix = "twilight";
			break;
		}

		filePrefix = "/sdcard/starcraft/tileset/" + filePrefix;

		// Loading palette

		byte[] pal = FileSystemUtils.readAllBytes(filePrefix + ".wpe");
		palette = new int[256];

		for (int i = 0; i < pal.length / 4; i++) {
			palette[i] = (255 << 24) + ((pal[i * 4]&0xFF)<<16) + ((pal[i * 4 + 1]&0xFF) << 8)
					+ (pal[i * 4 + 2]&0xFF);
		}

		// Loading tiles files

		CV5 = FileSystemUtils.readAllBytes(filePrefix + ".cv5");
		byte[] VX4 = FileSystemUtils.readAllBytes(filePrefix + ".vx4");
		byte[] VR4 = FileSystemUtils.readAllBytes(filePrefix + ".vr4");

		miniTiles = new Bitmap[VR4.length / 64];
		int[] tmpBuf = new int[64];

		for (int id = 0; id < miniTiles.length; id++) {
			int offset = id * 64;

			for (int i = 0; i < 64; i++)
				tmpBuf[i] = palette[VR4[offset + i] & 0xFF];

			miniTiles[id] = Bitmap.createBitmap(tmpBuf, 8, 8, Config.RGB_565);
		}
		
		VX4Indexes = new int[VX4.length/2];
		for(int i = 0; i< VX4Indexes.length; i++)
		{
			VX4Indexes[i] = (VX4[i * 2] & 0xFF)
					+ ((VX4[i * 2 + 1] & 0xFF) << 8);
		}
	}

	// ////////////////////////////
	// Drawing with canvas
	// ////////////////////////////

	public static final void draw(int x, int y, int id, Canvas c) {
		int cv5id = (id >> 4);
		// first half-byte is subid
		int cv5SubId = (id & 0x000F);

		int vx4Id = (CV5[cv5id * 52 + 20 + cv5SubId * 2] & 0xFF)
				+ ((CV5[cv5id * 52 + 20 + cv5SubId * 2 + 1] & 0xFF) << 8);

		Log.d("Draw tile", "Tile vx4id:" + vx4Id);
		drawMegaTile(x, y, vx4Id, c);
	}

	public static final void drawMegaTile(int x, int y, int vx4Id, Canvas c) {
		int index = vx4Id*16;
		
		drawTile(x, 	 y, VX4Indexes[index++], c);
		drawTile(x + 8,  y, VX4Indexes[index++], c);
		drawTile(x + 16, y, VX4Indexes[index++], c);
		drawTile(x + 24, y, VX4Indexes[index++], c);
		
		drawTile(x, 	 y + 8, VX4Indexes[index++], c);
		drawTile(x + 8,  y + 8, VX4Indexes[index++], c);
		drawTile(x + 16, y + 8, VX4Indexes[index++], c);
		drawTile(x + 24, y + 8, VX4Indexes[index++], c);
		
		drawTile(x, 	 y + 16, VX4Indexes[index++], c);
		drawTile(x + 8,  y + 16, VX4Indexes[index++], c);
		drawTile(x + 16, y + 16, VX4Indexes[index++], c);
		drawTile(x + 24, y + 16, VX4Indexes[index++], c);
		
		
		drawTile(x, 	 y + 24, VX4Indexes[index++], c);
		drawTile(x + 8,  y + 24, VX4Indexes[index++], c);
		drawTile(x + 16, y + 24, VX4Indexes[index++], c);
		drawTile(x + 24, y + 24, VX4Indexes[index++], c);
	}

	public static final void drawTile(int x, int y, int id, Canvas c) {
		Paint p = new Paint();
		boolean flipped = (id & 1) == 1;
		id = id >> 1;
		Matrix transform = new Matrix();
		if (!flipped)
		{
			transform.postTranslate(x, y);
		}
		else
		{
			transform.preScale(-1, 1);
			transform.postTranslate(x+8, y);
		}
		c.drawBitmap(miniTiles[id], transform, p);
	}
}
