package hotheart.starcraft.system;

import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.graphics.TeamColors;
import hotheart.starcraft.units.Unit;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class MapPreview extends View {

	private static final int SIDE = 96;

	Bitmap image = null;

	int vertOfs = 0;
	float resize = 1.0f;

	int selX = 0, selY = 0;

	public MapPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSelectionPos(int x, int y) {
		selX = x;
		selY = y;
	}

	public void setBitmap(Bitmap img) {
		image = img;

		int imageMaxSide = Math.max(image.getWidth(), image.getHeight());

		if (imageMaxSide > SIDE)
			resize = (float) SIDE / (float) imageMaxSide;
		else
			resize = 1.0f;

		vertOfs = (SIDE - (int) (image.getHeight() * resize)) >> 1;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		// Map Image

		if (image == null)
			canvas.drawARGB(255, 255, 0, 0);
		else {
			Rect src = new Rect(0, 0, image.getWidth(), image.getHeight());
			RectF dst = new RectF(0, vertOfs, image.getWidth() * resize, image
					.getHeight()
					* resize);
			canvas.drawBitmap(image, src, dst, new Paint());
		}

		// Units

		Paint unitPaint = new Paint();
		for (Unit u : StarcraftCore.context.units) {
			int color = Color.GRAY;
			
			switch (u.foregroundColor) {
			case TeamColors.COLOR_GREEN:
				color = Color.GREEN;
				break;
			case TeamColors.COLOR_BLUE:
				color = Color.BLUE;
				break;
			case TeamColors.COLOR_RED:
				color = Color.RED;
				break;
			}
			
			int x = u.getPosX()/32;
			int y = u.getPosX()/32;
			
			unitPaint.setColor(color);
			
			canvas.drawPoint(x*resize, y*resize, unitPaint);
		}

		// Selected region

		RectF rect = new RectF(selX * resize, selY * resize, (selX + 480 / 32)
				* resize, (selY + 320 / 32) * resize);

		Paint p = new Paint();

		p.setColor(Color.GREEN);
		p.setStyle(Style.STROKE);

		canvas.drawRect(rect, p);
		
		invalidate();
	}
}
