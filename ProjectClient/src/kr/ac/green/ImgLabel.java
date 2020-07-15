package kr.ac.green;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ImgLabel extends JLabel {
	private int order;
	public ImgLabel(ImageIcon icon, int order) {
		super(icon, CENTER);
		this.order = order;
	}
	public int getOrder() {
		return order;
	}
}
