/**
 * @author Korshakov Stepan
 * Module for loading Images as a container of some GRPContainers.
 * With layering of them 
 */
package hotheart.starcraft.graphics;

import hotheart.starcraft.configure.BuildParameters;
import hotheart.starcraft.units.ObjectPool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Canvas;
import android.graphics.Color;

public final class Image {

	public static final int MAX_IMAGE_LAYER = 10000;
	public static final int MIN_IMAGE_LAYER = -10000;

	// Data from images.tbl file
	private static int count; // number of elements
	private static byte[] data; // elements of tbl-file

	public final static void init(byte[] _data) {
		data = _data;
		count = data.length / 38;

		StarcraftPalette.initPalette();
	}

	public final static Image getImage(int id, int color, int layer) {
		int grpId = (data[id * 4] & 0xFF) + ((data[id * 4 + 1] & 0xFF) << 8)
				+ ((data[id * 4 + 2] & 0xFF) << 16)
				+ ((data[id * 4 + 3] & 0xFF) << 24);

		int scriptId = (data[id * 4 + count * 10] & 0xFF)
				+ ((data[id * 4 + count * 10 + 1] & 0xFF) << 8)
				+ ((data[id * 4 + count * 10 + 2] & 0xFF) << 16)
				+ ((data[id * 4 + count * 10 + 3] & 0xFF) << 24);

		int align = (data[id + count * 4] & 0xFF);

		int functionId = (data[id + count * 8] & 0xFF);
		int remapping = (data[id + count * 9] & 0xFF);

		Image res = new Image(new GRPContainer(grpId), ImageScriptEngine
				.createHeader(scriptId), id, layer);

		int[] pal = StarcraftPalette.normalPalette;
		// byte[] pal = redPalette;
		if (functionId == 10)
			pal = StarcraftPalette.shadowPalette;
		else if (functionId == 9) {
			switch (remapping) {
			case 1:
				pal = StarcraftPalette.ofirePalette;
				break;
			case 2:
				pal = StarcraftPalette.gfirePalette;
				break;
			case 3:
				pal = StarcraftPalette.bfirePalette;
				break;
			case 4:
				pal = StarcraftPalette.bexplPalette;
				break;
			}
		}

		if (BuildParameters.CACHE_GRP)
			res.grp.image.makeCache(pal);
		res.align = align == 1;
		res.graphicsFuntion = functionId;
		res.remapping = remapping;
		res.foregroundColor = color;
		return res;
	}

	public Image(GRPContainer ovGRP, ImageScriptHeader ovHeader, int id,
			int imageLayer) {
		imageId = id;
		this.grp = ovGRP;
		this.scriptHeader = ovHeader;
		ImageScriptEngine.init(this);
		currentImageLayer = imageLayer;
	}

	public int imageId;

	public int foregroundColor;

	public int currentImageLayer;

	// Graphics data
	public int baseFrame = 0;
	public boolean align = true;
	public boolean visible = true;
	public GRPContainer grp;

	public int graphicsFuntion = 0;
	public int remapping = 0;

	public int offsetX = 0;
	public int offsetY = 0;

	public Image[] childs = new Image[20];

	public boolean followParent = false;
	public boolean followParentAnim = false;
	public boolean followParentAngle = false;

	// Graphics script data
	public int scriptPos = 0;
	public int scriptWait = 0;
	public ImageScriptHeader scriptHeader;
	public int gotoLine = -1;
	public boolean isBlocked = false;
	public boolean isPaused = false;
	public int returnLine = 0;

	// Game Data
	public Image parentOverlay = null;
	public int angle = 0;
	public Sprite sprite = null;

	public boolean deleted = false;

	public int childCount = 0;

	public final void delete() {

		deleted = true;

		if (childCount == 0) {
			if (sprite != null) {
				if (sprite.image == this)
					sprite.delete();
			} else if (parentOverlay != null) {
				parentOverlay.removeChild(this);
			}
		}
	}

	public final void addOverlay(Image img) {
		for (int i = 0; i < childs.length; i++)
			if (childs[i] == null) {
				childs[i] = img;
				childCount++;
				return;
			}

	}

	public final void addUnderlay(Image img) {
		for (int i = 0; i < childs.length; i++)
			if (childs[i] == null) {
				childCount++;
				childs[i] = img;
				return;
			}
	}

	public final void removeChild(Image img) {
		for (int i = 0; i < childs.length; i++)
			if (childs[i] == img) {
				childs[i] = null;
				childCount--;
				break;
			}
		if ((deleted) & (childCount == 0))
			delete();
	}

	public final void update() {
		if (!deleted)
			ImageScriptEngine.exec(this);

		for (int i = 0; i < childs.length; i++)
			if (childs[i] != null)
				childs[i].update();

	}

	public final void play(int anim) {
		if (!deleted)
			ImageScriptEngine.play(this, anim);

		for (int i = 0; i < childs.length; i++)
			if (childs[i] != null)
				if (childs[i].followParentAnim)
					childs[i].play(anim);
	}

	public int resX = 0, resY = 0;
	public int sortIndex = 0;

	public final void preDraw(int dX, int dY, int dSortIndex) {
		if (!this.visible)
			return;
		resX = dX;
		resY = dY;
		sortIndex = dSortIndex;
		ObjectPool.drawObjects.add(this);

		for (int i = 0; i < childs.length; i++)
			if (childs[i] != null)
				childs[i].preDraw(dX, dY, dSortIndex);
	}

	public final void drawWithoutChilds(Canvas c) {
		if (!isBlocked)
			if (parentOverlay != null) {
				if (followParent)
					this.baseFrame = parentOverlay.baseFrame;
				if (followParentAngle)
					this.angle = parentOverlay.angle;
			}

		if (!deleted) {
			int[] pal = StarcraftPalette.normalPalette;
			// byte[] pal = redPalette;
			if (graphicsFuntion == 10)
				pal = StarcraftPalette.shadowPalette;
			else if (graphicsFuntion == 9) {
				switch (remapping) {
				case 1:
					pal = StarcraftPalette.ofirePalette;
					break;
				case 2:
					pal = StarcraftPalette.gfirePalette;
					break;
				case 3:
					pal = StarcraftPalette.bfirePalette;
					break;
				case 4:
					pal = StarcraftPalette.bexplPalette;
					break;
				}
			} else if (graphicsFuntion == 13)// WTF?! must be 11
				pal = StarcraftPalette.selectPalette;
			else
				switch (foregroundColor) {
				case TeamColors.COLOR_RED:
					pal = StarcraftPalette.redPalette;
					break;
				case TeamColors.COLOR_GREEN:
					pal = StarcraftPalette.greenPalette;
					break;
				case TeamColors.COLOR_BLUE:
					pal = StarcraftPalette.bluePalette;
					break;
				}

			grp.draw(c, this, pal, offsetX + resX, offsetY + resY);
		}
	}

	public final void draw(Canvas c, int dX, int dY) {
		if (!this.visible)
			return;

		if (!isBlocked)
			if (parentOverlay != null) {
				if (followParent)
					this.baseFrame = parentOverlay.baseFrame;
				if (followParentAngle)
					this.angle = parentOverlay.angle;
			}

		for (int i = 0; i < childs.length; i++)
			if (childs[i] != null)
				childs[i].draw(c, dX, dY);

		resX = dX;
		resY = dY;
		drawWithoutChilds(c);

	}
}
