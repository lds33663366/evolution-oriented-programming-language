package gravityTest.copy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;

public class Sun extends Star {
	
	@Override
	protected void init() {
		weight = 2E30;
		position = new MyPosition(0, 0);
		velocity = new MyPosition(100, 0);
	}

	@Override
	public void paintedOnAxis(Graphics2D g, double paint_x, double paint_y,
			String paint) {
		g.drawString(paint, 10, 70);
		Ellipse2D.Double star_draw = new Ellipse2D.Double(paint_x, paint_y, 30, 30);
		g.fill(star_draw);
	}

	
	
//	@Override
//	public void draw(Graphics2D g, Color color) {
//		g.setColor(color);
//		
//		double paint_x = 500 + position.x / GravityClient.scale;
//		double paint_y = 500 + position.y / GravityClient.scale;
//		Ellipse2D.Double star_draw = new Ellipse2D.Double(paint_x, paint_y, 30, 30);
//		g.fill(star_draw);
//		String name = this.getClass().getSimpleName().toUpperCase();
//		String axis = name + "坐标轴位置：["  + (int)paint_x + ", " + (int)paint_y + "];";
//		String ori_axis = "\t原始坐标:" + this.getPosition(); 
//		String volicity = "\t速度："+this.getVelocity().getModule();
//		g.drawString(axis + ori_axis + volicity, 10, 70);
//	}
}
