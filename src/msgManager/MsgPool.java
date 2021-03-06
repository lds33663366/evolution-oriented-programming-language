package msgManager;

import initiator.ThreadTimeConsole;
import initiator.TopicSubscriber;
import initiator.XMLServer;
import initiator.XMLSystem;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import log.MyLogger;

import org.apache.log4j.Logger;

import structure.Message;

/**系统有一个消息池MsgPool，为了所有的instance能拿到同一个消息池
 * 消息池构造为单例模式。
 * exp：MsgPool mp = MsgPool.getInstance();
 * 
 * 消息池包含发送队列和等待队列
 * 等待队列处于等候状态，当等候队列中的消息满足一定条件时，将该消息移入发送队列
 */
public class MsgPool implements Runnable{
	
	private static MsgPool msgPool = null;
	//消息池的发送队列（由instance发来的消息）
	Logger logger = MyLogger.getLogger();
	
	private Queue<Message> sendingQueue;
	//等待队列，当达到一定条件时，将放入发送队列
	//该队列构造为最短时间等待优先
	private Queue<Message> waitingQueue;
	//消息缓存，存储需要再次发送的消息
	private Set<Message> messageCache = new HashSet<Message>();
	
	private List<Topic> topics;
	private XMLSystem xmlSystem;
	
	private volatile boolean live;
	
	private XMLServer server;
	
	
	//阻止反序列化而生成新的单例
	private Object readResolve() {
		if (msgPool == null) {
			msgPool = new MsgPool();		
		}	
		return msgPool;
    }
	
	private MsgPool(){
		//启动线程管理消息池
		sendingQueue = 
				new PriorityBlockingQueue<Message>(100, new PriorityComparator());
		waitingQueue = 
				new PriorityBlockingQueue<Message>(100, new ShortestWaitComparator());

		topics = new ArrayList<Topic>();
		
		server = new XMLServer();
		new Thread(server).start();

		live = true;
	}
	
	/**
	 * 主题注册接口，返回是否注册成功
	 * @param topic 需要监听的主题
	 * @param msgHandler 监听对象
	 * @return 是否注册成功
	 */
	public boolean addListener(String topic, TopicSubscriber subscriber) {

		Topic t = takeTopic(topic);
		if (t == null) {
			t = createTopic(topic);
			if (!topics.contains(t)) {
				topics.add(t);
			}
		}
		t.addTopicSubscriber(subscriber);
		return true;
	}
	
	private synchronized Topic createTopic(String topic) {
		Topic t = takeTopic(topic);
		if (t == null) return new Topic(topic);
		else return t;
	}
	
	public boolean removeListener(String topic, TopicSubscriber subscriber) {
		Topic t = takeTopic(topic);
		if (t == null) {
			return true;
		}else {
			return t.removeTopicSubscriber(subscriber);
		}
	}
	
	public static MsgPool getInstance() {
		
		if (msgPool == null) {
			msgPool = new MsgPool();		
		}	
		return msgPool;
	}
	
	public void setXmlSystem(XMLSystem xmlSystem) {
		this.xmlSystem = xmlSystem;
	}

	/** 当消息满足发送条件时，将等待队列的消息送入发送队列
	 * 满足条件1：消息满足发送时间要求：消息的second属性满足
	 */
	void moveToSendingqueue() {

		if (!live) return;
		for (int i = 0; i < waitingQueue.size(); i++) {

			Message m = waitingQueue.peek();
			if (timeSubtract(new Date(), m.getDate()) >= (m.getSecond()*1000)) {
//				if (m.getName().equals("END")) {
//					System.out.println("如果END是推送到发送列表的消息：\n");
//					System.out.println("当前时间：" + new Date());
//					System.out.println("消息时间：" + m.getDate());
//					System.out.println("发送秒数：" + m.getSecond());
//				}
				
				//判断是否为主题消息，如果不是则放入发送队列，否则放入相应主题区域
				if (m.getTopic() == null) {
					sendingQueue.offer(m);
				} else {
					Topic t = takeTopic(m.getTopic());
					t.addMessage(m);
				}
				waitingQueue.remove(m);
			} else
				break;
		}
	}
	
	/** 判断是否将msg放入等待队列 判断方法：检验msg的frequency值,当消息的发送次数大于0时，
	 * 说明该消息需要加入等待队列，等待再次被发送的时间到来。
	 * 如果要放入等待队列，则waitingQueue添加message的最初信息，
	 * 将最初信息old_message修改frequency和date，添加入waitingQueue
	 * 并返回true，否则返回false
	 * @param 消息包
	 * @return 是否放入等待队列
	 */
	boolean moveToWaitingQueue(Message msg) {

		int frequency = msg.getFrequency() - 1;
		if (frequency > 0) {

			Message oldm = returnOldMessage(msg);
			oldm.setFrequency(frequency);
			oldm.setDate(new Date());
//			oldm.setShare(msg.getShare());

			waitingQueue.add(oldm.clone());
			// System.out.println("消息已存入等待队列！");
			return true;
		}
		// System.out.println("消息不能放入等待队列");
		return false;
	}

