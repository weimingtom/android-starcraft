package hotheart.starcraft.graphics.render.simple;

import hotheart.starcraft.controller.ViewController;
import hotheart.starcraft.graphics.render.Render;
import hotheart.starcraft.graphics.render.RenderImage;
import hotheart.starcraft.graphics.render.RenderTile;
import hotheart.starcraft.graphics.render.simple.grp.GrpRenderFactory;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class SimpleRender extends Render {

	public Canvas canvas = null;
	private SimpleViewController contr = null;

	@Override
	public void begin() {
	}

	@Override
	public void end() {
	}

	@Override
	protected RenderImage _createObject(int grpId) {
		return new SimpleRenderImage(this, GrpRenderFactory.getGraphics(grpId));
	}

	@Override
	protected ViewController createViewController(Context c) {
		contr = new SimpleViewController(c, this);
		return contr;
	}

	@Override
	public RenderTile createTileFromBitmap(Bitmap bitmap) {
		return null;
	}
}