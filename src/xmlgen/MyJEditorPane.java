package xmlgen;

import javax.swing.JEditorPane;
import javax.swing.text.EditorKit;

/**
 * ����JEditorPane���Զ����StyledEditorKit��Ķ����滻ԭ���ķ���ֵ
 * @author heb
 *
 */
public class MyJEditorPane extends JEditorPane {

	@Override
	protected EditorKit createDefaultEditorKit() {
		// TODO Auto-generated method stub
		return new MyStyledEditorKit();
	}

}
