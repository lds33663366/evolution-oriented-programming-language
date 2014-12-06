package structure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Property implements Serializable{

	private List<Instance> instanceList; // 解析<instance>元素生成的对象组成的集合
	private Map<String, Variable> variableMap; // 存放int, string等基本类型变量

	public Property(List<Instance> instanceList,
			Map<String, Variable> variableMap) {
		this.instanceList = instanceList;
		this.variableMap = variableMap;
	}

	public List<Instance> getInstanceList() {
		return instanceList;
	}

	public Map<String, Variable> getVariableMap() {
		return variableMap;
	}
	
	public Variable getVariable(String variableName) {
		if (variableMap.containsKey(variableName))
			return variableMap.get(variableName).clone();
		return null;
	}
	
	public void setVariable(String variableName, String value) {
		Variable v = null;
		if (variableMap.containsKey(variableName))
			v = variableMap.get(variableName);
		synchronized (v) {
			v.setValue(value);
		}
	}
	
	public Instance getInstance(String instanceName) {
		
		for (Iterator<Instance> i=instanceList.iterator(); i.hasNext();) {
			Instance inst = i.next();
			if (inst.getName().equals(instanceName)) {
				return inst;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		
		StringBuffer sb = new StringBuffer(40);
		for (Iterator<Entry<String, Variable>> i =
				variableMap.entrySet().iterator(); i.hasNext();) {
			
			Entry<String, Variable> entry = i.next();
			Variable v = entry.getValue();
			sb.append(v);
	//		sb.append("");
		}
		for (Iterator<Instance> i=instanceList.iterator(); i.hasNext();) {
			Instance inst = i.next();
			sb.append(inst.getProperty().toString());
		}
		return sb.toString();
	}

}
