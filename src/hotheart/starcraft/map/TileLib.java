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
import android.graphics.Paint;
import android.util.Log;
import hotheart.starcraft.utils.FileSystemUtils;

public class TileLib {
	public static int[] palette;//Palette - generated from WPE file
	public static byte[] CV5;	//Descriptions of sprites
	public static byte[] VX4;	//Mega-tiles(32x32)
	public static byte[] VR4;	//Tiles(8x8)
	
	public static final void init(int type) {
		String filePrefix = "";
		
		//This ids used in scenario.chk
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

		//Loading palette
		
		byte[] pal = FileSystemUtils.readAllBytes(filePrefix + ".wpe");
		palette = new int[256];

		for (int i = 0; i < pal.length / 4; i++) {
			palette[i] = (255 << 24) + (pal[i * 4]) + (pal[i * 4 + 1] << 8)
					+ (pal[i * 4 + 2] << 16);
		}
		
		//Loading tiles files

		CV5 = FileSystemUtils.readAllBytes(filePrefix + ".cv5");
		VX4 = FileSystemUtils.readAllBytes(filePrefix + ".vx4");
		VR4 = FileSystemUtils.readAllBytes(filePrefix + ".vr4");
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
		// Offset in VX4 file
		int vx4Offset = (vx4Id << 5);

		// For each of 16 subtiles
		for (int rowIndex = 0; rowIndex < 4; rowIndex++)
			for (int colIndex = 0; colIndex < 4; colIndex++) {
				// Index in section
				int index = (rowIndex << 2) + colIndex;

				// Calc id of mini-tile(8x8)
				int vr4id = (VX4[vx4Offset + (index << 1)] & 0xFF)
						+ ((VX4[vx4Offset + (index << 1) + 1] & 0xFF) << 8);

				// Draw mini-tile(8x8)
				drawTile(x + (colIndex << 3), y + (rowIndex << 3), vr4id, c,
						stride);
			}
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
	// Drawing with canvas(Deprecated)
	// ////////////////////////////

	public static final void draw(int x, int y, int id, Canvas c) {
		int n1 = (id & 0x000F);
		int n2 = (id & 0x00F0) >> 4;
		int n3 = (id & 0x0F00) >> 8;
		int n4 = (id & 0xF000) >> 16;

		int cv5id = n2 + (n3 << 4) + (n4 << 8);
		int cv5SubId = n1;

		int vx4Id = (CV5[cv5id * 52 + 20 + cv5SubId * 2] & 0xFF)
				+ ((CV5[cv5id * 52 + 20 + cv5SubId * 2 + 1] & 0xFF) << 8);

		Log.d("Draw tile", "Tile vx4id:" + vx4Id);
		drawMegaTile(x, y, vx4Id, c);
	}

	public static final void drawMegaTile(int x, int y, int vx4Id, Canvas c) {
		int vx4Offset = vx4Id * 32;
		Paint p = new Paint();

		for (int rowIndex = 0; rowIndex < 4; rowIndex++)
			for (int colIndex = 0; colIndex < 4; colIndex++) {
				int index = rowIndex * 4 + colIndex;

				int vr4id = (VX4[vx4Offset + index * 2] & 0xFF)
						+ ((VX4[vx4Offset + index * 2 + 1] & 0xFF) << 8);

				drawTile(x + colIndex * 8, y + rowIndex * 8, vr4id, c);
			}

		p.setColor(Color.GREEN);
		c.drawLine(x, y, x + 32, y, p);
		c.drawLine(x + 32, y, x + 32, y + 32, p);
		c.drawLine(x, y, x, y + 32, p);
		c.drawLine(x, y + 32, x + 32, y + 32, p);
	}

	public static final void drawTile(int x, int y, int id, Canvas c) {
		Paint p = new Paint();
		boolean flipped = (id & 1) == 1;
		id = id >> 1;
		int offset = id * 64;

		for (int rowIndex = 0; rowIndex < 8; rowIndex++)
			for (int colIndex = 0; colIndex < 8; colIndex++) {
				p
						.setColor(palette[VR4[offset + rowIndex * 8 + colIndex] & 0xFF]);
				if (flipped)
					c.drawPoint(x + 7 - colIndex, y + rowIndex, p);
				else
					c.drawPoint(x + colIndex, y + rowIndex, p);
			}
	}
}
