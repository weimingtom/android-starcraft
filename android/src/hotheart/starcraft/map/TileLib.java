/*
 * Module for loading and drawing map tiles
 * for Starcraft Android version
 * 
 * by Korshakov Stepan(c)
 * korshakov.stepan@gmail.com
 * 2009 year
 */
package hotheart.starcraft.map;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import hotheart.starcraft.configure.FilePaths;
import hotheart.starcraft.utils.FileSystemUtils;

public class TileLib {

	public static int IS_WALKABLE = 0x01;

	public static final int TILE_SIZE = 32;

	public static int[] palette;// Palette - generated from WPE file
	public static byte[] CV5; // Descriptions of sprites
	public static byte[] VR4; // Tiles(8x8)
	public static int[] tileFlags; // Tiles(8x8)

	public static int[] VX4Indexes; // Mega-tiles(32x32)
	
	private static int[] megaTileColors_HACK = new int[256];
	private static boolean[] megaTileColorsCalced_HACK = new boolean[256];

	// public static Bitmap[] miniTiles; // Tiles(8x8)

	// public static int[][] miniTiles;

	public static int[] getMegaTilesFlags(int id) {
		int cv5id = (id >> 4);
		// first half-byte is subid
		int cv5SubId = (id & 0x000F);

		int vx4Id = (CV5[cv5id * 52 + 20 + cv5SubId * 2] & 0xFF)
				+ ((CV5[cv5id * 52 + 20 + cv5SubId * 2 + 1] & 0xFF) << 8);

		int index = vx4Id * 16;

		return new int[] { tileFlags[index++], tileFlags[index++],
				tileFlags[index++], tileFlags[index++], tileFlags[index++],
				tileFlags[index++], tileFlags[index++], tileFlags[index++],
				tileFlags[index++], tileFlags[index++], tileFlags[index++],
				tileFlags[index++], tileFlags[index++], tileFlags[index++],
				tileFlags[index++], tileFlags[index++] };
	}

	public static final boolean haveFlagInstalled(int megaTileId, int miniX,
			int miniY, int flag) {

		int[] flags = getMegaTilesFlags(megaTileId);
		return (flags[miniX + miniY * 4] & flag) != 0;
	}

	public static final void init(int type) {

		String filePrefix = "";

		// This ids used in scenario.chk
		switch (type & 0x7) {
		case 0:
			filePrefix = FilePaths.BADLANDS_PREFIX;
			break;
		case 1:
			filePrefix = FilePaths.PLATFORM_PREFIX;
			break;
		case 2:
			filePrefix = FilePaths.INSTALL_PREFIX;
			break;
		case 3:
			filePrefix = FilePaths.ASHWORLD_PREFIX;
			break;
		case 4:
			filePrefix = FilePaths.JUNGLE_PREFIX;
			break;
		case 5:
			filePrefix = FilePaths.DESERT_PREFIX;
			break;
		case 6:
			filePrefix = FilePaths.ICE_PREFIX;
			break;
		case 7:
			filePrefix = FilePaths.TWILIGHT_PREFIX;
			break;
		}

		// Loading palette

		byte[] pal = FileSystemUtils.readAllBytes(filePrefix + ".wpe");
		palette = new int[256];

		for (int i = 0; i < pal.length / 4; i++) {
			palette[i] = (255 << 24) + ((pal[i * 4] & 0xFF) << 16)
					+ ((pal[i * 4 + 1] & 0xFF) << 8) + (pal[i * 4 + 2] & 0xFF);
		}

		// Loading tiles files

		CV5 = FileSystemUtils.readAllBytes(filePrefix + ".cv5");
		VR4 = FileSystemUtils.readAllBytes(filePrefix + ".vr4");

		byte[] VF4 = FileSystemUtils.readAllBytes(filePrefix + ".vf4");
		tileFlags = new int[VF4.length / 2];
		for (int i = 0; i < tileFlags.length; i++)
			tileFlags[i] = (VF4[i * 2] & 0xFF) + ((VF4[i * 2 + 1] & 0xFF) << 8);

		// miniTiles = new Bitmap[VR4.length / 64];
		// int[] tmpBuf = new int[64];

		// for (int id = 0; id < miniTiles.length; id++) {
		//			
		// int offset = id * 64;
		//
		// for (int i = 0; i < 64; i++)
		// miniTiles[id][i] = palette[VR4[offset + i] & 0xFF];
		//
		// //miniTiles[id] = Bitmap.createBitmap(tmpBuf, 8, 8, Config.RGB_565);
		// }

		byte[] VX4 = FileSystemUtils.readAllBytes(filePrefix + ".vx4");
		VX4Indexes = new int[VX4.length / 2];
		for (int i = 0; i < VX4Indexes.length; i++) {
			VX4Indexes[i] = (VX4[i * 2] & 0xFF)
					+ ((VX4[i * 2 + 1] & 0xFF) << 8);
		}
		
		for(int i = 0; i < megaTileColorsCalced_HACK.length; i++)
			megaTileColorsCalced_HACK[i] = false;
	}

