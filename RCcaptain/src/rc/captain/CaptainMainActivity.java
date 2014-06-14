package rc.captain;

import john.rccaptain.R;
import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Captain 은 유저가 직접 사용하는 device 로, 아래의 역할을 수행한다.<br>
 * - 컨트롤 화면을 나타냄<br>
 * - Sailor 에게 명령을 전송<br>
 * - bitmap 을 수신하여 화면에 띄움<br>
 * 
 * @author John
 *
 */
public class CaptainMainActivity extends Activity {
	
	public static final int REQUEST_CODE_MENU_IP = 101;
	
	boolean isRunning = true;
	Handler mainHandler;
	CaptainCommander commander;
	String strCaptainIPAddr, strSailorIPAddr;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.captain_activity_main);
        
        strCaptainIPAddr = getWifiIP();	// get this device's Wifi IP address
        strSailorIPAddr = null;	// the IP address of Sailor is set by the user
        
/* test */strSailorIPAddr = ("192.168.159.3");
        
        mainHandler = new Handler();
        
        new CaptainEventHandler(this);	// 이벤트 리스너 설정
        
        startImageReceiveThread();
        
        startCommandThread();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isRunning = false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int curID = item.getItemId();
		
		switch(curID){
		case R.id.menu_settings:	// IP 설정 Activity 실행
			Intent intent = new Intent(getBaseContext(), CaptainIPMenuActivity.class);
			intent.putExtra("captainAddr", strCaptainIPAddr);
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
                strSailorIPAddr = dataIntent.getExtras().getString("addr");
        	commander.setSailorIPAddress(strSailorIPAddr);
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
	
	private void startImageReceiveThread(){
		CaptainImageReceiver imageReceiver = new CaptainImageReceiver(this);
		imageReceiver.start();
	}
	
	private void startCommandThread(){
		commander = new CaptainDirectionCommander(this);
		commander.start();
	}
	
	boolean checkIP(){
		if(strCaptainIPAddr.equals("0.0.0.0")){
			Toast.makeText(getBaseContext(), "Wifi IP address is not available" , Toast.LENGTH_LONG).show();
			return false;
		}
		
		if(strSailorIPAddr==null){
			Toast.makeText(getBaseContext(), "check the IP address of Sailor" , Toast.LENGTH_LONG).show();
			return false;
		}
		
		return true;
	}
	
}
