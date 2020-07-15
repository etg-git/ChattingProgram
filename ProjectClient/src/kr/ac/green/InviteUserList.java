package kr.ac.green;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class InviteUserList extends JDialog { // test

	private JButton btnInvite;
	private JButton btnCancel;

	// 대기열 유저 JPanel 리스트
	private Vector<PnlWaitingUser> vWaitingUser;
	private JList waitingUserList;
	private DefaultListModel userList = new DefaultListModel<>();

	private String inviteName;
	private Socket sock;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Vector<UserInfo> waitList;
	private int roomNum;
	private String myId;

	public InviteUserList(ChatRoomForm chatRoom, Socket sock, ObjectOutputStream oos, ObjectInputStream ois,
			Vector<UserInfo> waitList, int roomNum, String myId) {
		super(chatRoom, "초대할 유저 목록", true);
		this.sock = sock;
		this.oos = oos;
		this.ois = ois;
		this.waitList = waitList;
		this.roomNum = roomNum;
		this.myId = myId;
		init();
		setDisplay();
		addListener();
		showFrame();

	}

	private void init() {
		btnInvite = new JButton("초대하기");
		btnCancel = new JButton("취    소");
		btnCancel.setPreferredSize(new Dimension(85, 28));

		// 대기열 유저 리스트 JLIST TEST 용
		vWaitingUser = new Vector<PnlWaitingUser>();
		for (UserInfo users : waitList) {
			vWaitingUser.add(new PnlWaitingUser(users.isGender(), users.getNickName()));
		}

		// 대기열 유저 리스트 구성
		for (PnlWaitingUser user : vWaitingUser) {
			userList.addElement(user);
		}
		waitingUserList = new JList(userList);
		waitingUserList.setCellRenderer(new MyListCellRenderer());

	}

	// vWaitingUser<Vector> -> JList 표현
	class MyListCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList<?> list, Object value, int idx, boolean isSelected,
				boolean cellHasFocus) {
			PnlWaitingUser user = (PnlWaitingUser) value;
			if (isSelected) {
				user.setBackground(new Color(0xF6A7E4));
				inviteName = user.getLblName().getText();
			} else {
				user.setBackground(Color.WHITE);
			}
			return user;
		};
	}

	private void setDisplay() {
		// 메인패널
		JPanel pnlMain = new JPanel(new BorderLayout());

		// 초대할 유저 목록을 보여주는 패널
		JPanel pnlUserList = new JPanel();
		pnlUserList.setBackground(Color.WHITE);
		waitingUserList.setPreferredSize(new Dimension(200, 300));
		pnlUserList.add(waitingUserList);
		JScrollPane scroll = new JScrollPane(pnlUserList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		;

		// 버튼 패널
		JPanel pnlButton = new JPanel(new FlowLayout());
		pnlButton.add(btnInvite);
		pnlButton.add(btnCancel);

		// 메인 패널에 각각의 패널 붙이기
		pnlMain.add(scroll, BorderLayout.CENTER);
		pnlMain.add(pnlButton, BorderLayout.SOUTH);

		add(pnlMain, BorderLayout.CENTER);

	}

	private void addListener() {

		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		btnInvite.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					oos.writeObject(new SendData(OperationCodeClient.INVITE_CHAT, inviteName, roomNum, myId));
					oos.reset();
					oos.flush();

					dispose();
				} catch (IOException e1) {
					e1.printStackTrace();
				} 
			}
		});
	}

	private void showFrame() {
		setSize(300, 350);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}

}
