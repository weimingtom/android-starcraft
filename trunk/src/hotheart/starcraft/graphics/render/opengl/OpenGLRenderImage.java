package hotheart.starcraft.graphics.render.opengl;

import hotheart.starcraft.graphics.render.RenderImage;

public class OpenGLRenderImage extends RenderImage {

	OpenGLRender render;

	float[] coords = { 20, 10, 20, 0, 0, 0, 0, 10 };

	float[] texturCoords = new float[] { 1f, 1f, 1f, 0.005f, 0.005f, 0.005f,
			0.005f, 1f };

	float[] vertex_strip = { 1, 0, 2, 3 };

	public OpenGLRenderImage(OpenGLRender r) {
		render = r;
	}

	@Override
	public void draw(int x, int y, boolean align, int baseFrame, int angle,
			int function, int remapping, int teamColor) {
		
	}

}
