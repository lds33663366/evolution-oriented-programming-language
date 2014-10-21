package function;

import initiator.XMLSystem;

import java.util.ArrayList;
import java.util.List;

import structure.Instance;

public class SystemFunctionbase {
	
	public static XMLSystem xmlSystem;
	

	private static List<String> systemFunctionName;
	static {
		systemFunctionName = new ArrayList<String>();
		systemFunctionName.add("NEW");
		systemFunctionName.add("DEAD");
	}
	
	static public boolean containFunction(String functionName) {
		return systemFunctionName.contains(functionName);
	}
	
	void launch(String functionName, Instance instance) {
		if (functionName.equals("NEW")) launchNEW(instance);
		else if(functionName.equals("DEAD")) launchDEAD(instance);
	}

	private void launchDEAD(Instance instance) {
		// 完成第1步
		instance.setLive(false);
		// 完成第2步
		xmlSystem.remove(instance);
	}

	private void launchNEW(Instance instance) {
		boolean isMeetUserRestriction = FunctionLauncher.launch(instance.getName()
		+ "_new", instance.getProperty().getVariableMap());
		if (isMeetUserRestriction) xmlSystem.createInstance(instance);
	}
	
	

}
