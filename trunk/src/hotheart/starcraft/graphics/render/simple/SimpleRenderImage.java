package hotheart.starcraft.graphics.render.simple;

import android.graphics.Canvas;
import android.graphics.Matrix;
import hotheart.starcraft.graphics.render.RenderImage;
import hotheart.starcraft.map.TileLib;

public class SimpleRenderImage extends RenderImage {

	SimpleRender render;
	GrpRender grpRender;

	public SimpleRenderImage(SimpleRender r, GrpRender grp) {
		render = r;
		grpRender = grp;
	}

	@Override
	public void draw(int x, int y, boolean align, int baseFrame, int angle,
			int function, int remapping, int teamColor) {
		Canvas c = render.canvas;
		c.save();
		grpRender.draw(x, y, align, baseFrame, angle, function, remapping,
				teamColor, c);
		c.restore();
	}

}
