package hotheart.starcraft.graphics.render.simple.grp;

import hotheart.starcraft.configure.BuildParameters;
import hotheart.starcraft.configure.FilePaths;
import hotheart.starcraft.files.LoFile;
import hotheart.starcraft.graphics.grp.GrpLibrary;
import hotheart.starcraft.utils.FileSystemUtils;

import java.io.ByteArrayInputStream;
import java.util.TreeMap;

public class GrpRenderFactory {
	public final static GrpRender getGraphics(int id) {
		GrpRender res;
		if (BuildParameters.CACHE_GRP)
			res = new BitmapGrpImage(GrpLibrary.getGraphics(id).image, id);
		else
			res = new ArrayGrpImage(GrpLibrary.getGraphics(id).image, id);
		return res;
	}

}