	/** 是MsgHandler检测消息的接口，当MsgHandler调用该接口时
	 * 如果该消息已死亡，则检测下一个消息包，知道消息包属于正常消息则返回该消息
	 * @return 要检测的消息包
	 */
	public Message detectMessage() {
		synchronized (sendingQueue) {
			if (sendingQueue == null) return null;
			while (messageDead(sendingQueue.peek())) {
				moveToWaitingQueue(sendingQueue.poll());
			}
			return sendingQueue.peek();
		}
	}

	//接收消息的方法体
	public void obtainMessage(Message msg) {
		
		synchronized(waitingQueue) {
			waitingQueue.offer(msg);
			if (msg.getFrequency() > 1) 
				messageCache.add(msg.clone());
//			printWaitingMessage();
		}
	}

	/**
	 * 获取主题消息，消息的topic属性在此方法体里设置；
	 * 与一般消息一样，也需要将主题消息放入等待队列，在此之前，如果没有topic主题，
	 * 则需要建立该topic对象，并需要设置消息topic属性，方便主题区域从等待队列
	 * 获取消息。
	 * @param topic 主题名称
	 * @param message 对应的主题消息
	 */
	public void obtainTopicMessage(String topic, Message message) {
		Topic t = takeTopic(topic);
		if (t == null) {
			t = createTopic(topic);
			if (!topics.contains(t)) {
				topics.add(createTopic(topic));
			}
		}
		message.setTopic(topic);
		obtainMessage(message);
	}

	/**
	 * 判断是否将消息移除发送队列,share值首先-1,如果share<=0，则需要移除
	 * 如果需要移除，则返回true，否则返回false
	 * @param msg
	 * @return 是否要移除msg
	 */
	boolean removeFromSendingMessage(Message msg) {
		
		int share = msg.getShare() - 1;
		msg.setShare(share);
		if (share <= 0) return true;
		return false;
	}
	
	//判断消息是否死亡
	boolean messageDead(Message msg) {
		
		if (msg == null) return false;
		if(timeSubtract(new Date(), msg.getDate()) > (msg.getMillisecondLife())) {
			logger.debug("该消息已过时:" + msg);
			return true;
		}
		return false;
	}
	
	/** 发送instance消息
	 * 首先判断是否该消息是否要被移除出发送队列；
	 * 其次判断该消息是否进入等待队列
	 * 最后发送消息
	 * @return 消息
	 */
	public Message sendMessage() {
	
		Message msg = null;
	
		synchronized (sendingQueue) {
			msg = sendingQueue.peek();
			if (msg == null) return null;
			if (removeFromSendingMessage(msg)) {
				moveToWaitingQueue(msg);
				sendingQueue.poll();
			}
			return msg.clone();
		}
	}

	/**
	 * 发送消息池里所有主题里的消息
	 */
	private void sendTopics() {
		
		if (topics == null || topics.size()<=0) return;

		for (int i=0, j=topics.size(); i<j; i++) {
			topics.get(i).notifyMessage();
		}
	}
	
	/** 查找消息的最早来源，用于修改
	 * @param 需要查询的消息
	 * @return 该消息的最早记录消息
	 */
	Message returnOldMessage(Message m) {
		
		for (Iterator<Message> i=messageCache.iterator(); i.hasNext();) {
			Message oldm = i.next();
//			System.out.println("new message is " + m);
//			System.out.println("old message is " + oldm);
			if (m.equals(oldm))
				return oldm;
		}
		throw new RuntimeException("消息没有放入缓存");
	}

	/**
	 * 计算两个时间的差值
	 * @param 被减日期
	 * @param 减数日期
	 * @return 差值
	 */
	long timeSubtract(Date d1, Date d2) {
		
//		SimpleDateFormat d= new SimpleDateFormat("HH:mm:ss");
//		String firstTime = d.format(d1);
//		String secondTime = d.format(d2);
//		
//		long result = Long.MIN_VALUE;
//		try {
//			// 第一时间减去第二时间 这个的除以1000得到秒，相应的60000得到分，3600000得到小时
//			result = (d.parse(firstTime).getTime() - d.parse(secondTime)
//					.getTime()) / 1000;
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		long l1 = d1.getTime();
		long l2 = d2.getTime();

		return l1-l2;

	}

/*	public void printWaitingMessage() {
		System.out.println("消息等待列表：");
		for (Iterator<Message> i=waitingQueue.iterator(); i.hasNext();) {
			System.out.println(i.next());
		}
	}

	public void printSendingMessage() {
		System.out.println("消息发送列表：");
		for (Iterator<Message> i = sendingQueue.iterator(); i.hasNext();) {
			System.out.println(i.next());
		}
	}
	
	public void printMessageCache() {
		System.out.println("消息缓存列表：");
		for (Iterator<Message> i=messageCache.iterator(); i.hasNext();) {
			System.out.println(i.next());
		}
	}*/

