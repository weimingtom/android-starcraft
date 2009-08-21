package hotheart.starcraft.graphics.render.opengl;

import hotheart.starcraft.configure.BuildParameters;
import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.map.MapRender;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.opengl.GLSurfaceView;

public class GLSceneRender implements GLSurfaceView.Renderer {
	OpenGLRender render;

	MapRender mapRender;

	public GLSceneRender(OpenGLRender r) {
		render = r;
		mapRender = new MapRender(StarcraftCore.context.map);
	}

	public void onDrawFrame(GL10 gl) {
		render.gl = gl;

		int offsetX = render.controller.getX();
		int offsetY = render.controller.getY();

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				GL10.GL_MODULATE);

		gl.glFrontFace(GL10.GL_CCW);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		// TODO fix sizes
		mapRender.drawMap(offsetX, offsetY, 480, 320);
		
		// mapRender.offsetX = offsetX;
		// mapRender.offsetY = offsetY;

		// mapRender.draw(gl);

		int dX = -offsetX;
		int dY = -offsetY;
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

	private void createMapTexture(GL10 gl) {
		// mapRender = new MapRender(gl);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		int[] res = new int[1];
		gl.glGetIntegerv(GL10.GL_MAX_ELEMENTS_INDICES, res, 0);
		createMapTexture(gl);

		// gl.glDisable(GL10.GL_DITHER);

		// gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		int bkgColor = BuildParameters.BACKGROUND_COLOR;

		gl.glClearColor(Color.red(bkgColor) / 255.0f,
				Color.green(bkgColor) / 255.0f, Color.blue(bkgColor) / 255.0f,
				1);

		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		OpenGLRenderImage.initData(gl);
	}
}