package hotheart.starcraft.graphics.render.opengl;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import hotheart.starcraft.graphics.render.Render;
import hotheart.starcraft.graphics.render.RenderImage;
import hotheart.starcraft.graphics.render.ViewController;

public class OpenGLRender extends Render {

	public GL10 gl;
	@Override
	public void begin() {
	}

	@Override
	public RenderImage createObject(int grpId) {
		return new OpenGLRenderImage(this);
	}

	@Override
	protected ViewController createViewController(Context c) {
		return new OpenGLViewController(c, this);
	}

	@Override
	public void end() {
	}

}
