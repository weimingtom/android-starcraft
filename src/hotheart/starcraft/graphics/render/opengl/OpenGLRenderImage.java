package hotheart.starcraft.graphics.render.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLUtils;

import hotheart.starcraft.files.GrpFile;
import hotheart.starcraft.graphics.StarcraftPalette;
import hotheart.starcraft.graphics.render.RenderImage;

public class OpenGLRenderImage extends RenderImage {

	class Frame {
		public IntBuffer mVertexBuffer;
		public FloatBuffer mTexBuffer;
		public FloatBuffer mMirTexBuffer;
		public int texture;

		public int width;
		public int height;

		public Frame(int i, GL10 gl) {

			// ======================================
			// Creating textures
			// ======================================
			int[] ids = new int[1];
			gl.glGenTextures(1, ids, 0);
			texture = ids[0];

			gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);

			gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
					GL10.GL_REPLACE);

			Bitmap bitmap = image.createBitmap(i,
					StarcraftPalette.greenPalette);

			calcSize(bitmap.getWidth(), bitmap.getHeight());
			genTexture(bitmap);

			bitmap.recycle();

			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR);

			// ======================================
			// Creating coord buffers
			// ======================================

			int[] coords = { image.widths[i], image.heights[i],
					image.widths[i], 0, 0, 0, 0, image.heights[i] };

			float[] texCoords = { (float) image.widths[i] / (float) width,
					(float) image.heights[i] / (float) height,
					(float) image.widths[i] / (float) width, 0, 0, 0, 0,
					(float) image.heights[i] / (float) height };

			float[] mirTexCoords = { 0,
					(float) image.heights[i] / (float) height, 0, 0,
					(float) image.widths[i] / (float) width, 0,
					(float) image.widths[i] / (float) width,
					(float) image.heights[i] / (float) height };

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

			ByteBuffer mtbb = ByteBuffer
					.allocateDirect(mirTexCoords.length * 4);
			mtbb.order(ByteOrder.nativeOrder());
			mMirTexBuffer = mtbb.asFloatBuffer();
			mMirTexBuffer.put(mirTexCoords);
			mMirTexBuffer.position(0);
		}

		void calcSize(int w, int h) {
			width = 1;
			height = 1;

			for (int i = 0; i < 10; i++) {
				if ((w >> i) == 0) {
					width = (int) Math.pow(2, i);
					break;
				}
			}

			for (int i = 0; i < 10; i++) {
				if ((h >> i) == 0) {
					height = (int) Math.pow(2, i);
					break;
				}
			}

		}

		void genTexture(Bitmap bitmap) {

			Bitmap res = Bitmap.createBitmap(width, height, bitmap.getConfig());
			Canvas c = new Canvas(res);
			c.drawBitmap(bitmap, 0, 0, new Paint());

			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, res, 0);

			res.recycle();
		}
	}

	static ByteBuffer mIndexBuffer;

	public static void initData(GL10 gl) {
		byte[] vertex_strip = { 1, 0, 2, 3 };

		mIndexBuffer = ByteBuffer.allocateDirect(vertex_strip.length);
		mIndexBuffer.order(ByteOrder.nativeOrder());
		mIndexBuffer.put(vertex_strip);
		mIndexBuffer.position(0);
	}

	GrpFile image;
	OpenGLRender render;

	int width = 0, height = 0;

	int[] xOffsets = null, yOffsets = null;

	int[] widths = null, heights = null;

	Frame[] frames = null;

	synchronized void init(GL10 gl) {
		if (frames != null)
			return;

		xOffsets = image.xOffset;
		yOffsets = image.yOffset;

		widths = image.widths;
		heights = image.heights;

		width = image.width;
		height = image.height;

		frames = new Frame[image.count];
		for (int i = 0; i < frames.length; i++)
			frames[i] = new Frame(i, gl);
	}

	public OpenGLRenderImage(OpenGLRender r, GrpFile data) {
		render = r;
		image = data;
	}

	@Override
	protected void draw(int x, int y, int frameId, boolean isMirrored,
			int function, int remapping, int teamColor) {

		init(render.gl);

		render.gl.glMatrixMode(GL10.GL_MODELVIEW);
		render.gl.glPushMatrix();

		if (isMirrored) {
			int dX = x - width / 2
					+ (width - widths[frameId] - xOffsets[frameId]);
			int dY = y - height / 2 + yOffsets[frameId];
			render.gl.glTranslatex(dX, dY, 0);
		} else {
			int dX = x - width / 2 + xOffsets[frameId];
			int dY = y - height / 2 + yOffsets[frameId];
			render.gl.glTranslatex(dX, dY, 0);
		}

		render.gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		render.gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		render.gl.glVertexPointer(2, GL10.GL_FIXED, 0,
				frames[frameId].mVertexBuffer);

		if (isMirrored)
			render.gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0,
					frames[frameId].mMirTexBuffer);
		else
			render.gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0,
					frames[frameId].mTexBuffer);

		render.gl.glActiveTexture(GL10.GL_TEXTURE0);
		render.gl.glBindTexture(GL10.GL_TEXTURE_2D, frames[frameId].texture);
		
		
		
//		if (function == RenderFunction.SHADOW)
//		{
//			render.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//		}
//		else
//		{
//			render.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//		}

		render.gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4,
				GL10.GL_UNSIGNED_BYTE, mIndexBuffer);

		render.gl.glPopMatrix();
	}
}
