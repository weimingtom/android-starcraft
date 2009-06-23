package hotheart.starcraft.graphics.render.simple.grp;

import hotheart.starcraft.configure.BuildParameters;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

public abstract class GrpRender {

	public int width = 0;
	public int height = 0;
	public int grpId;

	protected abstract void draw(Canvas c, int frameId, int function,
			int remapping, int teamColor);

	public final void drawSCFrame(Canvas c, int baseFrame, boolean isMirrored,
			int function, int remapping, int teamColor, int dX, int dY) {
		c.save();

		Matrix matr = c.getMatrix();
		matr.preTranslate(-width / 2 + dX, -height / 2 + dY);
		// matr.preTranslate(dX, dY);

		if (isMirrored) {
			// TilesCount - 1, because we use maxTileIndex
			// selIndex = (TilesCount - 1) - selIndex;
			matr.preTranslate(width, 0);
			// Because image is out of screen because of mirroring
			matr.preScale(-1, 1);
		}

		c.setMatrix(matr);

		// image.selectedFrame = tileStart + selIndex;
		draw(c, baseFrame, function, remapping, teamColor);

		c.restore();

	}

	public final void drawSCFrame(Canvas c, int tile, int function,
			int remapping, int teamColor, int dX, int dY) {
		c.save();
		Matrix matr = c.getMatrix();
		matr.preTranslate(-width / 2 + dX, -height / 2 + dY);
		c.setMatrix(matr);
		// image.selectedFrame = tile;
		draw(c, tile, function, remapping, teamColor);
		c.restore();
	}
}
