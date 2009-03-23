package hotheart.starcraft.system;

import hotheart.starcraft.graphics.GRPImage;
import hotheart.starcraft.graphics.ImageScriptEngine;
import hotheart.starcraft.graphics.Image;
import hotheart.starcraft.graphics.Sprite;
import hotheart.starcraft.sounds.StarcraftSoundPool;
import hotheart.starcraft.units.Flingy;
import hotheart.starcraft.units.ObjectPool;
import hotheart.starcraft.units.Unit;
import hotheart.starcraft.units.Weapon;
import hotheart.starcraft.utils.FileSystemUtils;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;


public final class MainActivity extends Activity {
	UnitView view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		
		GRPImage.init(FileSystemUtils.readAllBytes("/sdcard/starcraft/images.tbl"));
		StarcraftSoundPool.init(
				FileSystemUtils.readAllBytes("/sdcard/starcraft/sfxdata.tbl"),
				FileSystemUtils.readAllBytes("/sdcard/starcraft/sfxdata.dat"));
		ImageScriptEngine.init(FileSystemUtils.readAllBytes("/sdcard/starcraft/iscript.bin"));
		Image.init(FileSystemUtils.readAllBytes("/sdcard/starcraft/images.dat"));
		Sprite.init(FileSystemUtils.readAllBytes("/sdcard/starcraft/sprites.dat"));
		Sprite.initCircles();
		Flingy.init(FileSystemUtils.readAllBytes("/sdcard/starcraft/flingy.dat"));
		Unit.init(FileSystemUtils.readAllBytes("/sdcard/starcraft/units.dat"));
		Weapon.init(FileSystemUtils.readAllBytes("/sdcard/starcraft/weapons.dat"));
		
		ObjectPool.init();
		
		for(int i = 0; i < 5; i++)
			ObjectPool.addUnit(Unit.getUnit(38, Image.COLOR_RED), 64*32, 64*32);
			
        view = new UnitView(this);
        
        
        setContentView(view);
    }
}