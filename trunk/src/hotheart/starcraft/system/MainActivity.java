package hotheart.starcraft.system;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import hotheart.starcraft.configure.FilePaths;
import hotheart.starcraft.graphics.GRPImage;
import hotheart.starcraft.graphics.Image;
import hotheart.starcraft.graphics.Sprite;
import hotheart.starcraft.graphics.TeamColors;
import hotheart.starcraft.graphics.script.ImageScriptEngine;
import hotheart.starcraft.graphics.utils.SelectionCircles;
import hotheart.starcraft.sounds.StarcraftSoundPool;
import hotheart.starcraft.units.Flingy;
import hotheart.starcraft.units.ObjectPool;
import hotheart.starcraft.units.Unit;
import hotheart.starcraft.utils.FileSystemUtils;
import hotheart.starcraft.weapons.Weapon;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;


public final class MainActivity extends Activity {
	GameView view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		
		try {
			FileInputStream fs = null;
			
			StarcraftSoundPool.init(
					FileSystemUtils.readAllBytes(FilePaths.SFX_DATA_TBL),
					FileSystemUtils.readAllBytes(FilePaths.SFX_DATA_DAT));
			
			GRPImage.init(FileSystemUtils.readAllBytes(FilePaths.IMAGES_TBL));
			ImageScriptEngine.init(FileSystemUtils.readAllBytes(FilePaths.ISCRIPT_BIN));
			
			//Images
			fs = new FileInputStream(FilePaths.IMAGES_DAT); 
			Image.initImages(fs);
			fs.close();
			
			//Sprite
			fs = new FileInputStream(FilePaths.SPRITES_DAT);
			Sprite.initSprites(fs);
			fs.close();
			
			Flingy.init(FileSystemUtils.readAllBytes(FilePaths.FLINGY_DAT));
			Unit.init(FileSystemUtils.readAllBytes(FilePaths.UNITS_DAT));
			Weapon.init(FileSystemUtils.readAllBytes(FilePaths.WEAPONS_DAT));
			
			
			SelectionCircles.initCircles();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		ObjectPool.init();

		
		for(int i = 0; i < 1; i++)
			ObjectPool.addUnit(Unit.getUnit(3, TeamColors.COLOR_RED), 66*32, 66*32);
		
		for(int i = 0; i < 1; i++)
			ObjectPool.addUnit(Unit.getUnit(3, TeamColors.COLOR_BLUE), 62*32, 62*32);
		
//		ObjectPool.addUnit(Unit.getUnit(66, TeamColors.COLOR_BLUE), 62*32, 62*32);

		view = new GameView(this);
        setContentView(view);
    }
}