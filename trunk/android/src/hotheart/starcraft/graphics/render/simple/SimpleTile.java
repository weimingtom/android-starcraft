package hotheart.starcraft.graphics.render.simple;

import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.graphics.render.RenderTile;
import android.graphics.Bitmap;
import android.graphics.Paint;

public class SimpleTile extends RenderTile {
	
	private static final Paint imagePaint = new Paint(); 
	Bitmap img;
	public SimpleTile(Bitmap image)
	{
		img = image;
	}

	@Override
	public void draw(int x, int y) {
		SimpleRender render = (SimpleRender)StarcraftCore.render;
		render.canvas.drawBitmap(img, x, y, imagePaint);
	}

	@Override
	public boolean isRecycled() {
		return img.isRecycled();
	}

	@Override
	public void recycle() {
		img.recycle();	
	}

}
