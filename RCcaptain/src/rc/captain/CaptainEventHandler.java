package rc.captain;

import john.rccaptain.R;
import rc.info.CommandMode;
import rc.info.Direction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class CaptainEventHandler {
	CaptainMainActivity mainActivity;
	
	Button[] btnsAll;
	Button[] btnArrows;
	Button btnGravity, btnVoice, btnTracking;
	
	private int command_mode;
	
	public CaptainEventHandler(CaptainMainActivity mainActivity) {
		this.mainActivity = mainActivity;
		
		inflate();
		setListeners();
		
		command_mode = CommandMode.DIRECTION;
	}
	
	private void inflate(){
		btnArrows = new Button[6];
		btnArrows[0] = (Button) mainActivity.findViewById(R.id.btnArrow_fLeft);
		btnArrows[1] = (Button) mainActivity.findViewById(R.id.btnArrow_front);
		btnArrows[2] = (Button) mainActivity.findViewById(R.id.btnArrow_fRight);
		btnArrows[3] = (Button) mainActivity.findViewById(R.id.btnArrow_bLeft);
		btnArrows[4] = (Button) mainActivity.findViewById(R.id.btnArrow_back);
		btnArrows[5] = (Button) mainActivity.findViewById(R.id.btnArrow_bRight);
		
		btnGravity = (Button) mainActivity.findViewById(R.id.btnGravity);
		btnVoice = (Button) mainActivity.findViewById(R.id.btnVoice);
		btnTracking = (Button) mainActivity.findViewById(R.id.btnTracking);
		
		btnsAll = new Button[9];
		for(int i=0; i<6; i++)
			btnsAll[i] = btnArrows[i];
		btnsAll[6] = btnGravity;
		btnsAll[7] = btnVoice;
		btnsAll[8] = btnTracking;
	}
	
	private void setListeners(){
		for(int i=0; i<6; i++){
			btnArrows[i].setOnTouchListener(new ArrowButtonTouchListener(i));
		}
		
		btnGravity.setOnClickListener(new GravityButtonClickListener());
	}
	
	private void enableOtherButtons(Button curButton, boolean enable){
		for (Button btn : btnsAll)
			if(btn != curButton)
				btn.setEnabled(enable);
	}
	
	private void killOldThread(){
		while(mainActivity.commander.isAlive()){
			mainActivity.isRunning = false;
			
			mainActivity.commander.interrupt();
			try { Thread.sleep(10); }
			catch (InterruptedException e) {}
		}
		Log.d("CaptainEventHandler.killOldThread()",
				"the old commander is dead.");
		
		mainActivity.isRunning = true;
	}
	
	class ArrowButtonTouchListener implements OnTouchListener{
		final byte direction;
		
		public ArrowButtonTouchListener(int direction) {
			super();
			this.direction = (byte)direction;
		}
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(!mainActivity.checkIP()) return false;
			
			switch(event.getAction()){
		    case MotionEvent.ACTION_DOWN:
		    	((CaptainDirectionCommander) mainActivity.commander).direction = direction;
				return true;
			case MotionEvent.ACTION_UP:
				((CaptainDirectionCommander) mainActivity.commander).direction = Direction.STOP;
				return true;
		    }
			return false;
		}
	}
	
	class GravityButtonClickListener implements OnClickListener{
		@Override
		public void onClick(View curButton) {
			if(!mainActivity.checkIP()) return;
			
			if(command_mode == CommandMode.GRAVITY){
				enableOtherButtons((Button)curButton, true);
				command_mode = CommandMode.DIRECTION;
				
				/* 방향 조종 모드로 전환 */
				killOldThread();
				mainActivity.commander = new CaptainDirectionCommander(mainActivity);
				mainActivity.commander.start();
			}
			else if(command_mode != CommandMode.GRAVITY){
				enableOtherButtons((Button)curButton, false);
				command_mode = CommandMode.GRAVITY;
				
				/* 중력 센서 조종 모드로 전환 */
				killOldThread();
			}
				
		}
		
	}
}
