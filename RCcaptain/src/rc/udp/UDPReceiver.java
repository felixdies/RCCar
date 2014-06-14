package rc.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.util.Log;

/**
 * singleton
 * @author John
 *
 */
public class UDPReceiver extends UDPNet{
	
	private static UDPReceiver receiver = null;
	DatagramSocket receiveDatagramSocket;	

	private UDPReceiver(){
		try {
			receiveDatagramSocket = new DatagramSocket(receivePort);
		} catch (SocketException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * @return singleton receiver instance
	 */
	public static UDPReceiver getInstance(){
		if(receiver == null) receiver = new UDPReceiver();
		
		return receiver;
	}
	
	public byte[] receive(int msglength) {
		byte[] msg = new byte[msglength];

		try {
			DatagramPacket inPacket = new DatagramPacket(msg, msg.length);
			receiveDatagramSocket.receive(inPacket);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return msg;
	}
	
	public byte[] receive(int msglength, int timeout) {
		byte[] msg = new byte[msglength];

		try {
			DatagramPacket inPacket = new DatagramPacket(msg, msg.length);
			receiveDatagramSocket.setSoTimeout(timeout);
			receiveDatagramSocket.receive(inPacket);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Log.d("receive message.", "msg[0] : "+msg[0]);
		return msg;
	}
}
