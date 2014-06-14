package john.rcsailor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SailorIPMenuActivity extends Activity {
	TextView tvSailorAddr;
	EditText etCaptainAddr;
	Button btnSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.sailor_menu_activity_ipaddrs);
		
		tvSailorAddr = (TextView)findViewById(R.id.tvSailorAddr);
		tvSailorAddr.setText(getIntent().getExtras().getString("sailorAddr"));
		
		etCaptainAddr = (EditText)findViewById(R.id.etCaptainAddr);
		
		btnSave = (Button) findViewById(R.id.btnCaptainAddr);

		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("addr", etCaptainAddr.getText().toString());
 
                setResult(RESULT_OK, resultIntent);
                finish();
			}
		});
	}
}
