package msgManager;

import java.io.Serializable;
import java.util.Comparator;

import structure.Message;

/**优先级比较器
 * 如果两个消息都是SYSTEM的或者都不是，则比较这两个信息的priority值
 * 如果两个消息只有其中一个是SYSTEM消息，则SYSTEM消息的优先级高
 */
public class PriorityComparator implements Comparator<Message> {

	@Override
	public int compare(Message arg0, Message arg1) {
		
		if ((arg0.getFrom().equals("SYSTEM") && arg1.getFrom().equals("SYSTEM"))
				|| !(arg0.getFrom().equals("SYSTEM") || arg1.getFrom().equals("SYSTEM"))) {		
			return arg0.getPriority() - arg1.getPriority();
		}
		else return arg0.getFrom().equals("SYSTEM") ? -1 : 1;
	}

}
