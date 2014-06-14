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
 * 카메라에 찍힌 화면을 주기적으로 capture 한다.
 * capture 한 영상은 Captain 에게 전송하거나,
 * 직접 분석하여 목표 객체를 추적한다.
 * 
 * @author John
 *
 */
public class SailorImageHandler extends Thread{
	
	/**
	 * SailorImageHandler 는 send 모드 또는 analyze 모드 중 하나의 상태이다.
	 * default 는 send 모드로, Captain 에게 영상을 전송하고 명령을 기다린다.
	 * analyze 모드일 경우, 영상을 분석하여 목표 객체를 추적한다.
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
		
		// sailor 는 preview 를 보일 필요가 없으므로 크기를 1로 설정한다.
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