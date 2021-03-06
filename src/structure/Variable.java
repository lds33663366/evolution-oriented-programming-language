package structure;


import java.io.Serializable;

import structure.EnumType.XMLType.VariableType;
// 将<property>所定义的变量都看作是Variable

public class Variable implements Cloneable, Serializable{

    private String name; //变量的名字
    private VariableType type; //变量的类型
    private String value; //变量的值
    public static boolean isUpdate = true;

    public Variable(String name, VariableType type, String value){
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName(){
        return name;
    }
    public VariableType getType(){
        return type;
    }
    public synchronized String getValue(){
        return value;
    }

	public synchronized void setValue(String value) {
		isUpdate = true;
		this.value = value;
	}
		
	@Override
	public Variable clone() {
		Variable o = null;
		try {
			o = (Variable) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}

	@Override
	public String toString() {
		return "[" + name + "=" + value + "]";
	}
	
}
