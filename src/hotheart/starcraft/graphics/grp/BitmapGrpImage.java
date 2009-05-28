package hotheart.starcraft.graphics.grp;

import hotheart.starcraft.configure.BuildParameters;
import hotheart.starcraft.graphics.StarcraftPalette;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Bitmap.Config;

public class BitmapGrpImage extends AbstractGrpRender {

	@Override
	public void draw(Canvas c, int frameId, int function, int remapping,
			int teamColor) {

		c.save();
		try {

			Matrix matr = c.getMatrix();
			matr.preTranslate(xOffset[frameId], yOffset[frameId]);
			c.setMatrix(matr);

			// PorterDuffColorFilter filter = new
			// PorterDuffColorFilter(Color.RED,
			// android.graphics.PorterDuff.Mode.DST_ATOP);
			// p.setColorFilter(filter);

			// p.setXfermode(new AvoidXfermode(255, 255,
			// AvoidXfermode.Mode.AVOID));
			// paint.setXfermode(null);

			// c.setDrawFilter(filter);

			//			
			// Drawable dr = new BitmapDrawable(bitmaps[selectedFrame]);
			// dr.setColorFilter(filter);
			// dr.setBounds(0,0,bitmaps[selectedFrame].getWidth(),bitmaps[selectedFrame].getHeight());
			// dr.draw(c);

			// int color = Color.BLUE;
			// p.setColor(color);
			// //p.setColorFilter(filter)
			//		
			// ColorMatrix cm = new ColorMatrix();
			// cm.set(new float[] {
			// 0, 0, 0, 0, Color.red(color),
			// 0, 0, 0, 0, Color.green(color),
			// 0, 0, 0, 0, Color.blue(color),
			// 0, 0, 0, 1000, 0 });
			// p.setColorFilter(new ColorMatrixColorFilter(cm));
			// c.drawBitmap(bitmaps[frameId], 0, 0, p);
			// p.setColorFilter(null);
			c.drawBitmap(bitmaps[frameId], 0, 0, new Paint());

		} catch (Exception e) {
			if (BuildParameters.DEBUG_GRP_RENDER_ERROR) {

				System.err.print("GRPId: " + OldGrpImage.getFileName(grpId) + "\n");
				System.err.print("Frame ID:" + Integer.toString(frameId)
						+ " of " + dataOffset.length + "\n");
				e.printStackTrace();
			}
		}

		c.restore();

		// p.setXfermode(new AvoidXfermode(0, 0, AvoidXfermode.Mode.TARGET));

		// c.drawRect(0, 0, w, h, p);

		// p.setXfermode(null);
	}

	public int count;
	private int[] w, h, dataOffset, xOffset, yOffset;
	Bitmap[] bitmaps;

	public BitmapGrpImage(byte[] image, int _grpId) {
		grpId = _grpId;
		width = (image[2] & 0xFF) + ((image[3] & 0xFF) << 8);
		height = (image[4] & 0xFF) + ((image[5] & 0xFF) << 8);

		count = (image[0] & 0xFF) + ((image[1] & 0xFF) << 8);

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

		makeCache(image, StarcraftPalette.blendedPalette);
	}

	public void makeCache(byte[] image, int[] palette) {
		if (bitmaps != null)
			return;
		bitmaps = new Bitmap[count];
		for (int i = 0; i < count; i++) {
			int[] pixels = createBitmap(image, i, w[i], h[i], dataOffset[i],
					palette);
			bitmaps[i] = Bitmap.createBitmap(pixels, w[i], h[i],
					Config.ARGB_4444);
		}
		image = null;
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
