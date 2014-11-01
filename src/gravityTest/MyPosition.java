package gravityTest;
import java.awt.geom.Point2D;

public class MyPosition extends Point2D{
	public double x;
	public double y;
	
	public MyPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean isZero() {
		if(xisZero() && yisZero()) return true;
		return false;
	}

	public double getModule() {
		return Math.sqrt(x*x + y*y);
	}
	
	public MyPosition getScale() {
		double x = this.x/GravityClient.scale;
		double y = this.y/GravityClient.scale;
		return new MyPosition(x, y);
	}
	
	
	@Override
	public MyPosition clone() {
		MyPosition position = new MyPosition(x, y);
		return position;
	}
	
	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
		
	}

	public boolean xisZero() {
		if (x<0.000001 && x>-0.0000001) return true;
		return false;
	}
	
	public boolean yisZero() {
		if (y<0.000001 && y>-0.0000001) return true;
		return false;
	}
	
	
}
