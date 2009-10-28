package hotheart.starcraft.graphics.render;

import hotheart.starcraft.controller.ViewController;

import java.util.HashMap;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Bitmap;

public abstract class Render {

	public abstract void begin();

	public abstract void end();

	private ViewController cached = null;

	protected abstract ViewController createViewController(Context c);
	
	public abstract RenderTile createTileFromBitmap(Bitmap bitmap); 

	public ViewController getController(Context c) {
		if (cached == null)
			cached = createViewController(c);

		return cached;
	}

	protected abstract RenderImage _createObject(int grpId, RenderFlags flags);
	
	private static TreeMap<Long, RenderImage> resources = new TreeMap<Long, RenderImage>();
	
	private long getHash(int grpId, RenderFlags flags)
	{
		return grpId + ((flags.functionId&0xFF)<<16) + ((flags.teamColor&0xFF)<<24) + ((flags.remapping&0xFF)<<32);
	}

	public RenderImage createObject(int grpId, RenderFlags flags) {

		Long hash = getHash(grpId, flags);
		
		if (resources.containsKey(hash))
			return resources.get(hash);
		else {
			RenderImage res = _createObject(grpId, flags);
			resources.put(hash, res);
			return res;
		}
	}
}
