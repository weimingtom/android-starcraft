/**
 * @author Korshakov Stepan
 * Module for loading Images as a container of some GRPContainers.
 * With layering of them 
 */
package hotheart.starcraft.graphics;

import hotheart.starcraft.configure.BuildParameters;
import hotheart.starcraft.files.DatFile;
import hotheart.starcraft.graphics.script.ImageScriptEngine;
import hotheart.starcraft.graphics.script.ImageScriptHeader;
import hotheart.starcraft.graphics.script.ImageState;
import hotheart.starcraft.units.ObjectPool;
import android.graphics.Canvas;

public class Image {

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

	public static void init(byte[] _data) {
		StarcraftPalette.initPalette();
		initBuffers(_data);
	}

	final static void initBuffers(byte[] buff) {
		DatFile file = new DatFile(buff);
		grpFileId = file.read4ByteData(COUNT, buff);
		gfxTurns = file.read1ByteData(COUNT, buff);

		// Seek unused sections
		file.seekFromCurrentPos(COUNT * 3);
		// clickable = file.read1ByteData(COUNT, buff);
		// useFullISCript = file.read1ByteData(COUNT, buff);
		// drawIfCloacked = file.read1ByteData(COUNT, buff);

		drawFunc = file.read1ByteData(COUNT, buff);
		remappingData = file.read1ByteData(COUNT, buff);
		iScriptId = file.read4ByteData(COUNT, buff);

		// Unused sections
		// shieldOverlay = file.read4ByteData(COUNT, buff);
		// attackOverlay = file.read4ByteData(COUNT, buff);
		// damageOverlay = file.read4ByteData(COUNT, buff);
		// SpecialOverlay = file.read4ByteData(COUNT, buff);
		// landingDustOverlay = file.read4ByteData(COUNT, buff);
		// liftOffOverlay = file.read4ByteData(COUNT, buff);
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

	public Image(Image src) {
		this.align = src.align;
		this.childCount = src.childCount;
		this.childs = src.childs;
		this.currentImageLayer = src.currentImageLayer;
		this.deleted = src.deleted;
		this.foregroundColor = src.foregroundColor;
		this.graphicsFuntion = src.graphicsFuntion;
		this.grp = src.grp;
		this.imageId = src.imageId;
		this.imageState = new ImageState(src.imageState);
		this.imageState.image = this;
		this.offsetX = src.offsetX;
		this.offsetY = src.offsetY;
		this.parentOverlay = src.parentOverlay;
		this.posX = src.posX;
		this.posY = src.posY;
		this.remapping = src.remapping;
		this.sortIndex = src.sortIndex;
	}

	public int imageId;

	public int foregroundColor;

	public int currentImageLayer;

	// Graphics data
	private GRPContainer grp;

	private int graphicsFuntion = 0;
	private int remapping = 0;
	private boolean align = true;

	// Offset from main offset

	protected int offsetX = 0;
	protected int offsetY = 0;

	public void setOffset(int dx, int dy) {
		offsetX = dx;
		offsetY = dy;
	}

	public void setOffsetX(int dx) {
		offsetX = dx;
	}

	public void setOffsetY(int dy) {
		offsetY = dy;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	// Global positions
	protected int posX = 0;
	protected int posY = 0;

	public void setPos(int dx, int dy) {
		posX = dx;
		posY = dy;
	}

	public void setPosX(int dx) {
		posX = dx;
	}

	public void setPosY(int dy) {
		posY = dy;
	}

	// Children data
	public Image[] childs = new Image[20];

	// Graphics script data
	public ImageState imageState;
	// Game Data
	public Image parentOverlay = null;

	public boolean deleted = false;

	public int childCount = 0;

	public void delete() {

		deleted = true;

		if (childCount == 0) {
			if ( (!(this instanceof Sprite)) && (parentOverlay != null)) {
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

	public void update() {
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

	public int sortIndex = 0;

	public void preDraw(int dX, int dY, int dSortIndex) {
		if (!this.imageState.visible)
			return;

		setPos(dX, dY);

		sortIndex = dSortIndex;
		ObjectPool.drawObjects.add(this);

		for (int i = 0; i < childs.length; i++)
			if (childs[i] != null)
				childs[i].preDraw(dX, dY, dSortIndex);
	}

	public void drawWithoutChilds(Canvas c) {
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

			// c, this, pal,
			grp.draw(posX + offsetX, posY + offsetY, this.align,
					imageState.baseFrame, imageState.angle, pal, c);
		}
	}

	public void draw(Canvas c, int dX, int dY) {
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

		setPos(dX, dY);
		drawWithoutChilds(c);
	}
}
