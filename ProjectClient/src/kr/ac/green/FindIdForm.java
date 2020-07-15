package kr.ac.green;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FindIdForm extends JDialog implements Serializable {

	private LoginForm loginForm;
	private JPanel pnlNorth;
	private JLabel lblName;
	private JTextField tfName;
	private JPanel pnlName;
	private JLabel lblPhone;
	private JTextField tfPhone1;
	private JTextField tfPhone2;
	private JTextField tfPhone3;
	private JPanel pnlPhone;

	private JPanel pnlCenter;
	private JButton btnFind;
	private JButton btnCancel;

	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Socket sock;
	
	private Color color1 = new Color(0x3C92E0);

	public FindIdForm(LoginForm loginForm, Socket sock, ObjectInputStream ois, ObjectOutputStream oos) {
		super(loginForm, "아이디 찾기", true);
		this.sock = sock;
		this.oos = oos;
		this.ois = ois;
		this.loginForm = loginForm;
		init();
		setDisplay();
		addListeners();
		showFrame();
	}

	private void init() {
		pnlNorth = new JPanel(new GridLayout(0, 1));
		lblName = new JLabel("NAME");
		tfName = new JTextField(20);
		pnlName = new JPanel();
		pnlName.setBackground(color1);

		lblPhone = new JLabel("PHONE");
		tfPhone1 = new JTextField(6);
		tfPhone2 = new JTextField(6);
		tfPhone3 = new JTextField(6);
		pnlPhone = new JPanel();
		pnlPhone.setBackground(color1);

		pnlCenter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnlCenter.setOpaque(true);
		pnlCenter.setBackground(color1);
		btnFind = new JButton("찾     기");
		btnFind.setBackground(Color.WHITE);
		btnFind.setForeground(color1);
		btnFind.setPreferredSize(new Dimension(80,30));
		btnCancel = new JButton("취     소");
		btnCancel.setBackground(Color.white);
		btnCancel.setForeground(color1);
		btnCancel.setPreferredSize(new Dimension(80,30));

	}

	private void setDisplay() {
		pnlName.add(lblName);
		pnlName.add(tfName);

		pnlPhone.add(lblPhone);
		pnlPhone.add(tfPhone1);
		pnlPhone.add(tfPhone2);
		pnlPhone.add(tfPhone3);

		pnlNorth.add(pnlName);
		pnlNorth.add(pnlPhone);

		pnlCenter.add(btnFind);
		pnlCenter.add(btnCancel);

		add(pnlNorth, BorderLayout.NORTH);
		add(pnlCenter, BorderLayout.CENTER);
	}

	private void addListeners() {
		btnFind.addActionListener((ae) -> {
			boolean flag = true;
			if (tfName.getText().equals("")) {
				JOptionPane.showMessageDialog(FindIdForm.this, "이름을 입력하세요");
				flag = false;
			} else if (tfPhone1.getText().equals("") || tfPhone2.getText().equals("")
					|| tfPhone3.getText().equals("")) {
				JOptionPane.showMessageDialog(FindIdForm.this, "번호를 입력하세요");
				flag = false;
			}
		
			try {
				if (flag) {
					SendData data = new SendData(OperationCodeClient.FIND_ID, tfName.getText(),
							tfPhone1.getText() + tfPhone2.getText() + tfPhone3.getText());
					oos.writeObject(data);
					oos.flush();
					oos.reset();
					
					Object obj = null;
					if((obj = ois.readObject()) != null) {
						SendData readData = (SendData) obj;
						String id = null;
						if(readData.getCode() == OperationCodeServer.FIND_ID_OK) {
							id = (String)readData.getData()[0];
							JOptionPane.showMessageDialog(FindIdForm.this, "당신의 아이디는 \n" + id + "입니다");
							dispose();
							loginForm.dispose();
							new LoginForm();
						} else if(readData.getCode() == OperationCodeServer.FIND_ID_FAIL1) {
							JOptionPane.showMessageDialog(FindIdForm.this, "찾는 아이디가 없습니다");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		});

		btnCancel.addActionListener((ae) -> {
			dispose();
		});
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				dispose();
			}
		});
	}

	private void showFrame() {
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
	}
}
