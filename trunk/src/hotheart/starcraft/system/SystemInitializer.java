package hotheart.starcraft.system;

import hotheart.starcraft.configure.BuildParameters;
import hotheart.starcraft.configure.FilePaths;
import hotheart.starcraft.graphics.GRPImage;
import hotheart.starcraft.graphics.Image;
import hotheart.starcraft.graphics.Sprite;
import hotheart.starcraft.graphics.TeamColors;
import hotheart.starcraft.graphics.script.ImageScriptEngine;
import hotheart.starcraft.graphics.utils.SelectionCircles;
import hotheart.starcraft.map.Map;
import hotheart.starcraft.sounds.StarcraftSoundPool;
import hotheart.starcraft.units.Flingy;
import hotheart.starcraft.units.ObjectPool;
import hotheart.starcraft.units.Unit;
import hotheart.starcraft.utils.FileSystemUtils;
import hotheart.starcraft.weapons.Weapon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

public class SystemInitializer {

	public Map map = null;

	public String state = "";

	Activity act;

	ProgressDialog splash;

	private void showMessage(final String data) {
		state = data;
		act.runOnUiThread(new Runnable() {
			public void run() {
				if (splash == null)
					splash = ProgressDialog
							.show(act, "Android Starcraft", data);
				else
					splash.setMessage(data);
			}
		});

	}

	private void hideProgress() {
		act.runOnUiThread(new Runnable() {
			public void run() {
				if (splash != null)
					splash.hide();
			}
		});

	}

	public SystemInitializer(Activity parentAcrivity) {
		act = parentAcrivity;
	}

	public boolean init() {
		try {

			FileInputStream fs = null;

			showMessage("Init sound lib");
			StarcraftSoundPool.init(FileSystemUtils
					.readAllBytes(FilePaths.SFX_DATA_TBL), FileSystemUtils
					.readAllBytes(FilePaths.SFX_DATA_DAT));

			showMessage("GRP library");
			GRPImage.init(FileSystemUtils.readAllBytes(FilePaths.IMAGES_TBL));

			showMessage("Init script engine");
			ImageScriptEngine.init(FileSystemUtils
					.readAllBytes(FilePaths.ISCRIPT_BIN));

			showMessage("Init Images");
			// Images
			fs = new FileInputStream(FilePaths.IMAGES_DAT);
			Image.initImages(fs);
			fs.close();

			showMessage("Init Sprites");
			// Sprite
			fs = new FileInputStream(FilePaths.SPRITES_DAT);
			Sprite.initSprites(fs);
			fs.close();

			showMessage("Init Flingy");
			Flingy.init(FileSystemUtils.readAllBytes(FilePaths.FLINGY_DAT));
			showMessage("Init Units");
			Unit.init(FileSystemUtils.readAllBytes(FilePaths.UNITS_DAT));
			showMessage("Init Weapons");
			Weapon.init(FileSystemUtils.readAllBytes(FilePaths.WEAPONS_DAT));

			SelectionCircles.initCircles();

			ObjectPool.init();

			showMessage("Creating units");

			for (int i = 0; i < 10; i++)
				ObjectPool.addUnit(Unit.getUnit(0, TeamColors.COLOR_RED),
						66 * 32, 66 * 32);

			for (int i = 0; i < 10; i++)
				ObjectPool.addUnit(Unit.getUnit(0, TeamColors.COLOR_BLUE),
						62 * 32, 62 * 32);

			showMessage("Loading map");

			if (BuildParameters.LOAD_MAP)
				map = new Map(FileSystemUtils
						.readAllBytes(FilePaths.SCENARIO_CHK));

			hideProgress();

			return true;

		} catch (Exception e) {
			hideProgress();
			e.printStackTrace();
			return false;
		}
	}
}
