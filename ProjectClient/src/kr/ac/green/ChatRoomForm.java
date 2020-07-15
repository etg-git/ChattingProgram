package kr.ac.green;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ChatRoomForm extends JFrame implements Serializable {
	private static final String JPG = "jpg/";
	private static final String EMOTICON = "emotion.png";
	private static final String INVITE = "invite.PNG";
	private static final String LOGOUT = "logout.PNG";
	private static final String MAIL = ".PNG";
	private static final String SETTING = ".PNG";
	private static final String GIF = ".GIF";
	private static final String MOVEEMOTICON = "movingEmoticon/";

	private String title;
	private String subject;

	private Color color = new Color(0x49BFFF);
	private Color color2 = new Color(0x006494);

	private JButton btnInvite;
	private JButton btnExit;
	private JButton btnSetting;
	private JButton btnChatSend;
	private JButton btnImoticon;
	private JTextField tfChat;
	private JComboBox<String> combo;
	private JLabel lblCurrnent;
	private JLabel lblTitle;

	private JPopupMenu pMenu;
	private JMenuItem miHeadMandate;
	private JMenuItem miKickout;
	private JMenuItem miWhisper;

	private JPanel pnlList;

	private String relative[] = { "전체", "귓속말" };

	private Socket sock;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Vector<UserInfo> user;
	private RoomInfo room;

	private JTextPane textPane; // JTextPane 채팅방 구현
	private JScrollPane scroll; // 채팅창 스크롤바

	// 채팅창 유저
	private Vector<PnlChatUser> vChattingUser;
	private JList<PnlChatUser> chattingUserList;
	private DefaultListModel<PnlChatUser> userList = new DefaultListModel<>();

	private String myId;
	private JPanel pnlUserList;

	private String mandateNickName;
	private String header;

	private RoomSetting setting;
	private String nickName;

	// 방만들기 = 방장
	public ChatRoomForm(Socket sock, ObjectOutputStream oos, ObjectInputStream ois, Vector<UserInfo> user,
			RoomInfo room, String myId, String nickName) {
		this.sock = sock;
		this.oos = oos;
		this.ois = ois;
		this.user = user;
		this.room = room;
		this.myId = myId;
		this.nickName = nickName;
		header = user.get(0).getNickName();
		chatShowing();
		init();
		setJList();
		setDisplay();
		addListener();
		showFrame();
		new UpdateThread().start();
	}

	// 방들어오기 = 일반유저
	public ChatRoomForm(Socket sock, ObjectOutputStream oos, ObjectInputStream ois, Vector<UserInfo> user,
			RoomInfo room, String myId, String header, String nickName) {
		this.sock = sock;
		this.oos = oos;
		this.ois = ois;
		this.user = user;
		this.room = room;
		this.myId = myId;
		this.header = header;
		this.nickName = nickName;
		chatShowing();
		init();
		pMenu.getComponent(0).setEnabled(false);
		pMenu.getComponent(1).setEnabled(false);
		btnSetting.setEnabled(false);
		setDisplay();
		addListener();
		showFrame();
		new UpdateThread().start();
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	class UpdateThread extends Thread {
		@Override
		public void run() {
			Object obj = null;
			try {
				while ((obj = ois.readObject()) != null) {
					SendData readData = (SendData) obj;
					if (readData.getCode() == OperationCodeServer.CHATROOM_CHAT_OK) { // 채팅
						String msg = (String) readData.getData()[0];
						appendString("\n");
						appendChatLabel(msg);
						appendString("\n");
						scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
					} else if (readData.getCode() == OperationCodeServer.CHATROOM_EMOT_CHAT_OK) {
						ImageIcon icon = (ImageIcon) readData.getData()[0];
						String mynickName = (String) readData.getData()[1];
						if (nickName.equals(mynickName)) {
							appendString("[ 나  ]\n");
							appendImage(icon);
							appendString("\n");
						} else {
							appendString("[ " + mynickName + " ]\n");
							appendImage(icon);
							appendString("\n");
						}
						scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
					} else if (readData.getCode() == OperationCodeServer.CHATROOM_CHAT_OK2) { // 채팅방
						String msg = (String) readData.getData()[0];
						String mynickName = (String) readData.getData()[1];
						if (nickName.equals(mynickName)) {
							appendString("[ 나  ]\n");
							appendMyChatLabel(msg);
							appendString("\n");
						} else {
							appendString("[ " + mynickName + " ]\n");
							appendChatLabel(msg);
							appendString("\n");
						}
						scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
					} else if (readData.getCode() == OperationCodeServer.CHATTING_USER_UPDATE) { // 채팅방
																									// 업데이트
						Vector<UserInfo> userList = (Vector<UserInfo>) readData.getData()[0];
						vChattingUser = new Vector<>();
						for (UserInfo users : userList) {
							vChattingUser.add(new PnlChatUser(users.isGender(), users.getNickName()));
						}
						setJList();
					} else if (readData.getCode() == OperationCodeServer.CHATROOM_EMOT_CHAT_MOVE_OK) { // 움직이는
																										// 이모티콘
						String path = (String) readData.getData()[0];
						String mynickName = (String) readData.getData()[1];
						ImageIcon icon = new ImageIcon(path);
						if (nickName.equals(mynickName)) {
							appendString("[ 나  ]\n");
							appendImage(icon);
							appendString("\n");
						} else {
							appendString("[" + mynickName + "]\n");
							appendImage(icon);
							appendString("\n");
						}
						scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
					} else if (readData.getCode() == OperationCodeServer.HEAD_UPDATE_OK) { // 방장
																							// 위임
						JOptionPane.showMessageDialog(ChatRoomForm.this, "방장을 위임받으셨습니다");
						String headerMandate = (String) readData.getData()[0];
						header = headerMandate;
						pMenu.getComponent(0).setEnabled(true);
						pMenu.getComponent(1).setEnabled(true);
						btnSetting.setEnabled(true);
						setHeader();
						setJList();
					} else if (readData.getCode() == OperationCodeServer.HEAD_UPDATE) { // 방장
																						// 업데이트
						String headerMandate = (String) readData.getData()[0];
						header = headerMandate;
						setHeader();
						pMenu.getComponent(0).setEnabled(false);
						pMenu.getComponent(1).setEnabled(false);
						btnSetting.setEnabled(false);
					} else if (readData.getCode() == OperationCodeServer.ROOM_EXIT_OK) {
						Vector<UserInfo> userList = (Vector<UserInfo>) readData.getData()[0];
						Vector<RoomInfo> roomList = (Vector<RoomInfo>) readData.getData()[1];
						dispose();
						new Queue(sock, oos, ois, userList, roomList, myId, nickName);
						break;

					} else if (readData.getCode() == OperationCodeServer.KICK_OUT_OK) { // 강퇴알림
						JOptionPane.showMessageDialog(ChatRoomForm.this, "강퇴당하였습니다");
						Vector<UserInfo> userList = (Vector<UserInfo>) readData.getData()[0];
						Vector<RoomInfo> roomList = (Vector<RoomInfo>) readData.getData()[1];
						dispose();
						new Queue(sock, oos, ois, userList, roomList, myId, nickName);
						break;
					} else if (readData.getCode() == OperationCodeServer.ROOM_SETTING_SUCCESS) { // 방설정
																									// 알림
						JOptionPane.showMessageDialog(ChatRoomForm.this, "방설정이 완료되었습니다");
					} else if (readData.getCode() == OperationCodeServer.ROOM_SETTING_UPDATE) { // 방업데이트
						RoomInfo roomSet = (RoomInfo) readData.getData()[0];
						roomSet(roomSet);
					} else if (readData.getCode() == OperationCodeServer.ROOM_SETTING_FAIL) { // 인원적게
																								// 설정
						JOptionPane.showMessageDialog(setting, "현재인원보다 인원을 적게 설정하였습니다.");
					} else if (readData.getCode() == OperationCodeServer.ROOM_SETTING_FAIL2) { // 제목중복
						JOptionPane.showMessageDialog(setting, "방제목이 중복입니다.");
					} else if (readData.getCode() == OperationCodeServer.VIEW_OK) { // 모든
																					// 유저목록
																					// 보기
																					// 성공
						Vector<UserInfo> waitUser = (Vector<UserInfo>) readData.getData()[0];
						new InviteUserList(ChatRoomForm.this, sock, oos, ois, waitUser, room.getNumberRoom(), myId);
					} else if (readData.getCode() == OperationCodeServer.INVITE_CHAT_FAIL2) {// 초대거부
																								// 알림
						String nickName = (String) readData.getData()[0];
						JOptionPane.showMessageDialog(ChatRoomForm.this, nickName + "님이 초대 거부를 하였습니다");
					} else if (readData.getCode() == OperationCodeServer.WHISPER_CHAT_OK) { // 귓속말
						String msg = (String) readData.getData()[0];
						String chat = (String) readData.getData()[1];
						appendString(msg + "\n");
						appendWhisperChatLabel(chat);
						appendString("\n");
						scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
					} else if (readData.getCode() == OperationCodeServer.WHISPER_CHAT_FAIL) { // 귓속말
																								// 실패
						String msg = (String) readData.getData()[0];
						appendWhisperChatLabel(msg);
						appendString("\n");
						scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
					} else if (readData.getCode() == OperationCodeServer.WHISPER_CHAT_FAIL2) { // 귓속말
																								// 실패
						String msg = (String) readData.getData()[0];
						appendWhisperChatLabel(msg);
						appendString("\n");
						scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
					} else if (readData.getCode() == OperationCodeServer.WHISPER_CHAT_SUCCESS) { // 귓속말
																									// 송신성공
						String msg = (String) readData.getData()[0];
						String chat = (String) readData.getData()[1];
						appendString(msg + "\n");
						appendWhisperChatLabel(chat);
						appendString("\n"); // 메시지
						scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
					}
				}
			} catch (

			Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void roomSet(RoomInfo roomSet) {
		this.room = roomSet;
		lblTitle.setText("[" + room.getNumberRoom() + "번방] 제목 : " + room.getTitle());
		lblTitle.setFont(new Font("MD개성체", Font.PLAIN, 20));
	}

	private void setButton(JButton btn) {
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(false);
		btn.setFocusPainted(false);
		btn.setOpaque(false);
		btn.setPreferredSize(new Dimension(40, 50));
	}

	private ImageIcon setImage(String path) {
		Image img = Toolkit.getDefaultToolkit().getImage("FrameUI/" + path + ".png"); // 사이즈
																						// 조절
		Image setImg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);

		return new ImageIcon(setImg);
	}

	private ImageIcon setImage2(String path) {
		Image img = Toolkit.getDefaultToolkit().getImage("FrameUI/" + path + ".png"); // 사이즈
																						// 조절
		Image setImg = img.getScaledInstance(10, 22, Image.SCALE_SMOOTH);

		return new ImageIcon(setImg);
	}

	private void init() {
		lblTitle = new JLabel("[" + room.getNumberRoom() + "번방] 제목 : " + room.getTitle());
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setFont(new Font("MD개성체", Font.PLAIN, 20));
		pnlUserList = new JPanel(new GridLayout(0, 1));

		btnInvite = new JButton(setImage("invite"));
		btnExit = new JButton(setImage("Exit"));
		btnSetting = new JButton(setImage("setting"));
		btnChatSend = new JButton(setImage("chat-icon"));
		btnImoticon = new JButton(setImage("Emoticon-icon"));

		tfChat = new JTextField(20);
		tfChat.setBorder(new LineBorder(color, 2));

		lblCurrnent = new JLabel("현재인원 : " + 1 + "명");
		lblCurrnent.setForeground(Color.white);

		combo = new JComboBox<String>(relative);
		combo.setBackground(new Color(73, 191, 255));
		combo.setForeground(Color.WHITE);
		combo.setPreferredSize(new Dimension(80, 23));

		setButton(btnChatSend);
		setButton(btnImoticon);
		setButton(btnSetting);
		setButton(btnInvite);
		setButton(btnExit);

		pMenu = new JPopupMenu();
		miHeadMandate = new JMenuItem("방장위임");
		miKickout = new JMenuItem("강퇴하기");
		miWhisper = new JMenuItem("귓속말 하기");

		btnChatSend.setPressedIcon(setImage("chat-icon2"));
		btnImoticon.setPressedIcon(setImage("Emoticon-icon2"));
		btnSetting.setPressedIcon(setImage("setting2"));
		btnInvite.setPressedIcon(setImage("invite2"));
		btnExit.setPressedIcon(setImage("Exit2"));

		Image img = Toolkit.getDefaultToolkit().getImage("이웃집채팅방.gif"); // 사이즈
																		// 조절

		scroll = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) { // 배경 이미지 적용
			{
				setOpaque(false);
			}

			public void paintComponent(Graphics g) {
				g.drawImage(img, 0, 0, this);
				super.paintComponent(g);
			}
		};
		;
		scroll.setPreferredSize(new Dimension(950, 800));
		scroll.getVerticalScrollBar().setUI(new PlayListScrollBarUI());
		scroll.setBorder(new LineBorder(color, 2));
		scroll.setOpaque(false); // 불투명 적용
		scroll.getViewport().setOpaque(false);

		pMenu.add(miHeadMandate);
		pMenu.add(miKickout);
		pMenu.add(miWhisper);

		vChattingUser = new Vector<PnlChatUser>();
		for (int i = 0; i < user.size(); i++) {
			vChattingUser.add(new PnlChatUser(user.get(i).isGender(), user.get(i).getNickName()));
		}
	}

	public void appendMyChatLabel(String str) {
		JLabel lblLeft;
		JLabel lblCenter;
		JLabel lblRight;

		if (str.length() < 40) {
			lblLeft = new JLabel(setImage2("Rigth2_text"));
			lblCenter = new JLabel(str);
			lblRight = new JLabel(setImage2("Left2_text"));

			lblCenter.setOpaque(true);
			lblCenter.setFont(new Font("MD개성체", Font.PLAIN, 23));

			lblCenter.setBackground(Color.YELLOW);

			textPane.insertComponent(lblLeft);
			textPane.insertComponent(lblCenter);
			textPane.insertComponent(lblRight);
		} else {
			appendMyChatLabelAll(str);
		}

	}

	public void appendMyChatLabelAll(String str) {
		int size = 500;
		int count = 0;
		for (int index : checkType(str)) {
			if ((index >= 'a' - 0 && index <= 'z' - 0) || (index >= 'A' - 0 && index <= 'Z' - 0)) {
				count++;
			}
		}
		if (count > checkType(str).length / 3) {
			size = 400;
		}

		int j = str.length() / 30;

		JLabel lblLeft;
		JLabel lblCenter;
		JLabel lblRight;

		StringBuffer sb = new StringBuffer("<html>" + str + "</html>");
		for (int i = 1; j + 1 > i; i++) {
			sb.insert(i * 30, "<br/>");
		}
		int h = 0;

		for (int k = 0; j > k; k++) {
			h = k * 25;
		}

		lblLeft = new JLabel(setImage3("Rigth2_text", 50 + h));
		lblCenter = new JLabel(sb.toString());
		lblCenter.setPreferredSize(new Dimension(0, 50 + h));
		lblRight = new JLabel(setImage3("Left2_text", 50 + h));

		lblCenter.setOpaque(true);
		lblCenter.setFont(new Font("MD개성체", Font.PLAIN, 20));
		lblCenter.setBackground(Color.YELLOW);

		lblCenter.setMaximumSize(new Dimension(size, 5000));
		textPane.insertComponent(lblLeft);
		textPane.insertComponent(lblCenter);
		textPane.insertComponent(lblRight);
	}

	private void setHeader() {
		for (PnlChatUser user : vChattingUser) {
			if (header.equals(user.toString())) {
				Image img = Toolkit.getDefaultToolkit().getImage("header.png"); // 사이즈
																				// 조절
				Image setImg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
				user.getLblHeader().setIcon(new ImageIcon(setImg));

			} else {
				user.getLblHeader().setIcon(null);
			}
			if (nickName.equals(user.toString())) {
				Image img = Toolkit.getDefaultToolkit().getImage("itsme.png"); // 사이즈
				// 조절
				Image setImg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
				user.getLblMy().setIcon(new ImageIcon(setImg));
			}
		}
		pnlUserList.repaint();
		pnlUserList.revalidate();
	}

	private void setJList() {
		pnlUserList.removeAll();

		userList = new DefaultListModel<>();
		for (PnlChatUser user : vChattingUser) {
			userList.addElement(user);
		}
		chattingUserList = new JList<>(userList);
		chattingUserList.setCellRenderer(new MyListCellRenderer());
		chattingUserList.setFixedCellWidth(300);
		chattingUserList.setVisibleRowCount(8);
		
		pnlUserList.add(new JScrollPane(chattingUserList));
		setHeader();
		pnlUserList.repaint();
		pnlUserList.revalidate();
		
		
		lblCurrnent.setText("현재 인원 : " + userList.size());
		mAddListener();
	}

	private void mAddListener() {
		chattingUserList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				check(me);
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				check(me);
			}
		});

	}

	private void check(MouseEvent me) {
		if (me.isPopupTrigger()) {
			chattingUserList.setSelectedIndex(chattingUserList.locationToIndex(me.getPoint()));
			mandateNickName = chattingUserList.getSelectedValue().toString();
			pMenu.show(chattingUserList, me.getX(), me.getY());
		}
	}

	class MyListCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList<?> list, Object value, int idx, boolean isSelected,
				boolean cellHasFocus) {
			PnlChatUser user = (PnlChatUser) value;
			if (isSelected) {
				user.setBackground(new Color(0xF6A7E4));
			} else {
				user.setBackground(Color.WHITE);
			}
			return user;
		};
	}

	private ImageIcon setImage3(String path, int size) { // big 말풍선 구성
		Image img = Toolkit.getDefaultToolkit().getImage("FrameUI/" + path + ".png"); // 사이즈
																						// 조절
		Image setImg = img.getScaledInstance(10, size, Image.SCALE_SMOOTH);

		return new ImageIcon(setImg);
	}

	public void chatShowing() {
		textPane = new JTextPane();
		textPane.setOpaque(false); // 불투명 적용
		textPane.setBackground(new Color(0, 0, 0, 0));
		textPane.setEditable(false);
		textPane.setFont(new Font("MD개성체", Font.PLAIN, 18)); // 글(상/하) 간격
	}

	public int[] checkType(String str) {
		int index = 0;
		int[] charIndex = new int[str.length()];

		for (int i = 0; i < str.length(); i++) {
			index = str.charAt(i);
			charIndex[i] = index;
		}

		return charIndex;
	}

	public void appendChatLabelAll(String str) {
		int size = 500;
		int count = 0;
		for (int index : checkType(str)) {
			if ((index >= 'a' - 0 && index <= 'z' - 0) || (index >= 'A' - 0 && index <= 'Z' - 0)) {
				count++;
			}
		}
		System.out.println(count + "test");
		if (count > checkType(str).length / 3) {
			size = 400;
		}

		int j = str.length() / 30;

		JLabel lblLeft;
		JLabel lblCenter;
		JLabel lblRight;

		StringBuffer sb = new StringBuffer("<html>" + str + "</html>");
		for (int i = 1; j + 1 > i; i++) {
			sb.insert(i * 30, "<br/>");
		}
		int h = 0;

		for (int k = 0; j > k; k++) {
			h = k * 25;
		}

		lblLeft = new JLabel(setImage3("Rigth_text", 50 + h));
		lblCenter = new JLabel(sb.toString());
		lblCenter.setPreferredSize(new Dimension(0, 50 + h));
		lblRight = new JLabel(setImage3("Left_text", 50 + h));

		lblCenter.setOpaque(true);
		lblCenter.setFont(new Font("MD개성체", Font.PLAIN, 20));
		lblCenter.setBackground(Color.WHITE);

		lblCenter.setMaximumSize(new Dimension(size, 5000));
		textPane.insertComponent(lblLeft);
		textPane.insertComponent(lblCenter);
		textPane.insertComponent(lblRight);
	}

	public void appendWhisperLabelAll(String str) {
		int j = str.length() / 30;

		JLabel lblLeft;
		JLabel lblCenter;
		JLabel lblRight;

		StringBuffer sb = new StringBuffer("<html>" + str + "</html>");
		for (int i = 1; j + 1 > i; i++) {
			sb.insert(i * 30, "<br/>");
		}
		int h = 0;

		for (int k = 0; j > k; k++) {
			h = k * 25;
		}
		lblLeft = new JLabel(setImage3("Rigth_text", 50 + h));
		lblCenter = new JLabel(sb.toString());
		lblCenter.setPreferredSize(new Dimension(0, 50 + h));
		lblRight = new JLabel(setImage3("Left_text", 50 + h));

		lblCenter.setOpaque(true);
		lblCenter.setFont(new Font("MD개성체", Font.PLAIN, 23));
		lblCenter.setBackground(Color.WHITE);
		lblCenter.setForeground(Color.GREEN);
		lblCenter.setMaximumSize(new Dimension(450, 10000));

		textPane.insertComponent(lblLeft);
		textPane.insertComponent(lblCenter);
		textPane.insertComponent(lblRight);
	}

	public void appendWhisperChatLabel(String str) {

		JLabel lblLeft;
		JLabel lblCenter;
		JLabel lblRight;

		System.out.println(str.length() < 30);
		System.out.println(str.length());

		if (str.length() < 40) {
			lblLeft = new JLabel(setImage2("Rigth_text"));
			lblCenter = new JLabel(str);
			lblRight = new JLabel(setImage2("Left_text"));

			lblCenter.setOpaque(true);
			lblCenter.setFont(new Font("MD개성체", Font.PLAIN, 23));
			lblCenter.setBackground(Color.WHITE);
			lblCenter.setForeground(Color.GREEN);

			textPane.insertComponent(lblLeft);
			textPane.insertComponent(lblCenter);
			textPane.insertComponent(lblRight);
		} else {
			appendWhisperLabelAll(str);
		}

	}

	public void appendChatLabel(String str) {
		JLabel lblLeft;
		JLabel lblCenter;
		JLabel lblRight;

		if (str.length() < 30) {
			lblLeft = new JLabel(setImage2("Rigth_text"));
			lblCenter = new JLabel(str);
			lblRight = new JLabel(setImage2("Left_text"));

			lblCenter.setOpaque(true);
			lblCenter.setFont(new Font("MD개성체", Font.PLAIN, 23));
			lblCenter.setBackground(Color.WHITE);

			textPane.insertComponent(lblLeft);
			textPane.insertComponent(lblCenter);
			textPane.insertComponent(lblRight);
		} else {
			appendChatLabelAll(str);
		}
	}

	public void appendImage(ImageIcon icon) {
		StyledDocument doc2 = (StyledDocument) textPane.getDocument();
		Style style2 = doc2.addStyle("StyleName", null);
		StyleConstants.setIcon(style2, icon);
		try {
			doc2.insertString(doc2.getLength(), "invisible text", style2);
			textPane.setCaretPosition(textPane.getDocument().getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	// JTextPane 문자열 추가
	public void appendString(String str) {
		StyledDocument document = (StyledDocument) textPane.getDocument();
		try {
			document.insertString(document.getLength(), str, null);
			textPane.setCaretPosition(textPane.getDocument().getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void setDisplay() {
		// 메인
		JPanel pnlMain = new JPanel(new BorderLayout());
		pnlMain.setBackground(Color.WHITE);
		pnlMain.setBorder(new EmptyBorder(10, 10, 10, 10));

		// 채팅창 화면(글씨 쓰는부분)
		JPanel pnlChat = new JPanel(new BorderLayout());

		// 방 제목 패널
		JPanel pnlTitle = new JPanel();
		pnlTitle.setOpaque(true);
		pnlTitle.setBackground(color);
		pnlTitle.add(lblTitle);

		JPanel pnlChatBottom = new JPanel(new FlowLayout());
		pnlChatBottom.setBackground(Color.WHITE);
		pnlChatBottom.add(combo);
		pnlChatBottom.add(tfChat);
		pnlChatBottom.add(btnImoticon);
		pnlChatBottom.add(btnChatSend);

		pnlChat.add(pnlTitle, BorderLayout.NORTH);
		pnlChat.add(scroll, BorderLayout.CENTER);
		pnlChat.add(pnlChatBottom, BorderLayout.SOUTH);

		// 방 멤버 리스트 화면(유저 정보등을 보여주는 화면)
		pnlList = new JPanel(new BorderLayout());

		JPanel pnlCurrent = new JPanel();
		pnlCurrent.setOpaque(true);
		pnlCurrent.add(lblCurrnent);
		pnlCurrent.setBackground(color);
		pnlUserList.setBorder(new LineBorder(color, 2));

		// 유저리스트 패널의 밑 부분
		JPanel pnlListBottom = new JPanel(new GridLayout(0, 1));
		// 방설정, 나가기 버튼 패털
		JPanel pnlButton = new JPanel(new GridLayout(1, 0));
		JPanel pnlSetting = new JPanel();
		pnlSetting.setBackground(Color.WHITE);
		pnlSetting.add(btnSetting);

		JPanel pnlExit = new JPanel();
		pnlExit.setBackground(Color.WHITE);
		pnlExit.add(btnExit);

		pnlButton.setBackground(Color.WHITE);
		pnlButton.add(btnInvite);
		pnlButton.add(pnlSetting);
		pnlButton.add(pnlExit);

		pnlListBottom.add(pnlButton);

		pnlList.add(pnlCurrent, BorderLayout.NORTH);
		pnlList.add(pnlUserList, BorderLayout.CENTER);
		pnlList.add(pnlListBottom, BorderLayout.SOUTH);

		// 메인 패널에 붙이기
		pnlMain.add(pnlChat, BorderLayout.WEST);
		pnlMain.add(pnlList, BorderLayout.EAST);

		add(pnlMain, BorderLayout.CENTER);
	}

	private void addListener() {
		btnInvite.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					oos.writeObject(new SendData(OperationCodeClient.VIEW_ALL_USER));
					oos.flush();
					oos.reset();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					oos.writeObject(new SendData(OperationCodeClient.ROOM_EXIT, myId, room.getNumberRoom()));
					oos.flush();
					oos.reset();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnSetting.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setting = new RoomSetting(sock, oos, ois, ChatRoomForm.this, room.getNumberRoom(),
						room.getCurrentNum());
			}
		});

		btnChatSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (combo.getSelectedIndex() == 0) {
					try {
						boolean flag = true;
						if (tfChat.getText().length() >= 100) {
							JOptionPane.showMessageDialog(ChatRoomForm.this, "100자 이하로 입력해주세요 \n(도배방지)");
							flag = false;
							tfChat.setText("");
						}
						if (flag) {
							oos.writeObject(new SendData(OperationCodeClient.ROOM_CHAT, tfChat.getText(),
									room.getNumberRoom(), myId));
							oos.flush();
							oos.reset();
							tfChat.setText("");
							tfChat.requestFocus();
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else if (combo.getSelectedIndex() == 1) {
					try {
						oos.writeObject(new SendData(OperationCodeClient.WHISPER_CHAT, tfChat.getText(), myId));
						oos.flush();
						oos.reset();
						tfChat.setText("");
						tfChat.requestFocus();
					} catch (Exception e1) {
					}
				}
			}
		});

		tfChat.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent ke) {

				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
						if (combo.getSelectedIndex() == 0) {
							try {
								boolean flag = true;
								if (tfChat.getText().length() >= 100) {
									JOptionPane.showMessageDialog(ChatRoomForm.this, "100자 이하로 입력해주세요 \n(도배방지)");
									flag = false;
									tfChat.setText("");
								}
								if (flag) {
									oos.writeObject(new SendData(OperationCodeClient.ROOM_CHAT, tfChat.getText(),
											room.getNumberRoom(), myId));
									oos.flush();
									oos.reset();
									tfChat.setText("");
									tfChat.requestFocus();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (combo.getSelectedIndex() == 1) {
							try {
								oos.writeObject(new SendData(OperationCodeClient.WHISPER_CHAT, tfChat.getText(), myId));
								oos.flush();
								oos.reset();
								tfChat.setText("");
								tfChat.requestFocus();
							} catch (Exception e) {
							}
						}
					}
				}
		});

		btnImoticon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ShowImoticon(ChatRoomForm.this, sock, oos, ois, room.getNumberRoom(), myId);
			}
		});

		ActionListener listener = (ae) -> {
			Object src = ae.getSource();
			if (src == miHeadMandate) {
				boolean flag = true;
				try {
					if (header.equals(mandateNickName)) {
						JOptionPane.showMessageDialog(ChatRoomForm.this, "자기 자신을 위임할 수 없습니다.");
						flag = false;
					}
					if (flag) {
						oos.writeObject(
								new SendData(OperationCodeClient.HEAD_MANDATE, mandateNickName, room.getNumberRoom()));
						oos.flush();
						oos.reset();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else if (src == miKickout) {
				boolean flag = true;
				try {
					if (header.equals(mandateNickName)) {
						JOptionPane.showMessageDialog(ChatRoomForm.this, "자기 자신을  강퇴할 수 없습니다.");
						flag = false;
					}
					if (flag) {
						System.out.println(mandateNickName);
						oos.writeObject(

								new SendData(OperationCodeClient.KICK_OUT, mandateNickName, room.getNumberRoom()));
						oos.flush();
						oos.reset();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else {
				combo.setSelectedIndex(1);
				tfChat.setText("/to " + mandateNickName);
				tfChat.requestFocus();
			}
		};

		miHeadMandate.addActionListener(listener);
		miKickout.addActionListener(listener);
		miWhisper.addActionListener(listener);

		ItemListener aListener = (ie) -> {
			if (ie.getItem().equals("전체")) {
				tfChat.setText("");
			} else {
				tfChat.setText("/to ");
			}
		};
		combo.addItemListener(aListener);
	}

	private void showFrame() {
		setTitle("ChatRoom");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(1320, 900);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
