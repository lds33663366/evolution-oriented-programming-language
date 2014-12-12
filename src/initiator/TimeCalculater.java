package initiator;

public class TimeCalculater {
	
	public static long getMillisecond(String formatTime) {
		String sign = formatTime.substring(formatTime.length()-1);
		double time = Double.parseDouble(formatTime.substring(0, formatTime.length()-1));
		//转化成毫秒
		long msTime = (long) (time * 1000);
		switch(sign) {
		case "s":
			return msTime;
		case "m":
			return msTime * 60;
		case "h":
			return msTime * 3600;
		case "D":
			return msTime * 3600 * 24;
		case "M":
			return msTime * 30 * 3600 * 24;
		case "Y":
			return msTime * 12 * 30 * 3600 * 24;
		default:
			throw new RuntimeException("不合法的时间规范");
		}
	}
	
	public static long getSecond(String formatTime) {
		return getMillisecond(formatTime) / 1000;
	}

}
