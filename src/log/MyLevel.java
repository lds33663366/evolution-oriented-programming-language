package log;

import org.apache.log4j.Level;

public class MyLevel extends Level{
	
	public static final Level PATH = new MyLevel(Level.DEBUG_INT + 10, "SYSE", 7
			);

	protected MyLevel(int level, String levelStr, int syslogEquivalent) {
		super(level, levelStr, syslogEquivalent);
		// TODO Auto-generated constructor stub
	}

}
