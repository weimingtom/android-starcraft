package hotheart.starcraft.system;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public final class MainActivity extends Activity {
	GameView view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//		if (view != null) {
//			setContentView(view);
//			return;
//		}

		final SystemInitializer initializer = new SystemInitializer(this);
		final Activity currentActivity = this;

		new Thread() {
			public void run() {
				final boolean result = initializer.init();
				runOnUiThread(new Runnable() {
					public void run() {
						if (result) {
							
							setContentView(R.layout.map);
							GameView view = (GameView)findViewById(R.id.GameView);
							view.setMap(initializer.map);
//							view = new GameView(currentActivity);
//							view.setMap(initializer.map);
//							setContentView(view);

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