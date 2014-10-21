package structure.actionType;

import java.util.List;
import java.util.concurrent.TimeUnit;

import structure.EnumType;
import structure.Input;
import structure.Output;
import structure.EnumType.XMLType;
import structure.EnumType.XMLType.ActionType;

public class ListenAction extends Action{

	public ListenAction(String name, String function, List<Input> inputList,
			List<Output> outputList, ActionType type, String cycle, String topic) {
		super(name, function, inputList, outputList, type, cycle, topic);
	}

	@Override
	public void working() {
		if ((message = instance.sendMessageToAction(name)) != null) {
			System.out.println(instance.getIdName() + "拿到消息" + message);
			callFunction();
		}
		
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}

}
