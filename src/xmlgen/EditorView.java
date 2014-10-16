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
		int kind = 0;							//�ַ�����
		int bracketNumber = 0;                	//ͳ�Ƽ����ŵĸ���
		int quoteNumber = 0;					//ͳ�����ŵĸ���
		int spaceNumber = 0;					//ͳ�ƿո�ĸ���
		int HyphenNumber=0;						//ͳ�����ַ��ĸ���<!--asdf-->
		g.setColor(defaultColor);
		/**��ȡ�������ַ�*/
		Segment seg = new Segment();
		getDocument().getText(startOffset, length, seg);
		/*��ǰ�ַ��ڡ����ڲ��ı�־λ*/
		boolean isstring=false;
		/*��ǰ�ַ���ע�͵ı�־λ*/
		boolean isannotation=false;
		for (int wordIndex = 0; wordIndex < seg.length(); wordIndex++) {
			kind = kindOfChar(seg, wordIndex);
			
			/** ����" */
			if (kind == 8||isstring) {
				//����"
				if(kind == 8){
					quoteNumber++;
					/* ����ֵ����ɫ */
					if (quoteNumber % 2 == 1) {
						g.setColor(stringColor);
						isstring=true;
					}else{
						isstring=false;
					}					
				}
				// �ڡ����ڲ� ��ɫ����
			}
			/*ע��*/
			else if (kind == 9||isannotation) {
				if(kind==9){
					HyphenNumber++;
					HyphenNumber=HyphenNumber%4;
					/*ע�Ϳ�ͷ*/
					g.setColor(Color.gray);
					if(HyphenNumber==1){	
						isannotation=true;
					}
					/*ע�ͽ�β*/
					else if(HyphenNumber==0){
						isannotation=false;
					}
				}
			}
			/** ����<||> */
			else if (kind == 0 || kind == 2) {
				bracketNumber++;
				g.setColor(tagColor);
				spaceNumber = 0;
				/** �����ַ����ⲿ */
			} else if (kind == 1) {
				/* ����������ɫ */
				if (quoteNumber % 2 == 0 && bracketNumber % 2 == 1
						&& spaceNumber >= 1)
					g.setColor(attributeColor);
				/* ��ǩ�ⲿ�ַ�����ɫ */
				if (bracketNumber % 2 == 0)
					g.setColor(defaultColor);
			}
			/** �����ո� */
			else if (kind == 7) {
				spaceNumber++;
				// g.setColor(attributeColor);
			}
			/** ����= */
			else if (kind == 6) {
				/* =����ɫ */
				g.setColor(defaultColor);
			}
			/** ����'/' */
			else if (kind == 3) {
				g.setColor(tagColor);
			}
			/** ����'?' */
			else if (kind == 4) {
				g.setColor(defaultColor);
			}
			/** ����'!' */
			else if (kind == 5) {
				g.setColor(Color.ORANGE);
			}
			Segment text = getLineBuffer();
			/*ѡȡҪ���Ƶ��ַ�*/
			getDocument().getText(startOffset + wordIndex, 1, text);
			/*����*/
			ret = Utilities.drawTabbedText(text, ret, y, g, this, startOffset
					+ wordIndex);
		}
		return ret;
	}

	/**
	 * �жϵ�ǰ�ַ��������� 0��< 1���ַ� 2��> 3��/ 4��? 5:! 6:= 7:�ո� 8:"9:-
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
