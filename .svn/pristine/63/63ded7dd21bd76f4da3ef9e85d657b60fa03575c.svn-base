package initiator;
import java.io.File;
import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

//根据schema对XML进行有效性验证

public class XMLValidator {
	public XMLValidator(String xmlFile, String xsdFile) throws IOException,
			SAXException {
		SchemaFactory factory = SchemaFactory
				.newInstance("http://www.w3.org/2001/XMLSchema");

		// 编译schema
		Schema schema = factory.newSchema(new File(xsdFile));

		// 从schema得到一个验证器
		Validator validator = schema.newValidator();

		Source source = new StreamSource(xmlFile);

		// 检查XML
		validator.validate(source);
	}
}
