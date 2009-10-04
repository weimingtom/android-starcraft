package hotheart.starcraft.core;

import hotheart.starcraft.graphics.Image;
import hotheart.starcraft.graphics.render.Render;
import hotheart.starcraft.graphics.utils.SelectionCircles;
import hotheart.starcraft.map.Map;
import hotheart.starcraft.orders.MoveOrder;
import hotheart.starcraft.units.Unit;
import hotheart.starcraft.units.target.StaticPointTarget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeSet;

public final class GameContext {
	public Random rnd;
	public int drawCount;
	public static Map map = null;

	public Unit majorSelectedUnit = null;

	public GameContext() {
		rnd = new Random();
		drawCount = 0;
		units = new ArrayList<Unit>();
		drawObjects = new TreeSet<Image>(new Comparator<Image>() {

			public int compare(Image arg0, Image arg1) {
				if (arg0 == arg1)
					return 0;
				
				if (arg0.currentImageLayer == Image.MIN_IMAGE_LAYER)
					return 1;

				if (arg1.currentImageLayer == Image.MIN_IMAGE_LAYER)
					return -1;

				if (arg0.currentImageLayer < arg1.currentImageLayer)
					return -1;
				else
					return 1;
			}
		});
	}

	public ArrayList<Unit> units;
	
	public ArrayList<Unit> selectedUnits = new ArrayList<Unit>();

	public void addUnit(Unit u, int x, int y) {
		int R = 0;
		// int objR = Sprite.selCircleSize[u.flingy.sprite.selCircle];
		while (true) {
			for (int i = 0; i < R; i++) {
				int nx = x - R;
				int ny = y - R / 2 + i;
				// if ((nx < objR)||(ny < objR))
				// continue;

				if (PickUnit(nx, ny) == null) {
					u.setPos(nx, ny);

					addUnit(u);
					return;
				}
			}
			for (int i = 0; i < R; i++) {
				int nx = x + R;
				int ny = y - R / 2 + i;
				// if ((nx < objR)||(ny < objR))
				// continue;

				if (PickUnit(nx, ny) == null) {
					u.setPos(nx, ny);

					addUnit(u);
					return;

				}
			}

			for (int i = 0; i < R; i++) {
				int ny = y - R;
				int nx = x - R / 2 + i;

				// if ((nx < objR)||(ny < objR))
				// continue;
				if (PickUnit(nx, ny) == null) {
					u.setPos(nx, ny);

					addUnit(u);
					return;

				}
			}

			for (int i = 0; i < R; i++) {
				int ny = y + R;
				int nx = x - R / 2 + i;
				// if ((nx < objR)||(ny < objR))
				// continue;
				if (PickUnit(nx, ny) == null) {
					u.setPos(nx, ny);

					addUnit(u);
					return;
				}
			}

			R += 10;
		}
	}

	public Unit PickUnit(int x, int y) {
		for (Unit a : units) {
			if ((x - a.getPosX()) * (x - a.getPosX()) + (y - a.getPosY())
					* (y - a.getPosY()) < SelectionCircles.selCircleSize[a.selCircle]
					* SelectionCircles.selCircleSize[a.selCircle] / 4) {
				return a;
			}
		}
		return null;
	}

	public void addUnit(Unit u) {
		units.add(u);
	}

	public void removeUnit(Unit u) {
		units.remove(u);
		selectedUnits.remove(u);
	}

	public ArrayList<Image> sprites = new ArrayList<Image>();

	public void addImage(Image s) {
		sprites.add(s);
	}

	public void removeImage(Image s) {
		sprites.remove(s);
	}

	public TreeSet<Image> drawObjects = new TreeSet<Image>();

	public void buildTree() {
		drawObjects.clear();

		for (Image s : sprites)
			s.buildTree();

		for (Unit u : units)
			u.buildTree();
	}

	public void drawTree() {
		drawCount = 0;

		for (Image i : drawObjects)
			i.drawWithoutChilds();
		
		

		for (Unit u : selectedUnits) {
			u.drawSelection();
//			u.draw_selection();
//			u.draw_healths();
		}
	}

	public void update() {
		for (Object s : sprites.toArray()) {
			((Image) s).update();
		}

		for (Object u : units.toArray()) {
			((Unit) u).update();
		}
	}

	public void draw() {
		buildTree();

		Render render = StarcraftCore.render;
		render.begin();
		drawTree();
		
		render.end();
	}

	public void moveSelected(int x, int y)
	{
		for (Object u : selectedUnits.toArray()) {
			Unit unit = (Unit) u;
			unit.currentOrder = new MoveOrder(unit, new StaticPointTarget(x, y));
		}
	}
	
	public void removeSelection()
	{
		selectedUnits.clear();
	}
	
	public void selectUnit(Unit u)
	{
		selectedUnits.remove(u);
		selectedUnits.add(u);
		
		if (StarcraftCore.viewController!=null)
			StarcraftCore.viewController.setControlIcons(u.controlPanel.getButtons());
	}
	public void deselectUnit(Unit u)
	{
		selectedUnits.remove(u);
	}
}