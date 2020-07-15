package kr.ac.green;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class PwChangeForm extends JFrame {
	private JLabel lblPw;
	private JPasswordField pfPw;
	private JPanel pnlPw;

	private JLabel lblRePw;
	private JPasswordField pfRePw;
	private JPanel pnlRePw;

	private JButton btnConfirm;
	private JPanel pnlSouth;

	private JPanel pnlCenter;
	private Socket sock;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private String id;
	
	public PwChangeForm(Socket sock, ObjectOutputStream oos, ObjectInputStream ois, String id) {
		this.sock = sock;
		this.oos = oos;
		this.ois = ois;
		this.id = id;
		init();
		setDisplay();
		addListeners();
		showFrame();
	}

	private void init() {
		lblPw = new JLabel("PW");
		pfPw = new JPasswordField(20);
		pnlPw = new JPanel();
		lblPw.setPreferredSize(new Dimension(42, 18));
		lblRePw = new JLabel("RE_PW");
		pfRePw = new JPasswordField(20);
		pnlRePw = new JPanel();

		btnConfirm = new JButton("확인");

		pnlCenter = new JPanel(new GridLayout(0, 1));

		pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));

	}

	private void setDisplay() {
		pnlPw.add(lblPw);
		pnlPw.add(pfPw);

		pnlRePw.add(lblRePw);
		pnlRePw.add(pfRePw);

		pnlCenter.add(pnlPw);
		pnlCenter.add(pnlRePw);

		pnlSouth.add(btnConfirm);

		add(pnlCenter, BorderLayout.CENTER);
		add(pnlSouth, BorderLayout.SOUTH);
	}

	private void addListeners() {
		btnConfirm.addActionListener((ae) -> {
			boolean flag = true;
			if(pfPw.getText().trim().equals("")) {
				JOptionPane.showMessageDialog(PwChangeForm.this, "비밀번호를 입력하세요");
				flag = false;
			}
			else if(pfRePw.getText().trim().equals("")) {
				JOptionPane.showMessageDialog(PwChangeForm.this, "재확인을 입력하세요");
				flag = false;
			}
			else if (!pfPw.getText().equals(pfRePw.getText())) {
				JOptionPane.showMessageDialog(PwChangeForm.this, "비밀번호가 서로 다릅니다");
				flag = false;
			}

			try {
				if (flag) {
					SendData data = new SendData(OperationCodeClient.PW_CHANGE, pfPw.getText(), id);

					oos.writeObject(data);
					oos.flush();
					oos.reset();
					
					Object obj = null;
					if((obj = ois.readObject()) != null) {
						SendData readData = (SendData) obj;
						
						if(readData.getCode() == OperationCodeServer.PW_CHANGE) {
							JOptionPane.showMessageDialog(PwChangeForm.this, "비밀번호 변경되었습니다!");
							dispose();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void showFrame() {
		setTitle("비밀번호 변경");
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);

	}
}
