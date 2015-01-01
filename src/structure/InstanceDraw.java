package structure;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class InstanceDraw {

	public static final double SCALE = 8E8;
	Instance instance;
	Color instanceColor;
	BufferedImage bimage;
	
	List<Integer> xList = new ArrayList<Integer>(100);
	List<Integer> yList = new ArrayList<Integer>(100);
	
	public InstanceDraw(Instance instance) {
		instanceColor = getColor(instance.getName(), instance.getId());
		this.instance = instance;
		
		String path = "./src/images/" + instance.getName().toLowerCase().trim() + ".png";
		try {
			bimage = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Color getColor(String instanceName, int id) {
		
		int a = (instanceName.hashCode())%256;
		int b = (id * 15 + 37)%256;
		int c = (instanceName.length() * 40 + 37)%256;
		return new Color(a, b, c);
		
	}

	public void draw(Graphics2D g) {
		
		double x, y;
		synchronized (instance) {
			x = Double.parseDouble(instance.obtainValue("position.x"));
			y = Double.parseDouble(instance.obtainValue("position.y"));
		}
		int ix = (int) (500+ x / SCALE);
		int iy = (int) (500+ y / SCALE);
		
		//添加坐标到集合，用于查看轨迹
		xList.add(ix);
		yList.add(iy);
		
		int map_pointx = (int)(ix-bimage.getWidth()/2);
		int map_pointy = (int)(iy-bimage.getHeight()/2);
		
		g.setColor(instanceColor);
		//显示instance在坐标图上的坐标
		g.drawString(instance.getIdName() + ":[" + ix + "," + iy + "]" , map_pointx, map_pointy);
		if (instance.getName().equals("sun")){
			g.drawString( "[" + x + "," + y + "]" , map_pointx+100, map_pointy);
		}
		//显示instance在坐标图上的图片
		g.drawImage(bimage, map_pointx, map_pointy, null);

		drawTrack(g);
	}
	
	private void drawTrack(Graphics2D g) {
		g.setColor(instanceColor.darker());
		for (int i=0 ; i<xList.size()-1; i++) {
			g.drawLine(xList.get(i), yList.get(i), xList.get(i+1), yList.get(i+1));;
		}
		
	}

}
