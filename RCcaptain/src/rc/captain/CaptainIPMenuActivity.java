package rc.captain;

import john.rccaptain.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CaptainIPMenuActivity extends Activity {
	TextView tvCaptainAddr;
	EditText etSailorAddr;
	Button btnSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.captain_menu_activity_ipaddrs);
		
		tvCaptainAddr = (TextView)findViewById(R.id.tvCaptainAddr);
		tvCaptainAddr.setText(getIntent().getExtras().getString("captainAddr"));
		
		etSailorAddr = (EditText)findViewById(R.id.etSailorAddr);
		
		btnSave = (Button) findViewById(R.id.btnSailorAddr);

		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("addr", etSailorAddr.getText().toString());
 
                // 응답을 전달하고 이 액티비티를 종료합니다.
                setResult(RESULT_OK, resultIntent);
                finish();
			}
		});
	}
}
