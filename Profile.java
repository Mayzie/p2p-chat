import java.util.Date;

public class Profile {
	protected final String uniKey;	// UniKey of the participant
	protected final String name;	// Name of the participant, or 'pseudo'
	protected String message = null;	// Last message of the participant
	
	private final byte[] ipAddress;	// The IP Address in valid InetAddress.getByAddress() format
	private Date lastReceived;		// The date/time at which the last message was received
	
	/**
	 * Constructor for the profile class. This should be called.
	 * 
	 * @param uniKey The uni-key of the participant/profile
	 * @param name The name, or 'pseudo', of the participant/profile
	 * @param ipAddress The remote IP Address of the participant/profile
	 */
	public Profile(String uniKey, String name, byte[] ipAddress) {
		this.uniKey = uniKey;
		this.name = name;		
		this.ipAddress = ipAddress;
	}
	
	/**
	 * Setter method. Updates the message of the participant to newMessage.
	 * @param newMessage The new message to set.
	 */
	public void setMessage(String newMessage) {
		this.message = newMessage;
	}
	
	/**
	 * Getter method. Returns the current message of the participant.
	 * @return The message.
	 */
	public String getMessage() {
		return this.message;
	}
	
	public void updateTimeReceived() {
		this.lastReceived = new Date();
	}
	
	public Date getTimeReceived() {
		return this.lastReceived;
	}
	
	public byte[] getIPAddress() {
		return this.ipAddress;
	}
}
