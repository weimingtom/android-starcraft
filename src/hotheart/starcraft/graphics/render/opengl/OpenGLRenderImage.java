package hotheart.starcraft.graphics.render.opengl;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.opengl.GLUtils;

import hotheart.starcraft.files.GrpFile;
import hotheart.starcraft.graphics.StarcraftPalette;
import hotheart.starcraft.graphics.render.RenderImage;

public class OpenGLRenderImage extends RenderImage {

	class Frame {
		public IntBuffer mVertexBuffer;
		public int texture;

		public Frame(int i, GL10 gl) {
			int[] ids = new int[1];
			gl.glGenTextures(1, ids, 0);
			texture = ids[0];

			int[] coords = { image.widths[i], image.heights[i],
					image.widths[i], 0, 0, 0, 0, image.heights[i] };

			ByteBuffer vbb = ByteBuffer.allocateDirect(coords.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer = vbb.asIntBuffer();
			mVertexBuffer.put(coords);
			mVertexBuffer.position(0);

			gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);

			gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
					GL10.GL_REPLACE);

			Bitmap bitmap = image.createBitmap(i,
					StarcraftPalette.normalPalette);

			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, Bitmap
					.createScaledBitmap(bitmap, 32, 32, false), 0);
			bitmap.recycle();

			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR);

		}
	}

	FloatBuffer mTexBuffer;
	FloatBuffer mMirTexBuffer;
	ByteBuffer mIndexBuffer;

	// int[] coords = { 1, 1, 1, 0, 0, 0, 0, 1 };
	byte[] vertex_strip = { 1, 0, 2, 3 };

	GrpFile image;

	int[] xOffsets = null, yOffsets = null;
	int width = 0, height = 0;
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

		float[] texCoords = { 1, 1, 1, 0, 0, 0, 0, 1 };

		float[] mirTexCoords = { 0, 1, 0, 0, 1, 0, 1, 1 };

		ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		mTexBuffer = tbb.asFloatBuffer();
		mTexBuffer.put(texCoords);
		mTexBuffer.position(0);

		ByteBuffer mtbb = ByteBuffer.allocateDirect(mirTexCoords.length * 4);
		mtbb.order(ByteOrder.nativeOrder());
		mMirTexBuffer = mtbb.asFloatBuffer();
		mMirTexBuffer.put(mirTexCoords);
		mMirTexBuffer.position(0);

		mIndexBuffer = ByteBuffer.allocateDirect(vertex_strip.length);
		mIndexBuffer.order(ByteOrder.nativeOrder());
		mIndexBuffer.put(vertex_strip);
		mIndexBuffer.position(0);

		frames = new Frame[image.count];
		for (int i = 0; i < frames.length; i++)
			frames[i] = new Frame(i, gl);
	}

	OpenGLRender render;

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
			render.gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mMirTexBuffer);
		else
			render.gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexBuffer);

		render.gl.glActiveTexture(GL10.GL_TEXTURE0);
		render.gl.glBindTexture(GL10.GL_TEXTURE_2D, frames[frameId].texture);

		render.gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4,
				GL10.GL_UNSIGNED_BYTE, mIndexBuffer);

		render.gl.glPopMatrix();
	}

}
