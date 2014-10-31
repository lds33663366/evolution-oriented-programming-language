package gravityTest.copy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public abstract class Star {

	public double weight;
	protected MyPosition position;
	protected MyPosition velocity;
	
	public Star() {
		init();
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

	public void draw(Graphics2D g, Color color) {
		g.setColor(color);
		
		double paint_x = 500 + position.x / GravityClient.scale;
		double paint_y = 500 + position.y / GravityClient.scale;
		
		String name = this.getClass().getSimpleName().toUpperCase();
		String axis = name + "坐标轴位置：["  + (int)paint_x + ", " + (int)paint_y + "];";
		String ori_axis = "    原始坐标:" + this.getPosition(); 
		String volicity = "    速度："+this.getVelocity().getModule();
		String paint = axis + ori_axis + volicity;
		
		paintedOnAxis(g, paint_x, paint_y, paint);
	}
	
	public abstract void paintedOnAxis(Graphics2D g, double paint_x, double paint_y, String paint);
}