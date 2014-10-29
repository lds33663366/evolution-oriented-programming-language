package initiator;

// 对应<system>标签, 为与java.lang.System区别, 
// 取名为XMLSystem. 这是目标程序运行所在的整个环境
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import function.SystemFunctionbase;
import msgManager.MsgPool;
import structure.Instance;
import structure.Message;
import structure.Relation;

public class CopyOfXMLSystem {

	private List<Instance> instanceList;// 解析<instance>得到的对象
	private List<Message> messageList; // message组成的集合
	private List<Relation> relationList; // relation组成的集合
	private MsgPool mp; // 消息池
	private ExecutorService executor; // 线程池
	private InstancesManager iMgr;
	private PrintInformation pi;

	
	private volatile boolean live = true;

	// 存放Instance对象, 同名的对象放在同一线表中
	// 此处存放的Instance对象是以instanceList中的元素为蓝本创建的

	public void setLive(boolean live) {
		this.live = live;
	}

	public CopyOfXMLSystem(List<Instance> instanceList, List<Message> messageList,
			List<Relation> relationList) {
		this.instanceList = instanceList;
		this.messageList = messageList;
		this.relationList = relationList;
		iMgr = new InstancesManager();
	}
	
	public ExecutorService getExecutor() {
		return executor;
	}

	public List<Instance> getInstanceList() {
		return instanceList;
	}

	public List<Message> getMessageList() {
		return messageList;
	}

	public List<Relation> getRelationList() {
		return relationList;
	}

