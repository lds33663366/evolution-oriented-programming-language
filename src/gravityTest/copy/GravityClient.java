package gravityTest.copy;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;


public class GravityClient {

	Star sun, earth, mars;
	double time = 3600 * 24;
	public final double Gravity_constant = 6.67E-11;
	public static final double scale = 15E8;
	List<MyPosition> positions = new ArrayList<MyPosition>(10000);
	
//	public static void main(String[] args) {
//		Test t = new Test();
//		for (int i=0; i<5; i++) {
//			System.out.println("是否变动？ earth_v=" + t.earth_v);
//			t.compute(t.sun, t.earth, t.earth_v);
//		}
//	}
	public GravityClient() {
		init();
	}
	
	private void init() {
		sun = new Sun();
		earth = new Earth();
		mars = new Mars();
	}

	void draw(Graphics gr) {
		Graphics2D g = (Graphics2D)gr;
		
		sun.draw(g, Color.YELLOW);
		earth.draw(g, Color.BLUE);
		mars.draw(g, Color.RED);
		
		g.setColor(Color.WHITE);
		g.drawString("比例尺：" + scale + "/1", 1100, 800);
		
		compute(sun, earth);
		compute(earth, sun);
		compute(sun, mars);
		compute(mars, sun);
//		compute(earth, mars);
//		compute(mars, earth);
		
//		positions.add(new MyPosition(paint_earth_x+5, paint_earth_y+5));
//		drawTrack(g);
	}
	
	private void drawTrack(Graphics2D g) {
		g.setColor(Color.GRAY);
		Line2D.Double lines = null;
		for (int i=0; i<positions.size()-1; i++) {
//			if (i%2==0) continue;
			lines = new Line2D.Double(positions.get(i), positions.get(i+1));
			g.draw(lines);
		}
//		if (positions.size()>1000) positions.remove(0);
		
	}

	public Star compute(Star star1, Star star2) {
		
		System.out.println("开始计算----------------------------------------------------------------------------");
		System.out.println("已知sun = " + star1.getPosition() + "; star = " + star2.getPosition() + "; star的速度v = " + star2.getVelocity());
		double r = getDistance(star1.getPosition(), star2.getPosition());
		System.out.println("两点之间的距离是" + r);
		
//		a = 0.3;
		double a = getAccl(star1.weight, r);
		MyPosition a_vector = new MyPosition(star1.getPosition().x-star2.getPosition().x, 
				star1.getPosition().y-star2.getPosition().y);
		a_vector = aFlex(a, a_vector);
		System.out.println("两点之间的加速度向量a_vector=" + a_vector);

		double theta = getAngle(a_vector, star2.getVelocity());
		System.out.println("两点连成的直线与速度的夹角theta=" + theta);
		
		MyPosition record_first_v = star2.getVelocity().clone();
		star2.setVelocity(getNextV(record_first_v, theta, a_vector ));
		System.out.println("速度下一个方向向量为earth_v=" + star2.getVelocity());

		star2.setPosition(getNextPosition(star2, record_first_v, star2.getVelocity(), a_vector));

		return star2;
		
	}

	/**
	 * a=G/(r^2), 加速度等于引力常量比上距离的平方
	 * @param weight 另一星球的质量
	 * @param r 两星球之间的距离
	 * @return 加速度
	 */
	private double getAccl(double weight, double r) {

		return Gravity_constant * weight / (r*r);
	}

	/**
	 * 已知初始坐标，初速度向量，末速度向量，求出下一个坐标
	 * @param star2 初始坐标
	 * @param v 初速度向量
	 * @param a_vector 加速度向量
	 * @param earth_v2 末速度向量
	 * @return 下一个坐标
	 */
	private MyPosition getNextPosition(Star star2, MyPosition start_v,
			MyPosition end_v, MyPosition a_vector) {
		
		double x,y;
		x = star2.getPosition().x + (start_v.x + end_v.x) * time / 2;	
		y = star2.getPosition().y + (start_v.y + start_v.y) * time / 2;

		return new MyPosition(x, y);
	}

	/**
	 * 在给定加速度大小，方向的情况下获取加速度向量
	 * @param asize 加速度的大小
	 * @param a_vector 加速度的方向
	 * @return 加速度矢量
	 */
	private MyPosition aFlex(double asize, MyPosition a_vector) {
		double para = asize / (a_vector.getModule());
		return new MyPosition(para*a_vector.x, para*a_vector.y);
	}

	/**
	 * 在给定初始速度矢量，加速度向量和它们之间的夹角下，求末速度矢量
	 * 假定时间很短，有加速度向量乘以时间=速度向量。
	 * @param v 初始速度矢量
	 * @param theta 初始速度矢量与加速度矢量的夹角
	 * @param a_vector 加速度矢量
	 * @return 末速度矢量
	 */
	private MyPosition getNextV(MyPosition v, double theta,
			MyPosition a_vector) {

		//加速度向量转化的速度
		double v_tx = a_vector.x * time;	
		double v_ty = a_vector.y * time;

		//初始速度与加速度合成
		double v_x = v.x+v_tx;
		double v_y = v.y+v_ty;
		System.out.println("{v_x = v.x+v_tx; v_y = v.y+v_ty} @" 
				+ v_x + "=" + v.x + "+" + v_tx + ";" + v_y + "=" + v.y + "+" + v_ty );

		return new MyPosition(v.x+v_tx, v.y+v_ty);

	}

	/**
	 * 已知加速度矢量，初始速度矢量，可求它们之间的夹角
	 * @param a_vector 加速度矢量
	 * @param v 初始速度矢量
	 * @return 初始速度矢量与加速度矢量的夹角
	 */
	private double getAngle(MyPosition a_vector, MyPosition v) {
		
		if (v.isZero()) {
			System.out.println("速度为0");
			return 0.0;
		}
		double fenmu = a_vector.getModule() * v.getModule();
		double fenzi = a_vector.x * v.x + a_vector.y * v.y;
		return Math.acos(fenzi / fenmu);
	}

	/**
	 * 已知两点的坐标，求距离
	 * @param sun 一个坐标点
	 * @param earth 另一坐标点
	 * @return 距离
	 */
	private double getDistance(MyPosition sun, MyPosition earth) {
		double delta_x = earth.x - sun.x;
		double delta_y = earth.y - sun.y;
		double distance = Math.sqrt(delta_x * delta_x + delta_y * delta_y);
		return distance;
	}
}