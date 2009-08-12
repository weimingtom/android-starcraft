package hotheart.starcraft.files;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class GrpFile {
	public int width = 0;
	public int height = 0;
	public int count;
	public int[] widths, heights, xOffset, yOffset;

	public int[] dataOffset;

	public byte[] image;

	public GrpFile(byte[] _image) {
		image = _image;
		width = (image[2] & 0xFF) + ((image[3] & 0xFF) << 8);
		height = (image[4] & 0xFF) + ((image[5] & 0xFF) << 8);

		count = (image[0] & 0xFF) + ((image[1] & 0xFF) << 8);

		widths = new int[count];
		heights = new int[count];
		dataOffset = new int[count];
		xOffset = new int[count];
		yOffset = new int[count];

		for (int i = 0; i < count; i++) {
			int frameOffset = i * 8;

			widths[i] = image[8 + frameOffset] & 0xFF;
			heights[i] = image[9 + frameOffset] & 0xFF;

			dataOffset[i] = (image[10 + frameOffset] & 0xFF)
					+ ((image[11 + frameOffset] & 0xFF) << 8)
					+ ((image[12 + frameOffset] & 0xFF) << 16)
					+ ((image[13 + frameOffset] & 0xFF) << 24);

			xOffset[i] = image[6 + frameOffset] & 0xFF;
			yOffset[i] = image[7 + frameOffset] & 0xFF;
		}
	}

	public Bitmap createBitmap(int i, int[] palette) {
		int[] pixels = createBitmap(image, i, widths[i], heights[i],
				dataOffset[i], palette);
		Bitmap res = Bitmap.createBitmap(pixels, widths[i], heights[i],
				Config.ARGB_4444);

		return res;
	}

	private int[] createBitmap(byte[] image, int id, int xW, int yH,
			int dataOffset, int[] palette) {
		int[] res = new int[xW * yH];
		int index;
		int j;
		for (int i = 0; i < yH; i++) {
			int curX = 0;
			int bufPos = dataOffset + (int) (image[dataOffset + i * 2] & 0xFF)
					+ ((image[dataOffset + i * 2 + 1] & 0xFF) << 8);

			while (curX < xW) {
				int a = image[bufPos++] & 0xFF;

				int bufferOffset = i * xW + curX;
				if (a >= 0x80) {
					a -= 0x80;

					curX += a;
					for (j = 0; j < a; j++) {
						res[bufferOffset++] = 0;

					}
				} else if (a >= 0x40) {
					a -= 0x40;
					index = image[bufPos++] & 0xFF;
					int col = palette[index];
					curX += a;
					for (j = 0; j < a; j++)
						res[bufferOffset++] = col;
				} else {
					curX += a;

					for (j = 0; j < a; j++) {
						index = image[bufPos++] & 0xFF;

						res[bufferOffset++] = palette[index];
					}
				}
			}
		}

		return res;
	}
}
