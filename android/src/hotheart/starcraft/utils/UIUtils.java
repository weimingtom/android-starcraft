package hotheart.starcraft.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.widget.Toast;

public class UIUtils {
	private static Context currentContext;
	public static void init(Context viewContext)
	{
		currentContext = viewContext;
	}
	private static int result = -2;
	public static int showSelectionMessageBox(final String[] text)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
		builder.setTitle("Pick a unit");
		builder.setItems(text, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	result = item;
		        //Toast.makeText(currentContext, text[item], Toast.LENGTH_SHORT).show();
		    }
		});
		builder.setOnCancelListener(new OnCancelListener() {
			
			public void onCancel(DialogInterface dialog) {
				result = -1;
			}
		});
		
		AlertDialog alert = builder.create();
		alert.show();
		while(result == -2)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -1;
	}
}
