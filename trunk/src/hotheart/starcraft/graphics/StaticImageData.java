package hotheart.starcraft.graphics;

import hotheart.starcraft.graphics.script.ImageScriptHeader;

public class StaticImageData
{
	public StaticImageData(int _imageId, 
			GRPContainer _grp,
			ImageScriptHeader _scriptHeader,
			int _graphicsFuntion,
			int _remapping,
			boolean _align)
	{
		imageId = _imageId;
		grp = _grp;
		scriptHeader = _scriptHeader;
		graphicsFuntion = _graphicsFuntion;
		remapping = _remapping;
		align = _align;
	}
	
	public int imageId;
	public GRPContainer grp;
	
	public ImageScriptHeader scriptHeader;
	
	public int graphicsFuntion = 0;
	public int remapping = 0;
	public boolean align = false;
}