package hotheart.starcraft.graphics;

import hotheart.starcraft.configure.BuildParameters;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

public final class GRPContainer{
	public GRPImage image;
	public int grpId;

	public GRPContainer(int spriteId)
	{
		grpId = spriteId;
		image = GRPImage.getGraphics(spriteId);
	}
	
	public final void draw(Canvas c, Image data, int[] palette, int dX, int dY) {
		if (image == null)
			return;

		if (!data.align)
		{
			drawSCFrame(c, data.imageState.baseFrame, palette, dX, dY);
		}
		else
		{
			drawSCFrame(c, data.imageState.angle, data.imageState.baseFrame, data.imageState.baseFrame + 16, palette, dX, dY);
		}
	}

	protected final void drawSCFrame(Canvas c, int angle, int tileStart, int tileEnd, int[] palette, int dX, int dY)
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
    	matr.preTranslate(-image.width/2 + dX, -image.height/2 + dY);
    	
    	if (mirrorImage)
    	{
    		//TilesCount - 1, because we use maxTileIndex
    		selIndex = (TilesCount - 1)  - selIndex;
    		matr.preTranslate(image.width, 0);
    		//Because image is out of screen because of mirroring
    		matr.preScale(-1, 1);
    	}
    	
    	c.setMatrix(matr);
    	
    	image.selectedFrame = tileStart + selIndex;
    	image.draw(c, palette);
    	
    	c.restore();
    	
    	if (BuildParameters.DEBUG)
    	{
    		Paint p = new Paint();
    		p.setColor(Color.RED);
    	
    		c.drawLine(0, 0, (float)Math.cos(((angle - 90)/180.0f)*3.1415f)*30,
    			 (float)Math.sin(( (angle  - 90) /180.0f)*3.1415f)*30, p);
    	}
    	
    	
	}
	protected final void drawSCFrame(Canvas c, int tile, int[] palette, int dX, int dY)
	{
		c.save();
		Matrix matr = c.getMatrix();
    	matr.preTranslate(-image.width/2 + dX, -image.height/2 + dY);
    	c.setMatrix(matr);
    	image.selectedFrame = tile;
    	image.draw(c, palette);
		c.restore();
	}

}