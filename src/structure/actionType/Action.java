package structure.actionType;

import initiator.TimeCalculater;
import initiator.XMLSystem;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import log.MyLogger;

import org.apache.log4j.Logger;

import structure.EnumType;
import structure.Input;
import structure.Instance;
import structure.Message;
import structure.Output;
import structure.Variable;
import structure.EnumType.XMLType;
import structure.EnumType.XMLType.ActionType;
import function.FunctionLauncher;
import function.SystemFunctionbase;

public abstract class Action implements Runnable, Serializable {
	protected String name;
	private String function; // 在执行此Action时所调用的函数名
	private List<Input> inputList; // 子元素Input对象组成的集合
	private List<Output> outputList; // 子元素Output对象组成的集合
	private ActionType type; // 此Action的类型
	// 该action会使用到的variable, 即<input>与<output>指定的变量
	private Map<String, Variable> variableMap;
	protected Instance instance; // 指向父元素<instance>
	private String cycle;
	protected String topic;
	private String trigger;
	protected Message message;
	protected long sleepTime;
	
	static Logger logger = MyLogger.getLogger();

	public volatile boolean update = true;

	// public void setUpdate(boolean update) {
	// this.update = update;
	// }
	//
	// public static Action getDaultAction(){
	// return new Action();
	// }

	public Action(String name, String function, List<Input> inputList,
			List<Output> outputList, ActionType type, String cycle,
			String topic, String trigger) {
		this.name = name.trim();
		this.function = function.trim();
		this.inputList = inputList;
		this.outputList = outputList;
		this.type = type;
		this.cycle = cycle.trim();
		this.topic = topic.trim();
		this.trigger = trigger.trim();
		// checkTopic();
	}

	private long getTrigger() {
		if (trigger.equals("") || trigger.equals("init"))
			return -1;
		else {
//			if (name.equals("DEAD")) {
//				System.out.println("DEAD is " + TimeCalculater.getMillisecond(trigger));
//				System.out.println("DEAD is " + XMLSystem.timeScale);
//				System.out.println(Integer.MAX_VALUE);
//			}
			return TimeCalculater.getMillisecond(trigger) / XMLSystem.timeScale;
		}
	}
	
	protected abstract void init();

	// 实现Runnable的run方法
	public void run() {

		this.sleepTime = getTrigger();
		init();
		
		while (instance.isLive()) {

			sleep();

			working();

			if (trigger.equals("init"))
				return;
			// update = false;
			Thread.yield();
		}

	}

	/**
	 * 间隔一段时间之后再执行
	 */
	private void sleep() {
		try {
			TimeUnit.MILLISECONDS.sleep(sleepTime);
		} catch (InterruptedException e) {
			// e.printStackTrace();
		}
	}

	public abstract void working();

	public String getFunction() {
		return function;
	}

	public List<Input> getInputList() {
		return inputList;
	}

	public Instance getInstance() {
		return instance;
	}

	public List<Output> getOutputList() {
		return outputList;
	}

	public ActionType getType() {
		return type;
	}

	public Map<String, Variable> getVariableMap() {
		return variableMap;
	}

	// 根据function属性值调用函数库中的同名函数
	protected void callFunction() {
		if (function.equals("NEW")) {// 系统函数, 新创建一实体
			// 先判断该实体是否已达到生成新实例的条件
			// System.out.println("尝试新建instance……");
			boolean returnValue = false;
			returnValue = FunctionLauncher.launch(instance.getName() + "_new",
					variableMap);
			if (returnValue == true) {
				// instance.getSystem().createInstance(instance);
				instance.getSystem().createInstance(instance.getName());
			}
		} else if (function.equals("DEAD")) {// 系统函数, 销毁一个实体

			/*
			 * // 先判断该实体是否已达到死亡的条件 boolean returnValue = false; //
			 * 判断类型是否是监听类型，如果是，则直接调用销毁 if (type == ActionType.LISTEN)
			 * returnValue = true; // 其他类型通过用户编写的函数判断 else returnValue =
			 * FunctionLauncher.launch(instance.getName() + "Dieable",
			 * variableMap);
			 * 
			 * if (returnValue == true) { // System.out.println("dead is true");
			 */endInstance();// 结束该实体的运行
			// }
		} else {
			invokeUserFunction();
		}

		// if (SystemFunctionbase.containFunction(function)) {
		//
		// }else doUserFunction();

	}

	private void invokeUserFunction() {
		Object returnValue = null;// 函数调用的返回值
		switch (inputList.size()) {
		case 1:// 输入参数只有一个
			returnValue = FunctionLauncher.launch(function,
					variableMap.get(inputList.get(0).getName()));
			break;
		default:
			FunctionLauncher.launch(function, instance, message);
			break;
		}
		if (returnValue != null) {
			if (returnValue instanceof Variable) {
				// 更新variableMap中的变量
				Variable rv = (Variable) returnValue;
				// Variable v = variableMap.get(rv.getName());
				if (rv != null) {
					// synchronized(instance){
					// instance.updateProperty(rv.getName(), rv.getValue());
					instance.motify(rv.getName(), rv.getValue());
					// }
				}
			}
		}
	}

	// 结束instance对象, 步骤:
	// 1 将instance的live设置为false
	// 2 将此instance从XMLSystem的instanceMap中移除
	private void endInstance() {

		// System.out.println(instance.getIdName() + "死亡");
		// 完成第1步
		instance.setLive(false);

		// 完成第2步
		instance.getSystem().remove(instance);

		// 在实体离开之前检查性别是否初始化, 由于随机性, 有的实体性别还没初始化就死亡
		// System.out.println(variableMap.get("gender").getValue());
		// // 在实体离开之间检查年龄是否增加, 由于随机性, 有的实体年龄还没增加就死亡
		// System.out.println(variableMap.get("age").getValue());
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	public void setVariableMap(Map<String, Variable> map) {
		this.variableMap = map;
	}

	@Override
	public String toString() {
		return "Action [function=" + function + ", type=" + type + "]";
	}

	public void isUpdate() {
		update = true;
	}

	// private void unPackageMessage(Message message) {
	//
	// List<MessageContent> contentList = message.getContentList();
	// if (contentList == null || contentList.size()<=0) return;
	//
	// String instanceName = message.getFrom();
	// for (Iterator<MessageContent> i=contentList.iterator(); i.hasNext();) {
	// MessageContent mc = i.next();
	//
	// String propName = mc.getName();
	// switch (mc.getType()) {
	// case "property":
	// if (mc.getVariable() != null) {
	// String valueName = instanceName + "." + propName;
	// System.out.println("已放入" + valueName + "; 属性为" + mc.getVariable());
	// variableMap.put(valueName, mc.getVariable());
	// } else if (mc.getInstance() != null) {
	// /////////////////////////
	// }
	//
	// }
	// }
	// }
}
