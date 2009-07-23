package hotheart.starcraft.graphics.render;

import hotheart.starcraft.controller.ViewController;
import hotheart.starcraft.files.LoFile;

import java.util.TreeMap;

import android.content.Context;
import android.view.View;

public abstract class Render {

	public abstract void begin();

	public abstract void end();

	private ViewController cached = null;

	protected abstract ViewController createViewController(Context c);

	public ViewController getController(Context c) {
		if (cached == null)
			cached = createViewController(c);

		return cached;
	}

	protected abstract RenderImage _createObject(int grpId);

	private static TreeMap<Integer, RenderImage> resources = new TreeMap<Integer, RenderImage>();

	public RenderImage createObject(int grpId) {
		if (resources.containsKey((Integer) grpId))
			return resources.get((Integer) grpId);
		else {
			RenderImage res = _createObject(grpId);
			resources.put((Integer) grpId, res);
			return res;
		}
	}
}
