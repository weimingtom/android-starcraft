package hotheart.starcraft.controller;

import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.graphics.IconFactory;
import hotheart.starcraft.map.Map;
import hotheart.starcraft.system.MapPreview;
import hotheart.starcraft.units.Unit;
import hotheart.starcraft.units.target.AbstractTarget;
import hotheart.starcraft.units.target.StaticPointTarget;
import hotheart.starcraft.units.target.UnitTarget;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
	Button mapMoveButton = null;

	int selectedButton = -1;

	public View getRenderView() {
		if (renderView == null) {
			renderView = _getRenderView();
			renderView.setOnTouchListener(this);
			setScrollPosXY(mx, my);
		}
		return renderView;
	}

	public void onUnitSelectChanged() {
		selectedButton = -1;
		for (int i = 0; i < buttons.length; i++)
			buttons[i].setEnabled(true);
	}

	public void setUI(MapPreview mapPrev, ImageButton[] unitControls,
			Button mapMove) {
		buttons = unitControls;
		previewView = mapPrev;
		mapMoveButton = mapMove;
		initUI();
		onUnitSelectChanged();
	}

	private void initUI() {
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setTag((Integer) i);
			buttons[i].setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Integer index = (Integer) v.getTag();

					Unit selectedUnit = StarcraftCore.context.majorSelectedUnit;
					if (selectedUnit.controlPanel.orders[index].isTargeting) {

						for (int i = 0; i < buttons.length; i++)
							buttons[i].setEnabled(true);
						
						mapMoveButton.setEnabled(true);

						buttons[index].setEnabled(false);

						selectedButton = index;
					}
					else
					{
						for (int i = 0; i < buttons.length; i++)
							buttons[i].setEnabled(true);
						
						mapMoveButton.setEnabled(true);
						
						selectedButton = -1;
						selectedUnit.controlPanel.executeButton(index);
					}
				}
			});
		}
		
		mapMoveButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				for (int i = 0; i < buttons.length; i++)
					buttons[i].setEnabled(true);
				
				selectedButton = -1;
				
				mapMoveButton.setEnabled(false);
			}
		});
		

		if (StarcraftCore.context.majorSelectedUnit == null) {
			for (int i = 0; i < 9; i++)
				setControlIcon(i, -1);
		} else {
			int[] buttons = getButtons(StarcraftCore.context.majorSelectedUnit);
			for (int i = 0; i < buttons.length; i++)
				setControlIcon(i, buttons[i]);
		}
	}

	public int[] getButtons(Unit u) {
		int[] res = new int[9];
		for (int i = 0; i < 9; i++)
			res[i] = -1;

		for (int i = 0; i < u.controlPanel.orders.length; i++) {
			res[i] = u.controlPanel.orders[i].iconId;
		}

		return res;
	}

	public void setControlIcon(int buttonId, int iconId) {
		ImageButton btn = buttons[buttonId];
		if (btn == null)
			return;

		if (iconId == -1)
			btn.setVisibility(View.INVISIBLE);
		else {
			btn.setVisibility(View.VISIBLE);
			btn.setImageBitmap(IconFactory.getIcon(iconId));
		}
	}

	public void setScrollPosXY(int x, int y) {
		mx = x;
		my = y;

		_setPosXY(x, y);
	}

	public int getScrollX() {
		return mx;
	}

	public int getScrollY() {
		return my;
	}

	int oldX = 0, oldY = 0;

	public boolean onTouch(View v, MotionEvent event) {
		if (selectedButton == -1) {

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

					setScrollPosXY(mx, my);
				} catch (Exception e) {
				}
			}
			return true;
		} else {
			int mapX = (int) event.getX() + getScrollX();
			int mapY = (int) event.getY() + getScrollY();

			Unit selectedUnit = StarcraftCore.context.majorSelectedUnit;

			Unit dest = StarcraftCore.context.PickUnit(mapX, mapY);
			AbstractTarget target = new StaticPointTarget(mapX, mapY);
			if (dest != null)
				target = new UnitTarget(dest);

			selectedUnit.controlPanel.executeButton(selectedButton, target);

			return true;
		}
	}
}
