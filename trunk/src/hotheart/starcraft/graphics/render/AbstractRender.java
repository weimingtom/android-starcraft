package hotheart.starcraft.graphics.render;

import android.view.View;

public abstract class AbstractRender {
	
	public static AbstractRender defaultRender = null;  
	
	public abstract Object begin();
	public abstract void end(Object state);
	
	public abstract View createView();
	
	public abstract Object createObject(int grpId);
	public abstract void destroyObject(Object object);
}
