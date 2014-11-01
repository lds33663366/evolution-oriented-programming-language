package gravityTest.star;

import gravityTest.GravityClient;
import gravityTest.MyPosition;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public abstract class Star {

	public double weight;
	protected MyPosition position;
	protected MyPosition oriPosition;//记录原来位置
	protected MyPosition velocity;
	
	public static final double Gravity_constant = 6.67E-11;
	public static final double time = 3600 * 24;
	
	protected static Toolkit tk = Toolkit.getDefaultToolkit();
	public BufferedImage image;
	
	public Star() {
		init();
		oriPosition = position;
	}
	
	protected abstract void init();

	public MyPosition getPosition() {
		return position;
	}

	public void setPosition(MyPosition position) {
		this.position = position;
	}

	public MyPosition getVelocity() {
		return velocity;
	}

	public void setVelocity(MyPosition velocity) {
		this.velocity = velocity;
	}

	public void draw(Graphics2D g) {
		
		double paint_x = 500 + position.x / GravityClient.scale;
		double paint_y = 500 + position.y / GravityClient.scale;
		
		String name = this.getClass().getSimpleName().toUpperCase();
		String axis = name + "坐标轴位置：["  + (int)paint_x + ", " + (int)paint_y + "];";
		String ori_axis = "    原始坐标:" + this.getPosition(); 
		String volicity = "    速度："+this.getVelocity().getModule();
		String paint = axis + ori_axis + volicity;

		g.drawImage(image, (int)(paint_x-image.getWidth()/2), (int)paint_y-image.getHeight()/2, null);
		g.drawString(name, (int)(paint_x-image.getWidth()/2), (int)paint_y-image.getHeight()/2);
		
		paintedOnAxis(g, paint_x, paint_y, paint);
	}
	
	//越转越大
	public void moveForwardVelocity() {
		
		oriPosition = position.clone();
		double x, y;
		x = position.x + velocity.x * time;
		y = position.y + velocity.y * time;
		position.setLocation(x, y);
	}
	
	//越转越小
	public void moveForwardVelocity2() {
		
		double x, y;
		x = position.x + velocity.x * time;
		y = position.y + velocity.y * time;
		position.setLocation(x, y);
		oriPosition = position.clone();
	}
	
	public void moveForwardStar(Star star) {

		MyPosition a_vector = getAcclVector(star);
		
		velocity = getNextV(velocity, a_vector);//求末速度矢量
		
		position = getNextPosition(position, a_vector);
	}
	
//	public void move(Star star1, Star star2) {
//		
//	}
	private MyPosition getAcclVector(Star star) {
		// 求半径
		double r = getDistance(star.getPosition());

		// 求加速度
		double a = getAccl(star.weight, r);
		MyPosition a_vector = new MyPosition(star.getPosition().x - oriPosition.x,
				star.getPosition().y - oriPosition.y);
		a_vector = aFlex(a, a_vector);
		return a_vector;
	}
	
	/**
	 * 已知初始坐标，初速度向量，末速度向量，求出下一个坐标
	 * @param oriPosition 初始坐标
	 * @param v 初速度向量
	 * @param a_vector 加速度向量
	 * @param earth_v2 末速度向量
	 * @return 下一个坐标
	 */
	private MyPosition getNextPosition(MyPosition oriPosition, MyPosition a_vector) {
		
		double x,y;
//		x = oriPosition.x + (start_v.x + end_v.x) * time / 2;	
//		y = oriPosition.y + (start_v.y + start_v.y) * time / 2;
		x = oriPosition.x + a_vector.x * time * time / 2;
		y = oriPosition.y + a_vector.y * time * time / 2;

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
	 * a=G/(r^2), 加速度等于引力常量比上距离的平方
	 * @param weight 另一星球的质量
	 * @param r 两星球之间的距离
	 * @return 加速度
	 */
	private double getAccl(double weight, double r) {

		return Gravity_constant * weight / (r*r);
	}
	
	/**
	 * 在给定初始速度矢量，加速度向量，求末速度矢量
	 * 假定时间很短，有加速度向量乘以时间=速度向量。
	 * @param v 初始速度矢量
	 * @param a_vector 加速度矢量
	 * @return 末速度矢量
	 */
	private MyPosition getNextV(MyPosition v, MyPosition a_vector) {

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
	 * 已知两点的坐标，求距离
	 * @param star 一个坐标点
	 * @return 距离
	 */
	private double getDistance(MyPosition star) {
		double delta_x = oriPosition.x - star.x;
		double delta_y = oriPosition.y - star.y;
		double distance = Math.sqrt(delta_x * delta_x + delta_y * delta_y);
		System.out.println("两点之间距离：" + distance);
		return distance;
	}
	
	public abstract void paintedOnAxis(Graphics2D g, double paint_x, double paint_y, String paint);
}