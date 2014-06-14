package john.rcsailor;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

public class SailorMainActivity extends IOIOActivity {

public static final int REQUEST_CODE_MENU_IP = 101;
	
	boolean isRunning = true;

	byte direction=-1;
	
	SailorDummySurfaceView surfaceView;
	SailorImageView imageView;
	
	Handler mainHandler;
	SailorImageHandler imageHandler;
	SailorCommandReceiver commandReceiver;
	String strSailorIPAddr, strCaptainIPAddr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.sailor_activity_main);
		
		strSailorIPAddr = getWifiIP();	// get this device's Wifi IP address
        strCaptainIPAddr = null;	// the IP address of Captain is set by the user
        
        /* test */strCaptainIPAddr = ("192.168.159.3");
        
        mainHandler = new Handler();
        
        /* add dummy surfaceView to the main activity. */
		surfaceView = new SailorDummySurfaceView(this);
		FrameLayout surfaceFrame = (FrameLayout) findViewById(R.id.surfaceFrame);
		surfaceFrame.addView(surfaceView);
		
		/* add imageView to the main activity. */
		imageView = new SailorImageView(this);
		FrameLayout imageFrame = (FrameLayout) findViewById(R.id.imageFrame);
		imageFrame.addView(imageView);
        
        startImageHandleThread();
        
        startCommandReceiveThread();
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		isRunning = false;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int curID = item.getItemId();
		
		switch(curID){
		case R.id.menu_settings:	// IP 설정 Activity 실행
			Intent intent = new Intent(getBaseContext(), SailorIPMenuActivity.class);
			intent.putExtra("sailorAddr", strSailorIPAddr);
			startActivityForResult(intent, REQUEST_CODE_MENU_IP);
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);
 
        switch(requestCode){
        case REQUEST_CODE_MENU_IP:	// from the IP setting menu
        	if (resultCode == RESULT_OK)
                strCaptainIPAddr = dataIntent.getExtras().getString("addr");
        	imageHandler.setCaptainAddress(strCaptainIPAddr);
        }
    }
	
	private String getWifiIP(){
		WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
		
		if (!wifiMgr.isWifiEnabled()) {
        	Toast.makeText(getBaseContext(), "Wifi is not enabled" , Toast.LENGTH_LONG).show();
            finish();
        }
		
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		
		String ipAddress = String.format("%d.%d.%d.%d", (ip & 0xff),
				(ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
        
        return ipAddress;
	}

	private void startImageHandleThread(){
		imageHandler = new SailorImageHandler(this);
		imageHandler.start();
	}
	
	private void startCommandReceiveThread(){
		commandReceiver = new SailorCommandReceiver(this);
		commandReceiver.start();
	}
	
	boolean checkIP(){
		if(strSailorIPAddr.equals("0.0.0.0")){
			Toast.makeText(getBaseContext(), "Wifi IP address is not available" , Toast.LENGTH_LONG).show();
			return false;
		}
		
		if(strCaptainIPAddr==null){
			Toast.makeText(getBaseContext(), "check the IP address of Captain" , Toast.LENGTH_LONG).show();
			return false;
		}
		
		return true;
	}
	
	class Looper extends BaseIOIOLooper {
		private DigitalOutput forward, back, left, right;
		
		@Override
		protected void setup() throws ConnectionLostException {
			forward = ioio_.openDigitalOutput(44, false);
			back = ioio_.openDigitalOutput(43, false);
			left = ioio_.openDigitalOutput(46, false);
			right = ioio_.openDigitalOutput(45, false);
		}
		
		@Override
		public void loop() throws ConnectionLostException {
			Log.d("direction", ""+direction);
			switch(direction){
			case -1:
				turnOn(false, false, false, false);
				break;
			case 0:
				turnOn(true, false, true, false);
				break;
			case 1:
				turnOn(true, false, false, false);
				break;
			case 2:
				turnOn(true, false, false, true);
				break;
			case 3:
				turnOn(false, true, true, false);
				break;
			case 4:
				turnOn(false, true, false, false);
				break;
			case 5:
				turnOn(false, true, false, true);
				break;
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		
		private void turnOn(boolean forwardOn, boolean backOn, boolean leftOn, boolean rightOn){
			try {
				forward.write(forwardOn);
				back.write(backOn);
				left.write(leftOn);
				right.write(rightOn);
			} catch (ConnectionLostException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}
}
