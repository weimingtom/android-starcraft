package hotheart.starcraft.graphics.render;

public abstract class RenderImage {
	public void draw(int x, int y, boolean align, int baseFrame, int angle, int function, int remapping, int teamColor)
	{
		if (align)
		{
			draw(x,y, getFrameId(angle, baseFrame), isMirrored(angle), function, remapping, teamColor);
			//draw(x,y, getFrameId(angle, baseFrame), false, function, remapping, teamColor);
		}
		else
		{
			draw(x,y, baseFrame, false, function, remapping, teamColor);
		}
		//draw(x,y, getFrameId(angle))
	}
	
	protected abstract void draw(int x, int y, int frameId, boolean isMirrored, int function, int remapping, int teamColor);
	
	private int getFrameId(int alpha, int baseFrame)
	{
		alpha = alpha%360;
		if (alpha < 0)
			alpha += 360;
		//Now degree is in [0,360)
		
		boolean mirrorImage = alpha >= 180;
		
		int selIndex = 0;
		if (!mirrorImage)
		{
			//Degree is in [0,180)
			selIndex = (int)( (alpha * 16)/180.0f );
		}
		else
		{
			//Degree is in [180,360)
			selIndex = (int)( ((alpha - 180) * 16)/180.0f );
			
			selIndex = (16 - 1)  - selIndex;
		}
		
		return selIndex + baseFrame;
	}
	private boolean isMirrored(int alpha)
	{
		alpha = alpha%360;
		if (alpha < 0)
			alpha += 360;
		//Now degree is in [0,360)
		
		boolean mirrorImage = alpha >= 180;
		
		return mirrorImage;
	}
}
