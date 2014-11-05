package initiator;

import java.awt.Color;

public class InstanceColor {
	
	public static Color getColor(String instanceName, int id) {
		
		int a = (instanceName.hashCode())%256;
		int b = (id * 15 + 37)%256;
		int c = (instanceName.length() * 40 + 37)%256;
		return new Color(a, b, c);
		
	}

}
