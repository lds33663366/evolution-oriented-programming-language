package gravityTest.star;

import gravityTest.MyPosition;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Saturn extends Star{

	@Override
	protected void init() {
		weight = 5.6846E26;
		position = new MyPosition(1.4E12, 0);
		velocity = new MyPosition(0, 9690);
		
		try {
			image = ImageIO.read(new File("./src/images/saturn.png"));	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paintedOnAxis(Graphics2D g, double paint_x, double paint_y,
			String paint) {
		g.setColor(Color.WHITE);
		g.drawString(paint, 10, 130);
	}

}
