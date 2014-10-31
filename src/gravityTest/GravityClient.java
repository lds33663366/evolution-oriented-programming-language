package gravityTest;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;


public class GravityClient {

	Star sun, earth, mars;
	public static final double time = 3600 * 24;

	public static final double scale = 13E8;
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
		sun = new Sun();
		earth = new Earth();
		mars = new Mars();
	}

	void draw(Graphics gr) {
		Graphics2D g = (Graphics2D)gr;
		
		sun.draw(g, Color.YELLOW);
		earth.draw(g, Color.BLUE);
		mars.draw(g, Color.RED);
		
		g.setColor(Color.WHITE);
		g.drawString("比例尺：" + scale + "/1", 1100, 800);
		
		sun.moveForwardVelocity();
		sun.moveForwardStar(earth);
		sun.moveForwardStar(mars);
		
		earth.moveForwardVelocity();
		earth.moveForwardStar(sun);
		earth.moveForwardStar(mars);
		
		mars.moveForwardVelocity();
		mars.moveForwardStar(sun);
		mars.moveForwardStar(earth);
//		compute(sun, earth);
//		compute(earth, sun);
//		compute(sun, mars);
//		compute(mars, sun);
//		compute(earth, mars);
//		compute(mars, earth);
		
//		positions.add(new MyPosition(paint_earth_x+5, paint_earth_y+5));
//		drawTrack(g);
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