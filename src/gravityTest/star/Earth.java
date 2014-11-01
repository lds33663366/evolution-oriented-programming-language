package gravityTest.star;

import gravityTest.MyPosition;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Earth extends Star {

	@Override
	protected void init() {
		weight = 5.98E24;
		position = new MyPosition(1.496E11, 0);
		velocity = new MyPosition(0, 29783);
		
		try {
			image = ImageIO.read(new File("./src/images/earth.png"));	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void paintedOnAxis(Graphics2D g, double paint_x, double paint_y,
			String paint) {
		g.setColor(Color.BLUE);
		g.drawString(paint, 10, 90);
	}

}