	public static void main(String[] args) {/*
	
		MsgPool mp = new MsgPool();
	
		Message m10 = new Message("eat_zebra", new Date(), "lion", 0, 2, "1s", 
				10, 2);
		Message m11 = new Message("eat_zebra", new Date(), "lion", 0, 2, "1s", 
				12, 2);
		Message m12 = new Message("eat_zebra", new Date(), "lion", 0, 2, "1s", 
				18, 2);
		Message m13 = new Message("eat_zebra", new Date(), "lion", 0, 2, "1s", 
				8, 2);
		Message m2 = new Message("age_increment", new Date(), "SYSTEM", 0, 2, "10s",
				15, 1);
	
		mp.obtainMessage(m10);
		mp.obtainMessage(m11);
		mp.obtainMessage(m12);
		mp.obtainMessage(m13);
	
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		mp.sendMessage();
		mp.sendMessage();
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		mp.sendMessage();
		mp.sendMessage();
	
		while (mp.waitingQueue.size() != 0) {
			Message m = mp.waitingQueue.poll();
			System.out.println(m);
		}
	
	*/}

	public void close() {
		live = false;
	}

	private Topic takeTopic(String topic) {
		for (int i=0, j=topics.size(); i<j; i++) {
			Topic t = topics.get(i);
			if (t.getName().equals(topic)) {
				return t;
			}
		}
		return null;
	}
	
	/**一个管理消息池的线程，作用：
	 * 1、隔1秒询问是否将等待队列的消息放入发送队列
	 * 2、
	 */

		@Override
		public void run() {
			
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			while (live) {
				sendTopics();
				moveToSendingqueue();
				try {
					TimeUnit.MILLISECONDS.sleep(ThreadTimeConsole.Thread_MsgPool.getTime());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Thread.yield();
	
			}
			logger.info("消息池已关闭！");
		}
		

	class Topic {
		private String name; //主题名称
		private int priority;
		private List<TopicSubscriber> topicSubscribers; //订阅该主题的队列
		private Queue<Message> messages; //主题的消息包
		
		public Topic(String name) {
			this.name = name;
			topicSubscribers = new CopyOnWriteArrayList<TopicSubscriber>();
			messages = new ArrayBlockingQueue<Message>(10);
		}
		
		public String getName() {
			return name;
		}
		
		/**
		 * 订阅该主题的监听器添加接口
		 * @param msgHandler MsgHandler接口
		 */
		public void addTopicSubscriber(TopicSubscriber topicSubscriber) {
			topicSubscribers.add(topicSubscriber);
		}
		
		/**
		 * 移除监听接口
		 * @param msgHandler 要移除的MsgHandler接口
		 */
		public boolean removeTopicSubscriber(TopicSubscriber subscriber) {
			synchronized (topicSubscribers) {
				return topicSubscribers.remove(subscriber);
			}
		}
		
		/**
		 * 主题消息的添加接口
		 */
		public void addMessage(Message message) {
			messages.offer(message);
		}
		
		
		
		/**
		 * 发送主题里所有的消息给所有的订阅者
		 */
		public void notifyMessage() {
			//如果消息不存在，或者pool已关闭（时刻判断）则返回
			if (messages == null || !live)	return;
			
			while (!messages.isEmpty()) {
				
				
				Message m = messages.poll();
				//如果发送的是END消息，则SYSTEM关闭
				if (name.equals("SYSTEM") && m.getName().equals("END")){
					xmlSystem.setLive(false);
				}

				if (name.equals("DISPLAY")) {
					server.send(m.deepClone());
				}
				
				//调用每个监听的msgHandler的obtainTopicMessage()函数
				synchronized (topicSubscribers) {
					for (int i = 0; i < topicSubscribers.size(); i++) {

						TopicSubscriber tsub = topicSubscribers.get(i);
						tsub.obtainTopicMessage(m);
					}
				}
				moveToWaitingQueue(m);
			}
		}
		
//		public void printTopicInfo() {
//			System.out.println("消息主题：" + name
//					+ "; 订阅实体数: " + msgHandlerList.size() 
//					+ "; 消息列表：\n" + messages.toString());
//		}
	}
}


