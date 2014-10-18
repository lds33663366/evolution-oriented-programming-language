package xmlgen;

import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * 重载ViewFactory用自定义的EditorView类的对象替换原来的返回值(non-Javadoc)
 * @author heb
 *
 */
public class MyViewFactory implements ViewFactory {

	
	public View create(Element elem) {
		return new EditorView(elem);
	}

}
