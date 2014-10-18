package xmlgen;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
/**
 * 替换树形结构默认的图标
 * @author heb
 *
 */
class MyRenderer extends DefaultTreeCellRenderer { 
	public Component getTreeCellRendererComponent(JTree tree, Object value,boolean sel, boolean expanded, boolean leaf, int row,boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,row, hasFocus);
		setLeafIcon(new ImageIcon(XMLGenGUI.class.getResource("/images/1.jpg")));//叶子结点图片
		setClosedIcon(new ImageIcon(XMLGenGUI.class.getResource("/images/1.jpg")));//关闭树后显示的图片
		setOpenIcon(new ImageIcon(XMLGenGUI.class.getResource("/images/2.jpg")));//打开树时显示的图片
		return this;
	}
}
