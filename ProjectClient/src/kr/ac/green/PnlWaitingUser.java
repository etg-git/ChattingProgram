package kr.ac.green;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PnlWaitingUser extends JPanel {
	
	private JLabel lblGender;
	private JLabel lblName;
	private JLabel lblMy;
	private Icon male = new ImageIcon("male-icon.png");
	private Icon female = new ImageIcon("female-icon.png");

	public PnlWaitingUser(boolean gender, String name) {
		init();
		setDisplay();
		setGenderIcon(gender);
		setLblName(name);
	}
	
	public JLabel getLblName() {
		return lblName;
	}

	public void setLblName(String name) {
		this.lblName.setText(name);
	}
	
	
	public JLabel getLblMy() {
		return lblMy;
	}

	public void setLblMy(JLabel lblMy) {
		this.lblMy = lblMy;
	}

	public void setGenderIcon(boolean gender) {
		if(gender) {
			lblGender.setText(null);
			lblGender.setIcon(male);
		} else {
			lblGender.setText(null);
			lblGender.setIcon(female);
		}
		
	}
	public void init() {
		lblGender = new JLabel("남 or 여");
		lblName = new JLabel("하이요");
		lblMy = new JLabel();
		lblName.setFont(new Font("MD개성체", Font.BOLD, 18));
	}
	
	public void setDisplay() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 30, 20));
		
		
		add(lblGender);
		add(lblName);
		add(lblMy);
	}
	@Override
	public String toString() {
		return lblName.getText();
	}
}
