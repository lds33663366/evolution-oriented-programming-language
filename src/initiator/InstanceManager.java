package initiator;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import structure.Instance;

public class InstanceManager {

	private Map<String, CopyOnWriteArrayList<Instance>> instanceMap;
	private Map<String, Integer> idAssign;

	public Map<String, CopyOnWriteArrayList<Instance>> getInstanceMap() {
		return instanceMap;
	}
	
	public InstanceManager(){
		instanceMap = new HashMap<String, CopyOnWriteArrayList<Instance>>();
		idAssign = new HashMap<String, Integer>();
	}
	
	void setInstanceID(Instance instance) {
		
	}

	public void add(Instance instance) {
		CopyOnWriteArrayList<Instance> instanceList = instanceMap.get(instance.getName());
		if (instanceList == null) {
			instanceList = new CopyOnWriteArrayList<Instance>();
			instanceList.add(instance);
			instanceMap.put(instance.getName(), instanceList);
			idAssign.put(instance.getName(), 1);
			instance.setId(1);
		}
		else {
			instanceList.add(instance);
			int id = assign(instance.getName());
			instance.setId(id);
		}
	}
	
	int assign(String instanceName) {
		int id = idAssign.get(instanceName);
		id++;
		idAssign.put(instanceName, id);
		return id;
	}
	
	public int getSize(String instanceName) {
		return instanceMap.get(instanceName).size();
	}
	
	public void remove(Instance instance) {
		CopyOnWriteArrayList<Instance> instanceList;
		instanceList = instanceMap.get(instance.getName());
		synchronized (instanceList) { 
			instanceList.remove(instance);
		}
	}
	
	public String printInstance(String instanceName) {
		StringBuffer sb = new StringBuffer(1000);
		sb.append(instanceName + "有" + getSize(instanceName) + "个："
				+ "************************************************************************\n");
		CopyOnWriteArrayList<Instance> instanceList = null;
		//为了打印instance信息且在打印期间不占用信息列表，复制一份列表
		synchronized (instanceMap.get(instanceName)) {
			instanceList = new CopyOnWriteArrayList<Instance>(
					Arrays.asList(new Instance[instanceMap.get(instanceName)
							.size()]));
			Collections.copy(instanceList, instanceMap.get(instanceName));
		}

		for (int i=0, len=instanceList.size(); i<len; i++) {
			sb.append(instanceList.get(i).toString() + "\t");
			if (i%50 == 49) sb.append("\n");
		}
		sb.append("\n**************************************************************************\n");
		return sb.toString();
	}
	
	public String printAllInstances() {
		StringBuffer sb = new StringBuffer(1000);
		for (Iterator<Entry<String, CopyOnWriteArrayList<Instance>>> iter=
				instanceMap.entrySet().iterator(); iter.hasNext();) {
			Entry<String, CopyOnWriteArrayList<Instance>> entry = iter.next();
			String instanceName = entry.getKey();
			sb.append(printInstance(instanceName));
		}
		return sb.toString();
	}
	

}
