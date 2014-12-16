import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Configuration extends Parse {
	
	public static final boolean DEBUG = false;
	
	public static final int MAP_INITIALCAPACITY = 16;
	public static String FILE_CONFIG_LOC = "configuration.conf";
	
	public static String FILE_PEERS_LOC = "peers.conf";	// Location of the peers configuration file
	
	public static int PEER_TIME_IDLE = 10;	// Time after which a peer is considered "idle"
	public static int PEER_TIME_DROP = 20;	// Time after which we forget about a peer
	
	public static int RANDOM_LOW = 1000;	// The minimum amount of time that must be passed before we resend our message
	public static int RANDOM_HIGH = 3000;	// The maximum amount of time that must be passed before we resend our message
	
	public static int SOCK_PORT = 7014;	// Port to use
	public static int RECV_SIZE_UDP = 512;	// Size of each incoming UDP packet (should be greater than or equal to expected packet size)
	
	public static String localUniKey;	// The uni-key of the local peer running this program
	
	/**
	 * Parses the program configuration file specified by FILE_CONFIG_LOC, 
	 * and updates any relevant configuration properties.
	 */
	public static void parseConfigurationFile() {
		File fileConfig;
		BufferedReader reader;
		
		// Open the file
		try {
			fileConfig = new File(FILE_CONFIG_LOC);
			reader = new BufferedReader(new FileReader(fileConfig));
		} catch(IOException exception) {
			System.out.println("Could not find file '" + FILE_CONFIG_LOC + "'");
			return;
		}
		
		HashMap<String, String> mapConfig = new HashMap<String, String>(MAP_INITIALCAPACITY);
		String line;
		String[] lineSplit;
		try {
			while((line = fixLine(reader.readLine())) != null) {
				if(line.isEmpty())	// If the line is empty (e.g. it's just a comment), then do nothing
					continue;
				
				// Each line should of the format key=value, hence we split at the '=' character
				lineSplit = line.split("=", 2);
				if(lineSplit.length == 2) { 
					mapConfig.put(lineSplit[0], lineSplit[1]);
				}
			}
			
			reader.close();
		} catch (IOException e) {
			return;
		}
		
		if(mapConfig.isEmpty())
			return;
		
		// Update the relevant configuration properties
		if(mapConfig.get("FILE_PEERS_LOC") != null) {
			FILE_PEERS_LOC = mapConfig.get("FILE_PEERS_LOC");
		}
		
		if(mapConfig.get("PEER_TIME_IDLE") != null) {
			try {
				PEER_TIME_IDLE = Integer.parseInt(mapConfig.get("PEER_TIME_IDLE"));
			} catch(Exception exception) { }
		}
		
		if(mapConfig.get("PEER_TIME_DROP") != null) {
			try {
				PEER_TIME_DROP = Integer.parseInt(mapConfig.get("PEER_TIME_DROP"));
			} catch(Exception exception) { }
		}
		
		if(mapConfig.get("RANDOM_LOW") != null) {
			try {
				RANDOM_LOW = Integer.parseInt(mapConfig.get("RANDOM_LOW"));
			} catch(Exception exception) { }
		}
		
		if(mapConfig.get("RANDOM_HIGH") != null) {
			try {
				RANDOM_HIGH = Integer.parseInt(mapConfig.get("RANDOM_HIGH"));
			} catch(Exception exception) { }
		}
		
		if(mapConfig.get("SOCK_PORT") != null) {
			try {
				SOCK_PORT = Integer.parseInt(mapConfig.get("SOCK_PORT"));
			} catch(Exception exception) { }
		}
		
		if(mapConfig.get("RECV_SIZE_UDP") != null) {
			try {
				RECV_SIZE_UDP = Integer.parseInt(mapConfig.get("RECV_SIZE_UDP"));
			} catch(Exception exception) { }
		}
	}
}