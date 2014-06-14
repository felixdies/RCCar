package rc.captain;

import rc.udp.UDPSender;

public abstract class CaptainCommander extends Thread {

	long INTERVER_TIME = 500;
	
	UDPSender sender;
	
	protected long lastCommandTime;
	protected CaptainMainActivity mainActivity;
	
	CaptainCommander(CaptainMainActivity mainActivity){
		super("CommanderThread");
//		setDaemon(true);	// Daemon thread - ��� ������� �ʴ� ������ �߻��Ͽ� running flag �� ��ü
		
		this.sender = new UDPSender();
		this.mainActivity = mainActivity;
		
		setSailorIPAddress(mainActivity.strSailorIPAddr);
	}
	
	
	void send(byte direction){
		byte[] msg = new byte[1];
		msg[0]=direction;
		
		sender.send(msg);
	}
	
	
	void setSailorIPAddress(String address){
		sender.setTargetAddress(address);
	}
}
