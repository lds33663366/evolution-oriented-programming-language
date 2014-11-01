package gravityTest;
import gravityTest.star.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;


public class GravityClient {

	Star sun, earth, mars, saturn, jupiter;
	public static final double time = 3600 * 24;
	public List<Star> stars = new ArrayList<Star>();

	public static final double scale = 35E8;
	List<MyPosition> positions = new ArrayList<MyPosition>(10000);
	
//	public static void main(String[] args) {
//		Test t = new Test();
//		for (int i=0; i<5; i++) {
//			System.out.println("是否变动？ earth_v=" + t.earth_v);
//			t.compute(t.sun, t.earth, t.earth_v);
//		}
//	}
	public GravityClient() {
		init();
	}
	
	private void init() {
		
		stars.add(new Sun());
//		stars.add(new Earth());
//		stars.add(new Mars());
		stars.add(new Jupiter());
//		stars.add(new Saturn());
		
		
	}
	
	void drawStars(Graphics2D g, List<Star> stars) {
		for (int i=0; i<stars.size(); i++) {
			stars.get(i).draw(g);
		}
	}
	
	int tag = 0;
	void move(List<Star> stars) {
		
		for (int i=0; i<stars.size(); i++) {
			
			Star moveStar = stars.get(i);

			if ((tag++)%3!=0) moveStar.moveForwardVelocity();
			else moveStar.moveForwardVelocity2();
//			moveStar.moveForwardVelocity();
			for (int j=0; j<stars.size(); j++) {
				if (stars.get(j) == moveStar) continue;
				moveStar.moveForwardStar(stars.get(j));
			}
		}
	}

	void draw(Graphics gr) {
		Graphics2D g = (Graphics2D)gr;
		
		drawStars(g, stars);
		
		g.setColor(Color.WHITE);
		g.drawString("比例尺：" + scale + ":1", 1100, 800);
		
		move(stars);
	}
	
	private void drawTrack(Graphics2D g) {
		g.setColor(Color.GRAY);
		Line2D.Double lines = null;
		for (int i=0; i<positions.size()-1; i++) {
//			if (i%2==0) continue;
			lines = new Line2D.Double(positions.get(i), positions.get(i+1));
			g.draw(lines);
		}
//		if (positions.size()>1000) positions.remove(0);
		
	}
}