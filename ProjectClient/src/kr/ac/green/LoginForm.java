package kr.ac.green;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LoginForm extends JFrame implements Serializable{
	private JLabel lblId;
	private JTextField tfId;
	private JLabel lblPw;
	private JPanel pnlId;
	
	//north
	private JPanel pnlNorth;
	private JPasswordField tfPw;
	private JPanel pnlPw;
	
	//center
	private JPanel pnlCenter;
	private JButton btnConnect;
	private JButton btnCancel;
	
	//south
	private JPanel pnlSouth;
	private JLabel lblJoin;
	private JLabel lblFindId;
	private JLabel lblFindPw;
	
	private Socket sock;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	
	private Color color2 = new Color(0x3C92E0);
	public LoginForm() {
		init();
		setDisplay();
        addListeners();
		addMListener();
		showFrame();
	}
	
	private void init() {
		SetComponent.setLayout();
		Color color = new Color(0x47302E);
		lblId = new JLabel("아이디");
		lblPw = new JLabel("비밀번호");
		tfId = new JTextField(10);
		tfPw = new JPasswordField(10);
		pnlId = new JPanel();
		pnlPw = new JPanel();
		pnlNorth = new JPanel(new BorderLayout());
		
		pnlCenter = new JPanel();
		btnCancel = new JButton("취소");
		btnCancel.setBackground(color2);
		btnCancel.setForeground(Color.WHITE);
		btnConnect = new JButton("접속");
		btnConnect.setBackground(color2);
		btnConnect.setForeground(Color.WHITE);
		
		lblJoin = new JLabel("회원가입");
		lblFindId = new JLabel("아이디 찾기");
		lblFindPw = new JLabel("비밀번호 찾기");
		pnlSouth = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
		
		try {
			sock = new Socket("192.168.70.29", 10001);
			
			ois = new ObjectInputStream(sock.getInputStream());
			oos = new ObjectOutputStream(sock.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private void setDisplay() {
		SetComponent.setLabel(lblId);
		SetComponent.setLabel(lblPw);
		SetComponent.setLabel(lblJoin);
		SetComponent.setLabel(lblFindId);
		SetComponent.setLabel(lblFindPw);
		
		pnlId.add(lblId);
		pnlId.add(tfId);
		pnlId.setOpaque(true);
		pnlId.setBackground(color2);
		
	
		pnlPw.add(lblPw);
		pnlPw.add(tfPw);
		
		ImageIcon image = new ImageIcon("gagaoTalk.png");
		JLabel lblChat = new JLabel();
		lblChat.setIcon(image);
		lblChat.setOpaque(true);
		lblChat.setBackground(color2);
		
		pnlNorth.add(lblChat, BorderLayout.NORTH);
		pnlNorth.add(pnlId, BorderLayout.CENTER);
		pnlNorth.add(pnlPw, BorderLayout.SOUTH);
		pnlPw.setOpaque(true);
		pnlPw.setBackground(color2);
		pnlNorth.setOpaque(true);
		pnlNorth.setBackground(color2);
		
		pnlCenter.add(btnConnect);
		pnlCenter.add(btnCancel);
		pnlCenter.setBackground(color2);
		
		pnlSouth.add(lblJoin);
		pnlSouth.add(lblFindId);
		pnlSouth.add(lblFindPw);
		pnlSouth.setBackground(color2);
	
		
		add(pnlNorth, BorderLayout.NORTH);
		add(pnlCenter, BorderLayout.CENTER);
		add(pnlSouth, BorderLayout.SOUTH);
	}

	private void addListeners() {
		btnConnect.addActionListener((ae) -> {
			try {
				SendData data = new SendData(OperationCodeClient.LOGIN, tfId.getText(), tfPw.getText());
				
				oos.writeObject(data);
				oos.flush();
				oos.reset();
				
				Object obj = null;
				if((obj = ois.readObject()) != null) {
					SendData readData = (SendData) obj;
					
					if(readData.getCode() == OperationCodeServer.ALL_WAITING_ROOM_UPDATE) {
						Vector<UserInfo>  waitingList = (Vector<UserInfo>) readData.getData()[0];
						Vector<RoomInfo> chatRoomList = (Vector<RoomInfo>) readData.getData()[1];
						String nickName = (String) readData.getData()[2];
						JOptionPane.showMessageDialog(LoginForm.this, "로그인성공!!");
						dispose();
						new Queue(sock, oos, ois, waitingList, chatRoomList, tfId.getText(), nickName);
					} else if(readData.getCode() == OperationCodeServer.CONNECT_FAIL2) {
						JOptionPane.showMessageDialog(LoginForm.this, "아이디 혹은 비밀번호가 틀립니다");
					} else if(readData.getCode() == OperationCodeServer.CONNECT_FAIL) {
						JOptionPane.showMessageDialog(LoginForm.this, "이미 접속한 아이디입니다");
					}
				}
			} catch (Exception e) {
			}
		});
		btnCancel.addActionListener((ae) -> System.exit(0));
	}
	private void addMListener() {
		MouseListener mListener = new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent me) {
				Object src = me.getSource();
				Cursor cs = new Cursor(Cursor.HAND_CURSOR); 
				if(lblJoin == src || lblFindId == src || lblFindPw == src) { 
					setCursor(cs);
				} 
			}
			@Override
			public void mouseExited(MouseEvent me) {
				Object src = me.getSource();
				Cursor cs = new Cursor(DEFAULT_CURSOR);
				if(lblJoin == src || lblFindId == src || lblFindPw == src) { 
					setCursor(cs);
				} 
			}
			@Override
			public void mouseClicked(MouseEvent me) {
				Object src = me.getSource();
				if(src == lblJoin) {
					new JoinForm(LoginForm.this, sock, ois, oos);
				} else if(src == lblFindId) { 
					new FindIdForm(LoginForm.this, sock, ois, oos);
				} else if(src == lblFindPw){
					new FindPwForm(LoginForm.this, sock, oos, ois);
				}
			}
		};
		lblJoin.addMouseListener(mListener);
		lblFindId.addMouseListener(mListener);
		lblFindPw.addMouseListener(mListener);
	}
	private void showFrame() {
		setTitle("로그인화면");
		setLocationRelativeTo(null);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
	           public void run() {
	               new LoginForm();
	           }
	       });
	}
}
