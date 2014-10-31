package gravityTest.copy;
import java.awt.*;
import java.awt.event.*;

public class GravityModelGUI extends Frame {
	public static final int MODEL_WIDTH = 1200;
	public static final int MODEL_HEIGHT = 850;
	
	Image offScreenImage = null;
	
	int earth_x, earth_y;
	GravityClient test = new GravityClient();
	public void paint(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.WHITE);
		g.drawString("第" + day + "天", 1100, 770);
		test.draw(g);
		g.setColor(c);
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
		GravityModelGUI gm = new GravityModelGUI();
		gm.lauchFrame();
	}
	
	int day = 0;
	private class PaintThread implements Runnable {

		public void run() {
			while(true) {
//				count--;
//				if (count == 6);
				day++;
				repaint();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}