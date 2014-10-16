package structure;

import java.io.Serializable;

public class MessageContent implements Serializable{
	
	private String type;
	private String name;
	
	private Instance instance;
	private Variable variable;
	
	public MessageContent(String type, String name) {
		this.type = type;
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	public Variable getVariable() {
		return variable;
	}

	public void setVariable(Variable variable) {
		this.variable = variable;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append( "content (type=" + type + ", name=" + name );
		if (variable != null) sb.append(variable);
		sb.append(")");
		return sb.toString();
	}
	
	
	
	
	
	

}
