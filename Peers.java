import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Peers extends Parse {
	public static  Map<String, Profile> peers;
	
	/**
	 * Parses the peers properties file and returns a map of the key-value pairs.
	 * @param peersFileName The location of the peers properties file.
	 * @return
	 */
	public static Map<String, Profile> parsePeers(String peersFileName) {
		File fileConfig;
		BufferedReader reader;
		
		try {
			fileConfig = new File(peersFileName);
			reader = new BufferedReader(new FileReader(fileConfig));
		} catch(IOException exception) {
			System.out.println("Could not find file '" + peersFileName + "'");
			return null;
		}
		
		Map<String, PeerData> tempMap = null;
		String line;
		String[] lineSplit, anotherSplit;
		try {
			while((line = fixLine(reader.readLine())) != null) {
				if(line.isEmpty())
					continue;
				
				try {
					if((line.startsWith("participants")) && (tempMap == null)) {
						tempMap = new HashMap<String, PeerData>();
						
						for(String peerName : line.split("=", 2)[1].split(",")) {
							tempMap.put(peerName, new PeerData());
						}
					} else {
						if(tempMap == null)
							continue;
		
						lineSplit = line.split("\\.", 2);
						if(lineSplit.length == 2) {
							anotherSplit = lineSplit[1].split("=", 2);
							
							PeerData peerData = tempMap.get(lineSplit[0]);
							if(peerData == null) {
								continue;
							}
							
							if(anotherSplit[0].equals("ip")) {
								peerData.ipAddress = anotherSplit[1];
							} else if(anotherSplit[0].equals("pseudo")) {
								peerData.pseudo = anotherSplit[1];
							} else if(anotherSplit[0].equals("unikey")) {
								peerData.uniKey = anotherSplit[1];
							}
						}
					}
				} catch(Exception exception) { continue; }
			}
			
			reader.close();
		} catch(IOException exception) {
			return null;
		}
		
		Map<String, Profile> result = new HashMap<String, Profile>();
		for(PeerData peerData : tempMap.values()) {
			byte[] fixedIPAddress = new byte[4];
			
			String[] splitIPAddress = peerData.ipAddress.split("\\.");
			for(int i = 0; i < splitIPAddress.length; ++i) {
				fixedIPAddress[i] = (byte) Integer.parseInt(splitIPAddress[i]);
			}
			
			Profile profile = new Profile(peerData.uniKey, peerData.pseudo, fixedIPAddress);
			result.put(peerData.uniKey, profile);
		}
		
		return result;
	}
}
