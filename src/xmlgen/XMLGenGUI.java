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
	 * XMLTree�ĸ��ڵ�
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
	 * �Ҽ���ʾ�Ĳ˵�
	 */
	private JPopupMenu rightMenu=new JPopupMenu();
	private JInternalFrame leftInternalFrame;
	private JInternalFrame bottomInternalFrame;
	private static JTextArea resultArea;
	private JMenuItem resultMenuItem;
	private JMenu compileMenu;
	private JMenuItem runMenuItem;
	/*
	 * ��ʾ�ļ��Ƿ��޸ĵ�״̬����
	 */
	private boolean modefied;
	private JFileChooser filechooser;
	private Container con;
	private JScrollPane scrollPane_1;
	private JInternalFrame CodeShowInternalFrame;
	private JDesktopPane desktopPane;

	public XMLGenGUI() {
		super("δ����--***������");
		modefied=false;
		this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		getContentPane().setLayout(new BorderLayout(5, 5));
		int width = (int) (this.getWidth()*0.98);
		int height=(int) (this.getHeight()*0.90);
		con = getContentPane();
		ClickedEvent itemListener = new ClickedEvent();
		/*
		 * �˵���
		 */
		JMenuBar menuBar = new JMenuBar();
		con.add(menuBar, BorderLayout.NORTH);
		/*
		 * �ļ��˵�����˵���
		 */
		JMenu fileMenu = new JMenu("�ļ�");
		menuBar.add(fileMenu);
		
		newMenuItem = new JMenuItem("�½�");
		fileMenu.add(newMenuItem);
		newMenuItem.addActionListener(itemListener);
		
		openMenuItem = new JMenuItem("��");
		fileMenu.add(openMenuItem);
		openMenuItem.addActionListener(itemListener);
		
		saveMenuItem = new JMenuItem("����");
		fileMenu.add(saveMenuItem);
		saveMenuItem.addActionListener(itemListener);
		
		saveAsMenuItem = new JMenuItem("���Ϊ");
		fileMenu.add(saveAsMenuItem);
		saveAsMenuItem.addActionListener(itemListener);
		/*
		 * ��ͼ�˵�����˵���
		 */
		JMenu viewMenu = new JMenu("��ͼ");
		menuBar.add(viewMenu);
		
		showTreeMenuItem = new JMenuItem("��ʾXML Tree");
		viewMenu.add(showTreeMenuItem);
		
		resultMenuItem = new JMenuItem("��ʾResult");
		viewMenu.add(resultMenuItem);
		
		compileMenu = new JMenu("����");
		menuBar.add(compileMenu);
		
		runMenuItem = new JMenuItem("����");
		compileMenu.add(runMenuItem);
		/**
		 * �˵����¼�ע��
		 */
		showTreeMenuItem.addActionListener(itemListener);
		resultMenuItem.addActionListener(itemListener);
		runMenuItem.addActionListener(itemListener);
		/*������ڹ���pane*/
		desktopPane = new JDesktopPane();
		getContentPane().add(desktopPane, BorderLayout.CENTER);
		/*
		 * ������ʾ�ı���
		 */
		CodeShowInternalFrame = new JInternalFrame("Code",true,true,true,false);
		CodeShowInternalFrame.setBounds((int) (width*0.2),0,(int) (width*0.8),(int) (height*0.7));
		desktopPane.add(CodeShowInternalFrame);
		CodeShowInternalFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		CodeShowInternalFrame.setBorder(UIManager.getBorder("FileChooser.listViewBorder"));
		//����JEditorPane�������������ʾ����
		textArea = new MyJEditorPane();
		JScrollPane scrollPane = new JScrollPane(textArea);
		//CodeShowInternalFrame.getContentPane().add(scrollPane, BorderLayout.NORTH);
		
		CodeShowInternalFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		textArea.setContentType("text/xml");
		/*
		 * �״��ڵ�����
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
		 * ��ര�ڵ�����
		 */
		leftInternalFrame = new JInternalFrame("XML Tree",true,true,true,false);
		leftInternalFrame.setBounds(0, 0, (int)(0.2*width), (int)(0.7*height));
		desktopPane.add(leftInternalFrame);
		leftInternalFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		leftInternalFrame.setBorder(UIManager.getBorder("FileChooser.listViewBorder"));
		leftInternalFrame.setVisible(true);
		/*
		 * Ԫ�ؽڵ�����νṹ��ʾ
		 */
		jtree = new JTree(dmtnRoot);  
		// ���ø��ڵ��Ƿ���ʾ  
		jtree.setRootVisible(false);  
		jtree.putClientProperty("JTree.lineStyle", "None");// �����  
		jtree.setShowsRootHandles(true);
		// ����ͼ��  
		jtree.setCellRenderer(new MyRenderer());
		
		JScrollPane jscrollPane = new JScrollPane(jtree);
		leftInternalFrame.getContentPane().add(jscrollPane);
		jtree.addMouseListener(new RightClickedListener());
		
		//�ض���
		System.setOut(new PrintStream(System.out){
			public void println(String x) {
		        resultArea.append(x + "\n");
		      }
		});
		
		textArea.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				modefied=true;
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				modefied=true;
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				modefied=true;
			}});
		CodeShowInternalFrame.setVisible(true);
		/**
		 * �����ĵ��Ƿ񱻸���
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
	 * ����׷�Ӻ���
	 * @param str
	 */
	/*public void appendResultAreaText(String str) {
		resultArea.append(str);
	}*/
	/**�˵����ʵ�¼�������*/
	private class ClickedEvent implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == saveAsMenuItem) {
				saveAs();
			} else if (e.getSource() == saveMenuItem) {
				save();
			} else if (e.getSource() == newMenuItem) {
				/*
				 * �ر�ԭ���ļ�
				 */
				closeFile();
				newFile();
				createXML();
			} else if (e.getSource() == openMenuItem) {
				/*
				 * �ر�ԭ���ļ�
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
				 * ��ʼ �������� 
				 */
				try {
					new Compiler("foodchain_20140420.xml", "mdl20140420.xsd");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	/**
	 * �رմ򿪻��½����ļ��ĺ���
	 */
	
	private void closeFile(){
		//System.out.println(modefied);
		if(modefied){
			 int option = JOptionPane.showConfirmDialog(
					 null, "�ļ����޸ģ��Ƿ񱣴棿",
					 "�����ļ���", JOptionPane.YES_NO_OPTION, 
					 JOptionPane.WARNING_MESSAGE, null);
			 switch(option) { 
			 	// ȷ���ļ�����
			 case JOptionPane.YES_OPTION:
				 save(); // �����ļ�
				 break;
				 // �����ļ�����
			 case JOptionPane.NO_OPTION:
				 break;
			 }
		}
		textArea.setText("");
		modefied=false;
	}
	/**
	 * �½��ļ��ĺ���
	 */
	private void newFile(){
		this.setTitle("δ����--***������");
	}
	/**
	 * ���ļ����ڴ�������ʾ�ĺ���
	 */
	private void open(){
		/*
		 * ��ʾѡ���ļ��Ĵ���
		 */
		int n = filechooser.showOpenDialog(con);
		/*
		 * ���ȷ������ʾ�ļ�����
		 */
		if (n == JFileChooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();
			this.setTitle(file.getPath()+"--***������");
			textArea.setText(null);
			try {
				FileInputStream fis;
				fis = new FileInputStream(file);
				/*�����ļ�����������*/
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
					// TODO Auto-generated catch block
					e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	/**
	 * ��������������ݵ��ļ��ĺ���
	 */
	private void save(){
		String title=this.getTitle();
		/*
		 * �ļ�δ�����
		 */
		if("δ����".equals(title.substring(0,3))){
			saveAs();
		}else{
			/*
			 * �ļ������
			 */
			String filename=title.substring(0,title.lastIndexOf("--"));
			try {
				FileOutputStream fos;
				fos = new FileOutputStream(filename);
				/*�����ļ�������*/
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8"); 
				BufferedWriter out = new BufferedWriter(osw);

				out.write(textArea.getText());
				out.close();
				fos.close();
				osw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			modefied=false;
		}
	}
	/**
	 * ���Ϊ�����������ݵ��ļ��ĺ���
	 */
	private void saveAs(){
		/*
		 * ��ʾ�����ļ��Ĵ���
		 */
		int n = filechooser.showSaveDialog(con);
		/*
		 * ���ȷ������ʾ�ļ�����
		 */
		if (n == JFileChooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();
			this.setTitle(file.getPath()+"--***������");
			try {
				FileOutputStream fos;
				fos = new FileOutputStream(file);
				/*�����ļ�������*/
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8"); 
				BufferedWriter out = new BufferedWriter(osw);

				out.write(textArea.getText());
				out.close();
				fos.close();
				osw.close();
				modefied=false;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
		}
	}
	/**
	 * �Ҽ��˵�����¼�������
	 * @author heb
	 *
	 */
	private class RightMenuItemListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			/*
			 * �ҵ�Ҫ����Ԫ�صĸ��ڵ�
			 */
			JMenuItem item = (JMenuItem) e.getSource();
			
			/*
			 * �洢���Լ�ֵ��  �洢��������
			 */
			//HashMap<String,String> attributeList = new HashMap<String,String>();
			ArrayList<String> attributeNames = new ArrayList<String>();
			String itemValue=item.getText();
			/**�����½��ڵ��ѡ���*/
			if(itemValue.equals("instance")){
				//���������ѡ����е���Ŀ��:instance��Ҫ��name��popsize
				attributeNames.add("name");
				attributeNames.add("popsize");
				Option option = new Option(XMLGenGUI.this,"instance",attributeNames);
				generateXMLElement(option,"instance");
			}else if(itemValue.equals("message")){
			//���������ѡ����е���Ŀ
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
			//���������ѡ����е���Ŀ
				generateXMLElement("property");
			}else if(itemValue.equals("action")){
			//���������ѡ����е���Ŀ
				attributeNames.add("function");
				attributeNames.add("type");
				attributeNames.add("cycle");
				Option option = new Option(XMLGenGUI.this,"action",attributeNames);
				generateXMLElement(option,"action");
			}else if(itemValue.equals("relationMessage")){
				//���������ѡ����е���Ŀ
				attributeNames.add("name");
				Option option = new Option(XMLGenGUI.this,"relationMessage",attributeNames);
				generateXMLElement(option,"relationMessage");
			}else if(itemValue.equals("node")){
				//���������ѡ����е���Ŀ
				attributeNames.add("iName");
				attributeNames.add("aName");
				Option option = new Option(XMLGenGUI.this,"relationNode",attributeNames);
				generateXMLElement(option,"relationNode");
			}else if(itemValue.equals("string")){
			//���������ѡ����е���Ŀ
				attributeNames.add("name");
				attributeNames.add("value");
				Option option = new Option(XMLGenGUI.this,"string",attributeNames);
				generateXMLElement(option,"string");
			}else if(itemValue.equals("double")){
			//���������ѡ����е���Ŀ
				attributeNames.add("name");
				attributeNames.add("value");
				Option option = new Option(XMLGenGUI.this,"double",attributeNames);
				generateXMLElement(option,"double");
			}else if(itemValue.equals("int")){
			//���������ѡ����е���Ŀ
				attributeNames.add("name");
				attributeNames.add("value");
				Option option = new Option(XMLGenGUI.this,"int",attributeNames);
				generateXMLElement(option,"int");
			}else if(itemValue.equals("boolean")){
			//���������ѡ����е���Ŀ
				attributeNames.add("name");
				attributeNames.add("value");
				Option option = new Option(XMLGenGUI.this,"boolean",attributeNames);
				generateXMLElement(option,"boolean");
			}else if(itemValue.equals("date")){
			//���������ѡ����е���Ŀ
				attributeNames.add("name");
				attributeNames.add("value");
				Option option = new Option(XMLGenGUI.this,"date",attributeNames);
				generateXMLElement(option,"date");
			}else if(itemValue.equals("input")){
			//���������ѡ����е���Ŀ
				attributeNames.add("name");
				Option option = new Option(XMLGenGUI.this,"input",attributeNames);
				generateXMLElement(option,"input");
			}else if(itemValue.equals("output")){
			//���������ѡ����е���Ŀ
				attributeNames.add("name");
				Option option = new Option(XMLGenGUI.this,"output",attributeNames);
				generateXMLElement(option,"output");
			}else if(item.getText().equals("delete")){/**����ɾ��Ԫ�ص��¼�*/
				deleteXMLElement();			
			}else if(item.getText().equals("update")){/**����༭Ԫ�ص��¼�*/
				setTreeView();
			}
		}		
	}
	/**
	 * �����ڵ���Ҽ������¼��������
	 * @author heb
	 *
	 */
	private class RightClickedListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			DefaultMutableTreeNode seletedNode=
					(DefaultMutableTreeNode) jtree.getLastSelectedPathComponent();
			if(e.isMetaDown()&&seletedNode!=null){
				RightMenuItemListener rightMenuItemListener = new RightMenuItemListener();
				JMenuItem []menuItems=new JMenuItem[8];
				/*
				 * �����ǰ�Ĳ˵�ѡ��
				 */
				rightMenu.removeAll();
				String nodeValue=seletedNode.toString();
				if("system".equals(nodeValue))
				{
					/*
					 * �����Ҽ��˵���ѡ��
					 */
					menuItems[0]=new JMenuItem("instance");
					menuItems[1]=new JMenuItem("message");
					menuItems[2]=new JMenuItem("relation");
					menuItems[0].addActionListener(rightMenuItemListener);
					menuItems[1].addActionListener(rightMenuItemListener);
					menuItems[2].addActionListener(rightMenuItemListener);
					/*
					 * ��Ӳ˵���˵�
					 */
					rightMenu.add(menuItems[0]);
					rightMenu.add(menuItems[1]);
					rightMenu.add(menuItems[2]);
				}else if("instance".equals(nodeValue)){
					/*
					 * �����Ҽ��˵���ѡ��
					 */
					menuItems[0]=new JMenuItem("action");
					menuItems[1]=new JMenuItem("property");
					menuItems[0].addActionListener(rightMenuItemListener);
					menuItems[1].addActionListener(rightMenuItemListener);
					/*
					 * ��Ӳ˵���˵�
					 */
					rightMenu.add(menuItems[0]);
					rightMenu.add(menuItems[1]);
				}else if("action".equals(nodeValue)){
					/*
					 * �����Ҽ��˵���ѡ��
					 */
					menuItems[0]=new JMenuItem("input");
					menuItems[1]=new JMenuItem("output");
					menuItems[0].addActionListener(rightMenuItemListener);
					menuItems[1].addActionListener(rightMenuItemListener);
					/*
					 * ��Ӳ˵���˵�
					 */
					rightMenu.add(menuItems[0]);
					rightMenu.add(menuItems[1]);
				}
				else if("relation".equals(nodeValue)){
					/*
					 * �����Ҽ��˵���ѡ��
					 */
					menuItems[0]=new JMenuItem("relationMessage");
					menuItems[0].addActionListener(rightMenuItemListener);
					/*
					 * ��Ӳ˵���˵�
					 */
					rightMenu.add(menuItems[0]);
				}
				else if("relationMessage".equals(nodeValue)){
					/*
					 * �����Ҽ��˵���ѡ��
					 */
					menuItems[0]=new JMenuItem("node");
					menuItems[0].addActionListener(rightMenuItemListener);
					/*
					 * ��Ӳ˵���˵�
					 */
					rightMenu.add(menuItems[0]);
				}else if("property".equals(nodeValue)){
					/*
					 * �����Ҽ��˵���ѡ��
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
					 * ��Ӳ˵���˵�
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
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	/**
	 * ������ʼ��XML�ṹ
	 */
	private void createXML(){
		createXMLDocument();
		setTreeView();
		showXML();
	}
	/**
	 * �����û�����������XML�ڵ㣬�����뵽ǡ��λ�õĺ�����
	 * @param option
	 * @param name
	 */
	public void generateXMLElement(Option option, String name) {
		// TODO Auto-generated method stub
		DefaultMutableTreeNode selectedNode = 
				(DefaultMutableTreeNode)jtree.getLastSelectedPathComponent();
		TreeNode[] nodePath = selectedNode.getPath();
		/*
		 * ���ýڵ��·��XPath
		 */
		String XPath="/";
		for(int index=0;index<nodePath.length;index++){
			String nodeName=nodePath[index].toString();
			XPath+="/"+nodeName;
		}
		/*
		 *ȡ��ͬһ��·�������еĽڵ㼯���У���ѡ��Ľڵ������ 
		 */
		int nodeNumber=indexOfNodeInList(selectedNode);
		//
		if(option.isSure()){
			HashMap<String, String> attributeList = option.getAttributsValue();
			/*
			 *��document�����Ԫ��
			 */
			XMLGene.addElement(nodeNumber,XPath, name ,attributeList);
			/*
			 * �����ṹҲ��Ӷ�Ӧ�Ľڵ�
			 */
			selectedNode.add(new DefaultMutableTreeNode(name));
			/*
			 * չ���½��Ľڵ�
			 */
			jtree.updateUI();
			jtree.expandPath(jtree.getSelectionPath());
		}
		showXML();
	}
	/**
	 * ����Ҫ������Ϣֱ������XML�ڵ�ĺ���
	 * @param name
	 */
	public void generateXMLElement(String name) {
		// TODO Auto-generated method stub
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jtree
				.getLastSelectedPathComponent();
		TreeNode[] nodePath = selectedNode.getPath();
		//System.out.println(jtree.getX()+","+jtree.getY());
		/*
		 * ���ýڵ��·��XPath
		 */
		String XPath="/";
		for(int index=0;index<nodePath.length;index++){
			String nodeName=nodePath[index].toString();
			XPath+="/"+nodeName;
		}
		HashMap<String, String> attributeList=new HashMap<String, String>();
		/*
		 *ȡ��ͬһ��·�������еĽڵ㼯���У���ѡ��Ľڵ������ 
		 */
		int nodeNumber=indexOfNodeInList(selectedNode);
		/*
		 *��document�����Ԫ��
		 */
		XMLGene.addElement(nodeNumber,XPath, name, attributeList);
		/*
		 * �����ṹҲ��Ӷ�Ӧ�Ľڵ�
		 */
		selectedNode.add(new DefaultMutableTreeNode(name));	
		/*
		 * չ���½��Ľڵ�
		 */	
		jtree.updateUI();
		jtree.expandPath(jtree.getSelectionPath());
		showXML();

	}
	/**
	 * ɾ��XML�ڵ�ĺ���
	 */
	public void deleteXMLElement() {
		// TODO Auto-generated method stub
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jtree
				.getLastSelectedPathComponent();
		TreeNode[] nodePath = selectedNode.getPath();
		/*
		 * ���ýڵ��·��XPath
		 */
		String seletedNodeXPath = "/";
		for(int index=0;index<nodePath.length;index++){
			String nodeName=nodePath[index].toString();
			seletedNodeXPath+="/"+nodeName;
		}
		/*
		 *ȡ��ͬһ��·�������еĽڵ㼯���У���ѡ��Ľڵ������ 
		 */
		int nodeNumber=indexOfNodeInList(selectedNode);
		/*
		 *��document��ɾ��Ԫ��
		 */		
		Element seleted = (Element) XMLGene.getDocument().selectNodes(seletedNodeXPath)
				.get(nodeNumber);
		Element parent=seleted.getParent();
		XMLGene.deleteElement(parent,seleted);
		/*
		 * �����ṹҲɾ����Ӧ�Ľڵ�
		 */
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
		parentNode.remove(selectedNode);
		jtree.updateUI();
		jtree.expandPath(jtree.getSelectionPath());
		showXML();

	}
	/*
	 * ������ͬ·����ͬ���ڵ��������е������ĺ���
	 */
	private int indexOfNodeInList(DefaultMutableTreeNode selectedNode){
		/*
		 * selectedNode������ڵ�����
		 */
		int level=selectedNode.getLevel();
		int index=0;
		Enumeration<DefaultMutableTreeNode> nodeList = dmtnRoot.breadthFirstEnumeration();
		while(nodeList.hasMoreElements()){
			DefaultMutableTreeNode node = nodeList.nextElement();
			if(node.getLevel()==level&&!node.equals(selectedNode)){
				/*
				 * �����ڵ�·����ȫһ��������ͬһ���ڵ�
				 * ����Ϊ����˳���ǹ�����ȣ��������ͬ·���Ľڵ����Ϸ���������1
				 */
				if(node.toString().equals(selectedNode.toString())){
					//ps:����ͬ�ĸ��ڵ�Ϳ�������ͬ�����ӽڵ�ʱ����Ҫ��һ�����ָ��ڵ�
					index++;
				}
			}else if(node.equals(selectedNode)){
				break;
			}
		}
		return index;
	}
	/**
	 * ����XML�ĵ�
	 */
	private void createXMLDocument(){
		XMLGene = new XMLGenerator();
		XMLGene.createDocument("system");
	}
	/**
	 * ����XML�ĵ����ɶ�Ӧ�ڵ����ĺ���
	 */
	private void setTreeView(){
		jtree.setRootVisible(true); 
		Element element=XMLGene.getDocument().getRootElement();
		/*
		 * �����ǰ�Ľڵ㣬���������ӽڵ�
		 */
		dmtnRoot.removeAllChildren();
		
		DefaultMutableTreeNode dmtnParent =dmtnRoot;
		addNodeToTreeFromDocument(element,dmtnParent);
		jtree.updateUI();
	}
	/**
	 * �����ṹ����ӽڵ�ĺ���
	 * @param element
	 * @param dmtnParent
	 */
	private void addNodeToTreeFromDocument(Element element,DefaultMutableTreeNode dmtnParent){
		for(Iterator iterator = element.elementIterator();iterator.hasNext();){
			element=(Element) iterator.next();
			/*
			 * Ϊ���ṹ�γ��µ�Ԫ�ؽڵ�
			 */
			DefaultMutableTreeNode dmtnLeaf=new DefaultMutableTreeNode(element.getName());
			dmtnParent.add(dmtnLeaf);
			/*
			 * ��Ԫ�صݹ��������ṹ��Ӧ�Ľڵ�
			 */
			addNodeToTreeFromDocument(element,dmtnLeaf);
		}
	}
	/**
	 * �ڴ�������ʾXML�ĵ��ĺ���
	 */
	public void showXML(){
		textArea.setText(XMLGene.getXMLString());
		//textArea.setText();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new XMLGenGUI();
	}
}