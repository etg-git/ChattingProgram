package kr.ac.green;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class JoinForm extends JDialog implements Serializable {

	private LoginForm loginForm;
	String[] name = { "ID", "PW", "RE_PW", "NAME", "PHONE", "NICKNAME", "QUESTION", "ANSWER" };
	private JPanel pnlNorth;
	private JPanel[] pnlInput;
	private JLabel[] lblName;
	private JTextField tfId;
	private JPasswordField tfPw;
	private JPasswordField tfRePw;
	private JTextField tfName;
	private JTextField tfPhone1;
	private JTextField tfPhone2;
	private JTextField tfPhone3;
	private JTextField tfNickName;
	private JTextField tfAnswer;

	private JComboBox<String> cbxQuestion;

	private JPanel pnlCenter;
	private JRadioButton rbMale;
	private JRadioButton rbFemale;

	private JPanel pnlSouth;
	private JButton btnSuccess;
	private JButton btnCancel;
	
	public static final int ID = 0;
	public static final int PW = 1;
	public static final int RE_PW = 2;
	public static final int NAME = 3;
	public static final int PHONE = 4;
	public static final int NICKNAME = 5;
	public static final int QUESTION = 6;
	public static final int ANSWER = 7;

	private Socket sock;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	private Color color1 = new Color(0x3C92E0);

	public JoinForm(LoginForm loginForm, Socket sock, ObjectInputStream ois, ObjectOutputStream oos) {
		super(loginForm, "회원가입", true);
		this.loginForm = loginForm;
		this.sock = sock;
		this.ois = ois;
		this.oos = oos;

		init();
		setDisplay();
		addListeners();
		showFrame();
	}
	public JoinForm(LoginForm loginForm, Socket sock, ObjectInputStream ois, ObjectOutputStream oos, JoinForm join) {
		super(loginForm, "회원가입", true);
		this.loginForm = loginForm;
		this.sock = sock;
		this.ois = ois;
		this.oos = oos;
		
		init();
		setDisplay();
		setting(join);
		addListeners();
		showFrame();
		
	}
	public JTextField getTfId() {
		return tfId;
	}

	public void setTfId(JTextField tfId) {
		this.tfId = tfId;
	}

	public JPasswordField getTfPw() {
		return tfPw;
	}

	public void setTfPw(JPasswordField tfPw) {
		this.tfPw = tfPw;
	}

	public JPasswordField getTfRePw() {
		return tfRePw;
	}

	public void setTfRePw(JPasswordField tfRePw) {
		this.tfRePw = tfRePw;
	}

	public JTextField getTfName() {
		return tfName;
	}

	public void setTfName(JTextField tfName) {
		this.tfName = tfName;
	}

	public JTextField getTfPhone1() {
		return tfPhone1;
	}

	public void setTfPhone1(JTextField tfPhone1) {
		this.tfPhone1 = tfPhone1;
	}

	public JTextField getTfPhone2() {
		return tfPhone2;
	}

	public void setTfPhone2(JTextField tfPhone2) {
		this.tfPhone2 = tfPhone2;
	}

	public JTextField getTfPhone3() {
		return tfPhone3;
	}

	public void setTfPhone3(JTextField tfPhone3) {
		this.tfPhone3 = tfPhone3;
	}

	public JTextField getTfNickName() {
		return tfNickName;
	}

	public void setTfNickName(JTextField tfNickName) {
		this.tfNickName = tfNickName;
	}

	public JTextField getTfAnswer() {
		return tfAnswer;
	}

	public void setTfAnswer(JTextField tfAnswer) {
		this.tfAnswer = tfAnswer;
	}
	
	public JComboBox<String> getCbxQuestion() {
		return cbxQuestion;
	}

	public void setCbxQuestion(JComboBox<String> cbxQuestion) {
		this.cbxQuestion = cbxQuestion;
	}

	public JRadioButton getRbMale() {
		return rbMale;
	}

	public void setRbMale(JRadioButton rbMale) {
		this.rbMale = rbMale;
	}

	public JRadioButton getRbFemale() {
		return rbFemale;
	}

	public void setRbFemale(JRadioButton rbFemale) {
		this.rbFemale = rbFemale;
	}

	private void init() {
		pnlNorth = new JPanel(new GridLayout(0, 1));
		pnlNorth.setBackground(color1);
		pnlInput = new JPanel[8];
		lblName = new JLabel[8];
		tfId = new JTextField(20);
		tfPw = new JPasswordField(20);
		tfRePw = new JPasswordField(20);
		tfName = new JTextField(20);
		tfPhone1 = new JTextField(6);
		tfPhone2 = new JTextField(6);
		tfPhone3 = new JTextField(6);
		tfNickName = new JTextField(20);
		tfAnswer = new JTextField(20);

		cbxQuestion = new JComboBox<String>();
		cbxQuestion.setForeground(color1);
		cbxQuestion.setBackground(Color.white);

		pnlCenter = new JPanel();
		rbMale = new JRadioButton("남자");
		rbMale.setBackground(color1);
		rbFemale = new JRadioButton("여자");
		rbFemale.setBackground(color1);
		pnlCenter.setBackground(color1);

		pnlSouth = new JPanel();
		pnlSouth.setBackground(color1);
		btnSuccess = new JButton("가입완료");
		btnSuccess.setPreferredSize(new Dimension(90,30));
		btnSuccess.setBackground(Color.white);
		btnSuccess.setForeground(color1);
		btnCancel = new JButton("취소");
		btnCancel.setBackground(Color.WHITE);
		btnCancel.setForeground(color1);
		btnCancel.setPreferredSize(new Dimension(90,30));
	}

	private void setDisplay() {
		setNorth();

		pnlCenter.add(rbMale);
		pnlCenter.add(rbFemale);
		ButtonGroup group = new ButtonGroup();
		group.add(rbFemale);
		group.add(rbMale);
		rbMale.setSelected(true);
		pnlSouth.add(btnSuccess);
		pnlSouth.add(btnCancel);
		add(pnlNorth, BorderLayout.NORTH);
		add(pnlCenter, BorderLayout.CENTER);
		add(pnlSouth, BorderLayout.SOUTH);
	}

	private void setNorth() {
		cbxQuestion.addItem("사는곳은어딘가요?");
		cbxQuestion.addItem("가장좋아하는것");
		cbxQuestion.addItem("가장싫어하는것");
		cbxQuestion.addItem("가장기억에남는추억");

		for (int i = 0; i < name.length; i++) {
			pnlInput[i] = new JPanel();
			pnlInput[i].setOpaque(true);
			pnlInput[i].setBackground(color1);
			lblName[i] = new JLabel(name[i]);

			pnlInput[i].add(lblName[i]);
		}
		pnlInput[ID].add(tfId);
		pnlInput[PW].add(tfPw);
		pnlInput[RE_PW].add(tfRePw);
		pnlInput[NAME].add(tfName);
		pnlInput[PHONE].add(tfPhone1);
		pnlInput[PHONE].add(tfPhone2);
		pnlInput[PHONE].add(tfPhone3);
		pnlInput[NICKNAME].add(tfNickName);
		pnlInput[QUESTION].add(cbxQuestion);
		pnlInput[ANSWER].add(tfAnswer);

		for (int i = 0; i < pnlInput.length; i++) {
			lblName[i].setPreferredSize(new Dimension(62, 18));
			pnlNorth.add(pnlInput[i]);
		}
	}

	private void addListeners() {
		btnCancel.addActionListener((ae) -> {
			dispose();
		});
		btnSuccess.addActionListener((ae) -> {
			SendData sendData = null;
			boolean flag = true;
			if (tfId.getText().equals("")) {
				JOptionPane.showMessageDialog(JoinForm.this, "아이디를 입력하세요");
				flag = false;
			} else if (tfPw.getText().equals("")) {
				JOptionPane.showMessageDialog(JoinForm.this, "비밀번호를 입력하세요");
				flag = false;
			} else if (tfName.getText().equals("")) {
				JOptionPane.showMessageDialog(JoinForm.this, "이름을 입력하세요");
				flag = false;
			} else if (tfPhone1.getText().equals("") || tfPhone2.getText().equals("") || tfPhone3.getText().equals("")) {
				JOptionPane.showMessageDialog(JoinForm.this, "번호를 입력하세요");
				flag = false;
			} else if (tfNickName.getText().equals("")) {
				JOptionPane.showMessageDialog(JoinForm.this, "닉네임을 입력하세요");
				flag = false;
			} else if (tfAnswer.getText().equals("")) {
				JOptionPane.showMessageDialog(JoinForm.this, "답변을 입력하세요");
				flag = false;
			} else if (!tfPw.getText().equals(tfRePw.getText())) {
				flag = false;
				JOptionPane.showMessageDialog(JoinForm.this, "비밀번호가 서로 다릅니다");
			} else if (tfNickName.getText().length() >= 7) {
				flag = false;
				JOptionPane.showMessageDialog(JoinForm.this, "닉네임 7자 이하로 입력해주세요");
			}
			if (flag) {
				try {
					boolean gender = true;
					if (rbFemale.isSelected()) {
						gender = false;
					}

					sendData = new SendData(OperationCodeClient.SIGN_UP,
							new User(tfId.getText(), tfPw.getText(), gender, tfName.getText(), tfNickName.getText(),
									tfPhone1.getText() + tfPhone2.getText() + tfPhone3.getText(),
									cbxQuestion.getSelectedItem().toString(), tfAnswer.getText()));
					oos.writeObject(sendData);
					oos.flush();
					oos.reset();

					Object obj = null;
					if((obj = ois.readObject()) != null) {
						SendData readData = (SendData) obj;
						if (readData.getCode() == OperationCodeServer.SIGN_UP_OK) {
							JOptionPane.showMessageDialog(JoinForm.this, "회원가입을 성공하였습니다");
							dispose();
							loginForm.dispose();
							new LoginForm();
						} else if (readData.getCode() == OperationCodeServer.SIGN_UP_FAIL) {
							String msg = (String) readData.getData()[0];
							JOptionPane.showMessageDialog(JoinForm.this, msg);
							dispose();
							new JoinForm(loginForm, sock, ois, oos, this);
						}
					}
				} catch (IOException e) {
				} catch (ClassNotFoundException e) {
				}
			}
		});
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				dispose();
			}
		});

	}
	private void setting(JoinForm join) {
		tfId.setText(join.getTfId().getText());
		tfPw.setText(join.getTfPw().getText());
		tfRePw.setText(join.getTfRePw().getText());
		tfName.setText(join.getTfName().getText());
		tfNickName.setText(join.getTfNickName().getText());
		tfPhone1.setText(join.getTfPhone1().getText());
		tfPhone2.setText(join.getTfPhone2().getText());
		tfPhone3.setText(join.getTfPhone3().getText());
		tfAnswer.setText(join.getTfAnswer().getText());
		if(join.getRbFemale().isSelected()) {
			rbFemale.setSelected(true);
		} else {
			rbMale.setSelected(true);
		}
		cbxQuestion.setSelectedIndex(join.getCbxQuestion().getSelectedIndex());
	}
	private void showFrame() {
		setLocationRelativeTo(null);
		pack();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);

	}

}
