package hotheart.starcraft.system;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class MapPreview extends View {

	private static final int SIDE = 96;
	
	Bitmap image = null;
	
	int vertOfs = 0;
	float resize = 1.0f;

	
	public MapPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setBitmap(Bitmap img)
	{
		image = img;
		
		int imageMaxSide = Math.max(image.getWidth(), image.getHeight());
		
		if (imageMaxSide > SIDE)
			resize = (float)SIDE/(float)imageMaxSide;
		else
			resize = 1.0f;
		
		vertOfs = (SIDE -  (int)(image.getHeight()*resize)) >> 1;
		
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (image == null)
			canvas.drawARGB(255, 255, 0, 0);
		else
		{
			Rect src = new Rect(0,0,image.getWidth(), image.getHeight());
			RectF dst = new RectF(0,vertOfs, image.getWidth()*resize,image.getHeight()*resize);
			canvas.drawBitmap(image, src, dst, new Paint());
		}
		
	}
}
