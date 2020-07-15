package kr.ac.green;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class PnlChatRoom extends JPanel {
	
	private JLabel lblPw;
	private JLabel lblRoomNum;
	private JLabel lblTitle;
	private JLabel lblSubject;
	private JLabel currentMaxNum;
	
	private Icon lock = new ImageIcon("Lock-Lock-icon.jpg");
	private Icon unlock = new ImageIcon("Lock-Unlock-icon.jpg");
	
	private String pw;
	private int currentNum;
	private int maxNum;
	private String roomNum;
	private String subject;
	public PnlChatRoom(String pw, String roomNum, String title, String subject, String currentMaxNum) {
		init();
		setDisplay();
		this.pw = pw;
		this.roomNum = roomNum;
		this.subject = subject;
		setLblPw(pw);
		setLblRoomNum(roomNum);
		setLblTitle(title);
		setLblSubject(subject);
		int index = currentMaxNum.indexOf("/");
		currentNum = Integer.parseInt(currentMaxNum.substring(0, index));
		maxNum = Integer.parseInt(currentMaxNum.substring(index + 1, currentMaxNum.length()-1));
		setCurrentMaxNum(currentMaxNum);
	}
	
	
	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
		this.lblSubject.setText("< "+subject+" >");
	}


	public String getRoomNum() {
		return roomNum;
	}


	public void setRoomNum(String roomNum) {
		this.roomNum = roomNum;
	}


	public int getCurrentNum() {
		return currentNum;
	}


	public void setCurrentNum(int currentNum) {
		this.currentNum = currentNum;
		
		setCurrentMaxNum(currentNum + "/" + maxNum +"명");
	}


	public int getMaxNum() {
		return maxNum;
	}


	public void setMaxNum(int maxNum) {
		this.maxNum = maxNum;
		
		setCurrentMaxNum(currentNum + "/" + maxNum + "명");
	}


	public JLabel getLblPw() {
		return lblPw;
	}
	
	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public void setLblPw(String pw) {
		if(pw.equals("")) {
			lblPw.setIcon(unlock);
			lblPw.setText(null);
		} else {
			lblPw.setIcon(lock);
			lblPw.setText("");
		}
	}

	public JLabel getLblRoomNum() {
		return lblRoomNum;
	}

	public void setLblRoomNum(String roomNum) {
		this.lblRoomNum.setText("[" + roomNum +"]");
	}

	public JLabel getLblTitle() {
		return lblTitle;
	}

	public void setLblTitle(String title) {
		this.lblTitle.setText(title);
	}

	public JLabel getLblSubject() {
		return lblSubject;
	}

	public void setLblSubject(String subject) {
		this.lblSubject.setText("< "+subject+" >");
	}	
	public JLabel getCurrentMaxNum() {
		return currentMaxNum;
	}
	
	public void setCurrentMaxNum(String currentMaxNum) {
		this.currentMaxNum.setText(currentMaxNum);
	}
	private void init() {
		lblPw = new JLabel("Pw");
		lblRoomNum = new JLabel("0");
		lblTitle = new JLabel("title");
		lblTitle.setPreferredSize(new Dimension(170, 15));
		lblSubject = new JLabel("Subject");
		currentMaxNum = new JLabel("Max");
	}

	private void setDisplay() {
		JPanel pnlChat = new JPanel(new FlowLayout(FlowLayout.CENTER, 20 ,10));
		
		setBorder(new LineBorder(Color.BLACK, 2));
		lblPw.setBackground(Color.WHITE);
		lblRoomNum.setBackground(Color.WHITE);
		lblTitle.setBackground(Color.WHITE);
		currentMaxNum.setBackground(Color.WHITE);
		lblSubject.setBackground(Color.WHITE);
		pnlChat.setBackground(Color.WHITE);
		
		lblPw.setFont(new Font("MD개성체", Font.BOLD, 16));
		lblRoomNum.setFont(new Font("MD개성체", Font.BOLD, 16));
		lblTitle.setFont(new Font("MD개성체", Font.BOLD, 16));
		currentMaxNum.setFont(new Font("MD개성체", Font.BOLD, 16));
		lblSubject.setFont(new Font("MD개성체", Font.BOLD, 16));
		
		pnlChat.add(lblPw);
		pnlChat.add(lblRoomNum);
		pnlChat.add(lblTitle);
		pnlChat.add(lblSubject);
		pnlChat.add(currentMaxNum);
		
		add(pnlChat, BorderLayout.CENTER);
	}
}
