package hotheart.starcraft.controller;

import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.map.Map;
import hotheart.starcraft.system.MapPreview;
import android.view.MotionEvent;
import android.view.View;

public abstract class ViewController implements View.OnTouchListener {

	protected abstract View _getView();

	protected abstract void _setPosXY(int x, int y);

	public abstract void setMap(Map map);

	private int mx = 56 * 32, my = 56 * 32;
	private boolean isScrolling = false;

	View cached = null;

	public View getView() {
		if (cached == null) {
			cached = _getView();
			cached.setOnTouchListener(this);
			setPosXY(mx, my);
		}
		return cached;
	}

	MapPreview previewView = null;

	public void setMapPreview(MapPreview view) {
		previewView = view;

	}

	public void setPosXY(int x, int y) {
		mx = x;
		my = y;
		
		_setPosXY(x, y);

		if (previewView != null)
			previewView.setSelectionPos(x / 32, y / 32);
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
