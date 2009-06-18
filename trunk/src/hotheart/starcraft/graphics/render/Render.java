package hotheart.starcraft.graphics.render;

import android.content.Context;
import android.view.View;

public abstract class Render {
	
	public static Render defaultRender = null;  
	
	public abstract void begin();

	public abstract void end();
	
	private ViewController cached = null;
	protected abstract ViewController _createViewController(Context c);
	public ViewController createViewController(Context c)
	{
		if (cached == null)
			cached = _createViewController(c);
		
		return cached;
	}
	
	public abstract RenderImage createObject(int grpId);
}
