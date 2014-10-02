package structure;

import java.io.Serializable;

public class Input implements Serializable{
	private String name; // 对应于name属性

	public Input(String name) {
		this.name = name;
	}

	public Input(Input input) {
		this.name = input.getName();
	}

	public String getName() {
		return this.name;
	}
}
