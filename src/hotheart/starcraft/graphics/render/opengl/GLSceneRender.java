package hotheart.starcraft.graphics.render.opengl;

import hotheart.starcraft.core.StarcraftCore;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.opengl.GLSurfaceView;

public class GLSceneRender implements GLSurfaceView.Renderer {
	OpenGLRender render;

	int[] coords = { 
			200, 100,
			200, 0,
			0, 0,
			0, 100};

	int[] colors = { Color.RED, Color.BLUE, Color.WHITE, Color.WHITE };

	byte[] vertex_strip = { 1, 0, 2, 3 };

	IntBuffer mVertexBuffer;
	IntBuffer mColorBuffer;
	ByteBuffer mIndexBuffer;

	public GLSceneRender(OpenGLRender r) {
		render = r;

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

	public void onDrawFrame(GL10 gl) {
		render.gl = gl;
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
        gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
                GL10.GL_MODULATE);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glTranslatex(-render.controller.getX(), -render.controller.getY(), 0);

		gl.glFrontFace(GL10.GL_CCW);

		StarcraftCore.context.draw();
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		// Then change render type to orthogonal to prevent perspective using
		// real screen size (1 pixel = 1 unit)
		gl.glOrthox(0, width, height, 0, 0, 10);

		// Reset modelview
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		//gl.glDisable(GL10.GL_DITHER);

		//gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		gl.glClearColor(67 / 255.0f, 216 / 255.0f, 248 / 255.0f, 1);

		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_TEXTURE_2D);
	}
}