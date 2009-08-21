package hotheart.starcraft.graphics.render.opengl;

import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.graphics.StarcraftPalette;
import hotheart.starcraft.graphics.render.RenderTile;
import hotheart.starcraft.map.MapRender;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

public class OpenGLTile extends RenderTile {

	private static final int TILE_SIDE = MapRender.TILE_SIDE;

	private static IntBuffer vertexBuffer = null;
	private static FloatBuffer texBuffer = null;
	private static ShortBuffer indexBuffer = null;
	
	

	private static void initBuffers() {
		{
			int[] coords = new int[8];

			coords[0] = 0;
			coords[1] = 0;

			coords[2] = TILE_SIDE;
			coords[3] = 0;

			coords[4] = TILE_SIDE;
			coords[5] = TILE_SIDE;

			coords[6] = 0;
			coords[7] = TILE_SIDE;

			ByteBuffer vbb = ByteBuffer.allocateDirect(coords.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			vertexBuffer = vbb.asIntBuffer();
			vertexBuffer.put(coords);
			vertexBuffer.position(0);
		}

		{
			float[] texCoords = new float[8];
			texCoords[0] = 0;
			texCoords[1] = 0;

			texCoords[2] = 1;
			texCoords[3] = 0;

			texCoords[4] = 1;
			texCoords[5] = 1;

			texCoords[6] = 0;
			texCoords[7] = 1;

			ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
			tbb.order(ByteOrder.nativeOrder());
			texBuffer = tbb.asFloatBuffer();
			texBuffer.put(texCoords);
			texBuffer.position(0);
		}
		{
			short[] indexes = new short[4];
			indexes[0] = 3;
			indexes[1] = 2;
			indexes[2] = 0;
			indexes[3] = 1;

			ByteBuffer ibb = ByteBuffer.allocateDirect(indexes.length * 4);
			ibb.order(ByteOrder.nativeOrder());
			indexBuffer = ibb.asShortBuffer();
			indexBuffer.put(indexes);
			indexBuffer.position(0);
		}
	}

	public int texture;
	
	public OpenGLTile(Bitmap tile) {
		if (vertexBuffer == null)
			initBuffers();
		
		GL10 gl = ((OpenGLRender) StarcraftCore.render).gl;
		
		int[] ids = new int[1];
		gl.glGenTextures(1, ids, 0);
		texture = ids[0];

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);

		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				GL10.GL_REPLACE);
		
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, tile, 0);

		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		
	}

	@Override
	public void draw(int x, int y) {
		GL10 gl = ((OpenGLRender) StarcraftCore.render).gl;

		gl.glPushMatrix();

		gl.glVertexPointer(2, GL10.GL_FIXED, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);

		gl.glTranslatex(x, y, 0);
		
		gl.glActiveTexture(GL10.GL_TEXTURE0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);

		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_SHORT,
				indexBuffer);

		gl.glPopMatrix();
	}

	@Override
	public boolean isRecycled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void recycle() {
		// TODO Auto-generated method stub

	}

}
