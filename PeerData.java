
public class PeerData {
	public String ipAddress;
	public String pseudo;
	public String uniKey;
	
	@Override
	public String toString() {
		return uniKey + " (" + pseudo + ") : " + ipAddress;
	}
}
