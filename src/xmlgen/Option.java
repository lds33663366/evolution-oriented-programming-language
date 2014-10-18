package xmlgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * 实现名称为“选项”的窗口
 * 这个窗口主要接收关于要创建的子节点的属性信息
 * @author heb
 *
 */
public class Option extends JDialog {
	private JButton sure;
	private JButton cancel;
	private String name;
	/*
	 * 判断窗口是确认退出还是取消退出的标志
	 */
	private boolean sign=false;
	
	private HashMap<String,String> attributesValue=new HashMap<String, String>();
	private ArrayList<JTextField> attributeValues=new ArrayList<JTextField>();
	private ArrayList<String> attributes=new ArrayList<String>();
	
	public Option(JFrame frame,String name,ArrayList<String> attributes){
		super(frame,name,true);
		this.name=name;
		setLocation(frame.getWidth()/2-100,frame.getHeight()/2-200);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		/*
		 * 添加要显示的属性名称和对应的输入框并设置属性标签的布局
		 */
		JPanel attributesPanel=new JPanel();
		GridLayout gridLayOut = new GridLayout();
		attributesPanel.setLayout(gridLayOut);
		gridLayOut.setColumns(2);
		getContentPane().add(attributesPanel, BorderLayout.CENTER);	
		int rows=0;
		for(;rows<attributes.size();rows++) {
			String attribute = attributes.get(rows);
			this.attributes.add(attributes.get(rows));
			//显示标签
			JLabel attributeLable = new JLabel(attribute);
			attributesPanel.add(attributeLable);
			//显示输入框
			JTextField attributeTextFiled  = new JTextField(10);
			SetDefaultValue(attribute,attributeTextFiled);
			attributeValues.add(attributeTextFiled);
			attributesPanel.add(attributeTextFiled);
		}
		gridLayOut.setRows(rows);
		
		//确定取消按钮的添加与其监听事件的注册
		sure = new JButton("确认");		
		cancel = new JButton("取消");		
		JPanel panel=new JPanel();
		panel.add(sure);
		panel.add(cancel);
		
		ButtonListener buttonListener = new ButtonListener();
		sure.addActionListener(buttonListener);
		cancel.addActionListener(buttonListener);
		getContentPane().add(panel, BorderLayout.SOUTH);
		//setSize(getPreferredSize().width,getPreferredSize().height+100);
		pack();
		setVisible(true);	
	}
	/**
	 * 给属性 attribute 设置默认的值
	 * @param attribute
	 * @param attributeTextFiled
	 */
	private void SetDefaultValue(String attribute, JTextField attributeTextFiled) {
		if(name.equals("action")&&attribute.equals("type")){
			attributeTextFiled.setText("self");
		}
		if(name.equals("instance")&&attribute.equals("popsize"))
		{
			attributeTextFiled.setText("1");
		}
		if(name.equals("message")&&attribute.equals("priority"))
		{
			attributeTextFiled.setText("5");
		}
		if(name.equals("message")&&attribute.equals("share"))
		{
			attributeTextFiled.setText("1");
		}if(name.equals("message")&&attribute.equals("frequency"))
		{
			attributeTextFiled.setText("1");
		}
		if(name.equals("message")&&attribute.equals("second"))
		{
			attributeTextFiled.setText("0");
		}
		if(name.equals("string")&&attribute.equals("name"))
		{
			attributeTextFiled.setText("string");
		}
		if(name.equals("double")&&attribute.equals("name"))
		{
			attributeTextFiled.setText("double");
		}
		if(name.equals("int")&&attribute.equals("name"))
		{
			attributeTextFiled.setText("int");
		}
		if(name.equals("boolean")&&attribute.equals("name"))
		{
			attributeTextFiled.setText("boolean");
		}
		if(name.equals("date")&&attribute.equals("name"))
		{
			attributeTextFiled.setText("date");
		}
	}
	/**
	 * 判断窗口是点击确定按钮之后关闭的
	 * @return
	 */
	public boolean isSure() {
		// TODO Auto-generated method stub
		return sign;
	}
	/**
	 * 处理窗口中输入的属性信息
	 * @author heb
	 *
	 */
	private class ButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// 把所有的属性信息保存到指定集合
			if(e.getSource()==sure){
				sign=true;
				attributesValue.clear();
				for(int index=0;index<attributeValues.size();index++){
					if(IsRequired(attributes.get(index))&&
							attributeValues.get(index).getText().trim().equals("")){
						JOptionPane.showMessageDialog(Option.this, attributes.get(index)+"不能为空！");
						sign=false;
						break;
					}
					attributesValue.put(attributes.get(index),
							attributeValues.get(index).getText());
				}
				dispose();
				if(!sign)
				{
					setVisible(true);
				}
			}else if(e.getSource()==cancel){
				dispose();
			}
		}
		
	}
	/**
	 * 返回包含属性的值得集合
	 * @return
	 */
	public HashMap<String, String> getAttributsValue() {
		// TODO Auto-generated method stub
		return attributesValue;
	}
	/**
	 * 判断属性必填
	 * @param str
	 * @return
	 */
	private boolean IsRequired(String str)
	{
		if((name.equals("string")||name.equals("double")||name.equals("int")||
				name.equals("Boolean")||name.equals("date"))&&str.equals("name")){
			return true;
		}
		if(name.equals("action")&&str.equals("function"))
		{
			return true;
		}
		if(name.equals("instance")&&str.equals("name"))
		{
			return true;
		}
		if(name.equals("message")&&(str.equals("name")||str.equals("from")))
		{
			return true;
		}
		if(name.equals("relationNode")&&(str.equals("iName")||str.equals("aName")))
		{
			return true;
		}
		if(name.equals("relationMessage")&&str.equals("name"))
		{
			return true;
		}
		return false;
	}
}
