package xmlgen;

import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

/**
 * 重载StyledEditorKit用自定义的ViewFactory类的对象替换原来的返回值
 * @author heb
 *
 */
public class MyStyledEditorKit extends StyledEditorKit {

	@Override
	public Document createDefaultDocument() {
		return super.createDefaultDocument();
	}

	@Override
	public ViewFactory getViewFactory() {
		return new MyViewFactory();
	}

}