	/*
	 * 启动程序, 执行xml. 该步骤包括: 1.根据popsize生成Instance对象, 存入InstanceMap;
	 * 2.根据<relation>, 建立Message与Instance之间的联系 3.启动Instance
	 */
	public void start() {
		
		

//		SystemFunctionbase.xmlSystem = this;
		// 消息池初始化
		mp = MsgPool.getInstance();
		mp.setXmlSystem(this);
		// 以解析<instance>得到的对象为蓝本, 生成指定数量的Instance对象,
		// 每个Instance对象都自己独立的数据空间与运行时间
		messageRegister();
		generateInstances();

		// 初始化全部的Instance, 完善Instance对象的各个成员变量, 放入线程池中开始运行
		initializeInstances();

		// 将instanceMap中全部的实体放放线程池中运行
		putInstanceToPool();
		
		while (live) {
			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		close();
	}

	// 将instanceMap中全部的实体放放线程池中运行
	private void putInstanceToPool() {

		if (executor == null)
//			executor = Executors.newScheduledThreadPool(20);
//			executor = Executors.newFixedThreadPool(4000);
			executor = Executors.newCachedThreadPool();
		
		executor.execute(mp);

		for (Iterator<Entry<String, CopyOnWriteArrayList<Instance>>> iter = iMgr
				.getInstanceMap().entrySet().iterator(); iter.hasNext();) {
			Entry<String, CopyOnWriteArrayList<Instance>> entry = iter.next();
			CopyOnWriteArrayList<Instance> iList = entry.getValue();
			for (int j = 0; j < iList.size(); j++) {
				Instance inst = iList.get(j);
				executor.execute(inst);
			}
		}
		pi = new PrintInformation();
		executor.execute(pi);
	}

	public void close() {
		
		live = false;
		executor.shutdown();
		// 所有实体关闭
		System.out.println("结束消息已发布，系统将关闭运行！");
		//打印端关闭
		pi.close();
		mp.close();
		// 线程管理器关闭
		for (Iterator<Entry<String, CopyOnWriteArrayList<Instance>>> i = iMgr
				.getInstanceMap().entrySet().iterator(); i.hasNext();) {
			Entry<String, CopyOnWriteArrayList<Instance>> entry = i.next();
			CopyOnWriteArrayList<Instance> instanceList = entry.getValue();
			for (int j = 0, k = instanceList.size(); j < k; j++) {
				instanceList.get(j).setLive(false);
			}
		}
		try {
			if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
				executor.shutdownNow();
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("所有实体已关闭！");
		// 消息池关闭
		System.out.println("线程管理器已关闭！");
		
	}

	// 初始化instanceMap中全部的Instance, 完善Instance对象的各个成员变量
	private void initializeInstances() {
		// 完善Action对象的成员变量, 例如variableMap, instance
		for (Iterator<Entry<String, CopyOnWriteArrayList<Instance>>> iter = iMgr
				.getInstanceMap().entrySet().iterator(); iter.hasNext();) {
			Entry<String, CopyOnWriteArrayList<Instance>> entry = iter.next();
			CopyOnWriteArrayList<Instance> iList = entry.getValue();
			for (int j = 0; j < iList.size(); j++) {
				initializeInstance(iList.get(j));
			}
		}
	}

	private void initializeInstance(Instance inst) {
		// <action>调用函数时根据<input>与<output>访问某些变量,
		// 这些变量储存在Instance的variableMap中, 为方便访问,
		// 需将这些变量拷贝一份到action的variableMap中
		inst.delieverVariable();
		// Action需要访问或调用父元素Instance的某些成员,
		// 为访问方便, 需在Action设置一个指向父元素Instance
		// 的指针
		inst.delieverInstance();
		inst.setSystem(this);
		inst.setMessagePool(mp);
		if (relationList.size()>0) {
			inst.setRelation(relationList.get(0));
		}
	}

	private void messageRegister() {

		for (int i = 0; i < messageList.size(); i++) {
			Message msg = messageList.get(i);
			String from = msg.getFrom();

			if (from.equals("SYSTEM")) {
				mp.obtainTopicMessage("SYSTEM", msg);
				continue;
			}

			// instanceMap中可能没有from名称的实体, 例如当from为"SYSTEM"时
			if (instanceList == null || instanceList.size() == 0)
				throw new RuntimeException("没有instance在xml中，请检查xml文件");
			for (Iterator<Instance> iter = instanceList.iterator(); iter
					.hasNext();) {
				Instance inst = iter.next();
				if (from.equals(inst.getName())) {
					inst.addMessage(msg);
				}
			}
		}
	}

	// 以解析<instance>得到的对象为蓝本, 生成指定数量的Instance对象,
	// 每个Instance对象都自己独立的数据空间与运行时间
	private void generateInstances() {

		for (int i = 0; i < instanceList.size(); i++) {
			generateInstance(instanceList.get(i));
		}
	}

	private void generateInstance(Instance instance) {

		for (int j = 0; j < instance.getPopsize(); j++) {
			iMgr.add(instance.clone());
		}
	}

	// 创建一个新实体并放入instanceMap
	public void createInstance(Instance instance) {

		if (!live) return;//如果xmlSystem已关闭，则直接返回
		Instance instanceModel = null;
		for (Iterator<Instance> iter=instanceList.iterator(); iter.hasNext();) {
			instanceModel = iter.next();
			if (instanceModel.getName().equals(instance.getName()))
				break;
		}
		Instance newInstance = instanceModel.clone();
		iMgr.add(newInstance);
		initializeInstance(newInstance);
		executor.execute(newInstance);
	}

	// 从instanceMap中移除一个Instance对象
	public void remove(Instance instance) {
		iMgr.remove(instance);
	}

	//
	// // 测试方法
	// public void test() throws Exception {
	// executor.shutdown();
	// executor.awaitTermination(10, TimeUnit.SECONDS);
	// List<Instance> list = instanceMap.get("lion");
	// System.out.println("lion: " + list.size());
	// list = instanceMap.get("zebra");
	// System.out.println("zebra: " + list.size());
	//
	// }
	class PrintInformation implements Runnable {

		// 存放打印的信息
		StringBuffer sbuffer;
		private boolean isprint = true;

		@Override
		public void run() {

//			Thread.currentThread().setDaemon(true);
			Thread.currentThread().setPriority(4);
			while (isprint) {
				System.out.println("线程数有" + Thread.activeCount() + "个");
				sbuffer = new StringBuffer();
				sbuffer.append(iMgr.printAllInstances());
				printInfo();
				try {
					TimeUnit.SECONDS.sleep(3);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Thread.yield();
			}
			System.out.println("信息显示端已关闭！");
		}

		public void close() {
			isprint = false;
		}

		private void printInfo() {
			System.out.println(sbuffer.toString());
		}
	}

//	public void printInstances() {
//		iMgr.printAllInstances();
//	}
}