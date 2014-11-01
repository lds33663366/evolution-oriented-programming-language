package function;

import java.util.List;
import java.util.Map;
import java.util.Random;

import structure.EnumType.XMLType.VariableType;
import structure.Instance;
import structure.Message;
import structure.Variable;

public class Functionbase {

	/**
	 * 随机初试化狮子、斑马的性别(female/male)
	 * 
	 * @return
	 */
	public Variable initialize_gender(Variable v) {

		Variable gender = new Variable("gender", VariableType.STRING, "String");

		Random r = new Random();
		int i = r.nextInt(2);
		switch (i) {
		case 0:
			gender.setValue("male");
			break;
		case 1:
			gender.setValue("female");
			break;
		}

		return gender;
	}

	/**
	 * 随机初试化狮子、斑马的年龄(0-13)
	 * 
	 * @return
	 */
	public Variable initialize_age() {

		Variable age = new Variable("age", VariableType.STRING, "int");

		Random r = new Random();
		// int i = r.nextInt(13);

		int i = 0;
		age.setValue(i + "");

		return age;
	}

	// 年龄加1
	public Variable age_increment(Variable variable) {
		Variable v = variable;
		int age = Integer.parseInt(v.getValue());
		age++;
		v.setValue(age + "");
		return v;
	}
	
	public boolean zebra_new(Map<String, Variable> map) {
		Variable vgender = map.get("gender");
		Variable vage = map.get("age");
		
		String v = vage.getValue();
		if (v == null || v.trim().equals("")) return false;
		int age = Integer.parseInt(v);
		if (vgender.getValue().equals("female") && age > 1) {
			return true;
		}
			
		return false;	
	}
	
	public boolean lion_new(Map<String, Variable> map) {
		Variable vgender = map.get("gender");
		Variable vage = map.get("age");
		
		String v = vage.getValue();
		if (v == null || v.trim().equals("")) return false;
		int age = Integer.parseInt(v);
		if (vgender.getValue().equals("female") && age > 2) 
			return true;
		return false;	
	}

	// 判断斑马是否已达到死亡的条件
	public boolean zebraDieable(Map<String, Variable> map) {
		Variable v = map.get("age");
		Variable vlife = map.get("life");
		int life = Integer.parseInt(vlife.getValue());
		if (v != null) {
//			System.out.print("judge function dead---");
//			System.out.println(v.getValue());
			if (Integer.parseInt(v.getValue()) >= life) {
				return true;
			}
		}
		return false;
	}

	// 判断狮子是否已达到死亡的条件
	public boolean lionDieable(Map<String, Variable> map) {
		Variable v = map.get("age");
		Variable vlife = map.get("life");
		int life = Integer.parseInt(vlife.getValue());
		if (v != null) {
			if (Integer.parseInt(v.getValue()) >= life)
				return true;
		}
		return false;
	}
	
	//引力测试
	/**
	 * 已知坐标(x, y), 角度angle, 求变换了角度angle2后的坐标值(x1, y1)
	 * 先求得半径r, x1=r.cos(angle-angle2), y1=r.sin(angle-angle2)
	 * @param planet
	 * @param m
	 */
	public void centripetal_force(Instance planet, Message m) {
		double this_x = Double.parseDouble(planet.obtainValue("position.x"));
		double this_y = Double.parseDouble(planet.obtainValue("position.y"));
		double this_weight = Double.parseDouble(planet.obtainValue("weight"));

		double star_x = Double.parseDouble(m.obtainValue("sun.position.x"));
		double star_y = Double.parseDouble(m.obtainValue("sun.position.y"));
		double star_weight = Double.parseDouble(m.obtainValue("sun.weight"));

		double x = this_x + 1E11;
		planet.motify("position.x", x+"");
		
		
		
	}
}
