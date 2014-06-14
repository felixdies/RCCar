package john.rcsailor;

import john.rcsailor.udp.UDPReceiver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class SailorCommandReceiver extends Thread {
	
	SailorMainActivity mainActivity;
	
	UDPReceiver receiver;
	byte[] receivemsg;
	
	public SailorCommandReceiver(SailorMainActivity activity) {
		this.mainActivity = activity;
		
		receiver = UDPReceiver.getInstance();
	}
	
	public void run(){
		Log.d("direction", ""+mainActivity.direction);
		while (mainActivity.isRunning) {
			try {
				receivemsg = receiver.receive(1, 5000);
				mainActivity.direction = receivemsg[0];
				Log.d("direction", ""+mainActivity.direction);
			} catch (Exception ex) {
				Log.e("SampleThreadActivity",
						"Exception in processing message.", ex);
			}
		}
	}
}