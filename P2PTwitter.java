import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class P2PTwitter {

	public static void main(String[] args) {
		if((args.length != 1) && (args.length != 2)) {
			System.err.println("Invalid number of arguments provided");
			return;
		} else if(args.length == 2) {
			Configuration.FILE_CONFIG_LOC = args[1];
		}
		
		Configuration.localUniKey = args[0];
		
		try {
			Configuration.parseConfigurationFile();
		} catch(Exception exception) {
			System.err.println("An error has occurred while trying to read the configuration file '" 
					+ Configuration.FILE_CONFIG_LOC + "'");
		}
		
		try {
			Peers.peers = Peers.parsePeers(Configuration.FILE_PEERS_LOC);
		} catch(Exception exception) {
			System.err.println("An error has occurred while trying to read the peers configuration file '" 
					+ Configuration.FILE_PEERS_LOC + "'. Aborting");
			return;
		}
		
		if(Peers.peers == null) {
			System.err.println("No peers configuration file specified");
			return;
		}
		
		P2PTClient client = new P2PTClient();
		P2PTServer server = new P2PTServer(Configuration.SOCK_PORT);
		Thread threadClient = new Thread(client);
		Thread threadServer = new Thread(server);
		
		threadClient.start();
		threadServer.start();
		
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			String newMessage = null;
			boolean okay = false;
			
			while(!(okay)) {
				System.out.print("Status: ");
				try {
					newMessage = stdin.readLine();
				} catch(IOException exception) {
					continue;
				}
				
				if(newMessage.replaceAll("\\s+", "").isEmpty()) {
					System.out.println("Status is empty. Retry.");
				} else if(newMessage.length() > 140) {
					System.out.println("Status is too long, 140 characters max. Retry.");
				} else {
					okay = true;
				}
			}
			
			synchronized(Peers.peers) { 
				Profile profile = Peers.peers.get(Configuration.localUniKey);
				profile.setMessage(newMessage);
			}
			
			synchronized(client) {
				// Resume thread (ugly)
				client.notifyAll();
			}
			
			server.printStatus();
		}
	}

}
