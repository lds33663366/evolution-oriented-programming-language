import java.awt.*;
import java.awt.Graphics2D;
import java.awt.event.*;
import javax.swing.*;

public class TestGuiji extends JPanel implements MouseListener, MouseMotionListener {

private Polygon m_polygon;
private int m_beginX, m_beginY;
private boolean m_mousePressing = false;

public TestGuiji() {
super();
setPreferredSize(new Dimension(640, 640));
addMouseListener(this);
addMouseMotionListener(this);
}

@Override
public void paintComponent(Graphics g) {
Graphics2D g2d = (Graphics2D) g;
if (m_mousePressing) {
g2d.drawPolyline(m_polygon.xpoints, m_polygon.ypoints, m_polygon.npoints);
}

}

public void mouseClicked(MouseEvent e) {

}

public void mousePressed(MouseEvent e) {
m_beginX = e.getX();
m_beginY = e.getY();
m_mousePressing = true;
m_polygon = new Polygon();
m_polygon.addPoint(m_beginX, m_beginY);
}

public void mouseReleased(MouseEvent e) {
m_mousePressing = false;
}

public void mouseEntered(MouseEvent e) {

}

public void mouseExited(MouseEvent e) {

}

public void mouseDragged(MouseEvent e) {
int currentX = e.getX();
int currentY = e.getY();
m_polygon.addPoint(currentX, currentY);
repaint();
}

public void mouseMoved(MouseEvent e) {

}
}