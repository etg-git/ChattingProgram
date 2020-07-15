package kr.ac.green;

import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class SetComponent extends JComponent  implements Serializable{
	
	public static void setLayout() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	public static void setLabel(JLabel lbl) {
		lbl.setHorizontalAlignment(JLabel.CENTER);
		lbl.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
		lbl.setPreferredSize(new Dimension(100, 40));
	}
}
