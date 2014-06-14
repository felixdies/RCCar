package rc.captain;

import john.rccaptain.R;
import rc.udp.UDPReceiver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class CaptainImageReceiver extends Thread {
	
	private final int IMAGE_SIZE = 20000;
	
	CaptainMainActivity mainActivity;
	Handler mainHandler;
	CaptainImageView imageView;
	
	UDPReceiver receiver;
	byte[] receivemsg;
	
	public CaptainImageReceiver(CaptainMainActivity activity) {
		this.mainActivity = activity;
		this.mainHandler = mainActivity.mainHandler;
		
		imageView = new CaptainImageView(mainActivity);
		FrameLayout imageFrame = (FrameLayout) mainActivity.findViewById(R.id.imageFrame);
		imageFrame.addView(imageView);
		
		receiver = UDPReceiver.getInstance();
	}
	
	public void run(){
		while (mainActivity.isRunning) {
			try {
				receivemsg = receiver.receive(IMAGE_SIZE);//, 10000);
//				Log.d("receive", new String(receivemsg));
				try{
					imageView.setBitmap(receivemsg);
					
					mainHandler.post(new Runnable() {
						@Override
						public void run() {
							imageView.invalidate();
						}
					});
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			} catch (Exception ex) {
				Log.e("SampleThreadActivity",
						"Exception in processing message.", ex);
			}
		}
	}
}


class CaptainImageView extends View{
	Bitmap bitmapImage;

	public CaptainImageView(Context context) {
		super(context);
	}

	protected void onDraw(Canvas canvas) {
		if (bitmapImage != null) {
			canvas.drawBitmap(bitmapImage, -10, 0, null);
		}
	}
	
	void setBitmap(byte[] data){
		Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
		bitmapImage = Bitmap.createScaledBitmap( temp, 640, 480, true );
	}
}
