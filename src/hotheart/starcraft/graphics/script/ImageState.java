package hotheart.starcraft.graphics.script;

import hotheart.starcraft.graphics.Image;

public class ImageState {
	
	public ImageState(ImageState src)
	{
		this.angle = src.angle;
		this.baseFrame = src.baseFrame;
		this.followParent = src.followParent;
		this.followParentAngle = src.followParentAngle;
		this.followParentAnim = src.followParentAnim;
		this.gotoLine = src.gotoLine;
		this.image = src.image;
		this.isBlocked = src.isBlocked;
		this.isPaused = src.isPaused;
		this.returnLine = src.returnLine;
		this.scriptHeader = src.scriptHeader;
		this.scriptPos = src.scriptPos;
		this.scriptWait = src.scriptWait;
		this.visible = src.visible;
	}
	
	public ImageState(Image parent, ImageScriptHeader header) {
		image = parent;
		scriptHeader = header;
	}

	// Script execution parameters
	public int scriptPos = 0;
	public int scriptWait = 0;
	public ImageScriptHeader scriptHeader;
	public int gotoLine = -1;
	public boolean isBlocked = false;
	public boolean isPaused = false;
	public int returnLine = 0;

	// Image rendering parameters
	public int baseFrame = 0;
	public boolean visible = true;

	public boolean followParent = false;
	public boolean followParentAnim = false;
	public boolean followParentAngle = false;

	// TODO Hide this and add metod for changing this
	public int angle = 0;

	// TODO hide this and add metods for adding children
	public Image image;
}
