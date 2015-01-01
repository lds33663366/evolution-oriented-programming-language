package structure;
import initiator.TimeCalculater;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Message implements Cloneable, Serializable{
	private String name; // Message的名字
	private Date date; // 发送时间
	private String from; // 发关此Message的Instance名字
	private int frequency; // 发送次数
	private String life; // 生存时间
	private int priority; // 优先级
	private double second; // Message将于多长时间后发送
	private int share; // 可接收此Message的instance的最大数量
	private static int ID = 0;
	private int id;
	private String topic; //消息主题，如果没有则为null
	
	private List<MessageContent> contentList;

	public Message(String name, Date date, String from, int priority, int frequency, String life,
			double second, int share, List<MessageContent> contentList) {
		this.date = date;
		this.frequency = frequency;
		this.from = from;
		this.life = life;
		this.name = name;
		this.priority = priority;
		this.second = second;
		this.share = share;
		this.contentList = contentList;
		
		this.id = ID++;
	}

	public Message clone() {
		Message o = null;
		try {
			o = (Message) super.clone();
	//		o.id = ID++;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
	
	public int getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public int getFrequency() {
		return frequency;
	}

	public String getFrom() {
		return from;
	}

	public String getLife() {
		return life;
	}

	public String getName() {
		return name;
	}

	public int getPriority() {
		return priority;
	}

	public double getSecond() {
		return second;
	}

	public int getShare() {
		return share;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setLife(String life) {
		this.life = life;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public void setShare(int share) {
		this.share = share;
	}
	

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	

	public List<MessageContent> getContentList() {
		return contentList;
	}

	public void setContentList(List<MessageContent> contentList) {
		this.contentList = contentList;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Message "+id + " [name=" + name + ", date=" + date + ", from=" + from
				+ ", frequency=" + frequency + ", life=" + life + ", priority="
				+ priority + ", second=" + second + ", share=" + share + "]") ;
		if (contentList != null && contentList.size()>0) {
			sb.append("\n");
			for (MessageContent mc : contentList) {
				sb.append(mc + "\t");
			}
		}
		
		return sb.toString();
	}
	
	public long getMillisecondLife() {
		
		return TimeCalculater.getMillisecond(life);
		
	}

	@Override
	public boolean equals(Object arg0) {
		
		Message m = (Message)arg0;
		if (this.getName().equals(m.getName()) 
				&&this.getFrom().equals(m.getFrom())
				&&this.getDate().getTime() == m.getDate().getTime())
			return true;
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public String obtainValue(String valueName) {
//		String s[] = valueName.split("\\.", 2);
//		String fromName = s[0].trim();
//		String instanceName = from.split("\\d+")[0].trim();
//		if (!fromName.equals(instanceName)) {
//			new RuntimeException(valueName + "书写有误，请检查" + fromName + "是否正确！");
//		}
		String str[] = valueName.split("\\.", 2);
		String propName = str[0];
		if (str.length == 1) {
			Variable v = obtainVariable(propName);
			if (v != null) return v.getValue();
		} else if(str.length == 2) {
			Instance inst = obtainInstance(propName);
			if (inst != null) {
				return inst.obtainValue(str[1]);
			}
		}
		return null;
	}
	
	public double obtainDoubleValue(String valueName) {
		return Double.parseDouble(obtainValue(valueName));
	}
	
	public int obtainIntValue(String valueName) {
		return Integer.parseInt(obtainValue(valueName));
	}
	
	public boolean obtainBooleanValue(String valueName) {
		return Boolean.parseBoolean(obtainValue(valueName));
	}
	
	private Variable obtainVariable(String name) {
		
		for (int i=0; i<contentList.size(); i++) {
			MessageContent mc = contentList.get(i);
			if (mc.getName().equals(name)) return mc.getVariable();
		}
		return null;
	}
	
	private Instance obtainInstance(String name) {
		
		for (int i=0; i<contentList.size(); i++) {
			MessageContent mc = contentList.get(i);
			if (mc.getName().equals(name)) return mc.getInstance();
		}
		return null;
	}
}
