
public class Parse {
	public static String fixLine(String str) {
		if(str == null)
			return null;
		
		String tempStr = str.replaceAll("\\s+", "");	// Remove all whitespace characters
		
		// Find the position of the character indicating a comment
		int i;
		for(i = 0; i < tempStr.length(); ++i) {
			if(tempStr.charAt(i) == '#')
				break;
		}
		
		// Remove the comment
		tempStr = tempStr.substring(0, i);
		return tempStr;
	}
}
