package gravityTest;
import java.awt.*;
import java.awt.event.*;

public class GravityModel extends Frame {
	public static final int MODEL_WIDTH = 1200;
	public static final int MODEL_HEIGHT = 850;
	
	int x = 50, y = 50;
	
	Image offScreenImage = null;
	
	int earth_x, earth_y;
	Test2 test = new Test2();
	public void paint(Graphics g) {
		test.draw(g);
	}
	
	public void update(Graphics g) {
		if(offScreenImage == null) {
			offScreenImage = this.createImage(MODEL_WIDTH, MODEL_HEIGHT);
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.BLACK);
		gOffScreen.fillRect(0, 0, MODEL_WIDTH, MODEL_HEIGHT);
		gOffScreen.setColor(c);
		paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);
	}

	public void lauchFrame() {
//		this.setLocation(00, 300);
		this.setSize(MODEL_WIDTH, MODEL_HEIGHT);
		this.setTitle("引力模型");
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		this.setResizable(false);
		this.setBackground(Color.BLACK);
		setVisible(true);
		
		new Thread(new PaintThread()).start();
	}

	public static void main(String[] args) {
		GravityModel gm = new GravityModel();
		gm.lauchFrame();
	}
	
	private class PaintThread implements Runnable {

		public void run() {
			int count = 1000;
			while(true) {
//				count--;
//				if (count == 6);
				repaint();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}