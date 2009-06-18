package hotheart.starcraft.graphics.render.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

import hotheart.starcraft.graphics.render.RenderImage;

public class OpenGLRenderImage extends RenderImage {

	static IntBuffer mVertexBuffer;
	static IntBuffer mColorBuffer;
	static ByteBuffer mIndexBuffer;
	
	static int[] coords = { 
			10, 10,
			10, 0,
			0, 0,
			0, 10};

	static int[] colors = { Color.RED, Color.BLUE, Color.WHITE, Color.WHITE };

	static byte[] vertex_strip = { 1, 0, 2, 3 };
	
	
	synchronized static void init()
	{
		if (mVertexBuffer != null)
			return;
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(coords.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asIntBuffer();
		mVertexBuffer.put(coords);
		mVertexBuffer.position(0);

		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		cbb.order(ByteOrder.BIG_ENDIAN);
		mColorBuffer = cbb.asIntBuffer();
		mColorBuffer.put(colors);
		mColorBuffer.position(0);

		mIndexBuffer = ByteBuffer.allocateDirect(vertex_strip.length);
		mIndexBuffer.put(vertex_strip);
		mIndexBuffer.position(0);
	}

	OpenGLRender render;

	public OpenGLRenderImage(OpenGLRender r) {
		render = r;
	}

	@Override
	public void draw(int x, int y, boolean align, int baseFrame, int angle,
			int function, int remapping, int teamColor) {
		
		init();
		
		render.gl.glPushMatrix();
		
		render.gl.glTranslatex(x, y, 0);
		
		render.gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		render.gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		render.gl.glVertexPointer(2, GL10.GL_FIXED, 0, mVertexBuffer);
		render.gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
		render.gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE,
				mIndexBuffer);
		
		render.gl.glPopMatrix();
	}

}
