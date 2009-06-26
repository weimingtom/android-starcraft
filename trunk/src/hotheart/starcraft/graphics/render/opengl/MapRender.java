package hotheart.starcraft.graphics.render.opengl;

import hotheart.starcraft.core.GameContext;
import hotheart.starcraft.graphics.StarcraftPalette;
import hotheart.starcraft.map.TileLib;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.opengl.GLUtils;

public class MapRender {

	static final int TILELIB_STRIDE = 256;
	static final int TILELIB_HEIGHT = 256;
	static final float texDelta = 1.0f / 256f;

	class TileRender {

		public static final int ROWS = 40;
		public static final int COLS = 60;
		public static final int COUNT = ROWS * COLS * 6;
		public IntBuffer vertexBuffer;
		public FloatBuffer texBuffer;
		public ShortBuffer indexBuffer;

		public TileRender(int[] indexes) {

			int[] coords = new int[ROWS * COLS * 8];
			short[] strip = new short[ROWS * COLS * 6];
			float[] texCoords = new float[ROWS * COLS * 8];

			for (int x = 0; x < COLS; x++)
				for (int y = 0; y < ROWS; y++) {

					int quadId = x + y * COLS;

					float texX = getTextureX(indexes[quadId] >> 1);
					float texY = getTextureY(indexes[quadId] >> 1);

					int coordsId = quadId * 4;
					int stripId = quadId * 6;

					coords[coordsId * 2 + 0] = 8 * x;
					coords[coordsId * 2 + 1] = 8 * y;

					coords[coordsId * 2 + 2] = 8 * (x + 1);
					coords[coordsId * 2 + 3] = 8 * y;

					coords[coordsId * 2 + 4] = 8 * (x + 1);
					coords[coordsId * 2 + 5] = 8 * (y + 1);

					coords[coordsId * 2 + 6] = 8 * x;
					coords[coordsId * 2 + 7] = 8 * (y + 1);

					if ((indexes[quadId] & 1) == 1) {

						// MIRRORED

						texCoords[coordsId * 2 + 0] = texX + texDelta;
						texCoords[coordsId * 2 + 1] = texY;

						texCoords[coordsId * 2 + 2] = texX;
						texCoords[coordsId * 2 + 3] = texY;

						texCoords[coordsId * 2 + 4] = texX;
						texCoords[coordsId * 2 + 5] = texY + texDelta;

						texCoords[coordsId * 2 + 6] = texX + texDelta;
						texCoords[coordsId * 2 + 7] = texY + texDelta;

					} else {

						texCoords[coordsId * 2 + 0] = texX;
						texCoords[coordsId * 2 + 1] = texY;

						texCoords[coordsId * 2 + 2] = texX + texDelta;
						texCoords[coordsId * 2 + 3] = texY;

						texCoords[coordsId * 2 + 4] = texX + texDelta;
						texCoords[coordsId * 2 + 5] = texY + texDelta;

						texCoords[coordsId * 2 + 6] = texX;
						texCoords[coordsId * 2 + 7] = texY + texDelta;
					}

					strip[stripId + 0] = (short) (coordsId + 0);
					strip[stripId + 1] = (short) (coordsId + 1);
					strip[stripId + 2] = (short) (coordsId + 3);

					strip[stripId + 3] = (short) (coordsId + 1);
					strip[stripId + 4] = (short) (coordsId + 2);
					strip[stripId + 5] = (short) (coordsId + 3);
				}

			// byte[] vertex_strip = { 1, 0, 2, 3 };
			// float[] texCoords = { texDelta, texDelta, texDelta, 0, 0, 0, 0,
			// texDelta };
			//
			ByteBuffer vbb = ByteBuffer.allocateDirect(coords.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			vertexBuffer = vbb.asIntBuffer();
			vertexBuffer.put(coords);
			vertexBuffer.position(0);

			ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
			tbb.order(ByteOrder.nativeOrder());
			texBuffer = tbb.asFloatBuffer();
			texBuffer.put(texCoords);
			texBuffer.position(0);

			ByteBuffer ibb = ByteBuffer.allocateDirect(strip.length * 4);
			ibb.order(ByteOrder.nativeOrder());

			indexBuffer = ibb.asShortBuffer();
			indexBuffer.put(strip);
			indexBuffer.position(0);

		}

	}

	static float getTextureX(int id) {
		return (id % TILELIB_STRIDE) * texDelta;
	}

	static float getTextureY(int id) {
		return (id / TILELIB_STRIDE) * texDelta;
	}

	public IntBuffer mVertexBuffer;
	public FloatBuffer mTexBuffer;
	public ByteBuffer mIndexBuffer;
	public int texture;

	TileRender rend;

	public MapRender(GL10 gl) {

		// {
		// Bitmap image = Bitmap.createBitmap(32, 32, Config.RGB_565);
		// Canvas c = new Canvas(image);
		//
		// int[] tiles = TileLib.getTiles(180);
		//
		// for (int x = 0; x < 4; x++)
		// for (int y = 0; y < 4; y++) {
		// TileLib.drawTile(x * 8, y * 8, tiles[x + y * 4], c);
		// }
		//
		// try {
		// Bitmap.createScaledBitmap(image, 16, 16, true).compress(
		// CompressFormat.PNG, 1,
		// new FileOutputStream("/sdcard/test.png"));
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

		createBuffers();

		int[] ids = new int[1];
		gl.glGenTextures(1, ids, 0);
		texture = ids[0];

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);

		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				GL10.GL_REPLACE);

