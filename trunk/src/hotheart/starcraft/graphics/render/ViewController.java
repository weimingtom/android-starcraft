package hotheart.starcraft.graphics.render;

import hotheart.starcraft.map.Map;
import android.view.MotionEvent;
import android.view.View;

public abstract class ViewController implements View.OnTouchListener {
	
	private int mx = 56 * 32, my = 56 * 32;
	private boolean isScrolling = false;
	
	View cached = null;
	protected abstract View _getView();
	public View getView()
	{
		if (cached == null)
		{
			cached = _getView();
			cached.setOnTouchListener(this);
			setPosXY(mx, my);
		}
		return cached;
	}
	
	int oldX = 0, oldY = 0;
	public boolean onTouch(View v, MotionEvent event)
	{
		if (!isScrolling)
			return false;
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			oldX = (int) event.getX();
			oldY = (int) event.getY();
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			try {
				int dx = (int) (oldX - event.getX());
				int dy = (int) (oldY - event.getY());

				oldX = (int) event.getX();
				oldY = (int) event.getY();

				mx += dx;
				my += dy;
				
				setPosXY(mx, my);
			} catch (Exception e) {
			}
		}
		return true;
	}
	
	public abstract void setPosXY(int x, int y);
	public abstract void setMap(Map map);
	
	public boolean isMapScroll()
	{
		return isScrolling;
	}
	public void setMapScrollingState(boolean isScroll)
	{
		this.isScrolling = isScroll;
	}
	
	
}
