package hotheart.starcraft.graphics.render;

public abstract class RenderTile {
	public abstract void draw(int x, int y);

	public abstract void recycle();
	public abstract boolean isRecycled();
}
