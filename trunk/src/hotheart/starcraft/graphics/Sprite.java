package hotheart.starcraft.graphics;

import hotheart.starcraft.units.Flingy;
import hotheart.starcraft.units.ObjectPool;
import android.graphics.Canvas;

public class Sprite extends Image {

	public Sprite(Sprite src) {
		super(src);

		this.selCircle = src.selCircle;
		this.healthBar = src.healthBar;
		this.vertPos = src.vertPos;
		this.currentImageLayer = src.currentImageLayer;
		this.isVisible = src.isVisible;
		this.isSelectable = src.isSelectable;
	}

	private Sprite(Image src) {
		super(src);
	}

	private static byte[] sprites;
	private static int count;

	public static void init(byte[] arr) {
		sprites = arr;
		count = (sprites.length - 130 * 4) / 7 + 130;
	}

	// TODO: Add isVisible and isSelectable
	public static final Sprite getSprite(int id, int color, int layer) {
		int imageFile = (sprites[id * 2] & 0xFF)
				+ ((sprites[id * 2 + 1] & 0xFF) << 8);

		int healthBar = 0;
		if (id >= 130)
			healthBar = (sprites[(id - 130) + count * 2] & 0xFF);

		int selCircle = 0;
		if (id >= 130)
			selCircle = (sprites[(id - 130) + count * 4 + (count - 130) * 1] & 0xFF);

		int vertOffset = 0;
		if (id >= 130)
			vertOffset = (sprites[(id - 130) + count * 4 + (count - 130) * 2] & 0xFF);

		Sprite res = new Sprite(Image.getImage(imageFile, color, layer));
		res.healthBar = healthBar;
		res.selCircle = selCircle;
		res.vertPos = vertOffset;
		res.currentImageLayer = layer;
		return res;
	}

	public int selCircle = 0;
	public int healthBar = 6;
	public int vertPos = 9;

	public int currentImageLayer;

	public boolean isVisible = true;
	public boolean isSelectable = true;

	public void kill() {
		super.kill();

		// TODO Do something with this!
		ObjectPool.removeImage(this);
	}
}
