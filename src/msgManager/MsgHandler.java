package msgManager;

import initiator.ThreadTimeConsole;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.text.AbstractDocument.Content;

import structure.Instance;
import structure.Message;
import structure.MessageContent;
import structure.Relation;
import structure.Variable;

/**
 * 每个instance都有个MsgHandler，用于处理消息。
 */
public class MsgHandler implements Runnable, Serializable{

	transient MsgPool mp = null;
	//handler拿到relation，用于检测消息是否符合要求
	transient Relation relation = null;
	Instance instance = null;
	// 消息缓存，用于检查是否会拿到消息池的重复消息
	Set<Message> messageCache = new HashSet<Message>();
	List<Message> messageList = null;
	Map<String, CopyOnWriteArrayList<Message>> action_message = new HashMap<String, CopyOnWriteArrayList<Message>>();
	Map<String, String> topic_action = new HashMap<String, String>();
	
	private volatile boolean live = true;
	
	public MsgHandler(Instance instance) {
		this.instance = instance;
//		mp = MsgPool.getInstance();
	}
	

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}



	/** 发送消息
	 * @param 消息包
	 */
	public void sendMessageToPool(String messageName) {
		Message m = searchMessage(messageName);
		packageMessage(m);
		mp.obtainMessage(m);
	}
	
	public void sendMessageToPool(String topic, String messageName) {
		Message m = searchMessage(messageName);
		packageMessage(m);
		mp.obtainTopicMessage(topic, m);
	}
	
	/**
	 * 封装消息：如果消息有内容，则填写内容
	 * @param message 需要封装的消息
	 * @return 封装后的消息
	 */
	private void packageMessage(Message message) {
		
		List<MessageContent> contentList = message.getContentList();
		if (contentList == null || contentList.size()<=0) return;
		
		for (Iterator<MessageContent> i=contentList.iterator(); i.hasNext();) {
			MessageContent mc = i.next();
			fillContent(mc);
		}
	}
	
	/**
	 * 封装消息内容：判断消息内容的类型并填写
	 * @param mc 需要填写的消息内容
	 * @return 填写后的消息内容
	 */
	private void fillContent(MessageContent mc) {
		
		String type = mc.getType();
		switch (type) {
		case "instance":
			mc.setInstance(instance);
			break;
		case "property":
			String name = mc.getName();
			Variable v = instance.getProperty().getVariable(name);
			if (v == null) {
				Instance inst = instance.getProperty().getInstance(name);
				if (inst == null) 
					throw new RuntimeException("消息内容填写错误！请更改内容" + mc);
				else mc.setInstance(inst);
			} else mc.setVariable(v);
			break;
		}	
	}

	/**一个action可以获取消息的接口
	 * @param 动作名
	 * @return 消息名，如果没有消息则返回null
	 */
	public Message sendMessageToAction(String actionName) {
		
		CopyOnWriteArrayList<Message> messages;
		Message m = null;

		if (action_message.containsKey(actionName)) {
			messages = action_message.get(actionName);
			if (messages != null && messages.size() != 0) {
				m = messages.remove(0);
			}
			action_message.put(actionName, messages);

			return m;
		}

		return null;
		
	}
	
	public Relation getRelation() {
		return relation;
	}


	public void setRelation(Relation relation) {
		this.relation = relation;
	}


	public List<Message> getMessageList() {
		return messageList;
	}

	public void setMessageList(List<Message> messageList) {
		this.messageList = messageList;
	}

	/** 获取消息，并将action的名字和消息存入action_message该Map结构中
	 * @return 消息包
	 */
	public Message obtainMessage() {

		String actionName = null;
		if ((actionName = detectMessage()) != null) {
			Message m = mp.sendMessage();
			CopyOnWriteArrayList<Message> mList = action_message.get(actionName);
			if (mList == null) mList = new CopyOnWriteArrayList<Message>();
			mList.add(m);
			action_message.put(actionName, mList);
			messageCache.add(m);
			return m;
		}
		return null;
	}
	
	public Message obtainTopicMessage(Message msg) {

		String topic = msg.getTopic();
		String actionName = topic_action.get(topic);
		CopyOnWriteArrayList<Message> mList = action_message.get(actionName);
		if (mList == null) mList = new CopyOnWriteArrayList<Message>();
		mList.add(msg);
		action_message.put(actionName, mList);
//		System.out.println("已拿到主题消息" + topic + "; 关联动作是" + actionName);
		return msg;
	}

	//根据消息名字到消息列表messageList查找消息包
	private Message searchMessage(String messageName) {
		Message m = null;
		Message mclone = null;
		
		if (messageList == null) throw new RuntimeException("该instance没有要发送的消息");
		for (Iterator<Message> i=messageList.iterator();i.hasNext();) {
			m = i.next();
			if (m.getName().equals(messageName)) {
				mclone = m.clone();
				mclone.setDate(new Date());
				//如果From是大写单词，则该消息是主题消息，不修改from
//				if (mclone.getFrom().matches("\\[A-Z]+")) {
//					System.out.println(mclone.getFrom() + "是大写字母！");
				 mclone.setFrom(instance.getIdName());
//				System.out.println("要发送到池的消息是--" + mclone);
				return mclone;
			}
		}
		
		throw new RuntimeException("找不到要发送的消息(" + messageName 
				+ "), 请检查要发送的消息名字拼写是否正确");
	}


	 //检查是否是该instance想要的信息,并检查message是否是重复的，重复的则不进行读取
	private String detectMessage() {

		Message m = mp.detectMessage();
		String actionName = null;
		
		if (null == m) 
			return null;
		// 判断是否是重复的消息
		else if (messageCache.contains(m)) {
			System.out.println("消息重复，不接收");
			return null;
		}
		// 判断是否是需要的消息
		else if ((actionName = relation.detectRelation(m.getName(), instance.getName()))
				!= null) {
			return actionName;
		}
		else
			return null;
	}

	public void printMessages() {

		System.out.println("instance的消息发送队列：");
		for (int i=0, j=messageList.size(); i<j; i++) {
			System.out.println(messageList.get(i));
		}
	}
	
	@Override
	public void run() {
		
		while (live) {
			try {
				TimeUnit.MILLISECONDS.sleep(ThreadTimeConsole.Thread_MsgHandler.getTime());
			} catch (InterruptedException e) {
//				e.printStackTrace();
			} 
//			sendMessageToPool("eat_zebra");
//			if (!subSystem) {
//				subscription("SYSTEM", "");
//				subSystem = true;
//			}
			obtainMessage();
		}
	}

	public void addMessage(Message m) {
		if (messageList == null) 
			messageList = new ArrayList<Message>();
		messageList.add(m);
	}

	public void setMessagePool(MsgPool mp) {
		this.mp = mp;
	}

	public void subscription(String topic, String actionName) {
//		System.out.println("请求订阅" + topic);
		
		if (topic_action == null) {
			topic_action = new HashMap<String, String>();
		}
		topic_action.put(topic, actionName);
		while (!mp.addListener(topic, this)) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
//		System.out.println(actionName + "需要订阅" + topic);
	}

	

}
