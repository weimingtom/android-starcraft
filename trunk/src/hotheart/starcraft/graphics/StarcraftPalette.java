package hotheart.starcraft.graphics;

import hotheart.starcraft.configure.FilePaths;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Color;

public class StarcraftPalette {

	/*
	 * Palettes
	 */

	// Default palette
	public static int[] normalPalette;

	public static int[] blendedPalette;

	// Team palettes
	public static int[] redPalette;
	public static int[] greenPalette;
	public static int[] bluePalette;

	// Selection circle palette
	public static int[] selectPalette;

	// Effect palettes
	public static int[] shadowPalette;
	public static int[] ofirePalette;
	public static int[] gfirePalette;
	public static int[] bfirePalette;
	public static int[] bexplPalette;

	/*
	 * Algorithm for team colors: 1) copy default palette - normalPalette ->
	 * result 2) for each result[indexes[i]] <- teamColor * alpha[i]
	 */

	// Indexes for replacing some colors to team colors
	private static final short[] indexes = { 8, 9, 10, 11, 12, 13, 14, 15 };
	// Alpha values for replacing some colors to team colors
	private static final float[] alpha = { 254.0f / 255.0f, 222.0f / 255.0f,
			189.0f / 255.0f, 156.0f / 255.0f, 124.0f / 255.0f, 91.0f / 255.0f,
			58.0f / 255.0f, 25.0f / 255.0f };

	// Function for creating palettes for each team color
	private final static int[] createTeamPalette(int color) {
		int[] res = new int[256];
		for (int i = 0; i < res.length; i++)
			res[i] = normalPalette[i];

		int dR = Color.red(color);
		int dG = Color.green(color);
		int dB = Color.blue(color);
		for (int i = 0; i < indexes.length; i++) {
			int R = (int) (dR * alpha[i]);
			int G = (int) (dG * alpha[i]);
			int B = (int) (dB * alpha[i]);
			res[indexes[i]] = (255 << 24) + (R << 16) + (G << 8) + B;
		}

		return res;
	}

	private final static void initDefPalette() {

		// We can't use loadEffectPaletteFromFile func because of here is 3-byte
		// RGB color, but loadEffectPaletteFromFile used for 4-byte ARGB
		// palettes
		if (normalPalette == null)
			try {
				FileInputStream is = new FileInputStream(FilePaths.DEFAULT_PAL);
				byte[] tmp = new byte[is.available()];
				is.read(tmp);
				is.close();

				normalPalette = new int[256];
				for (int i = 0; i < 256; i++) {
					normalPalette[i] = (255 << 24) + (tmp[i * 3] << 16)
							+ (tmp[i * 3 + 1] << 8) + tmp[i * 3 + 2];
				}

				blendedPalette = new int[256];
				for (int i = 0; i < 256; i++) {
					blendedPalette[i] = normalPalette[i];
				}

				for (int i = 0; i < indexes.length; i++) {
					int A = (int) (255 * alpha[i]);
					int R = (int) (255 * alpha[i]);
					int G = (int) (255 * alpha[i]);
					int B = (int) (255 * alpha[i]);
					blendedPalette[indexes[i]] = (A << 24) + (R << 16)
							+ (G << 8) + B;
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	private static final void initTeamPalette() {
		if (redPalette == null)
			redPalette = createTeamPalette(Color.RED);
		if (greenPalette == null)
			greenPalette = createTeamPalette(Color.GREEN);
		if (bluePalette == null)
			bluePalette = createTeamPalette(Color.BLUE);
	}

	private static final void initShadowPalette() {
		if (shadowPalette == null) {
			shadowPalette = new int[256];
			for (int i = 0; i < 256; i++) {
				shadowPalette[i] = (127 << 24) + (127 << 16) + (127 << 8) + 127;
			}
		}
	}

	private static final void initSelectionPalette() {
		if (selectPalette == null) {
			selectPalette = new int[256];
			for (int i = 0; i < 256; i++) {
				selectPalette[i] = (255 << 24) + (0 << 16) + (255 << 8) + 0;
			}
		}
	}

	private static final int[] loadEffectPaletteFromFile(String fileName) {
		try {
			FileInputStream is = new FileInputStream(fileName);
			byte[] tmp = new byte[is.available()];
			is.read(tmp);
			is.close();

			int[] res = new int[256];

			for (int i = 0; i < 256; i++) {
				res[i] = (tmp[i * 4] << 24) + (tmp[i * 4 + 1] << 16)
						+ (tmp[i * 4 + 2] << 8) + tmp[i * 3 + 3];
			}

			return res;

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static final void initEffectPalette() {

		if (ofirePalette == null)
			ofirePalette = loadEffectPaletteFromFile(FilePaths.ORANGE_FIRE_PAL);
		if (gfirePalette == null)
			gfirePalette = loadEffectPaletteFromFile(FilePaths.GREEN_FIRE_PAL);
		if (bfirePalette == null)
			bfirePalette = loadEffectPaletteFromFile(FilePaths.BLUE_FIRE_PAL);
		if (bexplPalette == null)
			bexplPalette = loadEffectPaletteFromFile(FilePaths.BLUE_EXP_PAL);

	}

	public final static void initPalette() {

		initDefPalette();
		initTeamPalette();
		initShadowPalette();
		initSelectionPalette();
		initEffectPalette();
	}

	public final static int[] getImagePalette(int graphicsFunction,
			int remappingFunc, int foregroundColor) {
		int[] pal = normalPalette;
		// byte[] pal = redPalette;
		if (graphicsFunction == 10)
			pal = shadowPalette;
		else if (graphicsFunction == 9) {
			switch (remappingFunc) {
			case 1:
				pal = ofirePalette;
				break;
			case 2:
				pal = gfirePalette;
				break;
			case 3:
				pal = bfirePalette;
				break;
			case 4:
				pal = bexplPalette;
				break;
			}
		} else if (graphicsFunction == 13)// WTF?! must be 11
			pal = selectPalette;
		else
			switch (foregroundColor) {
			case TeamColors.COLOR_RED:
				pal = redPalette;
				break;
			case TeamColors.COLOR_GREEN:
				pal = greenPalette;
				break;
			case TeamColors.COLOR_BLUE:
				pal = bluePalette;
				break;
			}

		return pal;
	}
}
