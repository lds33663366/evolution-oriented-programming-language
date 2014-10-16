package structure;

import java.util.List;

//<relation>中的<message>,不同于<system>中的<message>
public class RelationMessage {

	private String name; // 该RelationMessage的名字

	private List<Node> nodeList; // node组成的集合

	public RelationMessage(String name, List<Node> nodeList) {
		this.name = name;
		this.nodeList = nodeList;
	}

	public String getName() {
		return name;
	}

	public List<Node> getNodeList() {
		return nodeList;
	}
	
	/** 找到与该消息关联的某一实体action名，如果没有关联，则返回null
	 * @param 实体名 
	 * @return 该实体的动作名
	 */
	public String relateInstance(String instanceName) {
		
		Node n = null;
		for (int i=0; i<nodeList.size(); i++) {
			n = nodeList.get(i);
			if (n.getiName().equals(instanceName))
				return n.getaName();
		}
		return null;
	}


}
