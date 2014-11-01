package gravityTest.star;

import gravityTest.MyPosition;

import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Moon extends Star {

	@Override
	protected void init() {
		weight = 7.3477E22;
		position = new MyPosition(1.496E11, 4E8);
		velocity = new MyPosition(-1000, 0);
		
		try {
			image = ImageIO.read(new File("./src/images/mars.png"));	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paintedOnAxis(Graphics2D g, double paint_x, double paint_y,
			String paint) {
		
		g.drawString(paint, 10, 130);

	}

}
