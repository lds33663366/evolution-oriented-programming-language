package initiator;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;

import log.MyLogger;

//编译器主类

public class Compiler {
	
	public static final String XML_PATH = "xml/";
	public static final String SCHEMA_NAME = "mdl20140629.xsd";
//	public static final String XML_NAME = "foodchain.xml";
	public static final String XML_NAME = "star.xml";
	
	public Compiler(String xmlFile, String xsdFile) throws Exception {

		// 先对XML进行有效性验证
		new XMLValidator(xmlFile, xsdFile);

		// System.out.println("XML验证通过");
		// 解析XML创建Java对象
		Parser parser = new Parser(xmlFile);
		XMLSystem system = parser.parseSystem();

		// 启动程序
		system.start();

		// system.test();
	}
	
	private static void setLogPath() {
		String logDir = "logs/";
		
		String name = XML_NAME.split("\\.")[0];
		
		// 初始化logger后，通过获取logger的appender，来修改appender写入文件的文件名
		Appender appender = Logger.getRootLogger().getAppender("myFile");
		if (appender instanceof RollingFileAppender) {
			RollingFileAppender fappender = (RollingFileAppender) appender;
			String logs = logDir + name + "6.log";
			fappender.setFile(logs);
			fappender.activateOptions();
			
		}
	}

	public static void main(String[] args) {

		// 仅测试用，10秒之后开始
//		try {
//			TimeUnit.SECONDS.sleep(15);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}

		setLogPath();
		
		try {
			new Compiler(XML_PATH + XML_NAME, XML_PATH + SCHEMA_NAME);
//			 new Compiler("star.xml", "mdl20140629.xsd");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
