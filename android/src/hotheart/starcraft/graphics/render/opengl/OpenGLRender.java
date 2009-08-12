package hotheart.starcraft.graphics.render.opengl;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import hotheart.starcraft.controller.ViewController;
import hotheart.starcraft.graphics.grp.GrpLibrary;
import hotheart.starcraft.graphics.render.Render;
import hotheart.starcraft.graphics.render.RenderImage;

public class OpenGLRender extends Render {

	public GL10 gl;

	public OpenGLViewController controller = null;

	@Override
	public void begin() {
	}

	@Override
	protected RenderImage _createObject(int grpId) {
		return new OpenGLRenderImage(this, GrpLibrary.getGraphics(grpId));
	}

	@Override
	protected ViewController createViewController(Context c) {
		controller = new OpenGLViewController(c, this);
		return controller;
	}

	@Override
	public void end() {
	}

}
