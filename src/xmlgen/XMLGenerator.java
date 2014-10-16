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
	 * 创建一个新document,system是默认的根元素
	 * @return document
	 */
	public XMLGenerator(){	
		document = DocumentHelper.createDocument();
	}
	public void createDocument(String rootname) {
        /*
		 * 生成声明和根元素
		 */
        //document.addDocType(rootname, "SYSTEM", "mdl20140420.xsd");
        document.addElement( rootname );
    }
	public Document getDocument(){
		return document;
	}
	/**在document中删除元素*/
	public void deleteElement(Element parent,
			Element seleted){
		parent.remove(seleted);
	}
	/**给document中添加元素*/
	public void addElement(
			int number,
			String path,
			String elementName,
			HashMap<String,String> attributeList) {
	
		/*
		 * 寻找要插入元素的父节点
		 */
		Element element;
		element=(Element) document.selectNodes(path).get(number);
			/*
			 * 插入新的元素
			 */
			Element newElement=element.addElement(elementName);
			/*
			 * 为新元素设置属性
			 */
			Iterator iterator = attributeList.entrySet().iterator();
			while(iterator.hasNext()) {
				Entry attribute = (Entry<String, String>)iterator.next();
				newElement.addAttribute((String)attribute.getKey(), (String)attribute.getValue());
			}
	}
	/**用字符串string表示节点属性时添加节点的方法*/
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
	/**没有属性时添加节点的方法*/
	public void addElement(int number, String path, String elementName) {
		HashMap<String,String> attributeList=new HashMap<String, String>();
		addElement(number, path, elementName, attributeList);
	}
	public String getXMLString() {
		Document doc = null;
		String s = null;
		try {
			doc = DocumentHelper.parseText(document.asXML());
			// 输出格式化器
			//OutputFormat format =OutputFormat.createPrettyPrint();
			OutputFormat format =new OutputFormat("        ",true);
			// 设置编码
			format.setEncoding("utf-8");
			// xml输出器
			StringWriter outer = new StringWriter();
			XMLWriter xmlWriter = new XMLWriter(outer, format);
			// 打印doc
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
