package hotheart.starcraft.graphics.render.simple;

import android.content.Context;
import android.view.View;
import hotheart.starcraft.graphics.render.ViewController;
import hotheart.starcraft.map.Map;

public class SimpleViewController extends ViewController {

	SimpleView view;
	public SimpleViewController(Context c)
	{
		view = new SimpleView(c);
	}
	
	@Override
	protected View _getView() {
		return view;
	}

	@Override
	public void setPosXY(int x, int y) {
		view.ofsX = x;
		view.ofsY = y;
	}

	@Override
	public void setMap(Map map) {
		view.setMap(map);
	}

}
