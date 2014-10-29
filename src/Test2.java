import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;


public class Test2 {
	
	MyPosition sun = new MyPosition(550, 400);
	MyPosition earth = new MyPosition(550, 300);
	MyPosition earth_v = new MyPosition(-5, -5);
	double time = 2;
	double a = 0.30;
	List<MyPosition> positions = new ArrayList<MyPosition>(130);
	
//	public static void main(String[] args) {
//		Test t = new Test();
//		for (int i=0; i<5; i++) {
//			System.out.println("是否变动？ earth_v=" + t.earth_v);
//			t.compute(t.sun, t.earth, t.earth_v);
//		}
//	}
	
	
	
	void draw(Graphics gr) {
		Graphics2D g = (Graphics2D)gr;
		Color c = g.getColor();
		g.setColor(Color.YELLOW);
		Ellipse2D.Double sun_draw = new Ellipse2D.Double(sun.x, sun.y, 30, 30);
		g.fill(sun_draw);
//		g.fillOval((int)sun.x, (int)sun.y, 15, 15);
		g.setColor(Color.BLUE);
//		g.fillOval((int)earth.x, (int)earth.y, 8, 8);
		Ellipse2D.Double earth_draw = new Ellipse2D.Double(earth.x, earth.y, 10, 10);
		g.fill(earth_draw);
		g.setColor(Color.WHITE);
		g.drawString("star的速度是："+earth_v.getModule(), 10, 50);
		drawTrack(g);
		g.setColor(c);
		
		compute(sun, earth, earth_v);
	}
	
	private void drawTrack(Graphics2D g) {
		g.setColor(Color.GRAY);
		Line2D.Double lines = null;
		for (int i=0; i<positions.size()-1; i++) {
			if (i%2==0) continue;
			lines = new Line2D.Double(positions.get(i), positions.get(i+1));
//			lines = new Line2D.Double(positions.get(i).x, positions.get(i).y, 
//					positions.get(i+1).x, positions.get(i+1).y);
			g.draw(lines);
		}
		if (positions.size()>130) positions.remove(0);
		
	}

	//给定两点坐标，另一点速度的方向与大小
	public MyPosition compute(MyPosition sun, MyPosition star, MyPosition v) {
		
		positions.add(star);
		System.out.println("开始计算----------------------------------------------------------------------------");
		System.out.println("已知sun = " + sun + "; star = " + star + "; star的速度v = " + v);
		double r = getDistance(sun, star);
		System.out.println("两点之间的距离是" + r);
//		a = 25000.0/(r*r);
		MyPosition a_vector = new MyPosition(sun.x-star.x, sun.y-star.y);
		a_vector = aFlex(a, a_vector);
		System.out.println("两点之间的加速度向量a_vector=" + a_vector);
//		double alpha = getDir(sun, earth); 
//		System.out.println("两点之间的方向alpha=" + alpha);
		double theta = getAngle(a_vector, v);
		System.out.println("两点连成的直线与速度的夹角theta=" + theta);
		earth_v = getNextV(v, theta, a, a_vector );
		System.out.println("速度下一个方向向量为earth_v=" + earth_v);
//		double F = 16.0/(r*r);
//		System.out.print("假设力有" + F);
		earth = getNextPosition(star, v, earth_v);
//		double s_x = getSx(v, theta, time, a);
//		System.out.println("加速度为" + a + ", 那么在两点直线的运动距离为" + s_x);
//		double s_y = getSy(v, theta, time);
//		System.out.println("在连线垂直方向上的运动距离为" + s_y);
//		MyPosition earth_forward = getForwardOffset(s_x, star, a_vector);
//		System.out.println("在连线上运动后的坐标为" + earth_forward);
//		
//		earth = getVerticalOffset(s_y, earth_forward, a_vector, v);
//		System.out.println("最后坐标为" + earth);
		return star;
		
	}

	/**
	 * 已知初始坐标，初速度向量，末速度向量，求出下一个坐标
	 * @param star 初始坐标
	 * @param v 初速度向量
	 * @param earth_v2 末速度向量
	 * @return 下一个坐标
	 */
	private MyPosition getNextPosition(MyPosition star, MyPosition v,
			MyPosition v2) {
		double x = star.x + (v.x + v2.x) * time / 2;
		double y = star.y + (v.y + v2.y) * time / 2;
		return new MyPosition(x, y);
	}

	private MyPosition aFlex(double asize, MyPosition a_vector) {
		double para = asize / (a_vector.getModule());
		return new MyPosition(para*a_vector.x, para*a_vector.y);
	}

	private MyPosition getNextV(MyPosition v, double theta, double accel,
			MyPosition a_vector) {
//		double sin_theta = Math.sin(theta);
//		double cos_theta = Math.cos(theta);
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

//	private MyPosition getVerticalOffset(double s_y, MyPosition earth_forward,
//			MyPosition a_vector, MyPosition v) {
//		double midx = a_vector.y * s_y / a_vector.getModule();
//		double midy = -a_vector.x * s_y / a_vector.getModule();
//		double judge = midx * v.x + midy * v.y;
//		//向量是否和速度v夹成锐角，如果不是，则取负值
//		if (judge < 0) {
//			midx = -midx;
//			midy = -midy;
//		}
//		double xoff = midx + earth_forward.x;
//		double yoff = midy + earth_forward.y;
//		return new MyPosition(xoff, yoff);
//	}

//	private MyPosition getForwardOffset(double s_x, MyPosition earth,
//			MyPosition a_vector) {
//		double midx = a_vector.x * s_x / a_vector.getModule();
//		double midy = a_vector.y * s_x / a_vector.getModule();
//		double xoff = midx + earth.x;
//		double yoff = midy + earth.y;
//		return new MyPosition(xoff, yoff);
//	}

	private double getAngle(MyPosition a_vector, MyPosition v) {
		
		if (v.isZero()) {
			System.out.println("速度为0");
			return 0.0;
		}
		double fenmu = a_vector.getModule() * v.getModule();
		double fenzi = a_vector.x * v.x + a_vector.y * v.y;
		return Math.acos(fenzi / fenmu);
	}

//	private double getSy(MyPosition v, double theta, double t) {
//		return v.getModule() * Math.sin(theta) * t;
//	}
//
//	private double getSx(MyPosition v, double theta, double t, double a) {
//		double s1 = v.getModule() * Math.cos(theta);
//		double s2 = a * t * t / 2;
//		System.out.println("{s1 = v.getModule() * Math.cos(theta)} @ " +
//					s1 + "=" + v.getModule() + "*" + Math.cos(theta) + "; s2=" + s2);
//		return s1 + s2;
//	}

	private double getDistance(MyPosition sun, MyPosition earth) {
		double delta_x = earth.x - sun.x;
		double delta_y = earth.y - sun.y;
		double distance = Math.sqrt(delta_x * delta_x + delta_y * delta_y);
		return distance;
	}

//	private double getDir(MyPosition sun, MyPosition earth) {
//		double number = (earth.y - sun.y) / (earth.x - sun.x);
//		double theta = Math.atan(number);
//		//加绝对值是否有问题？
//		return Math.abs(theta);
//	}

}

