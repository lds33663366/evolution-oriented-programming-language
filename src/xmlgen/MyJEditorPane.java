package xmlgen;

import javax.swing.JEditorPane;
import javax.swing.text.EditorKit;

/**
 * 重载JEditorPane用自定义的StyledEditorKit类的对象替换原来的返回值
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
