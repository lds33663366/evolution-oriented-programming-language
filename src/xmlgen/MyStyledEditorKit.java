package xmlgen;

import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

/**
 * ����StyledEditorKit���Զ����ViewFactory��Ķ����滻ԭ���ķ���ֵ
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
