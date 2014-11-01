package structure;

import initiator.XMLSystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import structure.actionType.Action;
import msgManager.MsgHandler;
import msgManager.MsgPool;

public class Instance implements Runnable, Serializable {
	private List<Action> actionList; // 直接子元素action组成的集合
	private String name; // 该instance的名字
	private int popsize; // 该instance的数量
	private Property property; // property子元素
	private MsgHandler msghandler = new MsgHandler(this); // 消息处理器
	// private List<Message> messageList; // 该实体所能发送的消息列表
	private volatile boolean live; // 标记此Instance是否存活
	private transient XMLSystem system; // 整个实体所在的系统
	private int id;
	ExecutorService exec = null;

	public Instance(List<Action> actionList, String name, int popsize,
			Property property) {
		this.actionList = actionList;
		this.name = name;
		this.popsize = popsize;
		this.property = property;
		this.live = true;
	}
	
	public synchronized void updateProperty(String name, String value) {
		this.property.getVariableMap().get(name).setValue(value);
		deliverUpdate();
		notifyAll();
	}

	public void setId(int id) {
		this.id = id;
	}

    /**
     * 深度复制instance
     * @param instance
     * @return 复制后的instance
     */
	public Instance clone() {// 将对象写到流里
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo;
		Instance inst = null;
		try {
			oo = new ObjectOutputStream(bo);
			oo.writeObject(this);// 从流里读出来
			ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
			ObjectInputStream oi = new ObjectInputStream(bi);
			inst = (Instance) oi.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return inst;
	}
	
	synchronized void waitForUpdate() {

		while (Variable.isUpdate == false && live) {
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		deliverUpdate();
		notifyAll();
		Variable.isUpdate = false;

	}

	// 实现Runnable接口的run方法
	public void run() {
		Thread.currentThread().setPriority(4);
		// 将所有的Action对象放入线程池中运行
		exec = Executors.newCachedThreadPool();
//		ExecutorService exec = system.getExecutor();
		for (int i = 0; i < actionList.size(); i++) {
			exec.execute(actionList.get(i));
		}
		exec.execute(msghandler);
		
		while (live) {
		try {
			TimeUnit.MILLISECONDS.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
		
		close();
		

	}

	public void close() {
		live = false;
		exec.shutdown();
		msghandler.setLive(false);

		waitForUpdate();
		try {
			if (!exec.awaitTermination(200, TimeUnit.MILLISECONDS)) {
				exec.shutdownNow();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void deliverUpdate() {
		for (int i = 0; i < actionList.size(); i++) {
			actionList.get(i).isUpdate();
		}
	}

	public List<Action> getActionList() {
		return actionList;
	}

	public boolean isLive() {
		return live;
	}

	public List<Message> getMessageList() {
		return msghandler.getMessageList();
	}

	public MsgHandler getMsgHandler() {
		return this.msghandler;
	}

	public String getName() {
		return name;
	}
	
	public String getIdName() {
		return name+id;
	}

	public int getPopsize() {
		return popsize;
	}

	public Property getProperty() {
		return property;
	}

	public XMLSystem getSystem() {
		return system;
	}

	public void setLive(boolean live) {
		this.live = live;
	}
	
	public void setMessageList(List<Message> messageList) {
		msghandler.setMessageList(messageList);
	}
	
	public void addMessage(Message message) {
		msghandler.addMessage(message);
	}

	public void setSystem(XMLSystem system) {
		this.system = system;
	}

	@Override
	public String toString() {
		return "【" + getIdName() + "】" + property;
	}

	// <action>调用函数时根据<input>与<output>访问某些变量,
	// 这些变量储存在Instance的variableMap中, 为方便访问,
	// 需将这些变量拷贝一份到action的variableMap中
	public void delieverVariable() {

		for (int i = 0; i < actionList.size(); i++) {
			actionList.get(i).setVariableMap(property.getVariableMap());
		}

		// <property>下也有<instance>, 继续调用它们的delieverVariable()
		List<Instance> iList = property.getInstanceList();
		for (int i = 0; i < iList.size(); i++) {
			iList.get(i).delieverVariable();
		}
	}

	// Action需要访问或调用父元素Instance的某些成员,
	// 为访问方便, 需在Action设置一个指向父元素Instance
	// 的指针
	public void delieverInstance() {

//		msghandler = new MsgHandler(this);
		for (int i = 0; i < actionList.size(); i++) {
			actionList.get(i).setInstance(this);
		}

		// <property>下也有<instance>, 继续调用它们的delieverVariable()
		List<Instance> iList = property.getInstanceList();
		for (int i = 0; i < iList.size(); i++) {
			iList.get(i).delieverInstance();
		}
	}

	// 发送名字为messageName的消息
	public void sendMessageToPool(String messageName) {
		msghandler.sendMessageToPool(messageName);
	}
	// 发送主题为topic, 名字为messageName的消息
	public void sendMessageToPool(String topic, String messageName) {
		msghandler.sendMessageToPool(topic, messageName);
		
	}

	// 接收消息
	public Message sendMessageToAction(String actionName) {
		return msghandler.sendMessageToAction(actionName);
	}

	public void setRelation(Relation relation) {
		msghandler.setRelation(relation);
	}

	public void setMessagePool(MsgPool mp) {
		msghandler.setMessagePool(mp);
	}

	public void subscription(String topic, String actionName) {
		msghandler.subscription(topic, actionName);
		
	}

	/**
	 * 取出instance的属性，如果instance里还有instance，可使用"."分隔取出
	 * 例如：position.x, weight, life等等
	 * @param valueName 属性名
	 * @return 属性值
	 */
	public String obtainValue(String valueName) {

		String str[] = valueName.split("\\.", 2);
		String propName = str[0];
		Property prop = this.getProperty();
		
		if (str.length == 1) {
			Variable v;
			if ((v = prop.getVariable(propName)) != null)
				return v.getValue();
		} else if (str.length == 2) {
			Instance inst;
			if ((inst = prop.getInstance(propName)) != null) {
				return inst.obtainValue(str[1]);
			}
		}
		return null;
	}

	public void motify(String valueName, String value) {
//System.out.println("要修改的属性：" + valueName + "; 修改的值为：" + value);
		String str[] = valueName.split("\\.", 2);
		String propName = str[0];
		Property prop = this.getProperty();
		
		if (str.length == 1) {
			prop.setVariable(propName, value);
		} else if (str.length == 2) {
			Instance inst;
			if ((inst = prop.getInstance(propName)) != null) {
				inst.motify(str[1], value);
			}
		}
	}

	
}
