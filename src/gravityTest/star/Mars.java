package gravityTest.star;

import gravityTest.MyPosition;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Mars extends Star {

	@Override
	protected void init() {
		weight = 6.418E23;
		position = new MyPosition(-2.496E11, 0);
		velocity = new MyPosition(0, -24000);
//		image = tk.getImage(Star.class.getClassLoader().getResource("images/mars.png"));
//		weight = 2E29;
//		position = new MyPosition(0, 0);
//		velocity = new MyPosition(100, 0);
		try {
			image = ImageIO.read(new File("./src/images/mars.png"));	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void paintedOnAxis(Graphics2D g, double paint_x, double paint_y,
			String paint) {
		g.setColor(Color.RED);
		g.drawString(paint, 10, 110);
//		Ellipse2D.Double star_draw = new Ellipse2D.Double(paint_x-7.5, paint_y-7.5, 15, 15);
//		try {
//			image = ImageIO.read(new File("./src/images/mars.png"));
//			g.drawImage(image, (int)(paint_x-image.getWidth()/2), (int)paint_y-image.getHeight()/2, null);
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		g.fill(star_draw);
	}
	
//	@Override
//	public void draw(Graphics2D g, Color color) {
//		g.setColor(color);
//		
//		double paint_x = 500 + position.x / GravityClient.scale;
//		double paint_y = 500 + position.y / GravityClient.scale;
//		Ellipse2D.Double star_draw = new Ellipse2D.Double(paint_x, paint_y, 15, 15);
//		g.fill(star_draw);
//		String name = this.getClass().getSimpleName().toUpperCase();
//		String axis = name + "坐标轴位置：["  + (int)paint_x + ", " + (int)paint_y + "];";
//		String ori_axis = "\t原始坐标:" + this.getPosition(); 
//		String volicity = "\t速度："+this.getVelocity().getModule();
//		g.drawString(axis + ori_axis + volicity, 10, 110);
//	}

}
