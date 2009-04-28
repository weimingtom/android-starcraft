/**
 * @author Korshakov Stepan
 * Module for loading Images as a container of some GRPContainers.
 * With layering of them 
 */
package hotheart.starcraft.graphics;

import hotheart.starcraft.configure.BuildParameters;
import hotheart.starcraft.files.DatFile;
import hotheart.starcraft.units.ObjectPool;
import android.graphics.Canvas;

public final class Image {

	public static final int MAX_IMAGE_LAYER = 10000;
	public static final int MIN_IMAGE_LAYER = -10000;

	/*
	 * File format: File is list of arrays(Stupid eclipse brokes formatting)
	 * 
	 * [HEADER] Varcount=14 InputEntrycount=999 OutputEntrycount=999
	 * 
	 * [FORMAT] 0Name=GRP File 0Size=4
	 * 
	 * 1Name=Gfx Turns 1Size=1
	 * 
	 * 2Name=Clickable 2Size=1
	 * 
	 * 3Name=Use Full Iscript 3Size=1
	 * 
	 * 4Name=Draw If Cloaked 4Size=1
	 * 
	 * 5Name=Draw Function 5Size=1
	 * 
	 * 6Name=Remapping 6Size=1
	 * 
	 * 7Name=Iscript ID 7Size=4
	 * 
	 * 8Name=Shield Overlay 8Size=4
	 * 
	 * 9Name=Attack Overlay 9Size=4
	 * 
	 * 10Name=Damage Overlay 10Size=4
	 * 
	 * 11Name=Special Overlay 11Size=4
	 * 
	 * 12Name=Landing Dust Overlay 12Size=4
	 * 
	 * 13Name=Lift-Off Overlay 13Size=4
	 */

	// Data from images.dat file
	private static final int COUNT = 999;

	private static int[] grpFileId;
	private static byte[] gfxTurns;
	// private static byte[] clickable;
	// private static byte[] useFullISCript;
	// private static byte[] drawIfCloacked;
	private static byte[] drawFunc;
	private static byte[] remappingData;
	private static int[] iScriptId;

	// private static int[] shieldOverlay;
	// private static int[] attackOverlay;
	// private static int[] damageOverlay;
	// private static int[] SpecialOverlay;
	// private static int[] landingDustOverlay;
	// private static int[] liftOffOverlay;

	public final static void init(byte[] _data) {
		StarcraftPalette.initPalette();
		initBuffers(_data);
	}

	final static void initBuffers(byte[] buff) {
		DatFile file = new DatFile(buff);
		grpFileId = file.read4ByteData(COUNT, buff);
		gfxTurns = file.read1ByteData(COUNT, buff);
		/* clickable = */file.read1ByteData(COUNT, buff);
		/* useFullISCript = */file.read1ByteData(COUNT, buff);
		/* drawIfCloacked = */file.read1ByteData(COUNT, buff);
		drawFunc = file.read1ByteData(COUNT, buff);
		remappingData = file.read1ByteData(COUNT, buff);
		iScriptId = file.read4ByteData(COUNT, buff);
//		shieldOverlay = file.read4ByteData(COUNT, buff);
//		attackOverlay = file.read4ByteData(COUNT, buff);
//		damageOverlay = file.read4ByteData(COUNT, buff);
//		SpecialOverlay = file.read4ByteData(COUNT, buff);
//		landingDustOverlay = file.read4ByteData(COUNT, buff);
//		liftOffOverlay = file.read4ByteData(COUNT, buff);
	}

	public final static Image getImage(int id, int color, int layer) {
		int grpId = grpFileId[id];
		int scriptId = iScriptId[id];
		int align = gfxTurns[id] & 0xFF;
		int functionId = drawFunc[id] & 0xFF;
		int remapping = remappingData[id] & 0xFF;

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
		this.imageState = new ImageState(this, ovHeader);
		ImageScriptEngine.init(this.imageState);
		currentImageLayer = imageLayer;
	}

	public int imageId;

	public int foregroundColor;

	public int currentImageLayer;

	// Graphics data
	public GRPContainer grp;

	public int graphicsFuntion = 0;
	public int remapping = 0;
	public boolean align = true;

	public int offsetX = 0;
	public int offsetY = 0;

	public Image[] childs = new Image[20];

	// Graphics script data
	public ImageState imageState;
	// Game Data
	public Image parentOverlay = null;
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
			ImageScriptEngine.exec(this.imageState);

		for (int i = 0; i < childs.length; i++)
			if (childs[i] != null)
				childs[i].update();

	}

	public final void play(int anim) {
		if (!deleted)
			ImageScriptEngine.play(this.imageState, anim);

		for (int i = 0; i < childs.length; i++)
			if (childs[i] != null)
				if (childs[i].imageState.followParentAnim)
					childs[i].play(anim);
	}

	public int resX = 0, resY = 0;
	public int sortIndex = 0;

	public final void preDraw(int dX, int dY, int dSortIndex) {
		if (!this.imageState.visible)
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
		if (!imageState.isBlocked)
			if (parentOverlay != null) {
				if (imageState.followParent)
					imageState.baseFrame = parentOverlay.imageState.baseFrame;
				if (imageState.followParentAngle)
					imageState.angle = parentOverlay.imageState.angle;
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
		if (!imageState.visible)
			return;

		if (!imageState.isBlocked)
			if (parentOverlay != null) {
				if (imageState.followParent)
					imageState.baseFrame = parentOverlay.imageState.baseFrame;
				if (imageState.followParentAngle)
					imageState.angle = parentOverlay.imageState.angle;
			}

		for (int i = 0; i < childs.length; i++)
			if (childs[i] != null)
				childs[i].draw(c, dX, dY);

		resX = dX;
		resY = dY;
		drawWithoutChilds(c);

	}
}
