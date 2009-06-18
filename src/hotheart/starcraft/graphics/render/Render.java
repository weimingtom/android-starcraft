package hotheart.starcraft.graphics.render;

import android.content.Context;
import android.view.View;

public abstract class Render {

	public abstract void begin();

	public abstract void end();
	
	private ViewController cached = null;
	protected abstract ViewController createViewController(Context c);
	public ViewController getController(Context c)
	{
		if (cached == null)
			cached = createViewController(c);
		
		return cached;
	}
	
	public abstract RenderImage createObject(int grpId);
}
