package kr.ac.green;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

public class CMouseListener<W extends Container> extends MouseAdapter {
	private ShowImoticon emoticon;
	private ChatRoom chatRoom;

	private ImageIcon[] arrImageIcon1 = new ImageIcon[emoticon.EMOTICONS.length];
	private JLabel[] arrShowIcon1 = new JLabel[emoticon.EMOTICONS.length];
	private ImageIcon[] arrImageIcon2 = new ImageIcon[emoticon.EMOTICONS2.length];
	private JLabel[] arrShowIcon2 = new JLabel[emoticon.EMOTICONS2.length];
	private ImageIcon[] arrImageIcon3 = new ImageIcon[emoticon.EMOTICONS3.length];
	private JLabel[] arrShowIcon3 = new JLabel[emoticon.EMOTICONS3.length];
	
	
	private W owner;

	public CMouseListener(W owner) {
		this.owner = owner;
	}

	@Override
	public void mouseEntered(MouseEvent me) {
		setBorder(me, true);
	}

	@Override
	public void mouseExited(MouseEvent me) {
		setBorder(me, false);
	}

	private void setBorder(MouseEvent me, boolean flag) {
		JComponent c = (JComponent) me.getComponent();
		if (flag) {
			c.setBorder(new LineBorder(Color.YELLOW, 2, true));
		} else {
			c.setBorder(null);
		}
	}

}
