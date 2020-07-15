package kr.ac.green;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class ShowImoticon extends JDialog implements Serializable {

	private static final String GIF = ".gif";
	private String PATH = "emoticon//"; // 이모티콘 경로
	private static final String EXT = ".png";
	public static final String[] EMOTICONS = { "1", "2", "3", "4", "5", "6", "7", "8", "9" };

	private static final String PATH2 = "basicEmoticon/";
	public static final String[] EMOTICONS2 = { "emotion1", "emotion2", "emotion3", "emotion4", "emotion5", "emotion6",
			"emotion7", "emotion8", "emotion9", "emotion10", "emotion11", "emotion12", "emotion13", "emotion14",
			"emotion15", "emotion16", "emotion17", "emotion18", "emotion19", "emotion20" };

	private static final String PATH3 = "emoticon2/";
	public static final String[] EMOTICONS3 = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };

	private static final String PATH4 = "movingEmoticon/";
	public static final String[] EMOTICONS4 = { "1", "2", "3", "4", "5", "6", "7" };
	private static final String PATH5 = "movingEmoticon2/";
	public static final String[] EMOTICONS5 = { "1", "2", "3", "4", "5", "6", "7" };
	
	private static final String PATH6 = "movingEmoticon3/";
	public static final String[] EMOTICONS6 = { "1", "2", "3", "4", "5", "6", "7", "8" };

	private JTabbedPane tab;

	private ImgLabel[] lblImgs;
	private ImgLabel[] lblImgs2;
	private ImgLabel[] lblImgs3;
	private ImgLabel[] lblImgs4;
	private ImgLabel[] lblImgs5;
	private ImgLabel[] lblImgs6;

	private CMouseListener<ShowImoticon> mListener;

	private Queue queue;
	private Socket sock;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	private ChatRoomForm chatRoom;
	private int roomNumber;

	private String myId;
	public ShowImoticon(Queue queue, Socket sock, ObjectOutputStream oos, ObjectInputStream ois, String myId) {
		super(queue, "이모티콘", false);
		this.queue = queue;
		this.sock = sock;
		this.oos = oos;
		this.ois = ois;
		this.myId = myId;
		init();
		setDisplay();
		addListiener();
		showFrame();
	}

	public ShowImoticon(ChatRoomForm chatRoom, Socket sock, ObjectOutputStream oos, ObjectInputStream ois,
			int roomNumber, String myId) {
		super(chatRoom, "이모티콘", false);
		this.chatRoom = chatRoom;
		this.sock = sock;
		this.oos = oos;
		this.ois = ois;
		this.roomNumber = roomNumber;
		this.myId = myId;
		init();
		setDisplay();
		addListiener2();
		showFrame();

	}

	private void init() {
		mListener = new CMouseListener<>(this);
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblImgs = new ImgLabel[EMOTICONS.length];
		for (int idx = 0; idx < lblImgs.length; idx++) {
			lblImgs[idx] = new ImgLabel(new ImageIcon(getSrcPath(idx)), idx);
		}
		lblImgs2 = new ImgLabel[EMOTICONS2.length];
		for (int idx = 0; idx < lblImgs2.length; idx++) {
			lblImgs2[idx] = new ImgLabel(new ImageIcon(getSrcPath2(idx)), idx);
		}
		lblImgs3 = new ImgLabel[EMOTICONS3.length];
		for (int idx = 0; idx < lblImgs3.length; idx++) {
			lblImgs3[idx] = new ImgLabel(new ImageIcon(getSrcPath3(idx)), idx);
		}
		lblImgs4 = new ImgLabel[EMOTICONS4.length];
		for (int idx = 0; idx < lblImgs4.length; idx++) {
			Image img = Toolkit.getDefaultToolkit().getImage(getSrcPath3(idx));
			Image setImg = img.getScaledInstance(50, 50, Image.SCALE_AREA_AVERAGING);
			lblImgs4[idx] = new ImgLabel(new ImageIcon(getSrcPath4(idx)), idx);
		}
		lblImgs5 = new ImgLabel[EMOTICONS5.length];
		for (int idx = 0; idx < lblImgs5.length; idx++) {
			lblImgs5[idx] = new ImgLabel(new ImageIcon(getSrcPath5(idx)), idx);
		}
		lblImgs6 = new ImgLabel[EMOTICONS6.length];
		for (int idx = 0; idx < lblImgs6.length; idx++) {
			lblImgs6[idx] = new ImgLabel(new ImageIcon(getSrcPath6(idx)), idx);
		}
	}

	private void setDisplay() {
		tab = new JTabbedPane();

		tab.addTab("카카오", pnlKakao());
		tab.addTab("기본아이콘", pnlBasicEmoticon());
		tab.addTab("하모닝즈", pnlHarmornings());
		tab.addTab("움직이는이모티콘", scrollSet(pnlMovingEmo()));
		tab.addTab("움직이는이모티콘2", scrollSet(pnlMovingEmo2()));
		tab.addTab("움직이는이모티콘3", scrollSet(pnlMovingEmo3()));
		add(tab);
	}

	public static ImageIcon getImageIcon(String srcPath) {
		return new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(srcPath).getScaledInstance(50, 50, Image.SCALE_DEFAULT));

	}

	public String getSrcPath(int idx) {
		return PATH + EMOTICONS[idx] + EXT;
	}

	public String getSrcPath2(int idx) {
		return PATH2 + EMOTICONS2[idx] + EXT;
	}

	public String getSrcPath3(int idx) {
		return PATH3 + EMOTICONS3[idx] + EXT;
	}

	public String getSrcPath4(int idx) {
		return PATH4 + EMOTICONS4[idx] + GIF;
	}

	public String getSrcPath5(int idx) {
		return PATH5 + EMOTICONS5[idx] + GIF;
	}
	public String getSrcPath6(int idx) {
		return PATH6 + EMOTICONS6[idx] + GIF;
	}

	private JPanel pnlKakao() {
		JPanel pnl1 = new JPanel();
		pnl1.setLayout(new GridLayout(0, 3));
		for (JLabel lbl : lblImgs) {
			pnl1.add(lbl);
		}
		return pnl1;
	}

	private JPanel pnlBasicEmoticon() {
		JPanel pnl2 = new JPanel();
		pnl2.setLayout(new GridLayout(0, 4));
		for (JLabel lbl : lblImgs2) {
			pnl2.add(lbl);
		}
		add(pnl2, BorderLayout.CENTER);
		return pnl2;
	}

	private JPanel pnlHarmornings() {
		JPanel pnl3 = new JPanel();
		pnl3.setLayout(new GridLayout(0, 4));
		for (JLabel lbl : lblImgs3) {
			pnl3.add(lbl);
		}
		add(pnl3, BorderLayout.CENTER);
		return pnl3;
	}

	// 움직이는 이모티콘
	private JPanel pnlMovingEmo() {
		JPanel pnl4 = new JPanel();
		pnl4.setLayout(new GridLayout(0, 3));
		for (JLabel lbl : lblImgs4) {
			pnl4.add(lbl);
		}
		return pnl4;
	}

	// 움직이는 이모티콘2
	private JPanel pnlMovingEmo2() {
		JPanel pnl5 = new JPanel();
		pnl5.setLayout(new GridLayout(0, 2));
		for (JLabel lbl : lblImgs5) {
			pnl5.add(lbl);
		}
		return pnl5;
	}
	
	// 움직이는 이모티콘3
	private JPanel pnlMovingEmo3() {
		JPanel pnl6 = new JPanel();
		pnl6.setLayout(new GridLayout(0, 2));
		for (JLabel lbl : lblImgs6) {
			pnl6.add(lbl);
		}
		return pnl6;
	}
	private JScrollPane scrollSet(JPanel pnl) {
		JScrollPane scroll = new JScrollPane(pnl);
		scroll.getVerticalScrollBar().setUnitIncrement(16); // 스크롤바 속도
		return scroll;
	}

	public void addListiener() {
		for (JLabel lbl : lblImgs) {
			lbl.addMouseListener(mListener);
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						try {
							ImageIcon a = (ImageIcon) lbl.getIcon();

							oos.writeObject(new SendData(OperationCodeClient.WAITING_ROOM_EMOTICON_CHAT, a, myId));
							oos.flush();
							oos.reset();
							dispose();
						} catch (Exception ie) {
							ie.printStackTrace();

						}
					}
				}
			});
		}
		for (JLabel lbl : lblImgs2) {
			lbl.addMouseListener(mListener);
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						try {
							ImageIcon a = (ImageIcon) lbl.getIcon();
							oos.writeObject(new SendData(OperationCodeClient.WAITING_ROOM_EMOTICON_CHAT, a, myId));
							oos.flush();
							oos.reset();
							dispose();
						} catch (Exception ie) {
							ie.printStackTrace();
						}
					}
				}
			});
		}
		for (JLabel lbl : lblImgs3) {
			lbl.addMouseListener(mListener);
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						try {
							ImageIcon a = (ImageIcon) lbl.getIcon();
							oos.writeObject(new SendData(OperationCodeClient.WAITING_ROOM_EMOTICON_CHAT, a, myId));
							oos.flush();
							oos.reset();
							dispose();
						} catch (Exception ie) {
							ie.printStackTrace();
						}
					}
				}
			});
		}
		for (int i = 0; i < lblImgs4.length; i++) {
			lblImgs4[i].addMouseListener(mListener);
			lblImgs4[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() > 1) {
						Object src = e.getSource();
						try {
							for (int j = 0; j < lblImgs4.length; j++) {
								if (src == lblImgs4[j]) {
									String path = getSrcPath4(j);
									oos.writeObject(new SendData(OperationCodeClient.WAITING_ROOM_EMOTICON_CHAT_MOVE, path, myId));
									oos.flush();
									oos.reset();
									dispose();
								}
							}
						} catch (Exception ie) {
							ie.printStackTrace();
						}
					}
				}
			});
		}

		for (int i = 0; i < lblImgs5.length; i++) {
			lblImgs5[i].addMouseListener(mListener);
			lblImgs5[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() > 1) {

					Object src = e.getSource();
						try {
							for (int j = 0; j < lblImgs5.length; j++) {
								if (src == lblImgs5[j]) {
									String path = getSrcPath5(j);
									oos.writeObject(new SendData(OperationCodeClient.WAITING_ROOM_EMOTICON_CHAT_MOVE, path, myId));
									oos.flush();
									oos.reset();
									dispose();
								}
							}
						} catch (Exception ie) {
							ie.printStackTrace();
						}
					}
				}
			});
		}
		for (int i = 0; i < lblImgs6.length; i++) {
			lblImgs6[i].addMouseListener(mListener);
			lblImgs6[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() > 1) {

					Object src = e.getSource();
						try {
							for (int j = 0; j < lblImgs6.length; j++) {
								if (src == lblImgs6[j]) {
									String path = getSrcPath6(j);
									oos.writeObject(new SendData(OperationCodeClient.WAITING_ROOM_EMOTICON_CHAT_MOVE, path, myId));
									oos.flush();
									oos.reset();
									dispose();
								}
							}
						} catch (Exception ie) {
							ie.printStackTrace();
						}
					}
				}
			});
		}
	}

	public void addListiener2() {
		for (JLabel lbl : lblImgs) {
			lbl.addMouseListener(mListener);
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						try {
							ImageIcon a = (ImageIcon) lbl.getIcon();

							oos.writeObject(new SendData(OperationCodeClient.ROOM_EMOTICON_CHAT, a, roomNumber, myId));
							oos.flush();
							oos.reset();
							dispose();
						} catch (Exception ie) {
							ie.printStackTrace();

						}
					}
				}
			});
		}
		for (JLabel lbl : lblImgs2) {
			lbl.addMouseListener(mListener);
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						try {
							ImageIcon a = (ImageIcon) lbl.getIcon();
							oos.writeObject(new SendData(OperationCodeClient.ROOM_EMOTICON_CHAT, a, roomNumber, myId));
							oos.flush();
							oos.reset();
							dispose();
						} catch (Exception ie) {
							ie.printStackTrace();
						}
					}
				}
			});
		}
		for (JLabel lbl : lblImgs3) {
			lbl.addMouseListener(mListener);
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						try {
							ImageIcon a = (ImageIcon) lbl.getIcon();

							oos.writeObject(new SendData(OperationCodeClient.ROOM_EMOTICON_CHAT, a, roomNumber, myId));
							oos.flush();
							oos.reset();
							dispose();
						} catch (Exception ie) {
							ie.printStackTrace();
						}
					}
				}
			});
		}
		for (int i = 0; i < lblImgs4.length; i++) {
			lblImgs4[i].addMouseListener(mListener);
			lblImgs4[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() > 1) {
						Object src = e.getSource();
						try {
							for (int j = 0; j < lblImgs4.length; j++) {
								if (src == lblImgs4[j]) {
									String path = getSrcPath4(j);
									oos.writeObject(new SendData(OperationCodeClient.ROOM_EMOTICON_CHAT_MOVE, path, roomNumber, myId));
									oos.flush();
									oos.reset();
									dispose();
								}
							}
						} catch (Exception ie) {
							ie.printStackTrace();
						}
					}
				}
			});
		}

		for (int i = 0; i < lblImgs5.length; i++) {
			lblImgs5[i].addMouseListener(mListener);
			lblImgs5[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() > 1) {
						Object src = e.getSource();
						try {
							for (int j = 0; j < lblImgs5.length; j++) {
								if (src == lblImgs5[j]) {
									String path = getSrcPath5(j);
									oos.writeObject(new SendData(OperationCodeClient.ROOM_EMOTICON_CHAT_MOVE, path, roomNumber, myId));
									oos.flush();
									oos.reset();
									dispose();
								}
							}
						} catch (Exception ie) {
							ie.printStackTrace();
						}
					}
				}
			});
		}
		for (int i = 0; i < lblImgs6.length; i++) {
			lblImgs6[i].addMouseListener(mListener);
			lblImgs6[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() > 1) {
						Object src = e.getSource();
						try {
							for (int j = 0; j < lblImgs6.length; j++) {
								if (src == lblImgs6[j]) {
									String path = getSrcPath6(j);
									oos.writeObject(new SendData(OperationCodeClient.ROOM_EMOTICON_CHAT_MOVE, path, roomNumber, myId));
									oos.flush();
									oos.reset();
									dispose();
								}
							}
						} catch (Exception ie) {
							ie.printStackTrace();
						}
					}
				}
			});
		}
	}

	private void showFrame() {
		setSize(700, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

}
