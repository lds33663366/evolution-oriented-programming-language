package initiator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import structure.EnumType.XMLType.ActionType;
import structure.EnumType.XMLType.VariableType;
import structure.actionType.Action;
import structure.actionType.ActionFactory;
import structure.Input;
import structure.Instance;
import structure.Message;
import structure.MessageContent;
import structure.Node;
import structure.Output;
import structure.Property;
import structure.Relation;
import structure.RelationMessage;
import structure.Variable;

public class Parser {

	private Document doc; // 被解析的XML

	public Parser(String file) {
		SAXReader reader = new SAXReader();
		try {
			doc = reader.read(new File(file));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	// 解析<action>并返回该对象
	private Action parseAction(Element action) throws Exception {
		String name = "";
		String function = "";
		List<Input> inputList = new LinkedList<Input>();
		List<Output> outputList = new LinkedList<Output>();
		ActionType type = ActionType.SELF;
		String cycle = "default";
		String topic = "SYSTEM";

		// 提取<action>的属性
		for (Iterator<Attribute> actionI = action.attributeIterator(); actionI
				.hasNext();) {
			Attribute actionAttr = (Attribute) actionI.next();
			if (actionAttr.getName().equals("name")) {
				name = actionAttr.getValue();
			}else if (actionAttr.getName().equals("function")) {
				function = actionAttr.getValue();
			}else if (actionAttr.getName().equals("type")) {
				type = ActionType.valueOf(actionAttr.getValue().toUpperCase());
			}else if (actionAttr.getName().equals("cycle")) {
				cycle = actionAttr.getValue();
			}else if (actionAttr.getName().equals("topic")) {
				topic = actionAttr.getValue();
			}
		}
//		function = action.attribute("function").getValue();
//		Attribute typeA = action.attribute("type");
//		if (typeA != null)
//			type = ActionType.valueOf(typeA.getValue().toUpperCase());

		// 迭代<action>的子元素
		for (Iterator<Element> i = action.elementIterator(); i.hasNext();) {
			Element sub = i.next();
			if (sub.getName().equals("input")) {
				inputList.add(parseInput(sub));
			} else {// <output>子元素
				outputList.add(parseOutput(sub));
			}
		}
		
		return ActionFactory.createAction(name, function, inputList, outputList, type, cycle, topic);
	}

	private Output parseOutput(Element output) {
		// 提取<output>属性
		String name = output.attribute("name").getValue();

		return new Output(name);
	}

	private Input parseInput(Element input) {
		// 提取<input>的属性
		String name = input.attribute("name").getValue();

		return new Input(name);
	}

	// 解析<instance>并返回该对象
	private Instance parseInstance(Element instance) throws Exception {

		List<Action> actionList = new LinkedList<Action>();
		String name = "";
		int popsize = 1;
		Property property = null;

		// 提取<instance>属性
		name = instance.attribute("name").getValue();
		Attribute popsizeA = instance.attribute("popsize");
		if (popsizeA != null)
			popsize = Integer.parseInt(popsizeA.getValue());

		// 迭代<instance>的直接子元素
		for (Iterator<Element> instanceSubI = instance.elementIterator(); instanceSubI
				.hasNext();) {
			Element instanceSubE = instanceSubI.next();
			if (instanceSubE.getName().equals("action")) {
				actionList.add(parseAction(instanceSubE));

			} else { // <property>
				property = parseProperty(instanceSubE);
			}
		}

		return new Instance(actionList, name, popsize, property);
	}

	// 解析<message>并返回该对象
	private Message parseMessage(Element message) throws Exception {
		Date date = new Date(); // 发送时间
		int frequency = 1; // 发送次数
		String from = ""; // 发关此Message的Instance名字
		String life = "1m"; // 生存时间默认1分钟
		String name = ""; // Message的名字
		int priority = 5; // 优先级
		int second = 2; // Message将于多长时间后发送
		int share = 1; // 可接收此Message的instance的最大数量
		
		List<MessageContent> mc = new ArrayList<MessageContent>();

		// 提取<message>属性
		for (Iterator<Attribute> messageAI = message.attributeIterator(); messageAI
				.hasNext();) {
			Attribute messageA = messageAI.next();
			if (messageA.getName().equals("date")) {
				date = (new SimpleDateFormat()).parse(messageA.getValue());
			} else if (messageA.getName().equals("frequency")) {
				frequency = Integer.parseInt(messageA.getValue());
			} else if (messageA.getName().equals("from")) {
				from = messageA.getValue();
			} else if (messageA.getName().equals("life")) {
				life = messageA.getValue();
			} else if (messageA.getName().equals("name")) {
				name = messageA.getValue();
			} else if (messageA.getName().equals("priority")) {
				priority = Integer.parseInt(messageA.getValue());
			} else if (messageA.getName().equals("second")) {
				second = Integer.parseInt(messageA.getValue());
			} else { // share属性
				share = Integer.parseInt(messageA.getValue());
			}
		}
		
		//提取message子元素
		for (Iterator<Element> i = message.elementIterator(); i.hasNext();) {
			Element sub = i.next();
			if (sub.getName().equals("content")) {
				mc.add(parseMessageContent(sub));
			} 
		}

		return new Message(name, date, from, priority, frequency, life, second,
				share, mc);

	}
	
	private MessageContent parseMessageContent(Element node) throws Exception {
		
		String type = node.attribute("type").getValue();
		String name = node.attribute("name").getValue();
		
		return new MessageContent(type, name);
		
		
	}

	// 解析<node>并返回该对象
	private Node parseNode(Element node) throws Exception {

		String aName = node.attribute("aName").getValue();
		String iName = node.attribute("iName").getValue();

		return new Node(aName, iName);
	}

	// 解析<property>并返回该对象
	private Property parseProperty(Element property) throws Exception {

		List<Instance> instanceList = new LinkedList<Instance>(); // 解析<instanc>标签所生成的对象组成的集合
		Map<String, Variable> variableMap = new HashMap<String, Variable>();

		// 迭代<property>的子元素
		for (Iterator<Element> propertySubI = property.elementIterator(); propertySubI
				.hasNext();) {
			Element propertySub = propertySubI.next();
			if (propertySub.getName().equals("instance")) {
				instanceList.add(parseInstance(propertySub));
			} else {
				Variable variable = parseVariable(propertySub);
				variableMap.put(variable.getName(), variable);
			}
		}

		return new Property(instanceList, variableMap);

	}

	// 解析<relation>并返回该对象
	private Relation parseRelation(Element relation) throws Exception {

		List<RelationMessage> messageList = new LinkedList<RelationMessage>();

		// 迭代<relation>的子元素
		for (Iterator<Element> relationSubI = relation.elementIterator(); relationSubI
				.hasNext();) {
			Element relationSub = relationSubI.next();
			messageList.add(parseRelationMessage(relationSub));
		}

		return new Relation(messageList);
	}

	// 解析<relation>的子元素<message>并返回该对象
	private RelationMessage parseRelationMessage(Element relationMessage)
			throws Exception {

		String name = "";
		List<Node> nodeList = new LinkedList<Node>();

		// 提取属性
		name = relationMessage.attribute("name").getValue();

		// 迭代子元素
		for (Iterator<Element> relationMessageSubI = relationMessage
				.elementIterator(); relationMessageSubI.hasNext();) {
			Element relationMessageSub = relationMessageSubI.next();
			nodeList.add(parseNode(relationMessageSub)); // node元素
		}

		return new RelationMessage(name, nodeList);
	}

	// 解析根元素<system>并返回该对象
	public XMLSystem parseSystem() throws Exception {
		List<Instance> instanceList = new LinkedList<Instance>();
		List<Message> messageList = new LinkedList<Message>();
		List<Relation> relationList = new LinkedList<Relation>();

		// 迭代<system>的直接子元素
		for (Iterator<Element> systemSubI = doc.getRootElement()
				.elementIterator(); systemSubI.hasNext();) {
			Element systemSubE = systemSubI.next();// 得到一个子元素

			if (systemSubE.getName().equals("instance")) { // <instance>
				instanceList.add(parseInstance(systemSubE));

			} else if (systemSubE.getName().equals("message")) {// <message>
				messageList.add(parseMessage(systemSubE));
			} else { // <relation>
				relationList.add(parseRelation(systemSubE));
			}
		}

		return new XMLSystem(instanceList, messageList, relationList);
	}

	// 解析int, double等定义基本类型变量的标签, 返回Variable对象
	private Variable parseVariable(Element variable) throws Exception {

		String name = variable.attribute("name").getValue();
		VariableType type = VariableType.valueOf(variable.getName()
				.toUpperCase());
		Attribute valueA = variable.attribute("value");
		String value = "";
		if (valueA != null)
			value = valueA.getValue();
		else { // value为空, 根据变量的类型赋默认值
			switch (type) {
			case BOOLEAN:
				value = "false";
				break;
			case DOUBLE:
			case INT:
				value = "0";
				break;
			case STRING:
				value = "";
				break;
			default:
				break;
			}
		}

		return new Variable(name, type, value);

	}

}
