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
	
	ImageButton b11 = null;
	ImageButton b21 = null;
	ImageButton b31 = null;
	
	ImageButton b12 = null;
	ImageButton b22 = null;
	ImageButton b32 = null;
	
	ImageButton b13 = null;
	ImageButton b23 = null;
	ImageButton b33 = null;

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
		b11 = _b11;
		b21 = _b21;
		b31 = _b31;
		
		b12 = _b12;
		b22 = _b22;
		b32 = _b32;
		
		b13 = _b13;
		b23 = _b23;
		b33 = _b33;
	}

	private ImageButton getButton(int id)
	{
		switch(id)
		{
			case 0:
				return b11;
			case 1:
				return b21;
			case 2:
				return b31;
				
			case 3:
				return b12;
			case 4:
				return b22;
			case 5:
				return b32;
				
			case 6:
				return b13;
			case 7:
				return b23;
			case 8:
				return b33;
		}
		
		return null;
	}
	public void setControlIcon(int buttonId, int iconId)
	{
		ImageButton btn = getButton(buttonId);
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