		Bitmap bitmap = createBitmap();
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();

		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_NEAREST);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_NEAREST);

		int[] indexes = new int[TileRender.ROWS * TileRender.COLS];

		for (int i = 0; i < TileRender.COLS; i += 4)
			for (int j = 0; j < TileRender.ROWS; j += 4) {

				int[] tiles = TileLib.getTiles(GameContext.map.mapTiles[(i / 4)
						+ (j / 4) * GameContext.map.width]);

				for (int x = 0; x < 4; x++)
					for (int y = 0; y < 4; y++) {
						indexes[i + x + (j + y) * TileRender.COLS] = tiles[x
								+ y * 4];
					}
			}
		rend = new TileRender(indexes);
	}

	void createBuffers() {
		int[] coords = { 8, 8, 8, 0, 0, 0, 0, 8 };
		byte[] vertex_strip = { 1, 0, 2, 3 };
		float[] texCoords = { texDelta, texDelta, texDelta, 0, 0, 0, 0,
				texDelta };

		ByteBuffer vbb = ByteBuffer.allocateDirect(coords.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asIntBuffer();
		mVertexBuffer.put(coords);
		mVertexBuffer.position(0);

		ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		mTexBuffer = tbb.asFloatBuffer();
		mTexBuffer.put(texCoords);
		mTexBuffer.position(0);

		mIndexBuffer = ByteBuffer.allocateDirect(vertex_strip.length);
		mIndexBuffer.order(ByteOrder.nativeOrder());
		mIndexBuffer.put(vertex_strip);
		mIndexBuffer.position(0);
	}

	Bitmap createBitmap() {
		int count = TileLib.VR4.length / 64;

		// int[] pixels = new int[TILELIB_STRIDE*8 * TILELIB_HEIGHT*8];

		Bitmap img = Bitmap.createBitmap(1024, 1024, Config.RGB_565);

		int[] pixels = new int[4 * 4];

		for (int i = 0; i < TILELIB_STRIDE; i++)
			for (int j = 0; j < TILELIB_HEIGHT; j++) {

				int ofs = (i + j * TILELIB_STRIDE) * 64;
				if (ofs >= TileLib.VR4.length)
					break;

				for (int x = 0; x < 4; x++)
					for (int y = 0; y < 4; y++) {

						int index = ofs + (x + y * 8) * 2;
						int a11 = TileLib.palette[TileLib.VR4[index] & 0xFF];
						int a21 = TileLib.palette[TileLib.VR4[index + 1] & 0xFF];
						int a12 = TileLib.palette[TileLib.VR4[index + 8] & 0xFF];
						int a22 = TileLib.palette[TileLib.VR4[index + 9] & 0xFF];
						pixels[x + y * 4] = getPixelColor(a11, a12, a21, a22);
					}

				img.setPixels(pixels, 0, 4, i * 4, j * 4, 4, 4);
			}

		return img;
	}

	int getPixelColor(int a, int b, int c, int d) {
		return a;

		// int R = Color.red(a) + Color.red(b) + Color.red(c) + Color.red(d);
		// int G = Color.green(a) + Color.green(b) + Color.green(c)
		// + Color.green(d);
		// int B = Color.blue(a) + Color.blue(b) + Color.blue(c) +
		// Color.blue(d);
		//
		// R /= 4;
		// G /= 4;
		// B /= 4;
		//
		// return Color.argb(255, R, G, B);
	}

	public void testDraw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glVertexPointer(2, GL10.GL_FIXED, 0, rend.vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, rend.texBuffer);

		gl.glActiveTexture(GL10.GL_TEXTURE0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);

		// gl.glPushMatrix();
		//
		// gl.glTranslatex(100, 100, 0);

		// for (int i = 0; i < 10; i++)
		gl.glDrawElements(GL10.GL_TRIANGLES, rend.COUNT,
				GL10.GL_UNSIGNED_SHORT, rend.indexBuffer);

		// gl.glPopMatrix();

		// gl.glMatrixMode(GL10.GL_TEXTURE);
		// gl.glPushMatrix();
		// gl.glTranslatef(0, texDelta * 10, 0);
		// gl.glMatrixMode(GL10.GL_MODELVIEW);
		// gl.glPushMatrix();
		// for (int i = 0; i < 480 / 32; i++) {
		// for (int j = 0; j < 320 / 32; j++) {
		//
		// gl.glMatrixMode(GL10.GL_TEXTURE);
		// gl.glTranslatef(texDelta, 0, 0);
		// gl.glMatrixMode(GL10.GL_MODELVIEW);
		//
		// // gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4,
		// // GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
		//				
		// gl.glDrawElements(GL10.GL_TRIANGLES, rend.COUNT,
		// GL10.GL_UNSIGNED_BYTE, rend.indexBuffer);
		// gl.glTranslatex(0, 32, 0);
		// }
		// gl.glTranslatex(32, -320, 0);
		// }
		// gl.glMatrixMode(GL10.GL_TEXTURE);
		// gl.glPopMatrix();
		// gl.glMatrixMode(GL10.GL_MODELVIEW);
		// gl.glPopMatrix();
	}
}
