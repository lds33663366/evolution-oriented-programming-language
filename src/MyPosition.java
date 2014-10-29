import java.awt.geom.Point2D;

class MyPosition extends Point2D{
	double x;
	double y;
	
	public MyPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean isZero() {
		if (x<0.000001 && x>-0.0000001){
			if (y<0.0000001 && y>-0.0000001)
				return true;
		}
		return false;
	}

	public double getModule() {
		return Math.sqrt(x*x + y*y);
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
	
	
}
