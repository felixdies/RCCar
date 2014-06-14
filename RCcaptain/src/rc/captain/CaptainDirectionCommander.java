package rc.captain;

import rc.info.Direction;

public class CaptainDirectionCommander extends CaptainCommander {
	
	byte direction = Direction.STOP;
	
	public CaptainDirectionCommander(CaptainMainActivity mainActivity) {
		super(mainActivity);
	}
	
	@Override
	public void run() {
		while (mainActivity.isRunning) {
			send(direction);
			
			try { sleep(INTERVER_TIME); }
			catch (InterruptedException e) {}	// interrupt ¹ß»ý
		}
	}
}
