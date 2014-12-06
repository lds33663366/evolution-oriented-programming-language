package structure.actionType;

import initiator.ThreadTimeConsole;

import java.util.List;
import java.util.concurrent.TimeUnit;

import structure.EnumType;
import structure.Input;
import structure.Output;
import structure.EnumType.XMLType;
import structure.EnumType.XMLType.ActionType;

public class SelfAction extends Action {

	public SelfAction(String name, String function, List<Input> inputList,
			List<Output> outputList, ActionType type, String cycle, String topic, String trigger) {
		super(name, function, inputList, outputList, type, cycle, topic, trigger);
		if (sleepTime == -1) sleepTime = ThreadTimeConsole.Thread_SelfAction.getTime();
	}

	@Override
	public void working() {
		// while (!update) {
//		if (name.equals("")) System.out.println("self is running!");
		try {
			TimeUnit.MILLISECONDS.sleep(sleepTime);
		} catch (InterruptedException e) {
//			e.printStackTrace();
		}
		// }
		if (instance.isLive())
			callFunction();
	}

}
