package hotheart.starcraft.graphics.utils;

import hotheart.starcraft.graphics.Image;
import hotheart.starcraft.graphics.TeamColors;

public class SelectionCircles {
	public static int[] selCircleSize = new int[] { 22, 32, 46, 62, 72, 94,
			110, 122, 146, 224, 22, 32, 46, 62, 72, 94, 110, 122, 146, 224 };
	private static final int FIRST_CIRCLE_IMAGE = 561;
	public static final int DEFAULT_LAYER = 15;

	public static Image[] selCircles;

	public static final void initCircles() {
		selCircles = new Image[20];
		for (int i = 0; i < 20; i++) {
			selCircles[i] = Image.getImage(i + FIRST_CIRCLE_IMAGE,
					TeamColors.COLOR_GREEN, Image.MAX_IMAGE_LAYER);
		}
	}
}
