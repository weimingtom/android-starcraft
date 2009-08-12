package hotheart.starcraft.configure;

public class FilePaths {

	/*
	 * Game folder
	 */
	public static final String GAME_FOLDER = "/sdcard/starcraft/";
	
	/*
	 * Units folder
	 */
	public static final String UNITS_FOLDER = GAME_FOLDER + "unit/";

	/*
	 * /arr folder
	 */
	public static final String ARR_FOLDER = GAME_FOLDER + "arr/";
	public static final String SFX_DATA_TBL = ARR_FOLDER + "sfxdata.tbl";
	public static final String SFX_DATA_DAT = ARR_FOLDER + "sfxdata.dat";
	public static final String IMAGES_TBL = ARR_FOLDER + "images.tbl";
	public static final String IMAGES_DAT = ARR_FOLDER + "images.dat";
	public static final String SPRITES_DAT = ARR_FOLDER + "sprites.dat";
	public static final String FLINGY_DAT = ARR_FOLDER + "flingy.dat";
	public static final String UNITS_DAT = ARR_FOLDER + "units.dat";
	public static final String WEAPONS_DAT = ARR_FOLDER + "weapons.dat";

	/*
	 * Script folder
	 */
	public static final String ISCRIPT_BIN = GAME_FOLDER + "scripts/"
			+ "iscript.bin";

	/*
	 * Tilesets fo rendering
	 */
	public static final String TILESET_FOLDER = GAME_FOLDER + "tileset/";

	public static final String BADLANDS_PREFIX = TILESET_FOLDER + "badlands";
	public static final String PLATFORM_PREFIX = TILESET_FOLDER + "platform";
	public static final String INSTALL_PREFIX = TILESET_FOLDER + "install";
	public static final String ASHWORLD_PREFIX = TILESET_FOLDER + "ashworld";
	public static final String JUNGLE_PREFIX = TILESET_FOLDER + "jungle";
	public static final String DESERT_PREFIX = TILESET_FOLDER + "desert";
	public static final String ICE_PREFIX = TILESET_FOLDER + "ice";
	public static final String TWILIGHT_PREFIX = TILESET_FOLDER + "twilight";

	/*
	 * Palettes paths
	 */
	public static final String PALETTE_FOLDER = GAME_FOLDER + "palette/";

	public static final String DEFAULT_PAL = PALETTE_FOLDER + "units.pal";
	public static final String ORANGE_FIRE_PAL = PALETTE_FOLDER + "ofire.pal";
	public static final String BLUE_FIRE_PAL = PALETTE_FOLDER + "blue.pal";
	public static final String GREEN_FIRE_PAL = PALETTE_FOLDER + "gfire.pal";
	public static final String BLUE_EXP_PAL = PALETTE_FOLDER + "bexpl.pal";

	/*
	 * Scenario file path
	 */
	public static final String SCENARIO_CHK = GAME_FOLDER + "scenario.chk";
}
