package initiator;

import java.util.concurrent.TimeUnit;

//编译器主类

public class Compiler {

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

	public static void main(String[] args) {

		// 仅测试用，10秒之后开始
//		try {
//			TimeUnit.SECONDS.sleep(15);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
		try {
			new Compiler("foodchain.xml", "mdl20140629.xsd");
//			 new Compiler("star.xml", "mdl20140629.xsd");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
