package hotheart.starcraft.system;

import hotheart.starcraft.controller.GameController;
import hotheart.starcraft.controller.ViewController;
import hotheart.starcraft.core.GameContext;
import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.graphics.IconFactory;
import hotheart.starcraft.graphics.render.Render;
import hotheart.starcraft.graphics.render.simple.SimpleRender;
import hotheart.starcraft.graphics.render.simple.SimpleView;
import hotheart.starcraft.units.Unit;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public final class GameActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		init();
	}

	ViewController createContentView() {
		ViewController cont = StarcraftCore.render.getController(this);

		RelativeLayout rl = new RelativeLayout(this);
		rl.addView(cont.getRenderView());
		View gui = LayoutInflater.from(this).inflate(R.layout.gameui, null);

		MapPreview prev = (MapPreview) gui.findViewById(R.id.mapPreview);
		prev.setBitmap(GameContext.map.generateMapPreview());
		cont.setMapPreview(prev);

		ImageButton b11 = (ImageButton) gui.findViewById(R.id.b11);
		ImageButton b21 = (ImageButton) gui.findViewById(R.id.b21);
		ImageButton b31 = (ImageButton) gui.findViewById(R.id.b31);

		ImageButton b12 = (ImageButton) gui.findViewById(R.id.b12);
		ImageButton b22 = (ImageButton) gui.findViewById(R.id.b22);
		ImageButton b32 = (ImageButton) gui.findViewById(R.id.b32);

		ImageButton b13 = (ImageButton) gui.findViewById(R.id.b13);
		ImageButton b23 = (ImageButton) gui.findViewById(R.id.b23);
		ImageButton b33 = (ImageButton) gui.findViewById(R.id.b33);

		cont.setControlButtons(b11, b21, b31, b12, b22, b32, b13, b23, b33);

		// gui.setVisibility(View.INVISIBLE);
		rl.addView(gui);
		setContentView(rl);

		return cont;
	}

	void init() {

		final Activity currentActivity = this;

		new Thread() {
			public void run() {
				final boolean result = StarcraftCore.init(currentActivity);
				runOnUiThread(new Runnable() {
					public void run() {
						if (result) {

							StarcraftCore.viewController = createContentView();

							final Button move = (Button) findViewById(R.id.moveButton);
							final Button umove = (Button) findViewById(R.id.unitMoveButton);
							final Button select = (Button) findViewById(R.id.selectButton);
							final Button attack = (Button) findViewById(R.id.attackButton);

							if (StarcraftCore.viewController.isMapScroll())
								move.setEnabled(false);
							else
								move.setEnabled(true);

							move.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {

									StarcraftCore.viewController
											.setMapScrollingState(true);

									move.setEnabled(false);
									select.setEnabled(true);
									umove.setEnabled(true);
									attack.setEnabled(true);
								}
							});

							if (StarcraftCore.gameController.currentAction == GameController.ACTION_SELECT)
								select.setEnabled(false);
							else
								select.setEnabled(true);

							select.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									StarcraftCore.viewController
											.setMapScrollingState(false);

									if (StarcraftCore.gameController.currentAction != GameController.ACTION_SELECT) {
										StarcraftCore.gameController.currentAction = GameController.ACTION_SELECT;
									}
									move.setEnabled(true);
									select.setEnabled(false);
									umove.setEnabled(true);
									attack.setEnabled(true);
								}
							});

							if (StarcraftCore.gameController.currentAction == GameController.ACTION_ATTACK)
								attack.setEnabled(false);
							else
								attack.setEnabled(true);

							attack.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									StarcraftCore.viewController
											.setMapScrollingState(false);
									if (StarcraftCore.gameController.currentAction != GameController.ACTION_ATTACK) {
										StarcraftCore.gameController.currentAction = GameController.ACTION_ATTACK;
									}
									move.setEnabled(true);
									select.setEnabled(true);
									umove.setEnabled(true);
									attack.setEnabled(false);
								}
							});

							if (StarcraftCore.gameController.currentAction == GameController.ACTION_MOVE)
								umove.setEnabled(false);
							else
								umove.setEnabled(true);

							umove.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									StarcraftCore.viewController
											.setMapScrollingState(false);
									if (StarcraftCore.gameController.currentAction != GameController.ACTION_MOVE) {
										StarcraftCore.gameController.currentAction = GameController.ACTION_MOVE;
									}
									move.setEnabled(true);
									select.setEnabled(true);
									umove.setEnabled(false);
									attack.setEnabled(true);
								}
							});

							for (int i = 0; i < 9; i++)
								StarcraftCore.viewController.setControlIcon(i,
										-1);

						} else // ERROR
						{

							new AlertDialog.Builder(currentActivity)
									.setMessage(
											"Error in initialization: "
													+ StarcraftCore.state)
									.setNeutralButton(
											"Close",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int whichButton) {
													finish();
												}
											}).create().show();
						}
					}
				});
			}
		}.start();
	}
}