package hotheart.starcraft.controller;

import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.graphics.IconFactory;
import hotheart.starcraft.map.Map;
import hotheart.starcraft.system.MapPreview;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public abstract class ViewController implements View.OnTouchListener {

	protected abstract View _getRenderView();

	protected abstract void _setPosXY(int x, int y);

	public abstract void setMap(Map map);

	private int mx = 56 * 32, my = 50 * 32;
	private boolean isScrolling = false;

	View renderView = null;
	
	MapPreview previewView = null;
	
	ImageButton buttons[] = null;

	public View getRenderView() {
		if (renderView == null) {
			renderView = _getRenderView();
			renderView.setOnTouchListener(this);
			setPosXY(mx, my);
		}
		return renderView;
	}

	public void setMapPreview(MapPreview view) {
		previewView = view;
	}
	
	public void setControlButtons(ImageButton _b11, ImageButton _b21, ImageButton _b31,
			ImageButton _b12, ImageButton _b22, ImageButton _b32,
			ImageButton _b13, ImageButton _b23, ImageButton _b33)
	{
		buttons = new ImageButton[] {_b11, _b21, _b31, _b12, _b22, _b32, _b13, _b23, _b33};
	}

	public void setControlIcon(int buttonId, int iconId)
	{
		ImageButton btn =buttons[buttonId];
		if (btn == null)
			return;
		
		if (iconId == -1)
			btn.setVisibility(View.INVISIBLE);
		else
		{
			btn.setVisibility(View.VISIBLE);
			btn.setImageBitmap(IconFactory.getIcon(iconId));
		}
	}
	
	public void setControlIcons(int[] ids)
	{
		for(int i = 0; i<ids.length; i++)
			setControlIcon(i,ids[i]);
	}
	
	public void setPosXY(int x, int y) {
		mx = x;
		my = y;
		
		_setPosXY(x, y);
	}

	public int getX() {
		return mx;
	}

	public int getY() {
		return my;
	}

	public boolean isMapScroll() {
		return isScrolling;
	}

	public void setMapScrollingState(boolean isScroll) {
		this.isScrolling = isScroll;
	}

	int oldX = 0, oldY = 0;

	public boolean onTouch(View v, MotionEvent event) {
		if (isScrolling) {

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
		} else {
			int mapX = (int) event.getX() + getX();
			int mapY = (int) event.getY() + getY();
			StarcraftCore.gameController.onClick(mapX, mapY);
			return true;
		}
	}
}
