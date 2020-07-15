package kr.ac.green;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
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
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

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
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Queue extends JFrame {
	private Color color = new Color(0x0B6DB7);
	// ��⿭ ���� ����
	private JLabel lblSearch;
	private JTextField tfSearch;
	private JButton btnSearch;
	private JLabel lblChatRoom; // ä�ù� ����Ʈ ��ġ����
	private JLabel lblPage;
	private JButton btnMakingRoom;

	// ��⿭ �߰� ����
	private JLabel lblTopBar;
	private JTextArea taChatShow;
	private JScrollPane scroll;
	private JComboBox cbChatSet;
	private JTextPane tpChatShowing;

	private String[] cbSet = { "��ü", "�ӼӸ�" };

	private JTextField tfChatting;
	private JButton btnEmoticon;
	private JButton btnSend;

	// ��⿭ ���� ����
	private JLabel lblCurrent;
	private JLabel lblWaitingUser; // ���� ����Ʈ ��ġ����
	private JButton btnWithdrawal;
	private JButton btnExit;

	// ä�ù� JPanel ����Ʈ
	private JLabel lblRoomList;
	private Vector<PnlChatRoom> vRoom;
	private JList<PnlChatRoom> chatRoomList;
	private DefaultListModel<PnlChatRoom> roomListModel = new DefaultListModel<>();

	// ��⿭ ���� JPanel ����Ʈ
	private Vector<PnlWaitingUser> vWaitingUser;
	private JList<PnlWaitingUser> waitingUserList;
	private DefaultListModel<PnlWaitingUser> userListModel = new DefaultListModel<>();

	// ���콺 �˾� �޴�
	private JPopupMenu pm = new JPopupMenu();
	private JMenuItem whisper = new JMenuItem("�ӼӸ�");
	// ä�ù� ����
	private JTextPane textPane;

	private Socket sock;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Vector<UserInfo> waitingRoomList;
	private Vector<RoomInfo> chatRoomMap;
	private Thread updateThread;
	private JPanel pnlRightCenter;
	private String myId;

	private JPanel pnlRight;

	private ChatRoomMaker crm;

	private String id;

	private JLabel after;
	private JLabel one;
	private JLabel two;
	private JLabel three;
	private JLabel four;
	private JLabel five;

	private CardLayout roomCard;
	private Vector<JPanel> roomList;
	private int componentCount = 0;
	private String nickName;
	
	private ExecutorService excutorService;
	public Queue(Socket sock, ObjectOutputStream oos, ObjectInputStream ois, Vector<UserInfo> waitingRoomList,
			Vector<RoomInfo> chatRoomList, String myId, String nickName) {
		this.sock = sock;
		this.oos = oos;
		this.ois = ois;
		this.waitingRoomList = waitingRoomList;
		this.chatRoomMap = chatRoomList;
		this.myId = myId;
		this.nickName = nickName;
		chatShowing();
		init();
		setDisplay();
		addListener();
		chatting();
		updateThread = new UpdateThread();
		updateThread.start();
		showFrame();
	}

	private void init() {
		// ���콺 �˾�
		pm.add(whisper);

		// ä�ù� ����Ʈ �ʱⰪ
		vRoom = new Vector<PnlChatRoom>();
		ImageIcon image = new ImageIcon("RoomList.png");
		lblRoomList = new JLabel(image);
		lblRoomList.setHorizontalAlignment(SwingConstants.LEFT);
		for (int i = 0; i < chatRoomMap.size(); i++) {
			vRoom.add(new PnlChatRoom(chatRoomMap.get(i).getPassword(), chatRoomMap.get(i).getNumberRoom() + "",
					chatRoomMap.get(i).getTitle(), chatRoomMap.get(i).getSubject(),
					chatRoomMap.get(i).getCurrentNum() + "/" + chatRoomMap.get(i).getMaxNumbers() + "��"));
		}
		// ���� ���� �ʱⰪ
		vWaitingUser = new Vector<PnlWaitingUser>();
		for (int i = 0; i < waitingRoomList.size(); i++) {
			vWaitingUser
					.add(new PnlWaitingUser(waitingRoomList.get(i).isGender(), waitingRoomList.get(i).getNickName()));
		}

		// ��⿭ ���� ����
		lblSearch = new JLabel(setImage("GagaoTalk2"));
		tfSearch = new JTextField(20);
		btnSearch = new JButton(setImage("search"));
		setButton(btnSearch);
		btnSearch.setPressedIcon(setImage("search2"));
		one = new JLabel("1");
		two = new JLabel("2");
		three = new JLabel("3");
		four = new JLabel("4");
		five = new JLabel("5");
		btnMakingRoom = new JButton(setImage("home"));
		setButton(btnMakingRoom);
		btnMakingRoom.setPressedIcon(setImage("home2"));

		// ��⿭ �߰� ����
		lblTopBar = new JLabel();
		taChatShow = new JTextArea(30, 30);
		tpChatShowing = new JTextPane();
		cbChatSet = new JComboBox<>(cbSet);
		cbChatSet.setBackground(color.WHITE);
		cbChatSet.setForeground(color);
		tfChatting = new JTextField(20);
		btnEmoticon = new JButton(setImage("Emoticon-icon"));
		setButton(btnEmoticon);
		btnEmoticon.setPressedIcon(setImage("Emoticon-icon2"));
		btnSend = new JButton(setImage("chat-icon"));
		setButton(btnSend);
		btnSend.setPressedIcon(setImage("chat-icon2"));
		pnlRightCenter = new JPanel();

		// ��⿭ ���� ����
		lblCurrent = new JLabel("�����ο� : " + vWaitingUser.size());
		lblCurrent.setForeground(Color.white);
		btnWithdrawal = new JButton(setImage4("withdraw"));
		setButton2(btnWithdrawal);
		btnWithdrawal.setPressedIcon(setImage4("withdraw2"));
		btnExit = new JButton(setImage("Exit"));
		setButton(btnExit);
		btnExit.setPressedIcon(setImage("Exit2"));
		pnlRight = new JPanel(new BorderLayout());
		// ��⿭ ���� ����Ʈ ����
		setJList();
		// ä�ù� ���� ����Ʈ
		setJList2();
	}

	private JList<PnlChatRoom> chatRoomList2;
	private JList<PnlChatRoom> chatRoomList3;
	private JList<PnlChatRoom> chatRoomList4;
	private JList<PnlChatRoom> chatRoomList5;

	private void setMy() {
		for (PnlWaitingUser user : vWaitingUser) {
			if (nickName.equals(user.toString())) {
				Image img = Toolkit.getDefaultToolkit().getImage("itsme.png"); // ������
																				// ����
				Image setImg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
				user.getLblMy().setIcon(new ImageIcon(setImg));
			} else {
				user.getLblMy().setIcon(null);
			}
		}
		pnlRightCenter.repaint();
		pnlRightCenter.revalidate();
	}

	private void setJList2() {
		DefaultListModel<PnlChatRoom> roomListModel2 = new DefaultListModel<>();
		DefaultListModel<PnlChatRoom> roomListModel3 = new DefaultListModel<>();
		DefaultListModel<PnlChatRoom> roomListModel4 = new DefaultListModel<>();
		DefaultListModel<PnlChatRoom> roomListModel5 = new DefaultListModel<>();

		one.setFont(new Font("MD����ü", Font.PLAIN, 23));
		two.setFont(new Font("MD����ü", Font.PLAIN, 23));
		three.setFont(new Font("MD����ü", Font.PLAIN, 23));
		four.setFont(new Font("MD����ü", Font.PLAIN, 23));
		five.setFont(new Font("MD����ü", Font.PLAIN, 23));
		
		two.setForeground(Color.GRAY);
		three.setForeground(Color.GRAY);
		four.setForeground(Color.GRAY);
		five.setForeground(Color.GRAY);
		
		pnlLeftCenter.removeAll();
		roomList = new Vector<>();
		roomCard = new CardLayout();
		pnlLeftCenter.setLayout(roomCard);
		int amount = 0;
		roomListModel = new DefaultListModel<>();
		for (PnlChatRoom room : vRoom) {
			amount++;
			if (amount <= 7) {
				roomListModel.addElement(room);
			} else if (amount >= 8 && amount <= 14) {
				roomListModel2.addElement(room);
			} else if (amount >= 15 && amount <= 21) {
				roomListModel3.addElement(room);
			} else if (amount >= 22 && amount <= 28) {
				roomListModel4.addElement(room);
			} else if (amount >= 29 && amount <= 35) {
				roomListModel5.addElement(room);
			}
		}
		chatRoomList = new JList<>(roomListModel);
		chatRoomList.setCellRenderer(new RoomListCellRenderer());
		pnlLeftCenter.add("First", chatRoomList);
		one.setFont(new Font("MD����ü", Font.BOLD, 26));
		chatRoomList.setFixedCellWidth(400);

		enterAddListener();
		if (roomListModel2.size() != 0) {
			chatRoomList2 = new JList<>(roomListModel2);
			chatRoomList2.setCellRenderer(new RoomListCellRenderer());
			pnlLeftCenter.add("Second", chatRoomList2);
			two.setFont(new Font("MD����ü", Font.BOLD, 26));
			two.setForeground(Color.BLACK);
			chatRoomList2.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {
					Object src = me.getSource();
					enterRoom2(src);
				}
			});
		}
		if (roomListModel3.size() != 0) {
			chatRoomList3 = new JList<>(roomListModel3);
			chatRoomList3.setCellRenderer(new RoomListCellRenderer());
			pnlLeftCenter.add("Third", chatRoomList3);
			three.setFont(new Font("MD����ü", Font.BOLD, 26));
			three.setForeground(Color.BLACK);
			chatRoomList3.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {
					Object src = me.getSource();
					enterRoom3(src);
				}
			});
		}
		if (roomListModel4.size() != 0) {
			chatRoomList4 = new JList<>(roomListModel4);
			chatRoomList4.setCellRenderer(new RoomListCellRenderer());
			pnlLeftCenter.add("Fourth", chatRoomList4);
			four.setFont(new Font("MD����ü", Font.BOLD, 26));
			four.setForeground(Color.BLACK);
			chatRoomList4.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {
					Object src = me.getSource();
					enterRoom4(src);
				}
			});
		}
		if (roomListModel5.size() != 0) {
			chatRoomList5 = new JList<>(roomListModel5);
			chatRoomList5.setCellRenderer(new RoomListCellRenderer());
			pnlLeftCenter.add("Fifth", chatRoomList5);
			five.setFont(new Font("MD����ü", Font.BOLD, 26));
			five.setForeground(Color.BLACK);
			chatRoomList5.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {
					Object src = me.getSource();
					enterRoom5(src);
				}
			});
		}
		System.out.println("�������� ������ : " + count);
		if (count == 1) {
			roomCard.show(pnlLeftCenter, "First");
		} else if (count == 2) {
			roomCard.show(pnlLeftCenter, "Second");
		} else if (count == 3) {
			roomCard.show(pnlLeftCenter, "Thirth");
		} else if (count == 4) {
			roomCard.show(pnlLeftCenter, "Fourth");
		} else if (count == 5) {
			roomCard.show(pnlLeftCenter, "Fifth");
		}
		pnlLeftCenter.revalidate();
		pnlLeftCenter.repaint();
		pnlLeftCenter.setBorder(new LineBorder(color));
		pnlLeftCenter.setPreferredSize(new Dimension(400, 530));
	}

	private void setJList() {
		pnlRightCenter.removeAll();

		userListModel = new DefaultListModel<>();
		for (PnlWaitingUser user : vWaitingUser) {
			userListModel.addElement(user);
		}
		waitingUserList = new JList(userListModel);
		waitingUserList.setCellRenderer(new MyListCellRenderer());
		pnlRightCenter.add(new JScrollPane(waitingUserList));
		waitingUserList.setFixedCellWidth(300);
		waitingUserList.setVisibleRowCount(8);
		
		pnlRightCenter.setPreferredSize(new Dimension(330, 580));
		pnlRightCenter.setBackground(Color.WHITE);
		lblCurrent.setText("�����ο� : " + vWaitingUser.size());
		pnlRightCenter.revalidate();
		pnlRightCenter.repaint();
		mAddListener();
		setMy();
	}

	// ä��â ����
	public void chatShowing() {
		textPane = new JTextPane();
		textPane.setOpaque(false);
		textPane.setBackground(new Color(0, 0, 0, 0));
		textPane.setEditable(false);
		textPane.setFont(new Font("MD����ü", Font.PLAIN, 18));
	}

	// JTextPane ���ڿ� �߰�
	public void appendString(String str) {
		StyledDocument document = (StyledDocument) textPane.getDocument();
		try {
			document.insertString(document.getLength(), str, null);
			textPane.setCaretPosition(textPane.getDocument().getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	// JTextPane �̸�Ƽ�� �߰�
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

	// vWaitingUser<Vector> -> JList ǥ��
	class MyListCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList<?> list, Object value, int idx, boolean isSelected,
				boolean cellHasFocus) {
			PnlWaitingUser user = (PnlWaitingUser) value;
			if (isSelected) {
				user.setBackground(new Color(0xF6A7E4));
			} else {
				user.setBackground(Color.WHITE);
			}
			return user;
		};
	}

	private ImageIcon setImage(String path) {
		Image img = Toolkit.getDefaultToolkit().getImage("FrameUI/" + path + ".png"); // ������
		Image setImg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);

		return new ImageIcon(setImg);
	}

	private ImageIcon setImage3(String path, int size) { // big ��ǳ�� ����
		Image img = Toolkit.getDefaultToolkit().getImage("FrameUI/" + path + ".png"); // ������
																						// ����
		Image setImg = img.getScaledInstance(10, size, Image.SCALE_SMOOTH);

		return new ImageIcon(setImg);
	}

	private void setButton(JButton btn) {
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(false);
		btn.setFocusPainted(false);
		btn.setOpaque(false);
		btn.setPreferredSize(new Dimension(40, 50));
	}

	private ImageIcon setImage2(String path) {
		Image img = Toolkit.getDefaultToolkit().getImage("FrameUI/" + path + ".png"); // ������
		// ����
		Image setImg = img.getScaledInstance(10, 25, Image.SCALE_SMOOTH);
		return new ImageIcon(setImg);
	}

	private ImageIcon setImage4(String path) {
		Image img = Toolkit.getDefaultToolkit().getImage("FrameUI/" + path + ".png"); // ������
		// ����
		Image setImg = img.getScaledInstance(100, 30, Image.SCALE_SMOOTH);
		return new ImageIcon(setImg);
	}

	private void setButton2(JButton btn) {
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(false);
		btn.setFocusPainted(false);
		btn.setOpaque(false);
	}

	// vRoom<Vector> -> JList ǥ��
	class RoomListCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList<?> list, Object value, int idx, boolean isSelected,
				boolean cellHasFocus) {

			PnlChatRoom room = (PnlChatRoom) value;
			if (isSelected) {
				room.setBackground(new Color(0xF6A7E4));
			} else {
				room.setBackground(Color.WHITE);
			}
			return room;
		};

	}

	private JPanel pnlLeftCenter = new JPanel();

	class UpdateThread extends Thread {
		@Override
		public void run() {
			try {
				Object obj = null;
				while ((obj = ois.readObject()) != null) {
					SendData readData = (SendData) obj;
					if (readData.getCode() == OperationCodeServer.WAITINGROOM_CHAT_OK) { // ä�ü���
						String msg = (String) readData.getData()[0];
						String mynickName = (String) readData.getData()[1];
						if (nickName.equals(mynickName)) {
							appendString("[ ��  ]\n");
							appendMyChatLabel(msg);
							appendString("\n");
						} else {
							appendString("[ " + mynickName + " ]\n");
							appendChatLabel(msg);
							appendString("\n");
						}
					} else if (readData.getCode() == OperationCodeServer.WAITINGROOM_EMOT_CHAT_OK) { // �̸�Ƽ�ܼ���
						ImageIcon emoticon = (ImageIcon) readData.getData()[0];
						String mynickName = (String) readData.getData()[1];
						if (nickName.equals(mynickName)) {
							appendString("[ ��  ]\n");
							appendImage(emoticon);
							appendString("\n");
						} else {
							appendString("[" + mynickName + "]\n");
							appendImage(emoticon);
							appendString("\n");
						}
					} else if (readData.getCode() == OperationCodeServer.WAITINGROOM_EMOT_CHAT_MOVE_OK) { // �����̴�
																											// �̸�Ƽ��
						String path = (String) readData.getData()[0];
						String mynickName = (String) readData.getData()[1];
						ImageIcon emoticon = new ImageIcon(path);
						if (nickName.equals(mynickName)) {
							appendString("[ ��  ]\n");
							appendImage(emoticon);
							appendString("\n");
						} else {
							appendString("[" + mynickName + "]\n");
							appendImage(emoticon);
							appendString("\n");
						}
					} else if (readData.getCode() == OperationCodeServer.WHISPER_CHAT_OK) { // �ӼӸ�
						String msg = (String) readData.getData()[0];
						String chat = (String) readData.getData()[1];
						appendString(msg + "\n");
						appendWhisperChatLabel(chat);
						appendString("\n");
						tfChatting.requestFocus();
					} else if (readData.getCode() == OperationCodeServer.WHISPER_CHAT_FAIL) { // �ӼӸ�
																								// ����
						String msg = (String) readData.getData()[0];
						appendWhisperChatLabel(msg);
						appendString("\n");
						tfChatting.requestFocus();
					} else if (readData.getCode() == OperationCodeServer.WHISPER_CHAT_FAIL2) { // �ӼӸ�
																								// ����
						String msg = (String) readData.getData()[0];
						appendWhisperChatLabel(msg);
						appendString("\n");
						tfChatting.requestFocus();
					} else if (readData.getCode() == OperationCodeServer.WHISPER_CHAT_SUCCESS) { // �ӼӸ�
						// �۽ż��� �޽���
						String msg = (String) readData.getData()[0];
						String chat = (String) readData.getData()[1];
						appendString(msg + "\n");
						appendWhisperChatLabel(chat);
						appendString("\n");
					} else if (readData.getCode() == OperationCodeServer.WITHDRAWAL_FAIL) { // ȸ��Ż��
																							// ����
						JOptionPane.showMessageDialog(Queue.this, "��й�ȣ�� Ʋ�� ȸ��Ż�� �� ���� �����ϴ�.");
					} else if (readData.getCode() == OperationCodeServer.WITHDRAWAL_OK) { // ȸ��Ż��
																							// ����
						JOptionPane.showMessageDialog(Queue.this, "ȸ��Ż�� �����Ͽ����ϴ�");
						dispose();
						new LoginForm();
					} else if (readData.getCode() == OperationCodeServer.ALL_WAITING_ROOM_UPDATE) { // ����
																									// ����
																									// ������Ʈ
						Vector<UserInfo> userList = (Vector<UserInfo>) readData.getData()[0];
						RoomInfo rooms = (RoomInfo) readData.getData()[1];
						vWaitingUser = new Vector<>();

						if (vRoom.size() == 0) {
							vRoom.add(new PnlChatRoom(rooms.getPassword(), rooms.getNumberRoom() + "", rooms.getTitle(),
									rooms.getSubject(), rooms.getCurrentNum() + "/" + rooms.getMaxNumbers() + "��"));
							setJList2();
						} else if (rooms.getCurrentNum() == 0) {
							for (int i = 0; i < vRoom.size(); i++) {
								if (Integer.parseInt(vRoom.get(i).getRoomNum()) == rooms.getNumberRoom()) {
									vRoom.remove(i);
									setJList2();
								}
							}
						} else {
							boolean flag = true;
							for (int i = 0; i < vRoom.size(); i++) {
								if (Integer.parseInt(vRoom.get(i).getRoomNum()) == rooms.getNumberRoom()) {
									vRoom.get(i).setPw(rooms.getPassword());
									vRoom.get(i).setSubject(rooms.getSubject());
									vRoom.get(i).setCurrentNum(rooms.getCurrentNum());
									vRoom.get(i).setMaxNum(rooms.getMaxNumbers());
									vRoom.get(i).setLblTitle(rooms.getTitle());
									vRoom.get(i).setLblPw(rooms.getPassword());
									flag = false;
								}
							}
							if (flag) {
								vRoom.add(new PnlChatRoom(rooms.getPassword(), rooms.getNumberRoom() + "",
										rooms.getTitle(), rooms.getSubject(),
										rooms.getCurrentNum() + "/" + rooms.getMaxNumbers() + "��"));
								setJList2();
							}
						}
						pnlLeftCenter.revalidate();
						pnlLeftCenter.repaint();
						for (UserInfo users : userList) {
							vWaitingUser.add(new PnlWaitingUser(users.isGender(), users.getNickName()));
						}
						setJList();
						// ���� ������ ������Ʈ
					} else if (readData.getCode() == OperationCodeServer.WAITING_USER_UPDATE) {
						Vector<UserInfo> userList = (Vector<UserInfo>) readData.getData()[0];
						vWaitingUser = new Vector<>();
						System.out.println(vWaitingUser);
						for (UserInfo users : userList) {
							vWaitingUser.add(new PnlWaitingUser(users.isGender(), users.getNickName()));
						}
						setJList();

						// �游��� ����
					} else if (readData.getCode() == OperationCodeServer.CREATE_FAIL) {
						JOptionPane.showMessageDialog(crm, "������ �ߺ��Դϴ�");

						// �游��� ����
					} else if (readData.getCode() == OperationCodeServer.CHATTING_USER_UPDATE) {
						Vector<UserInfo> chatList = (Vector<UserInfo>) readData.getData()[0];
						RoomInfo room = (RoomInfo) readData.getData()[1];
						dispose();
						new ChatRoomForm(sock, oos, ois, chatList, room, myId, nickName);
						break;

						// �� ����
					} else if (readData.getCode() == OperationCodeServer.ROOM_OK) {
						Vector<UserInfo> chatList = (Vector<UserInfo>) readData.getData()[0];
						RoomInfo rooms = (RoomInfo) readData.getData()[1];
						String header = (String) readData.getData()[2];
						dispose();
						new ChatRoomForm(sock, oos, ois, chatList, rooms, myId, header, nickName);
						break;
						// �ο�����
					} else if (readData.getCode() == OperationCodeServer.ROOM_FAIL) {
						JOptionPane.showMessageDialog(Queue.this, "�ο��� ������ ������ �� �����ϴ�");

						// ���ǿ� ä�ù� ����Ʈ ����
					} else if (readData.getCode() == OperationCodeServer.CHATTING_LIST_UPDATE) {
						RoomInfo rooms = (RoomInfo) readData.getData()[0];
						Iterator<PnlChatRoom> iter = vRoom.iterator();
						while (iter.hasNext()) {
							PnlChatRoom root = iter.next();
							if (Integer.parseInt(root.getRoomNum()) == rooms.getNumberRoom()) {
								root.setPw(rooms.getPassword());
								root.setSubject(rooms.getSubject());
								root.setMaxNum(rooms.getMaxNumbers());
								root.setLblTitle(rooms.getTitle());
								root.setLblPw(rooms.getPassword());
							}
						}
						pnlLeftCenter.repaint();
						pnlLeftCenter.revalidate();
						// �ʴ� �����˸�
					} else if (readData.getCode() == OperationCodeServer.INVITE_CHAT_OK) {
						String id = (String) readData.getData()[0];
						int roomNum = (int) readData.getData()[1];
						UserInfo inviteInfo = (UserInfo) readData.getData()[2];
						int result = JOptionPane.showConfirmDialog(Queue.this, id + "���� �ʴ��Ͽ����ϴ�. �ʴ������ �Ͻðڽ��ϱ�?", "�ʴ��ϱ�",
								JOptionPane.YES_NO_OPTION);
						if (result == JOptionPane.YES_OPTION) {
							oos.writeObject(new SendData(OperationCodeClient.INVITE_RESPONSE_YES, roomNum, myId, inviteInfo));
							oos.flush();
							oos.reset();
						} else {
							oos.writeObject(new SendData(OperationCodeClient.INVITE_RESPONSE_NO, id, roomNum, myId, inviteInfo));
						}

						// �ʴ� ����
					} else if (readData.getCode() == OperationCodeServer.INVITE_UPDATE) {
						Vector<UserInfo> chatList = (Vector<UserInfo>) readData.getData()[0];
						RoomInfo rooms = (RoomInfo) readData.getData()[1];
						String header = (String) readData.getData()[2];
						dispose();
						new ChatRoomForm(sock, oos, ois, chatList, rooms, myId, header, nickName);
						break;

						// �ο��� ������ ���� �Ҽ�����
					} else if (readData.getCode() == OperationCodeServer.INVITE_CHAT_FAIL) {
						JOptionPane.showMessageDialog(Queue.this, "�ʴ��� �濡 �ο��� ������ ������ �� �����ϴ�.");
						
						// �˻����
					} else if (readData.getCode() == OperationCodeServer.SEARCH_RESULT) {
						RoomInfo room = (RoomInfo) readData.getData()[0];
						int result = JOptionPane.showConfirmDialog(Queue.this, "�˻��� ��� ã�¹��� �ֽ��ϴ�. ���ðڽ��ϱ�?", "ä�ù� �˻�",
								JOptionPane.YES_NO_OPTION);
						if (result == JOptionPane.YES_OPTION) {
							if (room.getPassword().equals("")) {
								oos.writeObject(new SendData(OperationCodeClient.SEARCH_RESPONSE_YES, room, myId));
								oos.flush();
								oos.reset();
							} else {
								String input = JOptionPane.showInputDialog(Queue.this, "��й�ȣ�� �Է��ϼ���");
								boolean flag = true;
								if (input == null || !(input.equals(room.getPassword()))) {
									JOptionPane.showMessageDialog(Queue.this, "��й�ȣ�� Ʋ�Ƚ��ϴ�");
									flag = false;
								}
								if (flag) {
									oos.writeObject(
											new SendData(OperationCodeClient.SEARCH_RESPONSE_YES, room, myId, input));
									oos.flush();
									oos.reset();
								}
							}
						}
						// �˻���� ����
					} else if (readData.getCode() == OperationCodeServer.SEARCH_RESULT_FAIL) {
						JOptionPane.showMessageDialog(Queue.this, "�˻��� ��� ã�¹��� �����ϴ�.");
					}
				}

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
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
		lblCenter.setFont(new Font("MD����ü", Font.PLAIN, 20));
		lblCenter.setBackground(Color.WHITE);

		lblCenter.setMaximumSize(new Dimension(size, 5000));
		textPane.insertComponent(lblLeft);
		textPane.insertComponent(lblCenter);
		textPane.insertComponent(lblRight);
	}

	public void appendWhisperLabelAll(String str) {
		int j = str.length() / 40;

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
		lblCenter.setFont(new Font("MD����ü", Font.PLAIN, 20));
		lblCenter.setBackground(Color.WHITE);
		lblCenter.setForeground(Color.GREEN);
		lblCenter.setMaximumSize(new Dimension(450, 10000));

		textPane.insertComponent(lblLeft);
		textPane.insertComponent(lblCenter);
		textPane.insertComponent(lblRight);
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
			lblCenter.setFont(new Font("MD����ü", Font.PLAIN, 23));

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
		lblCenter.setFont(new Font("MD����ü", Font.PLAIN, 20));
		lblCenter.setBackground(Color.YELLOW);

		lblCenter.setMaximumSize(new Dimension(size, 5000));
		textPane.insertComponent(lblLeft);
		textPane.insertComponent(lblCenter);
		textPane.insertComponent(lblRight);
	}

	public void appendWhisperChatLabel(String str) {

		JLabel lblLeft;
		JLabel lblCenter;
		JLabel lblRight;

		if (str.length() < 30) {
			lblLeft = new JLabel(setImage2("Rigth_text"));
			lblCenter = new JLabel(str);
			lblRight = new JLabel(setImage2("Left_text"));

			lblCenter.setOpaque(true);
			lblCenter.setFont(new Font("MD����ü", Font.PLAIN, 23));
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
			lblCenter.setFont(new Font("MD����ü", Font.PLAIN, 23));
			lblCenter.setBackground(Color.WHITE);

			textPane.insertComponent(lblLeft);
			textPane.insertComponent(lblCenter);
			textPane.insertComponent(lblRight);
		} else {
			appendChatLabelAll(str);
		}
	}

	private void setDisplay() {
		JPanel pnlMain = new JPanel();
		pnlMain.setBorder(new LineBorder(color));
		pnlMain.setBackground(Color.WHITE);
		// Ŭ���� ���̾ƿ� ����
		pnlMain.setLayout(new FlowLayout());

		// ��⿭ ���� �г� ����
		JPanel pnlLeft = new JPanel(new BorderLayout());
		pnlLeft.setBackground(color.WHITE);

		// ���� ��
		JPanel pnlLeftTop = new JPanel();
		pnlLeftTop.setBackground(color.WHITE);
		// ���� ��� �˻�����г�
		JPanel pnlLeftTopSearch = new JPanel();
		pnlLeftTopSearch.setBorder(new LineBorder(color));
		pnlLeftTopSearch.setBackground(Color.WHITE);
		pnlLeftTopSearch.add(lblSearch);
		pnlLeftTopSearch.add(tfSearch);
		tfSearch.setText("ä�ù� ������ �Է��ϼ���");
		pnlLeftTopSearch.add(btnSearch);
		// ���� ��� ä�ù��� ���ȣ/�������/������/����/�����ο�/�ִ��ο� �г�
		JPanel pnlLeftTopLbl = new JPanel();
		pnlLeftTopLbl.setBackground(color.WHITE);
		pnlLeftTopLbl.add(lblRoomList);

		// ������� �гε� ��ġ�� �г�
		JPanel pnlLeftTopPnl = new JPanel(new GridLayout(0, 1));
		pnlLeftTopPnl.setBackground(color.WHITE);
		pnlLeftTopPnl.add(pnlLeftTopSearch);
		pnlLeftTopPnl.add(pnlLeftTopLbl);
		pnlLeftTop.add(pnlLeftTopPnl);

		pnlLeft.add(pnlLeftTop, BorderLayout.NORTH);
		// ���� ��

		pnlLeftCenter.setBorder(new LineBorder(color));
		pnlLeftCenter.setPreferredSize(new Dimension(400, 530));
		// �� ����Ʈ ����
		// pnlLeftCenter.add(chatRoomList);

		pnlLeft.add(pnlLeftCenter, BorderLayout.CENTER);
		// ���� ��
		JPanel pnlLeftBottom = new JPanel();
		pnlLeftBottom.setBackground(color.white);

		JPanel pnlLeftBottem1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 7));
		pnlLeftBottem1.setBackground(Color.white);
		pnlLeftBottem1.setBorder(new LineBorder(color, 2));
		pnlLeftBottem1.setPreferredSize(new Dimension(360, 40));
		JPanel pnlLeftBottem2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
		pnlLeftBottem2.setBackground(Color.WHITE);
		pnlLeftBottem1.add(one);
		pnlLeftBottem1.add(two);
		pnlLeftBottem1.add(three);
		pnlLeftBottem1.add(four);
		pnlLeftBottem1.add(five);

		pnlLeftBottem2.add(btnMakingRoom);

		pnlLeftBottom.add(pnlLeftBottem1);
		pnlLeftBottom.add(pnlLeftBottem2);

		pnlLeft.add(pnlLeftBottom, BorderLayout.SOUTH);

		// ��⿭ �߰� �г� ����
		JPanel pnlCenter = new JPanel(new BorderLayout());
		pnlCenter.setBackground(Color.WHITE);
		// �߰� ��
		pnlCenter.add(lblTopBar, BorderLayout.NORTH);
		// �߰� ��
		Image img = Toolkit.getDefaultToolkit().getImage("�̿���.gif"); // ������
		// ����
		Image setImg = img.getScaledInstance(700, 800, Image.SCALE_SMOOTH);
		scroll = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {
			{
				setOpaque(false);
			}

			public void paintComponent(Graphics g) {
				g.drawImage(img, 0, 0, this);
				super.paintComponent(g);
			}
		};

		scroll.getVerticalScrollBar().setUI(new PlayListScrollBarUI());
		scroll.setBorder(new LineBorder(color, 2));
		scroll.setOpaque(false); // ������ ����
		scroll.getViewport().setOpaque(false);
		scroll.setPreferredSize(new Dimension(650, 650));

		pnlCenter.add(scroll, BorderLayout.CENTER);
		// �߰� ��
		JPanel pnlCenterBottom = new JPanel();
		pnlCenterBottom.setBackground(color.WHITE);
		pnlCenterBottom.add(cbChatSet);
		pnlCenterBottom.add(tfChatting);
		pnlCenterBottom.add(btnEmoticon);
		pnlCenterBottom.add(btnSend);
		pnlCenter.add(pnlCenterBottom, BorderLayout.SOUTH);

		// ��⿭ ���� �г� ����

		// ���� ��
		JPanel pnlRightTop = new JPanel(new BorderLayout());
		pnlRightTop.setBackground(Color.WHITE);

		JPanel pnlBtnWithdrawal = new JPanel(new FlowLayout());
		pnlBtnWithdrawal.setBackground(Color.WHITE);
		pnlBtnWithdrawal.add(btnWithdrawal);

		JPanel pnlLblCurrent = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pnlLblCurrent.add(lblCurrent);
		pnlLblCurrent.setBackground(color);

		pnlRightTop.add(pnlBtnWithdrawal, BorderLayout.CENTER);
		pnlRightTop.add(pnlLblCurrent, BorderLayout.SOUTH);

		pnlRight.add(pnlRightTop, BorderLayout.NORTH);

		// ���� ��
		////////////////////////
		JScrollPane scroll2 = new JScrollPane(pnlRightCenter, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pnlRightCenter.setPreferredSize(new Dimension(200, 570));
		pnlRightCenter.setBackground(Color.WHITE);
		////////////////////////

		// ��⿭ ���� ����Ʈ ����
		pnlRight.add(scroll2, BorderLayout.CENTER);

		// ���� ��
		JPanel pnlRightBottom = new JPanel();
		pnlRightBottom.setBackground(Color.WHITE);
		JPanel pnlBtnExit = new JPanel();
		pnlBtnExit.setBackground(Color.white);
		pnlBtnExit.add(btnExit);
		pnlRightBottom.add(pnlBtnExit);
		pnlRight.add(pnlRightBottom, BorderLayout.SOUTH);

		pnlMain.add(pnlLeft);
		pnlMain.add(pnlCenter);
		pnlMain.add(pnlRight);

		add(pnlMain);
	}

	private void chatting() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				tfChatting.requestFocus();
			}
		});
		tfChatting.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					if (cbChatSet.getSelectedIndex() == 0) {
						try {
							boolean flag = true;
							if (tfChatting.getText().length() >= 100) {
								JOptionPane.showMessageDialog(Queue.this, "100�� ���Ϸ� �Է����ּ��� \n(�������)");
								flag = false;
								tfChatting.setText("");
							}
							if (flag) {
								oos.writeObject(new SendData(OperationCodeClient.WAITING_ROOM_CHAT,
										tfChatting.getText(), myId));
								oos.flush();
								oos.reset();
								tfChatting.setText("");
								tfChatting.requestFocus();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (cbChatSet.getSelectedIndex() == 1) {
						try {
							oos.writeObject(new SendData(OperationCodeClient.WHISPER_CHAT, tfChatting.getText(), myId));
							oos.flush();
							oos.reset();
							tfChatting.setText("");
							tfChatting.requestFocus();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		btnSend.addActionListener((ae) -> {
			if (cbChatSet.getSelectedIndex() == 0) {
				try {
					boolean flag = true;
					if (tfChatting.getText().length() >= 100) {
						JOptionPane.showMessageDialog(Queue.this, "100�� ���Ϸ� �Է����ּ��� \n(�������)");
						flag = false;
						tfChatting.setText("");
					}
					if (flag) {
						oos.writeObject(
								new SendData(OperationCodeClient.WAITING_ROOM_CHAT, tfChatting.getText(), myId));
						oos.flush();
						oos.reset();
						tfChatting.setText("");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (cbChatSet.getSelectedIndex() == 1) {
				try {
					oos.writeObject(new SendData(OperationCodeClient.WHISPER_CHAT, tfChatting.getText(), myId));
					oos.flush();
					oos.reset();
					tfChatting.setText("");
					tfChatting.requestFocus();
				} catch (Exception e) {
				}
			}
		});
		btnEmoticon.addActionListener((ae) -> {
			try {
				new ShowImoticon(Queue.this, sock, oos, ois, myId);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		btnSearch.addActionListener((ae) -> {
			try {
				oos.writeObject(new SendData(OperationCodeClient.SEARCH_REQUEST, tfSearch.getText()));
				oos.flush();
				oos.reset();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void enterRoom(Object src) {
		if (src == chatRoomList) {
			PnlChatRoom roomInfo = chatRoomList.getSelectedValue();
			boolean flag = true;

			int roomNum = Integer.parseInt(roomInfo.getRoomNum());
			System.out.println("���ȣ: " + roomNum);
			String input = null;
			if (roomInfo.getMaxNum() == roomInfo.getCurrentNum()) {
				JOptionPane.showMessageDialog(Queue.this, "�ο��� ������ �濡 ������ ���� �����ϴ�.");
				flag = false;
			}
			try {
				if (flag) {
					if (roomInfo.getPw().equals("")) {
						oos.writeObject(new SendData(OperationCodeClient.ROOM_REQUEST, myId, roomNum));
						oos.flush();
						oos.reset();
						flag = false;
					} else {
						input = JOptionPane.showInputDialog(this, "��й��Դϴ�. ��й�ȣ�� �Է��ϼ���.");
						if (input == null || !(input.equals(roomInfo.getPw()))) {
							JOptionPane.showMessageDialog(Queue.this, "��й�ȣ�� Ʋ�� �濡 ������ ���� �����ϴ�.");
							flag = false;
						}
					}
					if (flag) {
						oos.writeObject(new SendData(OperationCodeClient.ROOM_REQUEST, myId, roomNum, input));
						oos.flush();
						oos.reset();
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	synchronized private void enterRoom2(Object src) {
		if (src == chatRoomList2) {
			PnlChatRoom roomInfo = chatRoomList2.getSelectedValue();
			boolean flag = true;
			int roomNum = Integer.parseInt(roomInfo.getRoomNum());
			String input = null;
			if (roomInfo.getMaxNum() == roomInfo.getCurrentNum()) {
				JOptionPane.showMessageDialog(Queue.this, "�ο��� ������ �濡 ������ ���� �����ϴ�.");
				flag = false;
			}
			try {
				if (flag) {
					if (roomInfo.getPw().equals("")) {
						oos.writeObject(new SendData(OperationCodeClient.ROOM_REQUEST, myId, roomNum));
						oos.flush();
						oos.reset();
						flag = false;
					} else {
						input = JOptionPane.showInputDialog(this, "��й��Դϴ�. ��й�ȣ�� �Է��ϼ���.");
						if (input == null || !(input.equals(roomInfo.getPw()))) {
							JOptionPane.showMessageDialog(Queue.this, "��й�ȣ�� Ʋ�� �濡 ������ ���� �����ϴ�.");
							flag = false;
						}
					}
					if (flag) {
						oos.writeObject(new SendData(OperationCodeClient.ROOM_REQUEST, myId, roomNum, input));
						oos.flush();
						oos.reset();
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	synchronized private void enterRoom3(Object src) {
		if (src == chatRoomList3) {
			PnlChatRoom roomInfo = chatRoomList3.getSelectedValue();
			boolean flag = true;
			int roomNum = Integer.parseInt(roomInfo.getRoomNum());
			String input = null;
			if (roomInfo.getMaxNum() == roomInfo.getCurrentNum()) {
				JOptionPane.showMessageDialog(Queue.this, "�ο��� ������ �濡 ������ ���� �����ϴ�.");
				flag = false;
			}
			try {
				if (flag) {
					if (roomInfo.getPw().equals("")) {
						oos.writeObject(new SendData(OperationCodeClient.ROOM_REQUEST, myId, roomNum));
						oos.flush();
						oos.reset();
						flag = false;
					} else {
						input = JOptionPane.showInputDialog(this, "��й��Դϴ�. ��й�ȣ�� �Է��ϼ���.");
						if (input == null || !(input.equals(roomInfo.getPw()))) {
							JOptionPane.showMessageDialog(Queue.this, "��й�ȣ�� Ʋ�� �濡 ������ ���� �����ϴ�.");
							flag = false;
						}
					}
					if (flag) {
						oos.writeObject(new SendData(OperationCodeClient.ROOM_REQUEST, myId, roomNum, input));
						oos.flush();
						oos.reset();
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	synchronized private void enterRoom4(Object src) {
		if (src == chatRoomList4) {
			PnlChatRoom roomInfo = chatRoomList4.getSelectedValue();
			boolean flag = true;
			int roomNum = Integer.parseInt(roomInfo.getRoomNum());
			String input = null;
			if (roomInfo.getMaxNum() == roomInfo.getCurrentNum()) {
				JOptionPane.showMessageDialog(Queue.this, "�ο��� ������ �濡 ������ ���� �����ϴ�.");
				flag = false;
			}
			try {
				if (flag) {
					if (roomInfo.getPw().equals("")) {
						oos.writeObject(new SendData(OperationCodeClient.ROOM_REQUEST, myId, roomNum));
						oos.flush();
						oos.reset();
						flag = false;
					} else {
						input = JOptionPane.showInputDialog(this, "��й��Դϴ�. ��й�ȣ�� �Է��ϼ���.");
						if (input == null || !(input.equals(roomInfo.getPw()))) {
							JOptionPane.showMessageDialog(Queue.this, "��й�ȣ�� Ʋ�� �濡 ������ ���� �����ϴ�.");
							flag = false;
						}
					}
					if (flag) {
						oos.writeObject(new SendData(OperationCodeClient.ROOM_REQUEST, myId, roomNum, input));
						oos.flush();
						oos.reset();
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	synchronized private void enterRoom5(Object src) {
		if (src == chatRoomList5) {
			PnlChatRoom roomInfo = chatRoomList5.getSelectedValue();
			boolean flag = true;
			int roomNum = Integer.parseInt(roomInfo.getRoomNum());
			String input = null;
			if (roomInfo.getMaxNum() == roomInfo.getCurrentNum()) {
				JOptionPane.showMessageDialog(Queue.this, "�ο��� ������ �濡 ������ ���� �����ϴ�.");
				flag = false;
			}
			try {
				if (flag) {
					if (roomInfo.getPw().equals("")) {
						oos.writeObject(new SendData(OperationCodeClient.ROOM_REQUEST, myId, roomNum));
						oos.flush();
						oos.reset();
						flag = false;
					} else {
						input = JOptionPane.showInputDialog(this, "��й��Դϴ�. ��й�ȣ�� �Է��ϼ���.");
						if (input == null || !(input.equals(roomInfo.getPw()))) {
							JOptionPane.showMessageDialog(Queue.this, "��й�ȣ�� Ʋ�� �濡 ������ ���� �����ϴ�.");
							flag = false;
						}
					}
					if (flag) {
						oos.writeObject(new SendData(OperationCodeClient.ROOM_REQUEST, myId, roomNum, input));
						oos.flush();
						oos.reset();
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void enterAddListener() {
		chatRoomList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				Object src = me.getSource();
				enterRoom(src);
			}
		});
	}

	private void mAddListener() {
		// ���콺 �˾�â ������
		waitingUserList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				check(me);
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				check(me);
			}
		});

		MouseListener mListener = new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent me) {
				Cursor cs = new Cursor(Cursor.HAND_CURSOR);
				if (one == me.getSource() || two == me.getSource() || three == me.getSource() || four == me.getSource()
						|| five == me.getSource()) {
					setCursor(cs);
				}

			}

			@Override
			public void mouseExited(MouseEvent me) {
				Cursor cs = new Cursor(Cursor.DEFAULT_CURSOR);
				if (one == me.getSource() || two == me.getSource() || three == me.getSource() || four == me.getSource()
						|| five == me.getSource()) {
					setCursor(cs);
				}
			}

			@Override
			public void mouseClicked(MouseEvent me) {
				if (one == me.getSource()) {
					roomCard.first(pnlLeftCenter);
					count = 1;
				} else if (two == me.getSource()) {
					roomCard.show(pnlLeftCenter, "Second");
					count = 2;
				} else if (three == me.getSource()) {
					roomCard.show(pnlLeftCenter, "Third");
					count = 3;
				} else if (four == me.getSource()) {
					roomCard.show(pnlLeftCenter, "Fourth");
					count = 4;
				} else if (five == me.getSource()) {
					roomCard.show(pnlLeftCenter, "Fifth");
					count = 5;
				}
			}
		};
		one.addMouseListener(mListener);
		two.addMouseListener(mListener);
		three.addMouseListener(mListener);
		four.addMouseListener(mListener);
		five.addMouseListener(mListener);

	}

	private int count = 0;

	// ���콺 �˾�â ����
	public void check(MouseEvent me) {
		if (me.isPopupTrigger()) { // if the event shows the menu
			waitingUserList.setSelectedIndex(waitingUserList.locationToIndex(me.getPoint()));
			id = waitingUserList.getSelectedValue().toString();
			pm.show(waitingUserList, me.getX(), me.getY());
		}
	}

	private void addListener() {
		// �游���
		btnMakingRoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				crm = new ChatRoomMaker(Queue.this, sock, oos, ois, myId);
			}
		});

		// ȸ��Ż��
		btnWithdrawal.addActionListener((ae) -> {
			boolean flag = true;
			try {
				String result = JOptionPane.showInputDialog(Queue.this, "��й�ȣ�� �Է��ϼ���", "ȸ��Ż��",
						JOptionPane.ERROR_MESSAGE);
				if (result.equals("")) {
					JOptionPane.showMessageDialog(Queue.this, "��й�ȣ�� �Է��ϼ���");
					flag = false;
				}
				if (flag) {
					oos.writeObject(new SendData(OperationCodeClient.WITHDRAWAL, result));
					oos.flush();
					oos.reset();
				}
			} catch (Exception e1) {
			}
		});
		// �α׾ƿ�
		btnExit.addActionListener((ae) -> {
			int result = JOptionPane.showConfirmDialog(Queue.this, "�α׾ƿ� �Ͻðڽ��ϱ�?", "�α׾ƿ�", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);

			if (result == JOptionPane.YES_OPTION) {
				try {
					oos.writeObject(new SendData(OperationCodeClient.LOGOUT, myId));
					oos.flush();
					oos.reset();

					dispose();
					new LoginForm();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		ItemListener aListener = (ie) -> {
			if (ie.getItem().equals("��ü")) {
				tfChatting.setText("");
			} else {
				tfChatting.setText("/to ");
			}
		};
		cbChatSet.addItemListener(aListener);

		whisper.addActionListener((ae) -> {
			cbChatSet.setSelectedIndex(1);
			tfChatting.setText("/to " + id);
			tfChatting.requestFocus();
		});
		tfSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getSource() == tfSearch) {
					tfSearch.setText("");
				}
			}
		});
	}

	private void showFrame() {
		setTitle("��⿭");
		setSize(1600, 800);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
	}
}
