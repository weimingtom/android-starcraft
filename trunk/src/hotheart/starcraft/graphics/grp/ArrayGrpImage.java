package hotheart.starcraft.graphics.grp;

import hotheart.starcraft.configure.BuildParameters;
import hotheart.starcraft.graphics.StarcraftPalette;
import hotheart.starcraft.units.ObjectPool;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class ArrayGrpImage extends AbstractGrpRender {

	@Override
	public void draw(Canvas c, int frameId, int function, int remapping,
			int teamColor) {
		this.selectedFrame = frameId;
		int[] pal = StarcraftPalette.getImagePalette(function, remapping,
				teamColor);
		this.draw(c, pal);
	}

	public static int[] buffer = new int[512 * 512];

	byte[] image;

	public int selectedFrame = 0;
	public int count;

	public int id = 0;

	private int[] w, h, dataOffset, xOffset, yOffset;

	public ArrayGrpImage(byte[] _image, int grpid) {
		this.id = grpid;
		image = _image;
		count = (image[0] & 0xFF) + ((image[1] & 0xFF) << 8);
		width = (image[2] & 0xFF) + ((image[3] & 0xFF) << 8);
		height = (image[4] & 0xFF) + ((image[5] & 0xFF) << 8);
		w = new int[count];
		h = new int[count];
		dataOffset = new int[count];
		xOffset = new int[count];
		yOffset = new int[count];

		for (int i = 0; i < count; i++) {
			int frameOffset = i * 8;

			w[i] = image[8 + frameOffset] & 0xFF;
			h[i] = image[9 + frameOffset] & 0xFF;

			dataOffset[i] = (image[10 + frameOffset] & 0xFF)
					+ ((image[11 + frameOffset] & 0xFF) << 8)
					+ ((image[12 + frameOffset] & 0xFF) << 16)
					+ ((image[13 + frameOffset] & 0xFF) << 24);

			xOffset[i] = image[6 + frameOffset] & 0xFF;
			yOffset[i] = image[7 + frameOffset] & 0xFF;
		}
	}

	public final void draw(Canvas c, int[] palette) {

		if (dataOffset.length <= selectedFrame)
			return; // Wrong data!

		c.save();
		try {

			Matrix matr = c.getMatrix();
			matr.preTranslate(xOffset[selectedFrame], yOffset[selectedFrame]);
			c.setMatrix(matr);

			draw(dataOffset[selectedFrame], w[selectedFrame], h[selectedFrame],
					c, palette);

		} catch (Exception e) {
			if (BuildParameters.DEBUG_GRP_RENDER_ERROR) {

				System.err
						.print("GRPId: " + OldGrpImage.getFileName(id) + "\n");
				System.err.print("Frame ID:" + Integer.toString(selectedFrame)
						+ " of " + dataOffset.length + "\n");
				e.printStackTrace();
			}
		}

		c.restore();
	}

	private final void draw(int offset, int w, int h, Canvas c, int[] palette) {
		ObjectPool.drawCount++;

		Paint p = new Paint();

		int index;
		int j;
		for (int i = 0; i < h; i++) {
			int curX = 0;
			int bufPos = offset + (int) (image[offset + i * 2] & 0xFF)
					+ ((image[offset + i * 2 + 1] & 0xFF) << 8);

			while (curX < w) {
				int a = image[bufPos++] & 0xFF;

				int bufferOffset = i * w + curX;
				if (a >= 0x80) {
					a -= 0x80;

					curX += a;
					for (j = 0; j < a; j++) {
						buffer[bufferOffset++] = 0;

					}
				} else if (a >= 0x40) {
					a -= 0x40;
					index = image[bufPos++] & 0xFF;
					int col = palette[index];
					curX += a;
					for (j = 0; j < a; j++)
						buffer[bufferOffset++] = col;
				} else {
					curX += a;

					for (j = 0; j < a; j++) {
						index = image[bufPos++] & 0xFF;

						buffer[bufferOffset++] = palette[index];
					}
				}
			}
		}

		c.drawBitmap(buffer, 0, w, 0, 0, w, h, true, p);
	}

}
