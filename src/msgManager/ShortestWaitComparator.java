package msgManager;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

import structure.Message;

/** 一个最短等待时间的比较器，用于消息等待队列的排序
 * 比较方法：根据该消息的时间戳加上发送时间message.time() + message.second
 * 比较中的较小值说明该message等待发送的时间短
 */
public class ShortestWaitComparator implements Comparator<Message> {

	@Override
	public int compare(Message arg0, Message arg1) {
		
		SimpleDateFormat d= new SimpleDateFormat("HH:mm:ss");
		String firstTime = d.format(arg0.getDate());
		String secondTime = d.format(arg1.getDate());
		
		double time0 = 0l, time1 = 0l;
		try {
			// 第一时间减去第二时间 这个的除以1000得到秒，相应的60000得到分，3600000得到小时
			time0 = d.parse(firstTime).getTime()/1000 + arg0.getSecond();
			time1 = d.parse(secondTime).getTime()/1000 + arg1.getSecond();
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if (time0 < time1) return -1;
		else if (time0 == time1) return 0;
		else return 1;

	}

}
