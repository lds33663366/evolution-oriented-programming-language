package initiator;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import structure.Instance;

/**
 * 用于管理某个instance的添加、删除和打印的类
 * @author lods
 */
public class InstanceManager {

	private String instanceName;
	private CopyOnWriteArrayList<Instance> instanceList;
	private int idAssign;
	private int newInstanceRegister;
	
	/**
	 * 添加instances里所有带位置position的坐标到集合里
	 * @return 坐标集合
	 */
	public List<Point.Double> getGUIpoints() {
		
		List<Point.Double> points = new ArrayList<Point.Double>();
		CopyOnWriteArrayList<Instance> copyInstanceList = copyInstanceList();
		for (int i=0; i<copyInstanceList.size(); i++) {
			Instance inst = copyInstanceList.get(i);
			double x = Double.parseDouble(inst.obtainValue("position.x"));
			double y = Double.parseDouble(inst.obtainValue("position.y"));
			points.add(new Point.Double(x,y));
		}
		return points;
	}

	public void setNewInstanceRegister(int newInstanceRegister) {
		this.newInstanceRegister = newInstanceRegister;
	}

	public int getNewInstanceRegister() {
		return newInstanceRegister;
	}

	public CopyOnWriteArrayList<Instance> getInstanceList() {
		return instanceList;
	}
	
	public InstanceManager(){
		instanceList = new CopyOnWriteArrayList<Instance>();
		newInstanceRegister = 0;
		idAssign = 1;
	}

	public InstanceManager(String instanceName) {
		this.instanceName = instanceName;
	}

	public void add(Instance instance) {
		if (instanceList == null) {
			instanceList = new CopyOnWriteArrayList<Instance>();
		}
		instanceList.add(instance);
		instance.setId(idAssign);
		idAssign++;
	}
	
	public int getSize() {
		return instanceList.size();
	}
	
	public void remove(Instance instance) {
		synchronized (instanceList) { 
			instanceList.remove(instance);
		}
	}
	
	CopyOnWriteArrayList<Instance> copyInstanceList() {
		CopyOnWriteArrayList<Instance> copyInstanceList =  new CopyOnWriteArrayList<Instance>(
				Arrays.asList(new Instance[instanceList.size()]));
		synchronized(instanceList) {
			Collections.copy(copyInstanceList, instanceList);
		}
		return copyInstanceList;
	}
	
	public String printInstance() {
		StringBuffer sb = new StringBuffer(1000);
		sb.append(instanceName + "有" + getSize() + "个："
				+ "************************************************************************\n");
		//为了打印instance信息且在打印期间不占用信息列表，复制一份列表
		CopyOnWriteArrayList<Instance> copyInstanceList =  copyInstanceList();
		for (int i=0, len=instanceList.size(); i<len; i++) {
			sb.append(instanceList.get(i).toString() + "\t");
			if (i%50 == 49) sb.append("\n");
		}
		sb.append("\n**************************************************************************\n");
		return sb.toString();
	}

	public void close() {
		for (int j = 0, k = instanceList.size(); j < k; j++) {
			instanceList.get(j).setLive(false);
		}
	}

	public synchronized void registerNew() {
		newInstanceRegister++;
	}

	public void draw(Graphics2D g) {
		List<Point.Double> pointList = getGUIpoints();
		for (int i=0; i<pointList.size(); i++) {
			Point.Double point = pointList.get(i);
			int x = (int)(500 + point.x/10E8);
			int y = (int)(500 + point.y/10E8);
			g.fillOval(x, y, 30, 30);
		}
		
	}
}
