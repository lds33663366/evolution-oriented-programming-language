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
 * ʵ������Ϊ��ѡ��Ĵ���
 * ���������Ҫ���չ���Ҫ�������ӽڵ��������Ϣ
 * @author heb
 *
 */
public class Option extends JDialog {
	private JButton sure;
	private JButton cancel;
	private String name;
	/*
	 * �жϴ�����ȷ���˳�����ȡ���˳��ı�־
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
		 * ���Ҫ��ʾ���������ƺͶ�Ӧ��������������Ա�ǩ�Ĳ���
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
			//��ʾ��ǩ
			JLabel attributeLable = new JLabel(attribute);
			attributesPanel.add(attributeLable);
			//��ʾ�����
			JTextField attributeTextFiled  = new JTextField(10);
			SetDefaultValue(attribute,attributeTextFiled);
			attributeValues.add(attributeTextFiled);
			attributesPanel.add(attributeTextFiled);
		}
		gridLayOut.setRows(rows);
		
		//ȷ��ȡ����ť�������������¼���ע��
		sure = new JButton("ȷ��");		
		cancel = new JButton("ȡ��");		
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
	 * ������ attribute ����Ĭ�ϵ�ֵ
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
	 * �жϴ����ǵ��ȷ����ť֮��رյ�
	 * @return
	 */
	public boolean isSure() {
		// TODO Auto-generated method stub
		return sign;
	}
	/**
	 * �������������������Ϣ
	 * @author heb
	 *
	 */
	private class ButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// �����е�������Ϣ���浽ָ������
			if(e.getSource()==sure){
				sign=true;
				attributesValue.clear();
				for(int index=0;index<attributeValues.size();index++){
					if(IsRequired(attributes.get(index))&&
							attributeValues.get(index).getText().trim().equals("")){
						JOptionPane.showMessageDialog(Option.this, attributes.get(index)+"����Ϊ�գ�");
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
	 * ���ذ������Ե�ֵ�ü���
	 * @return
	 */
	public HashMap<String, String> getAttributsValue() {
		// TODO Auto-generated method stub
		return attributesValue;
	}
	/**
	 * �ж����Ա���
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
