package hotheart.starcraft.graphics;

import java.io.FileInputStream;
import java.io.IOException;

import hotheart.starcraft.files.DatFile;

public class Sprite extends Image {

	public Sprite(Sprite src) {
		super(src);

		this.selCircle = src.selCircle;
		this.healthBar = src.healthBar;
		this.vertPos = src.vertPos;
		this.isVisible = src.isVisible;
		this.isSelectable = src.isSelectable;
	}

	private Sprite(Image src) {
		super(src);
	}

	public static class Factory {
		private static int[] imageFiles;
		private static byte[] healthBars;
		private static byte[] selCircleImage;
		private static byte[] selCircleOffset;

		private static final int COUNT = 517;
		private static final int UNSELECTABLE_COUNT = 130;

		public static void initSprites(FileInputStream _is) throws IOException {
			initBuffers(_is);
		}

		private static void initBuffers(FileInputStream _is) throws IOException {
			DatFile reader = new DatFile(_is);
			imageFiles = reader.read2ByteData(COUNT);
			healthBars = reader.read1ByteData(COUNT - UNSELECTABLE_COUNT);
			reader.skip(COUNT);// Unknown
			reader.skip(COUNT);// Is Visible
			selCircleImage = reader.read1ByteData(COUNT - UNSELECTABLE_COUNT);
			selCircleOffset = reader.read1ByteData(COUNT - UNSELECTABLE_COUNT);
		}

		// TODO: Add isVisible and isSelectable
		public static final Sprite getSprite(int id, int color, int layer) {

			int imageFile = imageFiles[id];

			int healthBar = 0;
			if (id >= UNSELECTABLE_COUNT)
				healthBar = healthBars[id - UNSELECTABLE_COUNT] & 0xFF;

			int selCircle = 0;
			if (id >= UNSELECTABLE_COUNT)
				selCircle = selCircleImage[id - UNSELECTABLE_COUNT] & 0xFF;

			int vertOffset = 0;
			if (id >= UNSELECTABLE_COUNT)
				vertOffset = selCircleOffset[id - UNSELECTABLE_COUNT] & 0xFF;

			Sprite res = new Sprite(Image.Factory.getImage(imageFile, color, layer));
			res.healthBar = healthBar;
			res.selCircle = selCircle;
			res.vertPos = vertOffset;
			return res;
		}
	}

	public int selCircle = 0;
	public int healthBar = 6;
	public int vertPos = 9;

	public boolean isVisible = true;
	public boolean isSelectable = true;

}