	// it's works
	public static int[] getTiles(int id) {
		int cv5id = (id >> 4);
		// first half-byte is subid
		int cv5SubId = (id & 0x000F);

		int vx4Id = (CV5[cv5id * 52 + 20 + cv5SubId * 2] & 0xFF)
				+ ((CV5[cv5id * 52 + 20 + cv5SubId * 2 + 1] & 0xFF) << 8);

		int index = vx4Id * 16;

		return new int[] { VX4Indexes[index++], VX4Indexes[index++],
				VX4Indexes[index++], VX4Indexes[index++], VX4Indexes[index++],
				VX4Indexes[index++], VX4Indexes[index++], VX4Indexes[index++],
				VX4Indexes[index++], VX4Indexes[index++], VX4Indexes[index++],
				VX4Indexes[index++], VX4Indexes[index++], VX4Indexes[index++],
				VX4Indexes[index++], VX4Indexes[index++] };
	}
	
	public static final int getMegaTileColor_HACK(int id) {
		// Last 3 half-bytes is id
		int cv5id = (id >> 4);
		// first half-byte is subid
		int cv5SubId = (id & 0x000F);
		//
		// // Calc index in VX4 file
		// int vx4Id = (CV5[cv5id * 52 + 20 + cv5SubId * 2] & 0xFF)
		// + ((CV5[cv5id * 52 + 20 + cv5SubId * 2 + 1] & 0xFF) << 8);
		//
		// return getTilesColor(vx4Id);
		
		int cid = CV5[cv5id * 52] & 0xFF;
		
		if (!megaTileColorsCalced_HACK[cid])
		{
			megaTileColors_HACK[cid] = getMegaTileColor(id);
			megaTileColorsCalced_HACK[cid] = true;
		}
		return megaTileColors_HACK[cid];
		//return palette[CV5[cv5id * 52] & 0xFF];
	}

	public static final int getMegaTileColor(int id) {
		// Last 3 half-bytes is id
		int cv5id = (id >> 4);
		// first half-byte is subid
		int cv5SubId = (id & 0x000F);

		// Calc index in VX4 file
		int vx4Id = (CV5[cv5id * 52 + 20 + cv5SubId * 2] & 0xFF)
				+ ((CV5[cv5id * 52 + 20 + cv5SubId * 2 + 1] & 0xFF) << 8);

		return getTilesColor(vx4Id);
	}

	private static final int getTilesColor(int vx4Id) {

		int R = 0;
		int G = 0;
		int B = 0;

		int index = vx4Id * 16;

		for (int i = 0; i < 16; i++) {
			int col = getMiniTilesColor(VX4Indexes[index++]);

			R += (col >> 16) & 0xFF;
			G += (col >> 8) & 0xFF;
			B += col & 0xFF;
		}

		R = R >> 4;
		G = G >> 4;
		B = B >> 4;

		return Color.rgb(R, G, B);
	}

	private static final int getMiniTilesColor(int miniTileId) {

		int R = 0;
		int G = 0;
		int B = 0;

		// Readl id by shift the flipped flag
		miniTileId = miniTileId >> 1;

		// Offset in file
		int offset = miniTileId << 6;

		// For each of 64 pixel
		for (int rowIndex = 0; rowIndex < 8; rowIndex++)
			for (int colIndex = 0; colIndex < 8; colIndex++) {
				// Offset in file for pixel from first
				int pixelOffset = rowIndex * 8 + colIndex;

				int col = palette[VR4[offset + pixelOffset] & 0xFF];

				R += (col >> 16) & 0xFF;
				G += (col >> 8) & 0xFF;
				B += col & 0xFF;
			}

		R = R >> 6;
		G = G >> 6;
		B = B >> 6;
		return Color.rgb(R, G, B);
	}

	// ////////////////////////////////////
	// Drawing with int buffers
	// ////////////////////////////////////

	public static final void draw(int x, int y, int id, int[] b, int stride) {
		// Last 3 half-bytes is id
		int cv5id = (id >> 4);
		// first half-byte is subid
		int cv5SubId = (id & 0x000F);

		// Calc index in VX4 file
		int vx4Id = (CV5[cv5id * 52 + 20 + cv5SubId * 2] & 0xFF)
				+ ((CV5[cv5id * 52 + 20 + cv5SubId * 2 + 1] & 0xFF) << 8);

		// Draw mega-tile(32x32)
		drawMegaTile(x, y, vx4Id, b, stride);
	}

