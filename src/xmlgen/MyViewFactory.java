package xmlgen;

import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * ����ViewFactory���Զ����EditorView��Ķ����滻ԭ���ķ���ֵ(non-Javadoc)
 * @author heb
 *
 */
public class MyViewFactory implements ViewFactory {

	
	public View create(Element elem) {
		return new EditorView(elem);
	}

}
