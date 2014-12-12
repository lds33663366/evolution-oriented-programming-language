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
		
	}

	@Override
	public void working() {

		if (instance.isLive())
			callFunction();
	}

	@Override
	protected void init() {
		if (sleepTime == -1) sleepTime = ThreadTimeConsole.Thread_SelfAction.getTime();
	}

}
