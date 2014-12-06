package structure.actionType;

import java.util.List;

import structure.EnumType;
import structure.Input;
import structure.Output;
import structure.EnumType.XMLType;
import structure.EnumType.XMLType.ActionType;

/**
 * action的工厂，根据xml标记的self、listen、publish、subscription、notification
 * 生产对应的SelfAction、ListenAction、PublishAction、SubscriptionAction、
 * NotificationAction对象。
 * @author lods
 */
public class ActionFactory {
	
	public static Action createAction(String name, String function, List<Input> inputList,
	List<Output> outputList, ActionType type, String cycle, String topic, String trigger) {
		if (type.equals(ActionType.SELF)) 
			return new SelfAction(name, function, inputList,
					outputList, type, cycle, topic, trigger);
		else if (type.equals(ActionType.LISTEN)) 
			return new ListenAction(name, function, inputList,
					outputList, type, cycle, topic, trigger);
		else if (type.equals(ActionType.PUBLISH)) 
			return new PublishAction(name, function, inputList,
					outputList, type, cycle, topic, trigger);
		else if (type.equals(ActionType.SUBSCRIPTION)) 
			return new SubcriptionAction(name, function, inputList,
					outputList, type, cycle, topic, trigger);
		else if (type.equals(ActionType.NOTIFICATION)) 
			return new NotificationAction(name, function, inputList,
					outputList, type, cycle, topic, trigger);
		return null;
		
	}

}
