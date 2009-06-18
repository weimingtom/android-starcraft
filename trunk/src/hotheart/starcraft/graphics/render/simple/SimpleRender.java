package hotheart.starcraft.graphics.render.simple;

import android.content.Context;
import hotheart.starcraft.graphics.render.AbstractRender;
import hotheart.starcraft.graphics.render.ViewController;

public class SimpleRender extends AbstractRender {

	@Override
	public Object begin() {
		return null;
	}

	@Override
	public Object createObject(int grpId) {
		return null;
	}

	@Override
	protected ViewController _createViewController(Context c) {
		return new SimpleViewController(c);
	}

	@Override
	public void destroyObject(Object object) {
	}

	@Override
	public void end(Object state) {
	}
}