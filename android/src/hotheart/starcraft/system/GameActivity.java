package hotheart.starcraft.system;

import hotheart.starcraft.controller.ViewController;
import hotheart.starcraft.core.GameContext;
import hotheart.starcraft.core.StarcraftCore;
import hotheart.starcraft.graphics.render.Render;
import hotheart.starcraft.graphics.render.simple.SimpleRender;
import hotheart.starcraft.graphics.render.simple.SimpleView;
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
	ColorMatrixColorFilter activeFilter;
	ColorMatrixColorFilter unactiveFilter;

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
		rl.addView(cont.getView());
		View gui = LayoutInflater.from(this).inflate(R.layout.gameui, null);
		
		MapPreview prev = (MapPreview)gui.findViewById(R.id.mapPreview);
		prev.setBitmap(GameContext.map.generateMapPreview());
		cont.setMapPreview(prev);
//		gui.setVisibility(View.INVISIBLE);
		rl.addView(gui);
		setContentView(rl);

		return cont;
	}

	void init() {

		ColorMatrix cm = new ColorMatrix();
		cm.set(new float[] { 1, 0, 0, 0, 0, 0, 1, 0, 0, 100, 0, 0, 1, 0, 0, 0,
				0, 0, 1, 0 });

		activeFilter = new ColorMatrixColorFilter(cm);

		cm = new ColorMatrix();
		cm.set(new float[] { 1, 0, 0, 0, 100, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0,
				0, 0, 1, 0 });

		unactiveFilter = new ColorMatrixColorFilter(cm);

		final Activity currentActivity = this;

		new Thread() {
			public void run() {
				final boolean result = StarcraftCore.init(currentActivity);
				runOnUiThread(new Runnable() {
					public void run() {
						if (result) {

							StarcraftCore.viewController = createContentView();
							// view = (SimpleView) findViewById(R.id.GameView);
							// view.setMap(initializer.map);

							// final ImageButton kill = (ImageButton)
							// findViewById(R.id.killButton);
							// kill.setOnClickListener(new OnClickListener() {
							// public void onClick(View v) {
							// view.killSelectedUnit();
							// }
							// });
							//
							final ImageButton move = (ImageButton) findViewById(R.id.moveButton);

							if (StarcraftCore.viewController.isMapScroll())
								move.setColorFilter(activeFilter);
							else
								move.setColorFilter(unactiveFilter);

							move.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									StarcraftCore.viewController.setMapScrollingState(!StarcraftCore.viewController
											.isMapScroll());

									if (StarcraftCore.viewController.isMapScroll())
										move.setColorFilter(activeFilter);
									else
										move.setColorFilter(unactiveFilter);
								}
							});
							//
							// final ImageButton fixSelection = (ImageButton)
							// findViewById(R.id.fixButton);
							//
							// if (view.fixed)
							// fixSelection.setColorFilter(activeFilter);
							// else
							// fixSelection.setColorFilter(unactiveFilter);
							//
							// fixSelection
							// .setOnClickListener(new OnClickListener() {
							// public void onClick(View v) {
							// view.fixed = !view.fixed;
							//
							// if (view.fixed)
							// fixSelection
							// .setColorFilter(activeFilter);
							// else
							// fixSelection
							// .setColorFilter(unactiveFilter);
							// }
							// });
							//
							// final ImageButton attack = (ImageButton)
							// findViewById(R.id.attackButton);
							//
							// if (view.selectingTarget)
							// attack.setColorFilter(activeFilter);
							// else
							// attack.setColorFilter(unactiveFilter);
							//
							// attack
							// .setOnClickListener(new OnClickListener() {
							// public void onClick(View v) {
							// view.selectingTarget = !view.selectingTarget;
							//
							// if (view.selectingTarget)
							// attack
							// .setColorFilter(activeFilter);
							// else
							// attack
							// .setColorFilter(unactiveFilter);
							// }
							// });

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