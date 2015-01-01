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

/*		Random r = new Random();
		int i = r.nextInt(13);

		int i = 0;*/
		age.setValue(0 + "");

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

	public void hunger_decrease(Instance instance) {

		int hunger = instance.obtainIntValue("hunger");
		hunger -= 20;
		instance.motify("hunger", hunger + "");
	}

	public boolean zebra_new(Map<String, Variable> map) {
		Variable vgender = map.get("gender");
		Variable vage = map.get("age");

		String v = vage.getValue();
		if (v == null || v.trim().equals(""))
			return false;
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
		if (v == null || v.trim().equals(""))
			return false;
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
			// System.out.print("judge function dead---");
			// System.out.println(v.getValue());
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

	public void initialize(Instance planet) {

		Random random = new Random();
		double x = random.nextDouble();
		double y = random.nextDouble();

		int vx = random.nextInt(10000);
		int vy = random.nextInt(10000);

		String px = x + "E12";
		String py = y + "E12";

		String vxx = vx + "", vyy = vy + "";
		if (x > 0.6)
			vxx = "-" + vxx;
		if (y > 0.6)
			vyy = "-" + vyy;
		System.out.println("(" + px + ", " + py + ")");
		planet.motify("position.x", px);
		planet.motify("position.y", py);
		planet.motify("velocity.x", vxx + "");
		planet.motify("velocity.y", vyy + "");

	}

	public void move(Instance planet) {
		double time = 3600 * 24;
		double this_position_x = planet.obtainDoubleValue("position.x");
		double this_position_y = planet.obtainDoubleValue("position.y");
		double this_velocity_x = planet.obtainDoubleValue("velocity.x");
		double this_velocity_y = planet.obtainDoubleValue("velocity.y");

		planet.motify("old_position.x", this_position_x + "");
		planet.motify("old_position.y", this_position_y + "");

		double this_x2 = getPosition2(this_position_x, this_velocity_x, time);
		double this_y2 = getPosition2(this_position_y, this_velocity_y, time);

		synchronized (planet) {
			planet.motify("position.x", this_x2 + "");
			planet.motify("position.y", this_y2 + "");
		}

	}

	// 引力测试
	int count = 0;

	/**
	 * 已知坐标(x, y), 角度angle, 求变换了角度angle2后的坐标值(x1, y1) 先求得半径r,
	 * x1=r.cos(angle-angle2), y1=r.sin(angle-angle2)
	 * 
	 * @param planet
	 * @param m
	 */
	public void centripetal_force(Instance planet, Message m) {

		double time = 3600 * 24;
		double this_position_x = planet.obtainDoubleValue("position.x");
		double this_position_y = planet.obtainDoubleValue("position.y");
		double this_velocity_x = planet.obtainDoubleValue("velocity.x");
		double this_velocity_y = planet.obtainDoubleValue("velocity.y");
		double this_oldposition_x = planet.obtainDoubleValue("old_position.x");
		double this_oldposition_y = planet.obtainDoubleValue("old_position.y");

		double star_x = m.obtainDoubleValue("position.x");
		double star_y = m.obtainDoubleValue("position.y");
		double star_weight = m.obtainDoubleValue("weight");

		// 第一次坐标移动初始速度方向得到(x2, y2)
		// double this_x2 = getPosition2(this_position_x, this_velocity_x,
		// time);
		// double this_y2 = getPosition2(this_position_y, this_velocity_y,
		// time);

		// if ((count++)%2==0) {
		// this_position_x = this_oldposition_x;
		// this_position_y = this_oldposition_y;
		// }

		// 用于计算加速度矢量的点，可以是老点或新点
		double pointxa = 0, pointya = 0;
		if ((count++) % 2 == 0) {
			pointxa = this_oldposition_x;
			pointya = this_oldposition_y;
		} else {
			pointxa = this_position_x;
			pointya = this_position_y;
		}
		// 求加速度矢量
		double r = getDistance(pointxa, pointya, star_x, star_y);// 距离
		double a_size = getAccl(star_weight, r);// 大小
		double a_vector_x = star_x - pointxa;// 方向
		double a_vector_y = star_y - pointya;
		double para = a_size
				/ (Math.sqrt(a_vector_x * a_vector_x + a_vector_y * a_vector_y));
		double a_flex_x = para * a_vector_x;// 结果
		double a_flex_y = para * a_vector_y;

		// 下一次的速度矢量
		double this_velocity_x2 = getVelocity(this_velocity_x, a_flex_x, time);
		double this_velocity_y2 = getVelocity(this_velocity_y, a_flex_y, time);

		// 从(x2, y2)向引力方向移动,完成最终移动位置
		double this_x3 = getPosition3(this_position_x, a_flex_x, time);
		double this_y3 = getPosition3(this_position_y, a_flex_y, time);

		synchronized (planet) {
			planet.motify("position.x", this_x3 + "");
			planet.motify("position.y", this_y3 + "");
			planet.motify("velocity.x", this_velocity_x2 + "");
			planet.motify("velocity.y", this_velocity_y2 + "");
		}

	}

	private double getVelocity(double this_velocity, double a_flex, double time) {
		return this_velocity + a_flex * time;
	}

	// 从(x2, y2)向引力方向移动
	private double getPosition3(double this_position2, double a_flex,
			double time) {
		return this_position2 + a_flex * time * time / 2;
	}

	public static final double Gravity_constant = 6.67E-11;

	private double getAccl(double star_weight, double r) {
		return Gravity_constant * star_weight / (r * r);
	}

	private double getDistance(double this_position_x, double this_position_y,
			double star_x, double star_y) {
		double midx = star_x - this_position_x;
		double midy = star_y - this_position_y;
		return Math.sqrt(midx * midx + midy * midy);
	}

	private double getPosition2(double this_position, double this_velocity,
			double time) {
		return this_position + this_velocity * time;
	}

}
