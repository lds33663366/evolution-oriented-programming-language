package xmlgen;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;

public class EditorView extends PlainView {

	public EditorView(Element elem) {
		super(elem);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected int drawUnselectedText(Graphics g, int x, int y, int startOffset,
			int endOffset) throws BadLocationException {
		// TODO Auto-generated method stub
		int doclength = getDocument().getLength();
		int length = (endOffset < doclength ? endOffset : doclength)
				- startOffset;
		return scanParagraph(g, x, y, startOffset, length);
	}

	private int scanParagraph(Graphics g, int x, int y, int startOffset,
			int length) throws BadLocationException {
		// TODO Auto-generated method stub
		Color defaultColor = new Color(0, 0, 0);
		Color tagColor = new Color(0, 0, 255);
		Color attributeColor = new Color(255, 0, 0);
		Color stringColor = new Color(160, 32, 240);
		int ret = x;
		int kind = 0;							//字符种类
		int bracketNumber = 0;                	//统计尖括号的个数
		int quoteNumber = 0;					//统计引号的个数
		int spaceNumber = 0;					//统计空格的个数
		int HyphenNumber=0;						//统计连字符的个数<!--asdf-->
		g.setColor(defaultColor);
		/**获取上下文字符*/
		Segment seg = new Segment();
		getDocument().getText(startOffset, length, seg);
		/*当前字符在“”内部的标志位*/
		boolean isstring=false;
		/*当前字符是注释的标志位*/
		boolean isannotation=false;
		for (int wordIndex = 0; wordIndex < seg.length(); wordIndex++) {
			kind = kindOfChar(seg, wordIndex);
			
			/** 遇到" */
			if (kind == 8||isstring) {
				//遇到"
				if(kind == 8){
					quoteNumber++;
					/* 属性值的颜色 */
					if (quoteNumber % 2 == 1) {
						g.setColor(stringColor);
						isstring=true;
					}else{
						isstring=false;
					}					
				}
				// 在“”内部 颜色不变
			}
			/*注释*/
			else if (kind == 9||isannotation) {
				if(kind==9){
					HyphenNumber++;
					HyphenNumber=HyphenNumber%4;
					/*注释开头*/
					g.setColor(Color.gray);
					if(HyphenNumber==1){	
						isannotation=true;
					}
					/*注释结尾*/
					else if(HyphenNumber==0){
						isannotation=false;
					}
				}
			}
			/** 遇到<||> */
			else if (kind == 0 || kind == 2) {
				bracketNumber++;
				g.setColor(tagColor);
				spaceNumber = 0;
				/** 到了字符串外部 */
			} else if (kind == 1) {
				/* 属性名的颜色 */
				if (quoteNumber % 2 == 0 && bracketNumber % 2 == 1
						&& spaceNumber >= 1)
					g.setColor(attributeColor);
				/* 标签外部字符的颜色 */
				if (bracketNumber % 2 == 0)
					g.setColor(defaultColor);
			}
			/** 遇到空格 */
			else if (kind == 7) {
				spaceNumber++;
				// g.setColor(attributeColor);
			}
			/** 遇到= */
			else if (kind == 6) {
				/* =的颜色 */
				g.setColor(defaultColor);
			}
			/** 遇到'/' */
			else if (kind == 3) {
				g.setColor(tagColor);
			}
			/** 遇到'?' */
			else if (kind == 4) {
				g.setColor(defaultColor);
			}
			/** 遇到'!' */
			else if (kind == 5) {
				g.setColor(Color.ORANGE);
			}
			Segment text = getLineBuffer();
			/*选取要绘制的字符*/
			getDocument().getText(startOffset + wordIndex, 1, text);
			/*绘制*/
			ret = Utilities.drawTabbedText(text, ret, y, g, this, startOffset
					+ wordIndex);
		}
		return ret;
	}

	/**
	 * 判断当前字符所属种类 0：< 1：字符 2：> 3：/ 4：? 5:! 6:= 7:空格 8:"9:-
	 * */
	private int kindOfChar(Segment segment, int index) {
		int kind;
		char currentChar = segment.charAt(index);
		switch (currentChar) {
		case '<':
			kind = 0;
			break;
		case '>':
			kind = 2;
			break;
		case '/':
			kind = 3;
			break;
		case '?':
			kind = 4;
			break;
		case '!':
			kind = 5;
			break;
		case '=':
			kind = 6;
			break;
		case ' ':
			kind = 7;
			break;
		case '"':
			kind = 8;
			break;
		case '-':
			kind = 9;
			break;
		default:
			kind = 1;
			break;
		}
		return kind;
	}

}
