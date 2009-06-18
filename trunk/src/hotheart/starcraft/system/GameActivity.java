package hotheart.starcraft.system;

import hotheart.starcraft.graphics.render.AbstractRender;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public final class GameActivity extends Activity {
	SimpleView view;
	ColorMatrixColorFilter activeFilter;
	ColorMatrixColorFilter unactiveFilter;
	AbstractRender render;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		
		init();
	}
	
	SimpleView createContentView()
	{
		//setContentView(R.layout.map);
		
		SimpleView res = new SimpleView(this, null); 
		
		RelativeLayout rl = new RelativeLayout(this);
		rl.addView(res);
		rl.addView(LayoutInflater.from(this).inflate(R.layout.gameui, null));
		setContentView(rl);
		
		return res;
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

		final SystemInitializer initializer = new SystemInitializer(this);
		final Activity currentActivity = this;

		new Thread() {
			public void run() {
				final boolean result = initializer.init();
				runOnUiThread(new Runnable() {
					public void run() {
						if (result) {

							view = createContentView();
							//view = (SimpleView) findViewById(R.id.GameView);
//							view.setMap(initializer.map);

							final ImageButton kill = (ImageButton) findViewById(R.id.killButton);
							kill.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									view.killSelectedUnit();
								}
							});

							final ImageButton move = (ImageButton) findViewById(R.id.moveButton);

							if (view.mapMove)
								move.setColorFilter(activeFilter);
							else
								move.setColorFilter(unactiveFilter);

							move.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									view.mapMove = !view.mapMove;

									if (view.mapMove)
										move.setColorFilter(activeFilter);
									else
										move.setColorFilter(unactiveFilter);
								}
							});

							final ImageButton fixSelection = (ImageButton) findViewById(R.id.fixButton);

							if (view.fixed)
								fixSelection.setColorFilter(activeFilter);
							else
								fixSelection.setColorFilter(unactiveFilter);

							fixSelection
									.setOnClickListener(new OnClickListener() {
										public void onClick(View v) {
											view.fixed = !view.fixed;

											if (view.fixed)
												fixSelection
														.setColorFilter(activeFilter);
											else
												fixSelection
														.setColorFilter(unactiveFilter);
										}
									});

							final ImageButton attack = (ImageButton) findViewById(R.id.attackButton);

							if (view.selectingTarget)
								attack.setColorFilter(activeFilter);
							else
								attack.setColorFilter(unactiveFilter);

							attack
									.setOnClickListener(new OnClickListener() {
										public void onClick(View v) {
											view.selectingTarget = !view.selectingTarget;

											if (view.selectingTarget)
												attack
														.setColorFilter(activeFilter);
											else
												attack
														.setColorFilter(unactiveFilter);
										}
									});

						} else // ERROR
						{

							new AlertDialog.Builder(currentActivity)
									.setMessage(
											"Error in initialization: "
													+ initializer.state)
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