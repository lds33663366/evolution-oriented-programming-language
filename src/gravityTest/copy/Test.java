package gravityTest.copy;
import java.awt.Color;
import java.awt.Graphics;


public class Test {
	
	MyPosition sun = new MyPosition(300, 300);
	MyPosition earth = new MyPosition(300, 200);
	MyPosition earth_v = new MyPosition(-10, 0);
	double time = 0.1;
	double a = 0.30;
	
//	public static void main(String[] args) {
//		Test t = new Test();
//		for (int i=0; i<5; i++) {
//			System.out.println("是否变动？ earth_v=" + t.earth_v);
//			t.compute(t.sun, t.earth, t.earth_v);
//		}
//	}
	
	void draw(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.YELLOW);
		g.fillOval((int)sun.x, (int)sun.y, 10, 10);
		g.setColor(Color.RED);
		g.fillOval((int)earth.x, (int)earth.y, 5, 5);
		g.setColor(c);
//		earth.x +=5;
		
		compute(sun, earth, earth_v);
	}
	
	//给定两点坐标，另一点速度的方向与大小
	public MyPosition compute(MyPosition sun, MyPosition star, MyPosition v) {
		System.out.println("开始计算----------------------------------------------------------------------------");
		System.out.println("已知sun = " + sun + "; star = " + star + "; star的速度v = " + v);
//		a = 150.0/(r);
		MyPosition a_vector = new MyPosition(sun.x-star.x, sun.y-star.y);
		a_vector = aFlex(a, a_vector);
		System.out.println("两点之间的加速度向量a_vector=" + a_vector);
//		double alpha = getDir(sun, earth); 
//		System.out.println("两点之间的方向alpha=" + alpha);
		double theta = getAngle(a_vector, v);
		System.out.println("两点连成的直线与速度的夹角theta=" + theta);
		double r = getDistance(sun, star);
		System.out.println("两点之间的距离是" + r);
//		double F = 16.0/(r*r);
//		System.out.print("假设力有" + F);
		double s_x = getSx(v, theta, time, a);
		System.out.println("加速度为" + a + ", 那么在两点直线的运动距离为" + s_x);
		double s_y = getSy(v, theta, time);
		System.out.println("在连线垂直方向上的运动距离为" + s_y);
		MyPosition earth_forward = getForwardOffset(s_x, star, a_vector);
		System.out.println("在连线上运动后的坐标为" + earth_forward);
		
		earth = getVerticalOffset(s_y, earth_forward, a_vector, v);
		System.out.println("最后坐标为" + earth);
		earth_v = getNextV(v, theta, a, a_vector );
		System.out.println("速度的方向向量为earth_v=" + earth_v);
		return star;
		
	}

	private MyPosition aFlex(double asize, MyPosition a_vector) {
		double para = asize / (a_vector.getModule());
		return new MyPosition(para*a_vector.x, para*a_vector.y);
	}

	private MyPosition getNextV(MyPosition v, double theta, double accel,
			MyPosition a_vector) {
		double sin_theta = Math.sin(theta);
		double cos_theta = Math.cos(theta);
//		double v_y = v.getModule() * sin_theta * sin_theta;
//		double v_x = v.getModule() * sin_theta * cos_theta;
		
		//末速度v_t应该为正数，方向由cos(theta)正负决定？
//		double v_t = a * time + v.getModule() * cos_theta;
//		double v_t = accel * time;
//		v_t = Math.abs(v_t);
//		System.out.println("{v_t = a * t + v.getModule() * cos_theta}  @"
//				+ v_t + "=" + a + "*" + time + "+" + v.getModule() + "*" + cos_theta);
//		System.out.println("{v_t = a * t}  @"
//				+ v_t + "=" + accel + "*" + time);
//		int tag_y = a_vector.y==0?0:(a_vector.y>0?1:-1);
//		int tag_x = a_vector.x==0?0:(a_vector.x>0?1:-1);
		double v_ty = a_vector.y * time;
//		v_ty = Math.abs(v_ty) * tag_y;
		double v_tx = a_vector.x * time;	
//		v_tx = Math.abs(v_tx) * tag_x;
		
		double v_x = v.x+v_tx;
		double v_y = v.y+v_ty;
		System.out.println("{v_x = v.x+v_tx; v_y = v.y+v_ty} @" 
				+ v_x + "=" + v.x + "+" + v_tx + ";" + v_y + "=" + v.y + "+" + v_ty );
//		System.out.println("~~v_x=" + v + "~~v_y=" + v_y + "~~v_t=" + v_t
//				+ "~~v_tx=" + v_tx  + "~~v_ty=" + v_ty );
		return new MyPosition(v.x+v_tx, v.y+v_ty);
//		return new MyPosition(v_x, v_y);
	}

	private MyPosition getVerticalOffset(double s_y, MyPosition earth_forward,
			MyPosition a_vector, MyPosition v) {
		double midx = a_vector.y * s_y / a_vector.getModule();
		double midy = -a_vector.x * s_y / a_vector.getModule();
		double judge = midx * v.x + midy * v.y;
		//向量是否和速度v夹成锐角，如果不是，则取负值
		if (judge < 0) {
			midx = -midx;
			midy = -midy;
		}
		double xoff = midx + earth_forward.x;
		double yoff = midy + earth_forward.y;
		return new MyPosition(xoff, yoff);
	}

	private MyPosition getForwardOffset(double s_x, MyPosition earth,
			MyPosition a_vector) {
		double midx = a_vector.x * s_x / a_vector.getModule();
		double midy = a_vector.y * s_x / a_vector.getModule();
		double xoff = midx + earth.x;
		double yoff = midy + earth.y;
		return new MyPosition(xoff, yoff);
	}

	private double getAngle(MyPosition a_vector, MyPosition v) {
		
		if (v.isZero()) {
			System.out.println("速度为0");
			return 0.0;
		}
		double fenmu = a_vector.getModule() * v.getModule();
		double fenzi = a_vector.x * v.x + a_vector.y * v.y;
		return Math.acos(fenzi / fenmu);
	}

	private double getSy(MyPosition v, double theta, double t) {
		return v.getModule() * Math.sin(theta) * t;
	}

	private double getSx(MyPosition v, double theta, double t, double a) {
		double s1 = v.getModule() * Math.cos(theta);
		double s2 = a * t * t / 2;
		System.out.println("{s1 = v.getModule() * Math.cos(theta)} @ " +
					s1 + "=" + v.getModule() + "*" + Math.cos(theta) + "; s2=" + s2);
		return s1 + s2;
	}

	private double getDistance(MyPosition sun, MyPosition earth) {
		double delta_x = earth.x - sun.x;
		double delta_y = earth.y - sun.y;
		double distance = Math.sqrt(delta_x * delta_x + delta_y * delta_y);
		return distance;
	}

	private double getDir(MyPosition sun, MyPosition earth) {
		double number = (earth.y - sun.y) / (earth.x - sun.x);
		double theta = Math.atan(number);
		//加绝对值是否有问题？
		return Math.abs(theta);
	}

}
