package hotheart.starcraft.graphics;

public class ImageState {
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
