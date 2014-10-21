package structure.actionType;

import java.util.List;
import java.util.concurrent.TimeUnit;

import structure.EnumType;
import structure.Input;
import structure.Output;
import structure.EnumType.XMLType;
import structure.EnumType.XMLType.ActionType;

public class SubcriptionAction extends Action{

	boolean isSubscription = false;
	
	public SubcriptionAction(String name, String function,
			List<Input> inputList, List<Output> outputList, ActionType type,
			String cycle, String topic) {
		super(name, function, inputList, outputList, type, cycle, topic);
	}

	@Override
	public void working() {
		//只需订阅一次，而system主题的订阅已经在msgHandler里设置好了
				if (!isSubscription) {
					instance.subscription(topic, name);
					isSubscription = true;
				}
				
				if ((message = instance.sendMessageToAction(name)) != null) {
					callFunction();
				}
				
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}		
	}


}
