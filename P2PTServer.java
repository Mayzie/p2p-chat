import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class P2PTServer implements Runnable {
	
	private final int port;
	
	private final DatagramSocket socketServer;
	
	public P2PTServer(int port) {		
		this.port = port;
		
		DatagramSocket socket;
		try {
			socket = new DatagramSocket(this.port);			
		} catch (SocketException exception) {
			socket = null;
			System.err.println("Unable to bind socket to port " + this.port);
		}
		
		this.socketServer = socket;
	}
	
	public synchronized void printStatus() {
		Profile localUser = Peers.peers.get(Configuration.localUniKey);
		
		System.out.println("### P2P tweets ###");
		// Print our local peer first (special case)
		if(localUser != null) {
			System.out.println("# " + localUser.name + " (myself): " + localUser.message);
		}
		
		// Display the statuses of all our other peers
		for(Profile profile : Peers.peers.values()) {
			try {
				if(!(profile.uniKey.equals(Configuration.localUniKey))) {
					if(profile.getTimeReceived() == null) {
						System.out.println("# [" + profile.name + " (" + profile.uniKey + "): not yet initialized]");
						continue;
					}
					
					// Get the time (in seconds) since the current time and the time their last message was received 
					int timeDifference = (int) ((new Date()).getTime() - profile.getTimeReceived().getTime()) / 1000;
					if(timeDifference >= Configuration.PEER_TIME_DROP)	// Check if we haven't heard from the peer in a while
						continue;
					
					System.out.print("# ");
					if(timeDifference >= Configuration.PEER_TIME_IDLE) {	// Check if the peer is considered 'idle'
						System.out.println("[" + profile.name + " (" + profile.uniKey + "): idle]");
					} else {	// If they're not, display their status!
						System.out.println(profile.name + " (" + profile.uniKey + "): " + profile.message);
					}
				}
			} catch(Exception exception) {
				continue;
			}
		}
		System.out.println("### End tweets ###");
	}
	
	@Override
	public void run() {
		if(this.socketServer == null)
			return;
		
		byte[] buffer = new byte[Configuration.RECV_SIZE_UDP];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		while(true) {
			try {
				try {
					this.socketServer.receive(packet);
				} catch (IOException exception) {
					continue;
				}
				
				String packetData = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.ISO_8859_1);
				String[] packetSplit = packetData.split(":", 2);
				String packetUniKey = packetSplit[0];	// We are guaranteed that the UniKey will always be of length 8, and that all messages will have one
				String packetMessage = packetSplit[1].replaceAll("\\\\:", ":");
				
				if(Configuration.DEBUG) {
					System.out.print("(DEBUG) Incoming message: ");
					System.out.println(packetData);
				}
				
				synchronized(Peers.peers) {
					Profile profile = Peers.peers.get(packetUniKey);
					if(profile != null) {
						profile.setMessage(packetMessage);
						profile.updateTimeReceived();
					}
				}
			} catch(Exception exception) {
				continue;
			}
		}
	}
	
}
