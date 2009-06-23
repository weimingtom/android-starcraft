package hotheart.starcraft.core;

import hotheart.starcraft.configure.BuildParameters;
import hotheart.starcraft.configure.FilePaths;
import hotheart.starcraft.graphics.Image;
import hotheart.starcraft.graphics.Sprite;
import hotheart.starcraft.graphics.TeamColors;
import hotheart.starcraft.graphics.grp.GrpLibrary;
import hotheart.starcraft.graphics.render.Render;
import hotheart.starcraft.graphics.render.opengl.OpenGLRender;
import hotheart.starcraft.graphics.render.simple.SimpleRender;
import hotheart.starcraft.graphics.render.simple.grp.GrpRenderFactory;
import hotheart.starcraft.graphics.script.ImageScriptEngine;
import hotheart.starcraft.graphics.utils.SelectionCircles;
import hotheart.starcraft.map.Map;
import hotheart.starcraft.sounds.StarcraftSoundPool;
import hotheart.starcraft.units.Flingy;
import hotheart.starcraft.units.Unit;
import hotheart.starcraft.utils.FileSystemUtils;
import hotheart.starcraft.weapons.Weapon;

import java.io.FileInputStream;

import android.app.Activity;
import android.app.ProgressDialog;

public class StarcraftCore {

	public static Render render;
	public static GameContext context;

	public static String state = "";

	static Activity act;

	static ProgressDialog splash;

	private static void showMessage(final String data) {
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

	private static void hideProgress() {
		act.runOnUiThread(new Runnable() {
			public void run() {
				if (splash != null)
					splash.hide();
			}
		});

	}

	public static boolean init(Activity parentAcrivity) {
		try {
			act = parentAcrivity;
			context = new GameContext();
			render = new OpenGLRender();
			// render = new SimpleRender();

			FileInputStream fs = null;

			showMessage("Init sound lib");
			StarcraftSoundPool.init(FileSystemUtils
					.readAllBytes(FilePaths.SFX_DATA_TBL), FileSystemUtils
					.readAllBytes(FilePaths.SFX_DATA_DAT));

			showMessage("GRP library");
			GrpLibrary.init(FileSystemUtils.readAllBytes(FilePaths.IMAGES_TBL));

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

			showMessage("Creating units");

			// context.addUnit(Unit.getUnit(0, TeamColors.COLOR_RED), 62 * 32,
			// 66 * 32);
			//
			// context.addUnit(Unit.getUnit(1, TeamColors.COLOR_RED), 62 * 32,
			// 66 * 32);

			for (int i = 0; i < 5; i++) {
				context.addUnit(Unit.getUnit(0, TeamColors.COLOR_GREEN),
						66 * 32, 62 * 32);

				context.addUnit(Unit.getUnit(1, TeamColors.COLOR_GREEN),
						66 * 32, 62 * 32);
			}
			//
			// context.addUnit(Unit.getUnit(0, TeamColors.COLOR_BLUE), 66 * 32,
			// 66 * 32);
			//
			// context.addUnit(Unit.getUnit(1, TeamColors.COLOR_BLUE), 66 * 32,
			// 66 * 32);

			showMessage("Loading map");

			if (BuildParameters.LOAD_MAP)
				context.map = new Map(FileSystemUtils
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
