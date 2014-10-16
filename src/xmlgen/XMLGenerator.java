package xmlgen;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class XMLGenerator {
	private Document document=null;
	/**
	 * ����һ����document,system��Ĭ�ϵĸ�Ԫ��
	 * @return document
	 */
	public XMLGenerator(){	
		document = DocumentHelper.createDocument();
	}
	public void createDocument(String rootname) {
        /*
		 * ���������͸�Ԫ��
		 */
        //document.addDocType(rootname, "SYSTEM", "mdl20140420.xsd");
        document.addElement( rootname );
    }
	public Document getDocument(){
		return document;
	}
	/**��document��ɾ��Ԫ��*/
	public void deleteElement(Element parent,
			Element seleted){
		parent.remove(seleted);
	}
	/**��document�����Ԫ��*/
	public void addElement(
			int number,
			String path,
			String elementName,
			HashMap<String,String> attributeList) {
	
		/*
		 * Ѱ��Ҫ����Ԫ�صĸ��ڵ�
		 */
		Element element;
		element=(Element) document.selectNodes(path).get(number);
			/*
			 * �����µ�Ԫ��
			 */
			Element newElement=element.addElement(elementName);
			/*
			 * Ϊ��Ԫ����������
			 */
			Iterator iterator = attributeList.entrySet().iterator();
			while(iterator.hasNext()) {
				Entry attribute = (Entry<String, String>)iterator.next();
				newElement.addAttribute((String)attribute.getKey(), (String)attribute.getValue());
			}
	}
	/**���ַ���string��ʾ�ڵ�����ʱ��ӽڵ�ķ���*/
	public void addElement(int number, String path, String elementName,
			String string) {
		HashMap<String,String> attributeList=new HashMap<String, String>();
		StringTokenizer tokenList=new StringTokenizer(string,",");
		while(tokenList.hasMoreTokens()){
			StringTokenizer attribute=new StringTokenizer(tokenList.nextToken()+" ", ":");
			attributeList.put(attribute.nextToken(), attribute.nextToken().trim());
		}
		addElement(number, path, elementName, attributeList);
	}
	/**û������ʱ��ӽڵ�ķ���*/
	public void addElement(int number, String path, String elementName) {
		HashMap<String,String> attributeList=new HashMap<String, String>();
		addElement(number, path, elementName, attributeList);
	}
	public String getXMLString() {
		Document doc = null;
		String s = null;
		try {
			doc = DocumentHelper.parseText(document.asXML());
			// �����ʽ����
			//OutputFormat format =OutputFormat.createPrettyPrint();
			OutputFormat format =new OutputFormat("        ",true);
			// ���ñ���
			format.setEncoding("utf-8");
			// xml�����
			StringWriter outer = new StringWriter();
			XMLWriter xmlWriter = new XMLWriter(outer, format);
			// ��ӡdoc
			xmlWriter.write(doc);
			xmlWriter.flush();
			s = outer.toString();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return s;
	}
}
