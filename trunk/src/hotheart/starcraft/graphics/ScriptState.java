package hotheart.starcraft.graphics;

public class ScriptState {
	public ScriptState (Image parent, ImageScriptHeader header)
	{
		image = parent;
		scriptHeader = header;
	}
	
	
	public int scriptPos = 0;
	public int scriptWait = 0;
	public ImageScriptHeader scriptHeader;
	public int gotoLine = -1;
	public boolean isBlocked = false;
	public boolean isPaused = false;
	public int returnLine = 0;
	
	public int baseFrame = 0;
	public boolean align = true;
	public boolean visible = true;
	
	
	public boolean followParent = false;
	public boolean followParentAnim = false;
	public boolean followParentAngle = false;
	
	public int angle = 0;
	
	public Image image;
}
