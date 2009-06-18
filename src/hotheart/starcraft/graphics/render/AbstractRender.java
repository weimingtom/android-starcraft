package hotheart.starcraft.graphics.render;

import android.content.Context;
import android.view.View;

public abstract class AbstractRender {
	
	public static AbstractRender defaultRender = null;  
	
	public abstract Object begin();
	public abstract void end(Object state);
	
	private ViewController cached = null;
	protected abstract ViewController _createViewController(Context c);
	public ViewController createViewController(Context c)
	{
		if (cached == null)
			cached = _createViewController(c);
		
		return cached;
	}
	
	public abstract Object createObject(int grpId);
	public abstract void destroyObject(Object object);
}
