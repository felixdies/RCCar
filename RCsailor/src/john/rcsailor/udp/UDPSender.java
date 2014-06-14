package john.rcsailor.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.util.Log;

public class UDPSender extends UDPNet {

	InetAddress tgAddr;
	DatagramSocket sendDatagramSocket;
	
	public UDPSender(){
		super();
		try {
			sendDatagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void setTargetAddress(String targetAddr){
		try {
			tgAddr = InetAddress.getByName(targetAddr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getTargetAddress(){
		return tgAddr.toString();
	}
	
	public void send(byte[] msg) {
//		Log.d("sendMassage", "Captain IP:" + getTargetAddress() + " length " + msg.length);
		try {
			DatagramPacket outPacket = new DatagramPacket(msg, msg.length, tgAddr, receivePort);
			sendDatagramSocket.send(outPacket);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
