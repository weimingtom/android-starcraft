package hotheart.starcraft.graphics;

import hotheart.starcraft.graphics.render.RenderImage;
import hotheart.starcraft.graphics.render.simple.grp.GrpRender;
import hotheart.starcraft.graphics.script.ImageScriptHeader;

public class ImageStaticData
{
	public ImageStaticData(int _imageId, 
			RenderImage _renderImage,
			ImageScriptHeader _scriptHeader,
			int _graphicsFuntion,
			int _remapping,
			boolean _align)
	{
		imageId = _imageId;
		renderImage = _renderImage;
		scriptHeader = _scriptHeader;
		graphicsFuntion = _graphicsFuntion;
		remapping = _remapping;
		align = _align;
	}
	
	public int imageId;
	public RenderImage renderImage;
	
	public ImageScriptHeader scriptHeader;
	
	public int graphicsFuntion = 0;
	public int remapping = 0;
	public boolean align = false;
}