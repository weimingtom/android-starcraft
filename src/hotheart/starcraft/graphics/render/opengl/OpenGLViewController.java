package hotheart.starcraft.graphics.render.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.View;
import hotheart.starcraft.graphics.render.ViewController;
import hotheart.starcraft.map.Map;

public class OpenGLViewController extends ViewController {

	OpenGLRender render;
	GLSurfaceView surface;
	public OpenGLViewController(Context c, OpenGLRender r)
	{
		render = r;
		surface = new GLSurfaceView(c);
		surface.setRenderer(new GLSceneRender(render));
	}
	
	@Override
	protected View _getView() {
		return surface;
	}

	@Override
	protected void _setPosXY(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMap(Map map) {
		// TODO Auto-generated method stub
		
	}

}
