package hotheart.starcraft.graphics.grp;

import hotheart.starcraft.configure.BuildParameters;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

public abstract class AbstractGrpRender {
	
	public int width = 0;
	public int height = 0;
	public int grpId;
	
	
	public abstract void draw(Canvas c, int frameId, int function, int remapping, int teamColor);
		
	public final void draw(int dX, int dY, boolean align, int baseFrame, int angle, int function, int remapping, int teamColor, Canvas c) {
		if (!align)
		{
			drawSCFrame(c, baseFrame,  function, remapping, teamColor, dX, dY);
		}
		else
		{
			drawSCFrame(c, angle, baseFrame, baseFrame + 16,  function, remapping, teamColor, dX, dY);
		}
	}

	protected final void drawSCFrame(Canvas c, int angle, int tileStart, int tileEnd,  int function, int remapping, int teamColor, int dX, int dY)
	{
		int TilesCount = tileEnd - tileStart + 1;
		
		angle = angle%360;
		if (angle < 0)
			angle += 360;
		//Now degree is in [0,360)
		
		boolean mirrorImage = angle >= 180;
		
		int selIndex = 0;
		if (!mirrorImage)
		{
			//Degree is in [0,180)
			selIndex = (int)( (angle * TilesCount)/180.0f );
		}
		else
		{
			//Degree is in [180,360)
			selIndex = (int)( ((angle - 180) * TilesCount)/180.0f );
		}

		c.save();
    	
    	Matrix matr = c.getMatrix();
    	matr.preTranslate(-width/2 + dX, -height/2 + dY);
    	
    	if (mirrorImage)
    	{
    		//TilesCount - 1, because we use maxTileIndex
    		selIndex = (TilesCount - 1)  - selIndex;
    		matr.preTranslate(width, 0);
    		//Because image is out of screen because of mirroring
    		matr.preScale(-1, 1);
    	}
    	
    	c.setMatrix(matr);
    	
    	//image.selectedFrame = tileStart + selIndex;
    	draw(c,  tileStart + selIndex,  function, remapping, teamColor);
    	
    	c.restore();
    	
    	if (BuildParameters.DEBUG)
    	{
    		Paint p = new Paint();
    		p.setColor(Color.RED);
    	
    		c.drawLine(0, 0, (float)Math.cos(((angle - 90)/180.0f)*3.1415f)*30,
    			 (float)Math.sin(( (angle  - 90) /180.0f)*3.1415f)*30, p);
    	}
    	
    	
	}
	protected final void drawSCFrame(Canvas c, int tile,  int function, int remapping, int teamColor, int dX, int dY)
	{
		c.save();
		Matrix matr = c.getMatrix();
    	matr.preTranslate(-width/2 + dX, -height/2 + dY);
    	c.setMatrix(matr);
    	//image.selectedFrame = tile;
    	draw(c, tile, function, remapping, teamColor);
		c.restore();
	}
}
