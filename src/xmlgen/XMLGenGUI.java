package xmlgen;

import initiator.Compiler;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.XMLDecoder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JInternalFrame;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.dom4j.Element;
import javax.swing.JDesktopPane;

public class XMLGenGUI extends JFrame {
	private MyJEditorPane textArea;
	
	/*
	 * XMLTree的根节点
	 */
	private DefaultMutableTreeNode dmtnRoot=new DefaultMutableTreeNode("system");
	private JMenuItem newMenuItem;
	private JMenuItem openMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem saveAsMenuItem;
	private JMenuItem showTreeMenuItem;
	private XMLGenerator XMLGene;
	private JTree jtree;
	/*
	 * 右键显示的菜单
	 */
	private JPopupMenu rightMenu=new JPopupMenu();
	private JInternalFrame leftInternalFrame;
	private JInternalFrame bottomInternalFrame;
	private static JTextArea resultArea;
	private JMenuItem resultMenuItem;
	private JMenu compileMenu;
	private JMenuItem runMenuItem;
	/*
	 * 表示文件是否被修改的状态变量
	 */
	private boolean modefied;
	private JFileChooser filechooser;
	private Container con;
	private JScrollPane scrollPane_1;
	private JInternalFrame CodeShowInternalFrame;
	private JDesktopPane desktopPane;

