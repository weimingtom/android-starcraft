package hotheart.starcraft.graphics.render.opengl;

import hotheart.starcraft.graphics.StarcraftPalette;
import hotheart.starcraft.map.TileLib;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.opengl.GLUtils;

public class MapRender {

	static final int TILELIB_STRIDE = 256;
	static final int TILELIB_HEIGHT = 128;

	public IntBuffer mVertexBuffer;
	public FloatBuffer mTexBuffer;
	public ByteBuffer mIndexBuffer;
	public int texture;
	
	final float texDelta = 8f / 256f;

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
				GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
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

		int w = 128;
		int h = 128;
		Bitmap img = Bitmap.createBitmap(w * 8, w * 8, Config.RGB_565);

		int[] pixels = new int[8 * 8];

		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++) {

				int ofs = (j + i * h) * 64;
				if (ofs >= TileLib.VR4.length)
					break;

				for (int x = 0; x < 8; x++)
					for (int y = 0; y < 8; y++) {
						pixels[x + y * 8] = TileLib.palette[TileLib.VR4[ofs + x
								+ y * 8] & 0xFF];
					}

				img.setPixels(pixels, 0, 8, j * 8, i * 8, 8, 8);
			}

		return img;
	}

	public void testDraw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glVertexPointer(2, GL10.GL_FIXED, 0, mVertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexBuffer);

		gl.glActiveTexture(GL10.GL_TEXTURE0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);

		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glPushMatrix();
		gl.glTranslatef(0, texDelta*10, 0);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPushMatrix();
		for (int i = 0; i < 480 / 8; i++)
		{
			for (int j = 0; j < 320 / 8; j++) {
		
				gl.glMatrixMode(GL10.GL_TEXTURE);
				gl.glTranslatef(texDelta, 0, 0);
				gl.glMatrixMode(GL10.GL_MODELVIEW);
				
				gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4,
						GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
				gl.glTranslatex(0, 8, 0);
			}
			gl.glTranslatex(8, -320, 0);
		}
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glPopMatrix();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPopMatrix();
	}
}