	public static final void drawMegaTile(int x, int y, int vx4Id, int[] c,
			int stride) {

		int index = vx4Id * 16;

		drawTile(x, y, VX4Indexes[index++], c, stride);
		drawTile(x + 8, y, VX4Indexes[index++], c, stride);
		drawTile(x + 16, y, VX4Indexes[index++], c, stride);
		drawTile(x + 24, y, VX4Indexes[index++], c, stride);

		drawTile(x, y + 8, VX4Indexes[index++], c, stride);
		drawTile(x + 8, y + 8, VX4Indexes[index++], c, stride);
		drawTile(x + 16, y + 8, VX4Indexes[index++], c, stride);
		drawTile(x + 24, y + 8, VX4Indexes[index++], c, stride);

		drawTile(x, y + 16, VX4Indexes[index++], c, stride);
		drawTile(x + 8, y + 16, VX4Indexes[index++], c, stride);
		drawTile(x + 16, y + 16, VX4Indexes[index++], c, stride);
		drawTile(x + 24, y + 16, VX4Indexes[index++], c, stride);

		drawTile(x, y + 24, VX4Indexes[index++], c, stride);
		drawTile(x + 8, y + 24, VX4Indexes[index++], c, stride);
		drawTile(x + 16, y + 24, VX4Indexes[index++], c, stride);
		drawTile(x + 24, y + 24, VX4Indexes[index++], c, stride);
	}

	public static final void drawTile(int x, int y, int id, int[] c, int stride) {
		// First bit is flag is flipped horizontal
		boolean flipped = (id & 1) == 1;
		// Readl id by shift the flipped flag
		id = id >> 1;

		// Offset in file
		int offset = id << 6;

		// For each of 64 pixel
		for (int rowIndex = 0; rowIndex < 8; rowIndex++)
			for (int colIndex = 0; colIndex < 8; colIndex++) {
				// Offset in file for pixel from first
				int pixelOffset = rowIndex * 8 + colIndex;

				// Offset for first pixel in row
				int destBufferOffset = (y + rowIndex) * stride;

				if (flipped)
					c[x + 7 - colIndex + destBufferOffset] = palette[VR4[offset
							+ pixelOffset] & 0xFF];
				else
					c[x + colIndex + destBufferOffset] = palette[VR4[offset
							+ pixelOffset] & 0xFF];
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

		drawMegaTile(x, y, vx4Id, c);
	}

	public static final void drawMegaTile(int x, int y, int vx4Id, Canvas c) {
		int index = vx4Id * 16;

		drawTile(x, y, VX4Indexes[index++], c);
		drawTile(x + 8, y, VX4Indexes[index++], c);
		drawTile(x + 16, y, VX4Indexes[index++], c);
		drawTile(x + 24, y, VX4Indexes[index++], c);

		drawTile(x, y + 8, VX4Indexes[index++], c);
		drawTile(x + 8, y + 8, VX4Indexes[index++], c);
		drawTile(x + 16, y + 8, VX4Indexes[index++], c);
		drawTile(x + 24, y + 8, VX4Indexes[index++], c);

		drawTile(x, y + 16, VX4Indexes[index++], c);
		drawTile(x + 8, y + 16, VX4Indexes[index++], c);
		drawTile(x + 16, y + 16, VX4Indexes[index++], c);
		drawTile(x + 24, y + 16, VX4Indexes[index++], c);

		drawTile(x, y + 24, VX4Indexes[index++], c);
		drawTile(x + 8, y + 24, VX4Indexes[index++], c);
		drawTile(x + 16, y + 24, VX4Indexes[index++], c);
		drawTile(x + 24, y + 24, VX4Indexes[index++], c);
	}

	private static final int[] tmpBuf = new int[64];

	public static final void drawTile(int x, int y, int id, Canvas c) {
		Paint p = new Paint();
		// boolean flipped = (id & 1) == 1;
		boolean flipped = false;
		id = id >> 1;
		Matrix transform = new Matrix();
		if (!flipped) {
			transform.postTranslate(x, y);
		} else {
			transform.preScale(-1, 1);
			transform.postTranslate(x + 8, y);
		}
		int offset = id * 64;
		for (int i = 0; i < 64; i++)
			tmpBuf[i] = palette[VR4[offset + i] & 0xFF];
		transform.preConcat(c.getMatrix());
		c.save();
		c.setMatrix(transform);
		c.drawBitmap(tmpBuf, 0, 8, 0, 0, 8, 8, false, p);
		c.restore();

		// miniTiles[id] = Bitmap.createBitmap(tmpBuf, 8, 8,
		// Config.RGB_565);

		// c.drawBitmap(miniTiles[id], transform, p);
	}
}
