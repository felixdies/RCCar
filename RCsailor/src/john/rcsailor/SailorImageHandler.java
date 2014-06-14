package john.rcsailor;

import java.io.ByteArrayOutputStream;

import john.rcsailor.udp.UDPSender;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * ī�޶� ���� ȭ���� �ֱ������� capture �Ѵ�.
 * capture �� ������ Captain ���� �����ϰų�,
 * ���� �м��Ͽ� ��ǥ ��ü�� �����Ѵ�.
 * 
 * @author John
 *
 */
public class SailorImageHandler extends Thread{
	
	/**
	 * SailorImageHandler �� send ��� �Ǵ� analyze ��� �� �ϳ��� �����̴�.
	 * default �� send ����, Captain ���� ������ �����ϰ� ����� ��ٸ���.
	 * analyze ����� ���, ������ �м��Ͽ� ��ǥ ��ü�� �����Ѵ�.
	 */
	public enum Mode{ SEND, ANALYZE }
	public Mode currentMode;
	
	private SailorMainActivity mainActivity;
	
	private UDPSender senderForCaptain;

	
	public SailorImageHandler(SailorMainActivity activity) {
		this.mainActivity = activity;
		
		currentMode = Mode.SEND;

		senderForCaptain = new UDPSender();
		senderForCaptain.setTargetAddress(mainActivity.strCaptainIPAddr);
	}
	
	@Override
	public void run() {
		while(mainActivity.isRunning){
			switch(currentMode){

			case SEND:
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				mainActivity.mainHandler.post(new Runnable() {
					@Override
					public void run() {
						mainActivity.surfaceView.capture(mainActivity.imageView);
					}
				});
				
				if (mainActivity.imageView.bitmapData != null) {
					senderForCaptain.send(mainActivity.imageView.bitmapData);
				} else {
					Log.d("SailorImageHandler.run", "imageData is null.");
				}
			
				break;
			
			case ANALYZE:
				break;
			}
		}
	}	
	
	void setCaptainAddress(String captainAddr){
		senderForCaptain.setTargetAddress(captainAddr);
	}
}

class SailorDummySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder mHolder;
	private Camera camera;

	public SailorDummySurfaceView(Context context) {
		super(context);

		mHolder = getHolder();
		mHolder.addCallback(this);
		
		// sailor �� preview �� ���� �ʿ䰡 �����Ƿ� ũ�⸦ 1�� �����Ѵ�.
		mHolder.setFixedSize(1, 1);
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();

		setCameraParams();
		
		try {
			camera.setPreviewDisplay(mHolder);
		} catch (Exception e) {
			Log.e("CameraSurfaceView", "Failed to set camera preview.", e);
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		camera.startPreview();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	public boolean capture(Camera.PictureCallback handler) {
		if (camera != null) {
			camera.takePicture(null, null, handler);
			return true;
		} else {
			Log.d("SailorSurfaceView.capture", "camera is null.");
			return false;
		}
	}
	
	private void setCameraParams(){
		
		Camera.Parameters param = camera.getParameters();

		param.setJpegQuality(25);
        param.setSceneMode(Camera.Parameters.SCENE_MODE_SPORTS);
        param.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        param.setColorEffect(Camera.Parameters.EFFECT_NONE);
        
        camera.setParameters(param);
	}
}

class SailorImageView extends View implements PictureCallback{
	static final int IMAGE_WIDTH = 480;
	static final int IMAGE_HEIGHT = 360;
	
	Bitmap bitmapImage;
	byte[] bitmapData;

	public SailorImageView(Context context) {
		super(context);
		
	}

	protected void onDraw(Canvas canvas) {
		if (bitmapImage != null) {
			canvas.drawBitmap(bitmapImage, 0, 0, null);
		}
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
		bitmapImage = Bitmap.createScaledBitmap( temp, IMAGE_WIDTH, IMAGE_HEIGHT, true );
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmapImage.compress(Bitmap.CompressFormat.JPEG, 30, stream);
		bitmapData = stream.toByteArray();

		this.invalidate();	// redraw
		
		camera.startPreview();	// restart preview
	}
}