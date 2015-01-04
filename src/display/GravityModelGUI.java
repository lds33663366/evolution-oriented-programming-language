package display;
import initiator.InstancesManager;
import initiator.ThreadTimeConsole;
import initiator.TopicSubscriber;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import structure.InstanceImage;
import structure.Message;
import msgManager.MsgPool;

public class GravityModelGUI extends Frame{
	public static final int MODEL_WIDTH = 1200;
	public static final int MODEL_HEIGHT = 850;
	public static final double SCALE = 8E8;
	
	XMLClient nc;
	
	CopyOnWriteArrayList<Virtual> virtuals;
	
	Image offScreenImage = null;
	private boolean isShowed = true;
	private boolean messageReceived;
	
	public GravityModelGUI() {
		init();
	}
	
	void init() {
		
		virtuals = new CopyOnWriteArrayList<Virtual>(); 
		messageReceived = true;
		

	}
	
	public void paint(Graphics gr) {
		Graphics2D g = (Graphics2D) gr;
		Color c = g.getColor();
		drawVirtuals(g);
		g.setColor(c);
	}

	private void drawVirtuals(Graphics2D g) {

		for (Iterator<Virtual> iter=virtuals.iterator(); iter.hasNext();) {
			Virtual vir = iter.next();
			vir.draw(g);
		}
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
				close();
				System.exit(0);
			}
		});
		this.setResizable(false);
		this.setBackground(Color.BLACK);
		setVisible(true);
		
		new Thread(new PaintThread()).start();
		
		nc = new XMLClient(this);
		nc.start();
	}

	public static void main(String[] args) {
		GravityModelGUI gm = new GravityModelGUI();
		gm.lauchFrame();
	}
	
	private class PaintThread implements Runnable {

		public void run() {
			while(isShowed) {
//				if (messageReceived) {
//					messageReceived = false;
//				}
					repaint();
				try {
					Thread.sleep(ThreadTimeConsole.Thread_GUI.getTime());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void close() {
		isShowed = false;
		dispose();
		nc.disconnect();
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void obtainTopicMessage(Message message) {
		String name = message.getFrom();
		for (int i=0, j=virtuals.size(); i<j; i++) {
			Virtual vir = virtuals.get(i);
			if (vir.getName().equals(name)) {
				vir.setMessage(message);
				return;
			}
		}
		virtuals.add(new Virtual(name, message));
//		System.out.println("GUI拿到消息--" + message);
		messageReceived = true;
	}

}