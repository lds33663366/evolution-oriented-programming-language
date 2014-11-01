package gravityTest.star;

import gravityTest.MyPosition;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sun extends Star {

	@Override
	protected void init() {
		weight = 2E30;
		position = new MyPosition(0, 0);
		velocity = new MyPosition(10, 0);
		
		try {
			image = ImageIO.read(new File("./src/images/sun.png"));	
		} catch (IOException e) {
			e.printStackTrace();
		}
//		image = tk.getImage(Star.class.getClassLoader().getResource("images/sun.png"));
	}

	@Override
	public void paintedOnAxis(Graphics2D g, double paint_x, double paint_y,
			String paint) {
		g.setColor(Color.YELLOW);
		g.drawString(paint, 10, 70);
//		try {
//			image = ImageIO.read(new File("./src/images/sun.png"));
//			g.drawImage(image, (int)(paint_x-image.getWidth()/2), (int)paint_y-image.getHeight()/2, null);
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
//		Ellipse2D.Double star_draw = new Ellipse2D.Double(paint_x-15, paint_y-15, 30, 30);
//		g.fill(star_draw);
	}
}
