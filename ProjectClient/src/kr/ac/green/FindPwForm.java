package kr.ac.green;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FindPwForm extends JDialog  implements Serializable{
	private LoginForm loginForm;
	
	private JLabel lblId;
	private JTextField tfId;
	private JPanel pnlId;
	
	private JLabel lblPhone;
	private JTextField tfPhone1;
	private JTextField tfPhone2;
	private JTextField tfPhone3;
	private JPanel pnlPhone;
	
	private JLabel lblQuestion;
	private JComboBox<String> cbxQuestion;
	private JPanel pnlQuestion;
	
	private JLabel lblAnswer;
	private JTextField tfAnswer;
	private JPanel pnlAnswer;
	
	private JPanel pnlNorth;
	
	private JPanel pnlSouth;
	private JButton btnConfirm;
	
	private Socket sock;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	private Color color1 = new Color(0x3C92E0);
	
	public FindPwForm(LoginForm loginForm, Socket sock, ObjectOutputStream oos, ObjectInputStream ois) {
		super(loginForm, "비밀번호 찾기", true);
		this.loginForm = loginForm;
		this.sock = sock;
		this.oos = oos;
		this.ois = ois;
		init();
		setDisplay();
		addListeners();
		showFrame();
	}

	private void init() {
		lblId = new JLabel("ID");
		tfId = new JTextField(20);
		pnlId = new JPanel();
		pnlId.setBackground(color1);
		
		lblPhone = new JLabel("Phone");
		tfPhone1 = new JTextField(6);
		tfPhone2 = new JTextField(6);
		tfPhone3 = new JTextField(6);
		pnlPhone = new JPanel();
		pnlPhone.setBackground(color1);
		
		lblQuestion = new JLabel("Question");
		cbxQuestion = new JComboBox<String>();
		cbxQuestion.setForeground(color1);
		cbxQuestion.setBackground(Color.white);
		pnlQuestion = new JPanel();
		pnlQuestion.setBackground(color1);
		
		lblAnswer = new JLabel("Answer");
		tfAnswer = new JTextField(20);
		pnlAnswer = new JPanel();
		pnlAnswer.setBackground(color1);
		pnlNorth = new JPanel(new GridLayout(0,1));
		
		pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnlSouth.setBackground(color1);
		btnConfirm = new JButton("확     인");
		btnConfirm.setBackground(Color.WHITE);
		btnConfirm.setForeground(color1);
		btnConfirm.setPreferredSize(new Dimension(80,30));
	}

	private void setDisplay() {
		pnlId.add(lblId);
		pnlId.add(tfId);
		
		pnlPhone.add(lblPhone);
		pnlPhone.add(tfPhone1);
		pnlPhone.add(tfPhone2);
		pnlPhone.add(tfPhone3);
		
		pnlQuestion.add(lblQuestion);
		pnlQuestion.add(cbxQuestion);
		
		pnlAnswer.add(lblAnswer);
		pnlAnswer.add(tfAnswer);
		
		pnlNorth.add(pnlId);
		pnlNorth.add(pnlPhone);
		pnlNorth.add(pnlQuestion);
		pnlNorth.add(pnlAnswer);
		
		pnlSouth.add(btnConfirm);
		
		cbxQuestion.addItem("사는곳은어딘가요?");
		cbxQuestion.addItem("가장좋아하는것");
		cbxQuestion.addItem("가장싫어하는것");
		cbxQuestion.addItem("가장기억에남는추억");
		
		add(pnlNorth, BorderLayout.NORTH);
		add(pnlSouth, BorderLayout.SOUTH);
		
	}

	private void addListeners() {
		btnConfirm.addActionListener((ae) -> {
			try {
				SendData data = new SendData(OperationCodeClient.PW_SEARCH, tfId.getText(),
						tfPhone1.getText()+tfPhone2.getText()+tfPhone3.getText(), cbxQuestion.getSelectedItem().toString(),
						tfAnswer.getText());
				
				oos.writeObject(data);
				oos.flush();
				oos.reset();
				
				Object obj = null;
				if((obj = ois.readObject()) != null) {
					SendData readData = (SendData) obj;
					
					if(readData.getCode() == OperationCodeServer.PW_SEARCH_FAIL1) {
						JOptionPane.showMessageDialog(FindPwForm.this, "일치하지 않습니다"); 
					} else if(readData.getCode() == OperationCodeServer.PW_SEARCH) {
						JOptionPane.showMessageDialog(FindPwForm.this, "아이디를 찾았습니다!");
						dispose();
						new PwChangeForm(sock, oos, ois, tfId.getText());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				dispose();
			}
		});
	}

	private void showFrame() {
		setLocationRelativeTo(null);
		pack();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
	}
	
}
