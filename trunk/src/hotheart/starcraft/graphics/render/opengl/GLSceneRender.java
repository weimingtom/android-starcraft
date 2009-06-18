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

	CubeRenderer cube = new CubeRenderer(false);

	public void onDrawFrame(GL10 gl) {
		render.gl = gl;
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glFrontFace(GL10.GL_CCW);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		gl.glVertexPointer(2, GL10.GL_FIXED, 0, mVertexBuffer);
		gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE,
				mIndexBuffer);

		// StarcraftCore.context.draw();
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

	}
}

class CubeRenderer implements GLSurfaceView.Renderer {
	public CubeRenderer(boolean useTranslucentBackground) {
		mTranslucentBackground = useTranslucentBackground;
		mCube = new Cube();
	}

	public void onDrawFrame(GL10 gl) {
		/*
		 * Usually, the first thing one might want to do is to clear the screen.
		 * The most efficient way of doing this is to use glClear().
		 */

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		/*
		 * Now we're ready to draw some 3D objects
		 */

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, -3.0f);
		gl.glRotatef(mAngle, 0, 1, 0);
		gl.glRotatef(mAngle * 0.25f, 1, 0, 0);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		mCube.draw(gl);

		gl.glRotatef(mAngle * 2.0f, 0, 1, 1);
		gl.glTranslatef(0.5f, 0.5f, 0.5f);

		mCube.draw(gl);

		mAngle += 1.2f;
	}

	public int[] getConfigSpec() {
		if (mTranslucentBackground) {
			// We want a depth buffer and an alpha buffer
			int[] configSpec = { EGL10.EGL_RED_SIZE, 8, EGL10.EGL_GREEN_SIZE,
					8, EGL10.EGL_BLUE_SIZE, 8, EGL10.EGL_ALPHA_SIZE, 8,
					EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
			return configSpec;
		} else {
			// We want a depth buffer, don't care about the
			// details of the color buffer.
			int[] configSpec = { EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
			return configSpec;
		}
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);

		/*
		 * Set our projection matrix. This doesn't have to be done each time we
		 * draw, but usually a new projection needs to be set when the viewport
		 * is resized.
		 */

		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		/*
		 * By default, OpenGL enables features that improve quality but reduce
		 * performance. One might want to tweak that especially on software
		 * renderer.
		 */
		gl.glDisable(GL10.GL_DITHER);

		/*
		 * Some one-time OpenGL initialization can be made here probably based
		 * on features of this particular context
		 */
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		if (mTranslucentBackground) {
			gl.glClearColor(0, 0, 0, 0);
		} else {
			gl.glClearColor(1, 1, 1, 1);
		}
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glDisable(GL10.GL_DEPTH_TEST);
	}

	private boolean mTranslucentBackground;
	private Cube mCube;
	private float mAngle;
}

class Cube {
	public Cube() {
		int one = 0x10000;
		int vertices[] = { -one, -one, -one, one, -one, -one, one, one, -one,
				-one, one, -one, -one, -one, one, one, -one, one, one, one,
				one, -one, one, one, };

		int colors[] = { 0, 0, 0, one, one, 0, 0, one, one, one, 0, one, 0,
				one, 0, one, 0, 0, one, one, one, 0, one, one, one, one, one,
				one, 0, one, one, one, };

		byte indices[] = { 0, 4, 5, 0, 5, 1, 1, 5, 6, 1, 6, 2, 2, 6, 7, 2, 7,
				3, 3, 7, 4, 3, 4, 0, 4, 7, 6, 4, 6, 5, 3, 0, 1, 3, 1, 2 };

		// Buffers to be passed to gl*Pointer() functions
		// must be direct, i.e., they must be placed on the
		// native heap where the garbage collector cannot
		// move them.
		//
		// Buffers with multi-byte datatypes (e.g., short, int, float)
		// must have their byte order set to native order

		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asIntBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);

		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		cbb.order(ByteOrder.nativeOrder());
		mColorBuffer = cbb.asIntBuffer();
		mColorBuffer.put(colors);
		mColorBuffer.position(0);

		mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
		mIndexBuffer.put(indices);
		mIndexBuffer.position(0);
	}

	public void draw(GL10 gl) {
		gl.glFrontFace(gl.GL_CW);
		gl.glVertexPointer(3, gl.GL_FIXED, 0, mVertexBuffer);
		gl.glColorPointer(4, gl.GL_FIXED, 0, mColorBuffer);
		gl.glDrawElements(gl.GL_TRIANGLES, 36, gl.GL_UNSIGNED_BYTE,
				mIndexBuffer);
	}

	private IntBuffer mVertexBuffer;
	private IntBuffer mColorBuffer;
	private ByteBuffer mIndexBuffer;
}