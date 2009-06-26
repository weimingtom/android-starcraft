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
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.opengl.GLUtils;

public class MapRender {

	static final int TILELIB_STRIDE = 256;
	static final int TILELIB_HEIGHT = 256;
	static final float texDelta = 1.0f / 256f;

	class TileRender {

		// private static final int ROWS = 40;
		// private static final int COLS = 60;
		// public static final int COUNT = ROWS * COLS * 6;
		public IntBuffer vertexBuffer;
		public FloatBuffer texBuffer;
		public ShortBuffer indexBuffer;

		public int rowCount = 0;
		public int colCount = 0;

		public int xPos = 0;
		public int yPos = 0;

		public int width = 0;
		public int height = 0;

		public int getIndexesSize() {
			return rowCount * colCount * 6;
		}

		public TileRender(int[] indexes, int rows, int cols, int px, int py) {
			rowCount = rows;
			colCount = cols;
			xPos = px * 32;
			yPos = py * 32;
			width = colCount * 32;
			height = rowCount * 32;

			int[] coords = new int[rowCount * colCount * 8];
			short[] strip = new short[rowCount * colCount * 6];
			float[] texCoords = new float[rowCount * colCount * 8];

			for (int x = 0; x < colCount; x++)
				for (int y = 0; y < rowCount; y++) {

					int quadId = x + y * colCount;

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

	TileRender[][] mapRenders;

	final static int HOR_STEP = 8;
	final static int VER_STEP = 10;

	public int offsetX = 100;
	public int offsetY = 100;

	int count_x = 0;
	int count_y = 0;

	public MapRender(GL10 gl) {

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

		count_x = GameContext.map.width / HOR_STEP;
		if (GameContext.map.width % HOR_STEP != 0)
			count_x++;

		count_y = GameContext.map.width / VER_STEP;
		if (GameContext.map.height % VER_STEP != 0)
			count_y++;

		mapRenders = new TileRender[count_x][count_y];
		for (int i = 0; i < count_x; i++)
			for (int j = 0; j < count_y; j++) {

				int x1 = i * HOR_STEP;
				int y1 = j * VER_STEP;

				int x2 = Math
						.min((i + 1) * HOR_STEP, GameContext.map.width - 1);
				int y2 = Math.min((j + 1) * VER_STEP,
						GameContext.map.height - 1);

				mapRenders[i][j] = createRender(x1, y1, x2, y2);
			}
		rend = createRender(15, 10, 30, 20);
	}

	TileRender createRender(int x1, int y1, int x2, int y2) {
		int rowCount = y2 - y1 + 1;
		int colCount = x2 - x1 + 1;

		int[] indexes = new int[rowCount * colCount * 16];

		for (int i = x1; i <= x2; i++)
			for (int j = y1; j <= y2; j++) {

				int[] tiles = TileLib.getTiles(GameContext.map.mapTiles[i + j
						* GameContext.map.width]);

				for (int x = 0; x < 4; x++)
					for (int y = 0; y < 4; y++) {
						indexes[(i - x1) * 4 + x + ((j - y1) * 4 + y)
								* colCount * 4] = tiles[x + y * 4];
					}
			}

		return new TileRender(indexes, rowCount * 4, colCount * 4, x1, y1);
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
		Bitmap img = Bitmap.createBitmap(2048, 2048, Config.RGB_565);

		int[] pixels_8x8 = new int[8 * 8];

		for (int i = 0; i < TILELIB_STRIDE; i++)
			for (int j = 0; j < TILELIB_HEIGHT; j++) {

				int ofs = (i + j * TILELIB_STRIDE) * 64;
				if (ofs >= TileLib.VR4.length)
					break;

				for (int x = 0; x < 8; x++)
					for (int y = 0; y < 8; y++) {

						int index = ofs + (x + y * 8);
						pixels_8x8[x + y * 8] = TileLib.palette[TileLib.VR4[index] & 0xFF];
					}

				// image_8x8.setPixels(pixels_8x8, 0, 8, 0, 0, 8, 8);

				// Bitmap image_4x4 = Bitmap.createScaledBitmap(image_8x8, 4, 4,
				// true);
				// image_4x4.getPixels(pixels_4x4, 0, 4, 0, 0, 4, 4);
				// image_4x4.recycle();

				img.setPixels(pixels_8x8, 0, 8, i * 8, j * 8, 8, 8);
			}
		Bitmap res = Bitmap.createScaledBitmap(img, 1024, 1024, true);
		img.recycle();
		return res;
	}

	public void testDraw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glActiveTexture(GL10.GL_TEXTURE0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);

		Rect screenRect = new Rect(offsetX, offsetY, offsetX + 480,
				offsetY + 320);
		for (int i = 0; i < count_x; i++)
			for (int j = 0; j < count_y; j++) {
				TileRender tile = mapRenders[i][j];

				Rect tileRect = new Rect(tile.xPos, tile.yPos, tile.xPos
						+ tile.width, tile.yPos + tile.height);

				if (tileRect.intersect(screenRect))
					draw(tile, gl);

			}

	}

	void draw(TileRender tile, GL10 gl) {
		gl.glPushMatrix();

		gl.glVertexPointer(2, GL10.GL_FIXED, 0, tile.vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tile.texBuffer);

		gl.glTranslatex(tile.xPos - offsetX, tile.yPos - offsetY, 0);

		gl.glDrawElements(GL10.GL_TRIANGLES, rend.getIndexesSize(),
				GL10.GL_UNSIGNED_SHORT, tile.indexBuffer);

		gl.glPopMatrix();
	}
}
