package structure;

public class Node {

	private String aName; // action name, 实际上对应的是<action>的function属性
	private String iName; // instance name

	public Node(String aName, String iName) {
		this.aName = aName;
		this.iName = iName;
	}

	public String getaName() {
		return aName;
	}

	public String getiName() {
		return iName;
	}
}
