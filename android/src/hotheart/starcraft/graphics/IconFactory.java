package hotheart.starcraft.graphics;

import hotheart.starcraft.configure.FilePaths;
import hotheart.starcraft.files.GrpFile;
import hotheart.starcraft.graphics.grp.GrpLibrary;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class IconFactory {
	private static GrpFile iconGrp = null;
	public static Bitmap getIcon(int id)
	{
		if (iconGrp == null)
			iconGrp = GrpLibrary.getGraphics(FilePaths.ICON_GRP_FILE);
	
		try
		{
			return iconGrp.createBitmap(id, StarcraftPalette.iconPalette);
		}
		catch(Exception e)
		{
			return Bitmap.createBitmap(1, 1, Config.RGB_565); 
		}
	}
}
