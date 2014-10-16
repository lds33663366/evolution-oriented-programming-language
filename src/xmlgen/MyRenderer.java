package xmlgen;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
/**
 * �滻���νṹĬ�ϵ�ͼ��
 * @author heb
 *
 */
class MyRenderer extends DefaultTreeCellRenderer { 
	public Component getTreeCellRendererComponent(JTree tree, Object value,boolean sel, boolean expanded, boolean leaf, int row,boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,row, hasFocus);
		setLeafIcon(new ImageIcon(XMLGenGUI.class.getResource("/images/1.jpg")));//Ҷ�ӽ��ͼƬ
		setClosedIcon(new ImageIcon(XMLGenGUI.class.getResource("/images/1.jpg")));//�ر�������ʾ��ͼƬ
		setOpenIcon(new ImageIcon(XMLGenGUI.class.getResource("/images/2.jpg")));//����ʱ��ʾ��ͼƬ
		return this;
	}
}
