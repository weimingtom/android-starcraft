package hotheart.starcraft.graphics.render.opengl;

import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.map.Map;
import hotheart.starcraft.map.TileLib;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGL10;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.opengl.GLSurfaceView;

public class GLSceneRender implements GLSurfaceView.Renderer {
	OpenGLRender render;

	public GLSceneRender(OpenGLRender r) {
		render = r;
	}

	public void onDrawFrame(GL10 gl) {
		render.gl = gl;
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				GL10.GL_MODULATE);
		
		gl.glFrontFace(GL10.GL_CCW);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		mapRender.testDraw(gl);

		int dX = -render.controller.getX();
		int dY = -render.controller.getY();
		gl.glTranslatex(dX, dY, 0);

		

		StarcraftCore.context.draw();

		StarcraftCore.context.update();
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		// Then change render type to orthogonal to prevent perspective using
		// real screen size (1 pixel = 1 unit)
		gl.glOrthox(0, width, height, 0, 0, 1);

		// Reset modelview
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	

	MapRender mapRender;
	private void createMapTexture(GL10 gl)
	{
		mapRender = new MapRender(gl);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		int[] res = new int[1];
		gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, res, 0);
		createMapTexture(gl);
		
		
		// gl.glDisable(GL10.GL_DITHER);

		// gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		gl.glClearColor(67 / 255.0f, 216 / 255.0f, 248 / 255.0f, 1);

		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	}
}