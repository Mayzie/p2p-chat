import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class P2PTClient implements Runnable {
	
	private final DatagramSocket socketClient;
	private final Random X;
	
	public P2PTClient() {
		this.X = new Random();
		
		// Establish a new outgoing socket connection
		DatagramSocket tempSocket = null;
		try {
			tempSocket = new DatagramSocket();
		} catch (SocketException e) {
			System.err.println("Unable to create client socket");
		}
		
		this.socketClient = tempSocket;
	}
	
	@Override
	public void run() {
		while(true) {
			try{
				if(this.socketClient != null) {
					String escapedMessage;
					synchronized(Peers.peers) {
						escapedMessage = Configuration.localUniKey + ":" + Peers.peers.get(Configuration.localUniKey).message.replaceAll(":", "\\\\:");
					}
					byte[] fixedMessage = escapedMessage.getBytes(StandardCharsets.ISO_8859_1);	// We must use ISO-8859-1 encoding (Task 2)
					
					for(Profile profile : Peers.peers.values()) {
						if(!(profile.uniKey.equals(Configuration.localUniKey)) || (Configuration.DEBUG)) {	// Ignore the local peer
							try {
								// Send our current message to all the peers defined in the peers configuration file
								DatagramPacket packet = new DatagramPacket(fixedMessage, fixedMessage.length, InetAddress.getByAddress(profile.getIPAddress()), Configuration.SOCK_PORT);
								socketClient.send(packet);
							} catch(UnknownHostException exception) { 
								continue; 
							} catch(IOException exception) {
								continue;
							}
						}
					}
				}
			} catch(Exception exception) { }
			
			try {
				synchronized(this) {
					// Get the thread to wait (ugly) RANDOM_LOW <= X <= RANDOM_HIGH amount of time
					this.wait(Configuration.RANDOM_LOW + this.X.nextInt(Configuration.RANDOM_HIGH - Configuration.RANDOM_LOW + 1));
				}
			} catch(InterruptedException exception) { }
		}
	}
	
}
