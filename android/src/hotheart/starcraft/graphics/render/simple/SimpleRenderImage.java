package hotheart.starcraft.graphics.render.simple;

import android.graphics.Canvas;
import android.graphics.Matrix;
import hotheart.starcraft.graphics.render.RenderFlags;
import hotheart.starcraft.graphics.render.RenderImage;
import hotheart.starcraft.graphics.render.simple.grp.GrpRender;
import hotheart.starcraft.map.TileLib;

public class SimpleRenderImage extends RenderImage {

	SimpleRender render;
	GrpRender grpRender;
	RenderFlags flags;

	public SimpleRenderImage(SimpleRender r, GrpRender grp, RenderFlags flags) {
		render = r;
		grpRender = grp;
		this.flags = flags;
	}

	@Override
	protected void draw(int x, int y, int frameId, boolean isMirrored) {

		Canvas c = render.canvas;
		c.save();
		grpRender.drawSCFrame(c, frameId, isMirrored, flags.functionId, flags.remapping, flags.teamColor, x, y);
		c.restore();

	}

}