	public XMLGenGUI() {
		super("未命名--***编译器");
		modefied=false;
		this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		getContentPane().setLayout(new BorderLayout(5, 5));
		int width = (int) (this.getWidth()*0.98);
		int height=(int) (this.getHeight()*0.90);
		con = getContentPane();
		ClickedEvent itemListener = new ClickedEvent();
		/*
		 * 菜单栏
		 */
		JMenuBar menuBar = new JMenuBar();
		con.add(menuBar, BorderLayout.NORTH);
		/*
		 * 文件菜单及其菜单项
		 */
		JMenu fileMenu = new JMenu("文件");
		menuBar.add(fileMenu);
		
		newMenuItem = new JMenuItem("新建");
		fileMenu.add(newMenuItem);
		newMenuItem.addActionListener(itemListener);
		
		openMenuItem = new JMenuItem("打开");
		fileMenu.add(openMenuItem);
		openMenuItem.addActionListener(itemListener);
		
		saveMenuItem = new JMenuItem("保存");
		fileMenu.add(saveMenuItem);
		saveMenuItem.addActionListener(itemListener);
		
		saveAsMenuItem = new JMenuItem("另存为");
		fileMenu.add(saveAsMenuItem);
		saveAsMenuItem.addActionListener(itemListener);
		/*
		 * 视图菜单及其菜单项
		 */
		JMenu viewMenu = new JMenu("视图");
		menuBar.add(viewMenu);
		
		showTreeMenuItem = new JMenuItem("显示XML Tree");
		viewMenu.add(showTreeMenuItem);
		
		resultMenuItem = new JMenuItem("显示Result");
		viewMenu.add(resultMenuItem);
		
		compileMenu = new JMenu("编译");
		menuBar.add(compileMenu);
		
		runMenuItem = new JMenuItem("运行");
		compileMenu.add(runMenuItem);
		/**
		 * 菜单项事件注册
		 */
		showTreeMenuItem.addActionListener(itemListener);
		resultMenuItem.addActionListener(itemListener);
		runMenuItem.addActionListener(itemListener);
		/*多个窗口管理pane*/
		desktopPane = new JDesktopPane();
		getContentPane().add(desktopPane, BorderLayout.CENTER);
		/*
		 * 代码显示文本域
		 */
		CodeShowInternalFrame = new JInternalFrame("Code",true,true,true,false);
		CodeShowInternalFrame.setBounds((int) (width*0.2),0,(int) (width*0.8),(int) (height*0.7));
		desktopPane.add(CodeShowInternalFrame);
		CodeShowInternalFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		CodeShowInternalFrame.setBorder(UIManager.getBorder("FileChooser.listViewBorder"));
		//重载JEditorPane的类对象，用来显示代码
		textArea = new MyJEditorPane();
		JScrollPane scrollPane = new JScrollPane(textArea);
		//CodeShowInternalFrame.getContentPane().add(scrollPane, BorderLayout.NORTH);
		
		CodeShowInternalFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		textArea.setContentType("text/xml");
		/*
		 * 底窗口的设置
		 */
		bottomInternalFrame = new JInternalFrame("Result",true,true,true,false);
		bottomInternalFrame.setBounds(0,(int) (height*0.7),width,(int) (height*0.3));
		desktopPane.add(bottomInternalFrame);
		bottomInternalFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		bottomInternalFrame.setBorder(UIManager.getBorder("FileChooser.listViewBorder"));
		
		//bottomInternalFrame.setVisible(true);
		
		resultArea = new JTextArea();
		//resultArea.setRows(12);
		scrollPane_1 = new JScrollPane(resultArea);
		bottomInternalFrame.getContentPane().add(scrollPane_1, BorderLayout.CENTER);
		bottomInternalFrame.setVisible(true);
		/*
		 * 左侧窗口的设置
		 */
		leftInternalFrame = new JInternalFrame("XML Tree",true,true,true,false);
		leftInternalFrame.setBounds(0, 0, (int)(0.2*width), (int)(0.7*height));
		desktopPane.add(leftInternalFrame);
		leftInternalFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		leftInternalFrame.setBorder(UIManager.getBorder("FileChooser.listViewBorder"));
		leftInternalFrame.setVisible(true);
		/*
		 * 元素节点的树形结构显示
		 */
		jtree = new JTree(dmtnRoot);  
		// 设置根节点是否显示  
		jtree.setRootVisible(false);  
		jtree.putClientProperty("JTree.lineStyle", "None");// 清除线  
		jtree.setShowsRootHandles(true);
		// 设置图标  
		jtree.setCellRenderer(new MyRenderer());
		
		JScrollPane jscrollPane = new JScrollPane(jtree);
		leftInternalFrame.getContentPane().add(jscrollPane);
		jtree.addMouseListener(new RightClickedListener());
		
		//重定向
		System.setOut(new PrintStream(System.out){
			public void println(String x) {
		        resultArea.append(x + "\n");
		      }
		});
		
		textArea.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void insertUpdate(DocumentEvent e) {
				modefied=true;
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				modefied=true;
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				modefied=true;
			}});
		CodeShowInternalFrame.setVisible(true);
		/**
		 * 监听文档是否被更新
		 */

		filechooser = new JFileChooser();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setBackground(Color.WHITE);
		
		setVisible(true);
	}
	public static JTextArea getResultArea() {
		return resultArea;
	}
	/**
	 * 内容追加函数
	 * @param str
	 */
	/*public void appendResultAreaText(String str) {
		resultArea.append(str);
	}*/
	/**菜单项的实事件监听类*/
	private class ClickedEvent implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == saveAsMenuItem) {
				saveAs();
			} else if (e.getSource() == saveMenuItem) {
				save();
			} else if (e.getSource() == newMenuItem) {
				/*
				 * 关闭原有文件
				 */
				closeFile();
				newFile();
				createXML();
			} else if (e.getSource() == openMenuItem) {
				/*
				 * 关闭原有文件
				 */
				closeFile();
				newFile();
				open();
			}else if (e.getSource() == showTreeMenuItem) {
				leftInternalFrame.setVisible(true);
			}else if (e.getSource() == resultMenuItem) {
				bottomInternalFrame.setVisible(true);
			}else if (e.getSource() == runMenuItem) {
				bottomInternalFrame.setVisible(true);
				/*
				 * 开始 编译运行 
				 */
				try {
					new Compiler("foodchain_20140629.xml", "mdl20140629.xsd");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	/**
	 * 关闭打开或新建的文件的函数
	 */
	
	private void closeFile(){
		//System.out.println(modefied);
		if(modefied){
			 int option = JOptionPane.showConfirmDialog(
					 null, "文件已修改，是否保存？",
					 "保存文件？", JOptionPane.YES_NO_OPTION, 
					 JOptionPane.WARNING_MESSAGE, null);
			 switch(option) { 
			 	// 确认文件保存
			 case JOptionPane.YES_OPTION:
				 save(); // 保存文件
				 break;
				 // 放弃文件保存
			 case JOptionPane.NO_OPTION:
				 break;
			 }
		}
		textArea.setText("");
		modefied=false;
	}
	/**
	 * 新建文件的函数
	 */
	private void newFile(){
		this.setTitle("未命名--***编译器");
	}
	/**
	 * 打开文件并在代码区显示的函数
	 */
	private void open(){
		/*
		 * 显示选择文件的窗口
		 */
		int n = filechooser.showOpenDialog(con);
		/*
		 * 点击确定后显示文件内容
		 */
		if (n == JFileChooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();
			this.setTitle(file.getPath()+"--***编译器");
			textArea.setText(null);
			try {
				FileInputStream fis;
				fis = new FileInputStream(file);
				/*设置文件输入流编码*/
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8"); 
				BufferedReader in = new BufferedReader(isr);
				String s;
				while ((s = in.readLine()) != null) {
					textArea.setText(textArea.getText()+s+"\r\n");
				}
				in.close();
				fis.close();
				isr.close();
			} catch (FileNotFoundException e1) {
					e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	/**
	 * 保存代码区的内容到文件的函数
	 */
	private void save(){
		String title=this.getTitle();
		/*
		 * 文件未保存过
		 */
		if("未命名".equals(title.substring(0,3))){
			saveAs();
		}else{
			/*
			 * 文件保存过
			 */
			String filename=title.substring(0,title.lastIndexOf("--"));
			try {
				FileOutputStream fos;
				fos = new FileOutputStream(filename);
				/*设置文件流编码*/
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8"); 
				BufferedWriter out = new BufferedWriter(osw);

				out.write(textArea.getText());
				out.close();
				fos.close();
				osw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			modefied=false;
		}
	}
	/**
	 * 另存为代码区的内容到文件的函数
	 */
	private void saveAs(){
		/*
		 * 显示保存文件的窗口
		 */
		int n = filechooser.showSaveDialog(con);
		/*
		 * 点击确定后显示文件内容
		 */
		if (n == JFileChooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();
			this.setTitle(file.getPath()+"--***编译器");
			try {
				FileOutputStream fos;
				fos = new FileOutputStream(file);
				/*设置文件流编码*/
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8"); 
				BufferedWriter out = new BufferedWriter(osw);

				out.write(textArea.getText());
				out.close();
				fos.close();
				osw.close();
				modefied=false;
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
		}
	}
	/**
	 * 右键菜单项的事件监听类
	 * @author heb
	 *
	 */
	private class RightMenuItemListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			/*
			 * 找到要插入元素的父节点
			 */
			JMenuItem item = (JMenuItem) e.getSource();
			
			/*
			 * 存储属性键值对  存储属性名称
			 */
			//HashMap<String,String> attributeList = new HashMap<String,String>();
			ArrayList<String> attributeNames = new ArrayList<String>();
			String itemValue=item.getText();
			/**生成新建节点的选项窗口*/
			if(itemValue.equals("instance")){
				//设置输入的选项窗口中的项目如:instance需要的name和popsize
				attributeNames.add("name");
				attributeNames.add("popsize");
				Option option = new Option(XMLGenGUI.this,"instance",attributeNames);
				generateXMLElement(option,"instance");
			}else if(itemValue.equals("message")){
			//设置输入的选项窗口中的项目
				attributeNames.add("name");
				attributeNames.add("from");
				attributeNames.add("priority");
				attributeNames.add("life");
				attributeNames.add("date");
				attributeNames.add("share");
				attributeNames.add("frequency");
				attributeNames.add("second");
				Option option = new Option(XMLGenGUI.this,"message",attributeNames);
				generateXMLElement(option,"message");
			}else if(itemValue.equals("relation")){
				generateXMLElement("relation");
			}else if(itemValue.equals("property")){
			//设置输入的选项窗口中的项目
				generateXMLElement("property");
			}else if(itemValue.equals("action")){
			//设置输入的选项窗口中的项目
				attributeNames.add("function");
				attributeNames.add("type");
				attributeNames.add("cycle");
				Option option = new Option(XMLGenGUI.this,"action",attributeNames);
				generateXMLElement(option,"action");
			}else if(itemValue.equals("relationMessage")){
				//设置输入的选项窗口中的项目
				attributeNames.add("name");
				Option option = new Option(XMLGenGUI.this,"relationMessage",attributeNames);
				generateXMLElement(option,"relationMessage");
			}else if(itemValue.equals("node")){
				//设置输入的选项窗口中的项目
				attributeNames.add("iName");
				attributeNames.add("aName");
				Option option = new Option(XMLGenGUI.this,"relationNode",attributeNames);
				generateXMLElement(option,"relationNode");
			}else if(itemValue.equals("string")){
			//设置输入的选项窗口中的项目
				attributeNames.add("name");
				attributeNames.add("value");
				Option option = new Option(XMLGenGUI.this,"string",attributeNames);
				generateXMLElement(option,"string");
			}else if(itemValue.equals("double")){
			//设置输入的选项窗口中的项目
				attributeNames.add("name");
				attributeNames.add("value");
				Option option = new Option(XMLGenGUI.this,"double",attributeNames);
				generateXMLElement(option,"double");
			}else if(itemValue.equals("int")){
			//设置输入的选项窗口中的项目
				attributeNames.add("name");
				attributeNames.add("value");
				Option option = new Option(XMLGenGUI.this,"int",attributeNames);
				generateXMLElement(option,"int");
			}else if(itemValue.equals("boolean")){
			//设置输入的选项窗口中的项目
				attributeNames.add("name");
				attributeNames.add("value");
				Option option = new Option(XMLGenGUI.this,"boolean",attributeNames);
				generateXMLElement(option,"boolean");
			}else if(itemValue.equals("date")){
			//设置输入的选项窗口中的项目
				attributeNames.add("name");
				attributeNames.add("value");
				Option option = new Option(XMLGenGUI.this,"date",attributeNames);
				generateXMLElement(option,"date");
			}else if(itemValue.equals("input")){
			//设置输入的选项窗口中的项目
				attributeNames.add("name");
				Option option = new Option(XMLGenGUI.this,"input",attributeNames);
				generateXMLElement(option,"input");
			}else if(itemValue.equals("output")){
			//设置输入的选项窗口中的项目
				attributeNames.add("name");
				Option option = new Option(XMLGenGUI.this,"output",attributeNames);
				generateXMLElement(option,"output");
			}else if(item.getText().equals("delete")){/**处理删除元素的事件*/
				deleteXMLElement();			
			}else if(item.getText().equals("update")){/**处理编辑元素的事件*/
				setTreeView();
			}
		}		
	}
	/**
	 * 对树节点的右键单击事件处理的类
	 * @author heb
	 *
	 */
	private class RightClickedListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			DefaultMutableTreeNode seletedNode=
					(DefaultMutableTreeNode) jtree.getLastSelectedPathComponent();
			if(e.isMetaDown()&&seletedNode!=null){
				RightMenuItemListener rightMenuItemListener = new RightMenuItemListener();
				JMenuItem []menuItems=new JMenuItem[8];
				/*
				 * 清除以前的菜单选项
				 */
				rightMenu.removeAll();
				String nodeValue=seletedNode.toString();
				if("system".equals(nodeValue))
				{
					/*
					 * 设置右键菜单的选项
					 */
					menuItems[0]=new JMenuItem("instance");
					menuItems[1]=new JMenuItem("message");
					menuItems[2]=new JMenuItem("relation");
					menuItems[0].addActionListener(rightMenuItemListener);
					menuItems[1].addActionListener(rightMenuItemListener);
					menuItems[2].addActionListener(rightMenuItemListener);
					/*
					 * 添加菜单项到菜单
					 */
					rightMenu.add(menuItems[0]);
					rightMenu.add(menuItems[1]);
					rightMenu.add(menuItems[2]);
				}else if("instance".equals(nodeValue)){
					/*
					 * 设置右键菜单的选项
					 */
					menuItems[0]=new JMenuItem("action");
					menuItems[1]=new JMenuItem("property");
					menuItems[0].addActionListener(rightMenuItemListener);
					menuItems[1].addActionListener(rightMenuItemListener);
					/*
					 * 添加菜单项到菜单
					 */
					rightMenu.add(menuItems[0]);
					rightMenu.add(menuItems[1]);
				}else if("action".equals(nodeValue)){
					/*
					 * 设置右键菜单的选项
					 */
					menuItems[0]=new JMenuItem("input");
					menuItems[1]=new JMenuItem("output");
					menuItems[0].addActionListener(rightMenuItemListener);
					menuItems[1].addActionListener(rightMenuItemListener);
					/*
					 * 添加菜单项到菜单
					 */
					rightMenu.add(menuItems[0]);
					rightMenu.add(menuItems[1]);
				}
				else if("relation".equals(nodeValue)){
					/*
					 * 设置右键菜单的选项
					 */
					menuItems[0]=new JMenuItem("relationMessage");
					menuItems[0].addActionListener(rightMenuItemListener);
					/*
					 * 添加菜单项到菜单
					 */
					rightMenu.add(menuItems[0]);
				}
				else if("relationMessage".equals(nodeValue)){
					/*
					 * 设置右键菜单的选项
					 */
					menuItems[0]=new JMenuItem("node");
					menuItems[0].addActionListener(rightMenuItemListener);
					/*
					 * 添加菜单项到菜单
					 */
					rightMenu.add(menuItems[0]);
				}else if("property".equals(nodeValue)){
					/*
					 * 设置右键菜单的选项
					 */
					menuItems[0]=new JMenuItem("string");
					menuItems[0].addActionListener(rightMenuItemListener);
					menuItems[1]=new JMenuItem("int");
					menuItems[1].addActionListener(rightMenuItemListener);
					menuItems[2]=new JMenuItem("double");
					menuItems[2].addActionListener(rightMenuItemListener);
					menuItems[3]=new JMenuItem("date");
					menuItems[3].addActionListener(rightMenuItemListener);
					menuItems[4]=new JMenuItem("boolean");
					menuItems[4].addActionListener(rightMenuItemListener);
					menuItems[5]=new JMenuItem("instance");
					menuItems[5].addActionListener(rightMenuItemListener);
					/*
					 * 添加菜单项到菜单
					 */
					rightMenu.add(menuItems[0]);
					rightMenu.add(menuItems[1]);
					rightMenu.add(menuItems[2]);
					rightMenu.add(menuItems[3]);
					rightMenu.add(menuItems[4]);
					rightMenu.add(menuItems[5]);
				}
				menuItems[6]=new JMenuItem("delete");
				menuItems[6].addActionListener(rightMenuItemListener);
				menuItems[7]=new JMenuItem("update");
				menuItems[7].addActionListener(rightMenuItemListener);
				rightMenu.add(menuItems[6]);
				rightMenu.add(menuItems[7]);
				if (jtree.isRowSelected(jtree.getRowForLocation(e.getX(), e.getY()))) 
					rightMenu.show(jtree, e.getX(), e.getY());
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
		}
		
	}
	/**
	 * 创建初始的XML结构
	 */
	private void createXML(){
		createXMLDocument();
		setTreeView();
		showXML();
	}
	/**
	 * 根据用户的输入生成XML节点，并插入到恰当位置的函数，
	 * @param option
	 * @param name
	 */
	public void generateXMLElement(Option option, String name) {
		DefaultMutableTreeNode selectedNode = 
				(DefaultMutableTreeNode)jtree.getLastSelectedPathComponent();
		TreeNode[] nodePath = selectedNode.getPath();
		/*
		 * 设置节点的路径XPath
		 */
		String XPath="/";
		for(int index=0;index<nodePath.length;index++){
			String nodeName=nodePath[index].toString();
			XPath+="/"+nodeName;
		}
		/*
		 *取得同一个路径下所有的节点集合中，被选择的节点的索引 
		 */
		int nodeNumber=indexOfNodeInList(selectedNode);
		//
		if(option.isSure()){
			HashMap<String, String> attributeList = option.getAttributsValue();
			/*
			 *在document中添加元素
			 */
			XMLGene.addElement(nodeNumber,XPath, name ,attributeList);
			/*
			 * 在树结构也添加对应的节点
			 */
			selectedNode.add(new DefaultMutableTreeNode(name));
			/*
			 * 展开新建的节点
			 */
			jtree.updateUI();
			jtree.expandPath(jtree.getSelectionPath());
		}
		showXML();
	}
	/**
	 * 不需要输入信息直接生成XML节点的函数
	 * @param name
	 */
	public void generateXMLElement(String name) {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jtree
				.getLastSelectedPathComponent();
		TreeNode[] nodePath = selectedNode.getPath();
		//System.out.println(jtree.getX()+","+jtree.getY());
		/*
		 * 设置节点的路径XPath
		 */
		String XPath="/";
		for(int index=0;index<nodePath.length;index++){
			String nodeName=nodePath[index].toString();
			XPath+="/"+nodeName;
		}
		HashMap<String, String> attributeList=new HashMap<String, String>();
		/*
		 *取得同一个路径下所有的节点集合中，被选择的节点的索引 
		 */
		int nodeNumber=indexOfNodeInList(selectedNode);
		/*
		 *在document中添加元素
		 */
		XMLGene.addElement(nodeNumber,XPath, name, attributeList);
		/*
		 * 在树结构也添加对应的节点
		 */
		selectedNode.add(new DefaultMutableTreeNode(name));	
		/*
		 * 展开新建的节点
		 */	
		jtree.updateUI();
		jtree.expandPath(jtree.getSelectionPath());
		showXML();

	}
	/**
	 * 删除XML节点的函数
	 */
	public void deleteXMLElement() {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jtree
				.getLastSelectedPathComponent();
		TreeNode[] nodePath = selectedNode.getPath();
		/*
		 * 设置节点的路径XPath
		 */
		String seletedNodeXPath = "/";
		for(int index=0;index<nodePath.length;index++){
			String nodeName=nodePath[index].toString();
			seletedNodeXPath+="/"+nodeName;
		}
		/*
		 *取得同一个路径下所有的节点集合中，被选择的节点的索引 
		 */
		int nodeNumber=indexOfNodeInList(selectedNode);
		/*
		 *在document中删除元素
		 */		
		Element seleted = (Element) XMLGene.getDocument().selectNodes(seletedNodeXPath)
				.get(nodeNumber);
		Element parent=seleted.getParent();
		XMLGene.deleteElement(parent,seleted);
		/*
		 * 在树结构也删除对应的节点
		 */
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
		parentNode.remove(selectedNode);
		jtree.updateUI();
		jtree.expandPath(jtree.getSelectionPath());
		showXML();

	}
	/*
	 * 查找相同路径的同名节点在数组中的索引的函数
	 */
	private int indexOfNodeInList(DefaultMutableTreeNode selectedNode){
		/*
		 * selectedNode距离根节点的深度
		 */
		int level=selectedNode.getLevel();
		int index=0;
		Enumeration<DefaultMutableTreeNode> nodeList = dmtnRoot.breadthFirstEnumeration();
		while(nodeList.hasMoreElements()){
			DefaultMutableTreeNode node = nodeList.nextElement();
			if(node.getLevel()==level&&!node.equals(selectedNode)){
				/*
				 * 两个节点路径完全一样但不是同一个节点
				 * 又因为遍历顺序是广度优先，所以这个同路径的节点在上方，索引加1
				 */
				if(node.toString().equals(selectedNode.toString())){
					//ps:当不同的父节点和可以用于同样的子节点时，需要进一步区分父节点
					index++;
				}
			}else if(node.equals(selectedNode)){
				break;
			}
		}
		return index;
	}
	/**
	 * 创建XML文档
	 */
	private void createXMLDocument(){
		XMLGene = new XMLGenerator();
		XMLGene.createDocument("system");
	}
	/**
	 * 根据XML文档生成对应节点数的函数
	 */
	private void setTreeView(){
		jtree.setRootVisible(true); 
		Element element=XMLGene.getDocument().getRootElement();
		/*
		 * 清除以前的节点，重新设置子节点
		 */
		dmtnRoot.removeAllChildren();
		
		DefaultMutableTreeNode dmtnParent =dmtnRoot;
		addNodeToTreeFromDocument(element,dmtnParent);
		jtree.updateUI();
	}
	/**
	 * 在树结构中添加节点的函数
	 * @param element
	 * @param dmtnParent
	 */
	private void addNodeToTreeFromDocument(Element element,DefaultMutableTreeNode dmtnParent){
		for(Iterator iterator = element.elementIterator();iterator.hasNext();){
			element=(Element) iterator.next();
			/*
			 * 为树结构形成新的元素节点
			 */
			DefaultMutableTreeNode dmtnLeaf=new DefaultMutableTreeNode(element.getName());
			dmtnParent.add(dmtnLeaf);
			/*
			 * 子元素递归生成树结构对应的节点
			 */
			addNodeToTreeFromDocument(element,dmtnLeaf);
		}
	}
	/**
	 * 在代码区显示XML文档的函数
	 */
	public void showXML(){
		textArea.setText(XMLGene.getXMLString());
		//textArea.setText();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new XMLGenGUI();
	}
}