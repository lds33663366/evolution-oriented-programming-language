package initiator;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import structure.Instance;

public class InstancesManager {

	private Map<String, InstanceManager> instanceMap;

	public Map<String, InstanceManager> getInstanceMap() {
		return instanceMap;
	}

	public InstancesManager() {
		instanceMap = new HashMap<String, InstanceManager>();
	}

	public void add(Instance instance) {
		String instanceName = instance.getName();
		if (!instanceMap.containsKey(instanceName)) {
			instanceMap.put(instanceName, new InstanceManager(instanceName));
		}
		InstanceManager im = instanceMap.get(instanceName);
		im.add(instance);
	}

	public void remove(Instance instance) {
		InstanceManager im = instanceMap.get(instance.getName());
		im.remove(instance);
	}

	public String printAllInstances() {
		StringBuffer sb = new StringBuffer(1000);
		sb.append("线程数有" + Thread.activeCount() + "个\n");
		for (Iterator<Entry<String, InstanceManager>> iter = instanceMap
				.entrySet().iterator(); iter.hasNext();) {
			Entry<String, InstanceManager> entry = iter.next();
			String instanceName = entry.getKey();
			InstanceManager im = instanceMap.get(instanceName);
			sb.append(im.printInstance());
		}
		return sb.toString();
	}

	public void registerNEW(Instance instance) {
		InstanceManager im = instanceMap.get(instance.getName());
		im.registerNew();

	}

	public void draw(Graphics2D g) {

		for (Iterator<Entry<String, InstanceManager>> iterator = instanceMap
				.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, InstanceManager> entry = iterator.next();
			InstanceManager im = entry.getValue();
			im.draw(g);
		}
	}

	public void save() {

		for (Iterator<Entry<String, InstanceManager>> iterator = instanceMap
				.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, InstanceManager> entry = iterator.next();
			InstanceManager im = entry.getValue();
			im.save();
		}
	}

}
