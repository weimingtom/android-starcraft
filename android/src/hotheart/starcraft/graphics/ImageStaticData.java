package hotheart.starcraft.graphics;

import hotheart.starcraft.graphics.render.RenderImage;
import hotheart.starcraft.graphics.render.simple.grp.GrpRender;
import hotheart.starcraft.graphics.script.ImageScriptHeader;

public class ImageStaticData
{
	public ImageStaticData(int _imageId, 
			RenderImage _renderImage,
			ImageScriptHeader _scriptHeader,
			boolean _align)
	{
		imageId = _imageId;
		renderImage = _renderImage;
		scriptHeader = _scriptHeader;
		align = _align;
	}
	
	public int imageId;
	public RenderImage renderImage;
	
	public ImageScriptHeader scriptHeader;
	
	public boolean align = false;
}