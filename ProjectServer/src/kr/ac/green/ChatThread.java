package kr.ac.green;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;

public class ChatThread extends Thread implements Serializable {

	private Socket sock;
	private HashMap<UserInfo, ObjectOutputStream> waitingUserList;
	private Vector<ChatRoom> allChatRoomList;

	private HashMap<Integer, HashMap<UserInfo, ObjectOutputStream>> chatUserList;
	private Vector<User> allUserList;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	private Vector<RoomInfo> roomList;
	private Vector<UserInfo> connectUserList;
	private Vector<UserInfo> inviteList;

	public ChatThread(Socket sock, HashMap<UserInfo, ObjectOutputStream> userList, Vector<ChatRoom> allChatRoomList,
			HashMap<Integer, HashMap<UserInfo, ObjectOutputStream>> chatUserList, Vector<User> allUserList,
			Vector<RoomInfo> roomList, Vector<UserInfo> connectUserList, Vector<UserInfo> inviteList) {
		this.waitingUserList = userList;
		this.allChatRoomList = allChatRoomList;
		this.sock = sock;
		this.chatUserList = chatUserList;
		this.allUserList = allUserList;
		this.roomList = roomList;
		this.connectUserList = connectUserList;
		this.inviteList = inviteList;
		try {
			oos = new ObjectOutputStream(sock.getOutputStream());
			ois = new ObjectInputStream(sock.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			Object obj = null;
			while ((obj = ois.readObject()) != null) {
				SendData data = (SendData) obj;
				FileWriter fw = null;
				// 회원가입
				if (data.getCode() == OperationCodeClient.SIGN_UP) {
					User user = (User) data.getData()[0];
					fw = new FileWriter("..\\users\\user.txt", true);
					boolean flag = true;
					if (allUserList.size() == 0) {
						synchronized (allUserList) {
							allUserList.add(user);
						}
						oos.writeObject(new SendData(OperationCodeServer.SIGN_UP_OK));
						oos.flush();
						oos.reset();

						fw.write(user.getId() + " " + user.getPw() + " " + user.getNickName() + " "
								+ user.getPhoneNumber() + " " + user.getName() + " " + user.getQuestion() + " "
								+ user.getAnswer() + " " + user.isGender());
						fw.write("\n");
						fw.flush();

						flag = false;
					}
					if (flag) {
						for (User users : allUserList) {
							if (users.getId().equals(user.getId())) {
								flag = false;
								oos.writeObject(new SendData(OperationCodeServer.SIGN_UP_FAIL, "동일한 아이디입니다"));
								oos.flush();
								oos.reset();
							} else if (users.getNickName().equals(user.getNickName())) {
								flag = false;
								oos.writeObject(new SendData(OperationCodeServer.SIGN_UP_FAIL, "동일한 닉네임입니다"));
								oos.flush();
								oos.reset();
							} else if (users.getPhoneNumber().equals(user.getPhoneNumber())) {
								flag = false;
								oos.writeObject(new SendData(OperationCodeServer.SIGN_UP_FAIL, "동일한 전화번호입니다"));
								oos.flush();
								oos.reset();
							}
						}
					}
					if (flag) {
						synchronized (allUserList) {
							allUserList.add((User) data.getData()[0]);

							fw.write(user.getId() + " " + user.getPw() + " " + user.getNickName() + " "
									+ user.getPhoneNumber() + " " + user.getName() + " " + user.getQuestion() + " "
									+ user.getAnswer() + " " + user.isGender());
							fw.write("\n");
							fw.flush();
						}
						oos.writeObject(new SendData(OperationCodeServer.SIGN_UP_OK));
					}
					oos.flush();
					oos.reset();

					// 아이디 찾기
				} else if (data.getCode() == OperationCodeClient.FIND_ID) {
					boolean flag = true;
					String name = (String) data.getData()[0];
					String phoneNumber = (String) data.getData()[1];
					if (allUserList.size() == 0) {
						oos.writeObject(new SendData(OperationCodeServer.FIND_ID_FAIL1));
					}
					for (User users : allUserList) {
						if (users.getPhoneNumber().equals(phoneNumber) && users.getName().equals(name)) {
							oos.writeObject(new SendData(OperationCodeServer.FIND_ID_OK, users.getId()));
							flag = false;
						}
					}

					if (flag) {
						oos.writeObject(new SendData(OperationCodeServer.FIND_ID_FAIL1));
					}
					oos.flush();
					oos.reset();

					// 비번찾기전 아이디찾기
				} else if (data.getCode() == OperationCodeClient.PW_SEARCH) {
					boolean flag = true;
					String id = (String) data.getData()[0];
					String phone = (String) data.getData()[1];
					String question = (String) data.getData()[2];
					String answer = (String) data.getData()[3];

					if (allUserList.size() == 0) {
						oos.writeObject(new SendData(OperationCodeServer.PW_SEARCH_FAIL1));
						flag = false;
					}
					if (flag) {
						for (User users : allUserList) {
							if (users.getPhoneNumber().equals(phone) && users.getId().equals(id)
									&& users.getQuestion().equals(question) && users.getAnswer().equals(answer)) {

								oos.writeObject(new SendData(OperationCodeServer.PW_SEARCH));
								flag = false;
							}
						}
					}
					if (flag) {
						oos.writeObject(new SendData(OperationCodeServer.PW_SEARCH_FAIL1));
					}
					oos.flush();
					oos.reset();

					// 비번 변경
				} else if (data.getCode() == OperationCodeClient.PW_CHANGE) {
					User changeUser = null;
					String pw = (String) data.getData()[0];
					String id = (String) data.getData()[1];
					for (User users : allUserList) {
						if (users.getId().equals(id)) {
							changeUser = users;
							changeUser.setPw(pw);
							oos.writeObject(new SendData(OperationCodeServer.PW_CHANGE));
						}
					}
					fw = new FileWriter("..\\users\\user.txt");
					for (User users : allUserList) {
						fw.write(users.getId() + " " + users.getPw() + " " + users.getNickName() + " "
								+ users.getPhoneNumber() + " " + users.getName() + " " + users.getQuestion() + " "
								+ users.getAnswer() + " " + users.isGender());
						fw.write("\n");
						fw.flush();
					}

					// 로그인 접속
				} else if (data.getCode() == OperationCodeClient.LOGIN) {
					String id = (String) data.getData()[0];
					String pw = (String) data.getData()[1];
					boolean flag = true;
					if (allUserList.size() == 0) {
						oos.writeObject(new SendData(OperationCodeServer.CONNECT_FAIL2));
					}

					for (User users : allUserList) {
						if (users.getId().equals(id) && users.getPw().equals(pw)) {
							for (UserInfo user : connectUserList) {
								if (user.getNickName().equals(users.getNickName())) {
									oos.writeObject(new SendData(OperationCodeServer.CONNECT_FAIL));
									flag = false;
								}
							}

							if (flag) {
								System.out.println(id + "님이 접속하였습니다");
								synchronized (waitingUserList) {
									UserInfo user = new UserInfo(users.isGender(), users.getNickName());
									waitingUserList.put(user, oos);
									connectUserList.add(user);
								}
								Vector<UserInfo> waitList = new Vector<>(waitingUserList.keySet());
								SendData wrUpdate = new SendData(OperationCodeServer.ALL_WAITING_ROOM_UPDATE, waitList,
										roomList, users.getNickName());
								oos.writeObject(wrUpdate);
								updateWaitingRoom(new SendData(OperationCodeServer.WAITING_USER_UPDATE, waitList));
								flag = false;
							}
						}
					}

					if (flag) {
						oos.writeObject(new SendData(OperationCodeServer.CONNECT_FAIL2));
					}
					oos.flush();
					oos.reset();

					// 채팅 메시지 전송
				} else if (data.getCode() == OperationCodeClient.WAITING_ROOM_CHAT) {
					String msg = (String) data.getData()[0];
					String id = (String) data.getData()[1];
					UserInfo myUserInfo = null;
					for (User user : allUserList) {
						if (id.equals(user.getId()))
							myUserInfo = new UserInfo(user.isGender(), user.getNickName());
					}
					broadcast(msg, myUserInfo.getNickName());

					// 이모티콘 전송
				} else if (data.getCode() == OperationCodeClient.WAITING_ROOM_EMOTICON_CHAT) {
					ImageIcon emoticon = (ImageIcon) data.getData()[0];
					String id = (String) data.getData()[1];
					UserInfo myUserInfo = null;
					for (User user : allUserList) {
						if (id.equals(user.getId()))
							myUserInfo = new UserInfo(user.isGender(), user.getNickName());
					}
					broadcast(emoticon, myUserInfo.getNickName());

					// 채팅방 채팅 메시지 전송
				} else if (data.getCode() == OperationCodeClient.ROOM_CHAT) {
					String msg = (String) data.getData()[0];
					int roomNumber = (int) data.getData()[1];
					String id = (String) data.getData()[2];
					UserInfo myUserInfo = null;
					for (User user : allUserList) {
						if (id.equals(user.getId()))
							myUserInfo = new UserInfo(user.isGender(), user.getNickName());
					}
					chatBroadcast(msg, roomNumber, myUserInfo.getNickName());
					// 채팅방 이모티콘 메시지 전송
				} else if (data.getCode() == OperationCodeClient.ROOM_EMOTICON_CHAT) {
					ImageIcon emoticon = (ImageIcon) data.getData()[0];
					int roomNumber = (int) data.getData()[1];
					String id = (String) data.getData()[2];
					UserInfo myUserInfo = null;
					for (User user : allUserList) {
						if (id.equals(user.getId()))
							myUserInfo = new UserInfo(user.isGender(), user.getNickName());
					}
					chatBroadcast(emoticon, roomNumber, myUserInfo.getNickName());
					// 움직이는 이모티콘 채팅방 전송
				} else if (data.getCode() == OperationCodeClient.ROOM_EMOTICON_CHAT_MOVE) {
					String path = (String) data.getData()[0];
					int roomNumber = (int) data.getData()[1];
					String id = (String) data.getData()[2];
					UserInfo myUserInfo = null;
					for (User user : allUserList) {
						if (id.equals(user.getId()))
							myUserInfo = new UserInfo(user.isGender(), user.getNickName());
					}
					moveChatBroadcast(path, roomNumber, myUserInfo.getNickName());

					// 움직이는 이모티콘 채팅방 전송
				} else if (data.getCode() == OperationCodeClient.WAITING_ROOM_EMOTICON_CHAT_MOVE) {
					String path = (String) data.getData()[0];
					String id = (String) data.getData()[1];
					UserInfo myUserInfo = null;
					for (User user : allUserList) {
						if (id.equals(user.getId()))
							myUserInfo = new UserInfo(user.isGender(), user.getNickName());
					}
					movebroadcast(path, myUserInfo.getNickName());
				}

				// 회원탈퇴
				else if (data.getCode() == OperationCodeClient.WITHDRAWAL) {
					String pw = (String) data.getData()[0];
					boolean flag = true;
					User withdrawUser = null;
					fw = new FileWriter("..\\users\\user.txt");
					FileReader fr = new FileReader("..\\users\\user.txt");
					BufferedReader br = new BufferedReader(fr);
					Vector<UserInfo> waitList = new Vector<>(waitingUserList.keySet());
					String user = null;
					for (int i = 0; i < allUserList.size(); i++) {
						if (allUserList.get(i).getPw().equals(pw)) {
							withdrawUser = allUserList.remove(i);
							flag = false;
							oos.writeObject(new SendData(OperationCodeServer.WITHDRAWAL_OK));
							synchronized (waitingUserList) {
								for (int j = 0; j < waitList.size(); j++) {
									if (waitList.get(j).getNickName().equals(withdrawUser.getNickName())) {
										waitingUserList.remove(waitList.get(j));
										connectUserList.remove(waitList.get(j));
										waitList = new Vector<>(waitingUserList.keySet());
										setWaitingUpdate(
												new SendData(OperationCodeServer.WAITING_USER_UPDATE, waitList));
									}
								}
							}
						}
					}
					for (User users : allUserList) {
						fw.write(users.getId() + " " + users.getPw() + " " + users.getNickName() + " "
								+ users.getPhoneNumber() + " " + users.getName() + " " + users.getQuestion() + " "
								+ users.getAnswer() + " " + users.isGender());
						fw.write("\n");
						fw.flush();
					}

					if (flag) {
						oos.writeObject(new SendData(OperationCodeServer.WITHDRAWAL_FAIL));
					}
					oos.flush();
					oos.reset();
					// 로그아웃
				} else if (data.getCode() == OperationCodeClient.LOGOUT) {
					String id = (String) data.getData()[0];
					UserInfo myUserInfo = null;
					Vector<UserInfo> waitList = new Vector<>(waitingUserList.keySet());
					for (User user : allUserList) {
						if (id.equals(user.getId())) {
							myUserInfo = new UserInfo(user.isGender(), user.getNickName());
						}
					}
					synchronized (waitingUserList) {
						for (int i = 0; i < waitList.size(); i++) {
							if (waitList.get(i).getNickName().equals(myUserInfo.getNickName())) {
								waitingUserList.remove(waitList.get(i));
								connectUserList.remove(waitList.get(i));
								waitList = new Vector<>(waitingUserList.keySet());
								setWaitingUpdate(new SendData(OperationCodeServer.WAITING_USER_UPDATE, waitList));
							}
						}
					}

					// 방만들기 요청
				} else if (data.getCode() == OperationCodeClient.CREATE_ROOM_REQUEST) {
					RoomInfo info = (RoomInfo) data.getData()[0];
					String id = (String) data.getData()[1];
					UserInfo myUserInfo = null;
					boolean flag = true;
					Vector<UserInfo> waitList = new Vector<>(waitingUserList.keySet());
					synchronized (chatUserList) {
						if (flag) {
							for (User user : allUserList) {
								if (id.equals(user.getId())) {
									myUserInfo = new UserInfo(user.isGender(), user.getNickName());
								}
							}
						}
					}
					synchronized (allChatRoomList) {
						for (ChatRoom rooms : allChatRoomList) {
							if (info.getTitle().equals(rooms.getTitle())) {
								oos.writeObject(new SendData(OperationCodeServer.CREATE_FAIL));
								flag = false;
							}
						}
					}
					synchronized (roomList) {
						if (flag) {
							roomList.add(info);
						}
					}
					Vector<UserInfo> chatList = new Vector<>();
					synchronized (chatUserList) {
						if (flag) {
							for (int i = 0; i < waitList.size(); i++) {
								if (waitList.get(i).getNickName().equals(myUserInfo.getNickName())) {
									if (chatUserList.size() == 0) {
										chatUserList.put(info.getNumberRoom(), new HashMap<>());
										chatUserList.get(info.getNumberRoom()).put(myUserInfo, oos);
									} else {
										for (int roomNum : chatUserList.keySet()) {
											if (roomNum == info.getNumberRoom()) {
												info.setNumberRoom(info.getNumberRoom() + 1);
											}
										}
										chatUserList.put(info.getNumberRoom(), new HashMap<>());
										chatUserList.get(info.getNumberRoom()).put(myUserInfo, oos);
									}
									waitingUserList.remove(waitList.get(i));
									waitList = new Vector<>(waitingUserList.keySet());
									chatList = new Vector<>(chatUserList.get(info.getNumberRoom()).keySet());
									oos.writeObject(
											new SendData(OperationCodeServer.CHATTING_USER_UPDATE, chatList, info));
									oos.flush();
									oos.reset();

									updateWaitingRoom(
											new SendData(OperationCodeServer.ALL_WAITING_ROOM_UPDATE, waitList, info));
								}
							}
						}
					}
					if (flag) {
						allChatRoomList.add(new ChatRoom(info.getTitle(), info.getMaxNumbers(), info.getPassword(),
								info.getNumberRoom(), chatList, myUserInfo.getNickName(), info));

					}
					// 방들어가기 요청(비밀방 포함)
				} else if (data.getCode() == OperationCodeClient.ROOM_REQUEST) {
					String id = (String) data.getData()[0];
					int roomNum = (int) data.getData()[1];
					UserInfo myUserInfo = null;
					RoomInfo myRoomInfo = null;
					String headerNickName = null;
					synchronized (allUserList) {
						for (User user : allUserList) {
							if (id.equals(user.getId())) {
								myUserInfo = new UserInfo(user.isGender(), user.getNickName());
							}
						}
					}
					for (RoomInfo rooms : roomList) {
						if (rooms.getNumberRoom() == roomNum) {
							myRoomInfo = rooms;
						}
					}
					for (int i = 0; i < allChatRoomList.size(); i++) {
						if (allChatRoomList.get(i).getNumberRoom() == roomNum) {
							headerNickName = allChatRoomList.get(i).getHeader();
						}
					}
					for (int roomNums : chatUserList.keySet()) {
						// 공개방일때
						if (roomNums == roomNum) {
							if (myRoomInfo.getPassword().equals("")) {
								chatBroadcast(myUserInfo.getNickName() + "님이 입장하셨습니다", roomNum);
								chatUserList.get(roomNums).put(myUserInfo, oos);

								myRoomInfo.setCurrentNum(chatUserList.get(roomNums).size());
								Vector<UserInfo> chatList = new Vector<>(chatUserList.get(roomNum).keySet());
								Vector<UserInfo> waitList = new Vector<>(waitingUserList.keySet());
								synchronized (waitingUserList) {
									for (int i = 0; i < waitList.size(); i++) {
										if (waitList.get(i).getNickName().equals(myUserInfo.getNickName())) {
											waitingUserList.remove(waitList.get(i));
										}
									}
								}
								waitList = new Vector<>(waitingUserList.keySet());
								oos.writeObject(new SendData(OperationCodeServer.ROOM_OK, chatList, myRoomInfo,
										headerNickName));
								oos.flush();
								oos.reset();

								setChatUserUpdate(
										new SendData(OperationCodeServer.CHATTING_USER_UPDATE, chatList, myRoomInfo),
										roomNum);

								updateWaitingRoom(new SendData(OperationCodeServer.ALL_WAITING_ROOM_UPDATE, waitList,
										myRoomInfo));
								// 비밀방일떄
							} else {
								boolean flag = true;
								String pw = (String) data.getData()[2];
								if (pw.equals(myRoomInfo.getPassword())) {
									if (chatUserList.get(roomNums).size() >= myRoomInfo.getMaxNumbers()) {
										oos.writeObject(new SendData(OperationCodeServer.ROOM_FAIL));
										oos.flush();
										oos.reset();
										flag = false;
									}
									if (flag) {
										chatBroadcast(myUserInfo.getNickName() + "님이 입장하셨습니다", roomNum);
										chatUserList.get(roomNums).put(myUserInfo, oos);
										Vector<UserInfo> chatList = new Vector<>(chatUserList.get(roomNum).keySet());
										Vector<UserInfo> waitList = new Vector<>(waitingUserList.keySet());
										myRoomInfo.setCurrentNum(chatUserList.get(roomNums).size());
										synchronized (waitingUserList) {
											for (int i = 0; i < waitList.size(); i++) {
												if (waitList.get(i).getNickName().equals(myUserInfo.getNickName())) {
													waitingUserList.remove(waitList.get(i));
												}
											}
										}
										waitList = new Vector<>(waitingUserList.keySet());
										oos.writeObject(new SendData(OperationCodeServer.ROOM_OK, chatList, myRoomInfo,
												headerNickName));
										oos.flush();
										oos.reset();
										setChatUserUpdate(new SendData(OperationCodeServer.CHATTING_USER_UPDATE,
												chatList, myRoomInfo), roomNum);

										updateWaitingRoom(new SendData(OperationCodeServer.ALL_WAITING_ROOM_UPDATE,
												waitList, myRoomInfo));

									}

								}
							}
						}
					}
					// 방나가기
				} else if (data.getCode() == OperationCodeClient.ROOM_EXIT) {
					String id = (String) data.getData()[0];
					int roomNum = (int) data.getData()[1];
					UserInfo myUserInfo = null;
					RoomInfo myRoomInfo = null;
					String header = "";
					UserInfo mandateUser = null;

					for (User user : allUserList) {
						if (id.equals(user.getId())) {
							myUserInfo = new UserInfo(user.isGender(), user.getNickName());
						}
					}
					for (RoomInfo rooms : roomList) {
						if (rooms.getNumberRoom() == roomNum) {
							myRoomInfo = rooms;
						}
					}
					Vector<UserInfo> chatList = new Vector<>(chatUserList.get(roomNum).keySet());
					Vector<UserInfo> waitList = new Vector<>(waitingUserList.keySet());
					for (int i = 0; i < chatList.size(); i++) {
						if (chatList.get(i).getNickName().equals(myUserInfo.getNickName())) {
							waitingUserList.put(myUserInfo, oos);
							chatUserList.get(roomNum).remove(myUserInfo);
						}
					}
					waitList = new Vector<>(waitingUserList.keySet());
					chatList = new Vector<>(chatUserList.get(roomNum).keySet());
					for (int roomNums : chatUserList.keySet()) {
						if (roomNums == roomNum) {
							try {
								myRoomInfo.setCurrentNum(chatUserList.get(roomNums).size());
							} catch (NullPointerException e) {
								myRoomInfo.setCurrentNum(0);
							}

							setChatUserUpdate(
									new SendData(OperationCodeServer.CHATTING_USER_UPDATE, chatList, myRoomInfo),
									roomNum);
							oos.writeObject(new SendData(OperationCodeServer.ROOM_EXIT_OK, waitList, roomList));
							oos.flush();
							oos.reset();
							if (myRoomInfo.getCurrentNum() == 0) {
								synchronized (roomList) {
									for (int j = 0; j < roomList.size(); j++) {
										if (roomList.get(j).getTitle().equals(myRoomInfo.getTitle())) {
											roomList.remove(j);
										}
									}
								}
								synchronized (allChatRoomList) {
									for (int k = 0; k < allChatRoomList.size(); k++) {
										if (allChatRoomList.get(k).getTitle().equals(myRoomInfo.getTitle())) {
											allChatRoomList.remove(k);
										}
									}
								}
							}
							boolean headerFlag = true;
							for (int i = 0; i < allChatRoomList.size(); i++) {
								if (allChatRoomList.get(i).getNumberRoom() == roomNums) {
									if (allChatRoomList.get(i).getHeader().equals(myUserInfo.getNickName())
											&& headerFlag) {
										for (UserInfo users : chatUserList.get(roomNum).keySet()) {
											allChatRoomList.get(i).setHeader(users.getNickName());
											mandateUser = users;
										}
										header = mandateUser.getNickName();
										chatBroadcast(header + "님이 방장을 위임받으셨습니다", roomNum);
										headerFlag = false;
										onlyChatSendData(new SendData(OperationCodeServer.HEAD_UPDATE_OK, header),
												roomNum, mandateUser);
										setMandate(new SendData(OperationCodeServer.HEAD_UPDATE, header), roomNums,
												mandateUser);
									}
								}
							}
							updateWaitingRoom(
									new SendData(OperationCodeServer.ALL_WAITING_ROOM_UPDATE, waitList, myRoomInfo));
							chatBroadcast(id + "님이 나가셨습니다.", roomNum);
						}
					}

					// 방장 위임
				} else if (data.getCode() == OperationCodeClient.HEAD_MANDATE) {
					String headerName = (String) data.getData()[0];
					int roomNumber = (int) data.getData()[1];
					boolean flag = true;
					for (int i = 0; i < allChatRoomList.size(); i++) {
						if (allChatRoomList.get(i).getNumberRoom() == roomNumber) {
							allChatRoomList.get(i).setHeader(headerName);
						}
					}
					UserInfo mandateUser = null;
					for (UserInfo users : chatUserList.get(roomNumber).keySet()) {
						if (headerName.equals(users.getNickName()) && flag) {
							mandateUser = users;
							flag = false;
						}
					}
					onlyChatSendData(new SendData(OperationCodeServer.HEAD_UPDATE_OK, headerName), roomNumber,
							mandateUser);
					setMandate(new SendData(OperationCodeServer.HEAD_UPDATE, headerName), roomNumber, mandateUser);
					chatBroadcast(headerName + "님이 방장을 위임받으셨습니다", roomNumber);

					// 방장 강퇴권한
				} else if (data.getCode() == OperationCodeClient.KICK_OUT) {
					String kickName = (String) data.getData()[0];
					int roomNumber = (int) data.getData()[1];
					boolean flag = true;
					UserInfo kickUser = null;
					for (UserInfo users : chatUserList.get(roomNumber).keySet()) {
						if (kickName.equals(users.getNickName())) {
							kickUser = users;
						}
					}
					RoomInfo myRoomInfo = null;
					for (RoomInfo rooms : roomList) {
						if (rooms.getNumberRoom() == roomNumber) {
							myRoomInfo = rooms;
						}
					}

					Vector<UserInfo> chatList = new Vector<>(chatUserList.get(roomNumber).keySet());
					Vector<UserInfo> waitList = new Vector<>(waitingUserList.keySet());
					for (int i = 0; i < chatList.size(); i++) {
						if (chatList.get(i).getNickName().equals(kickUser.getNickName())) {
							waitingUserList.put(kickUser, chatUserList.get(roomNumber).get(kickUser));
							waitList = new Vector<>(waitingUserList.keySet());
							onlyChatSendData(new SendData(OperationCodeServer.KICK_OUT_OK, waitList, roomList),
									roomNumber, kickUser);
							chatUserList.get(roomNumber).remove(kickUser);
						}
					}
					chatList = new Vector<>(chatUserList.get(roomNumber).keySet());

					for (int roomNums : chatUserList.keySet()) {
						if (roomNums == roomNumber) {
							myRoomInfo.setCurrentNum(chatUserList.get(roomNums).size());
							setChatUserUpdate(
									new SendData(OperationCodeServer.CHATTING_USER_UPDATE, chatList, myRoomInfo),
									roomNumber);
							updateWaitingRoom(
									new SendData(OperationCodeServer.ALL_WAITING_ROOM_UPDATE, waitList, myRoomInfo));
						}
					}
					chatBroadcast(kickName + "님을 강퇴하였습니다.", roomNumber);

					// 방설정
				} else if (data.getCode() == OperationCodeClient.ROOM_SETTING) {
					RoomInfo roomSetting = (RoomInfo) data.getData()[0];
					int roomNum = roomSetting.getNumberRoom();
					boolean flag = true;
					// 유저수보다 인원을 적게 설정할때
					if (chatUserList.get(roomNum).size() > roomSetting.getMaxNumbers()) {
						oos.writeObject(new SendData(OperationCodeServer.ROOM_SETTING_FAIL));
						flag = false;
					}
					// 제목 중복
					if (flag) {
						for (int i = 0; i < roomList.size(); i++) {
							if (roomList.get(i).getNumberRoom() != roomSetting.getNumberRoom()) {
								if (roomList.get(i).getTitle().equals(roomSetting.getTitle())) {
									oos.writeObject(new SendData(OperationCodeServer.ROOM_SETTING_FAIL2));
									flag = false;
								}
							}
						}
					}
					// 방설정 성공
					if (flag) {
						for (int i = 0; i < roomList.size(); i++) {
							if (roomList.get(i).getNumberRoom() == roomNum) {
								roomList.set(i, roomSetting);
							}
						}
						for (int i = 0; i < allChatRoomList.size(); i++) {
							if (allChatRoomList.get(i).getNumberRoom() == roomNum) {
								allChatRoomList.get(i).setTitle(roomSetting.getTitle());
								allChatRoomList.get(i).setMaxNumbers(roomSetting.getMaxNumbers());
								allChatRoomList.get(i).setPassword(roomSetting.getPassword());
								allChatRoomList.get(i).setRoomInfo(roomSetting);
							}
						}
						oos.writeObject(new SendData(OperationCodeServer.ROOM_SETTING_SUCCESS));

						setChatUserUpdate(new SendData(OperationCodeServer.ROOM_SETTING_UPDATE, roomSetting), roomNum);

						setWaitingUpdate(new SendData(OperationCodeServer.CHATTING_LIST_UPDATE, roomSetting));
						chatBroadcast("방 환경이 변경되었습니다.", roomNum);
					}

					// 모든유저 목록 보기
				} else if (data.getCode() == OperationCodeClient.VIEW_ALL_USER) {
					Vector<UserInfo> waitList = new Vector<>(waitingUserList.keySet());
					oos.writeObject(new SendData(OperationCodeServer.VIEW_OK, waitList));
					oos.flush();
					oos.reset();

					// 초대 송신하기
				} else if (data.getCode() == OperationCodeClient.INVITE_CHAT) {
					String inviteName = (String) data.getData()[0];
					int roomNum = (int) data.getData()[1];
					String myId = (String) data.getData()[2];
					UserInfo inviteInfo = null;
					UserInfo myUserInfo = null;
					for (User user : allUserList) {
						if (inviteName.equals(user.getNickName())) {
							inviteInfo = new UserInfo(user.isGender(), user.getNickName());
						}
						if (myId.equals(user.getId())) {
							myUserInfo = new UserInfo(user.isGender(), user.getNickName());
						}
					}
					boolean flag = true;
					synchronized (inviteList) {
						if (inviteList.size() == 0) {
							inviteList.add(inviteInfo);
							onlyWaitSendData(new SendData(OperationCodeServer.INVITE_CHAT_OK, myUserInfo.getNickName(),
									roomNum, inviteInfo), inviteInfo);
						} else {
							for (int i = 0; i < inviteList.size(); i++) {
								if ((inviteList.get(i).getNickName().equals(inviteInfo.getNickName()))) {
									flag = false;
								}
							}
							if(flag) {
								inviteList.add(inviteInfo);
								onlyWaitSendData(new SendData(OperationCodeServer.INVITE_CHAT_OK,
										myUserInfo.getNickName(), roomNum, inviteInfo), inviteInfo);
							}
						}
					}

					// 초대 수락
				} else if (data.getCode() == OperationCodeClient.INVITE_RESPONSE_YES) {
					int roomNum = (int) data.getData()[0];
					String id = (String) data.getData()[1];
					UserInfo inviteUser = (UserInfo) data.getData()[2];
					RoomInfo myRoomInfo = null;
					UserInfo myUserInfo = null;
					String headerNickName = "";

					boolean flag = true;
					for (User user : allUserList) {
						if (id.equals(user.getId())) {
							myUserInfo = new UserInfo(user.isGender(), user.getNickName());
						}

					}
					for (int i = 0; i < allChatRoomList.size(); i++) {
						if (allChatRoomList.get(i).getNumberRoom() == roomNum) {
							headerNickName = allChatRoomList.get(i).getHeader();
						}
					}
					synchronized (roomList) {
						for (RoomInfo rooms : roomList) {
							if (rooms.getNumberRoom() == roomNum) {
								myRoomInfo = rooms;
							}
						}
					}
					synchronized (inviteList) {
						for (int i = 0; i < inviteList.size(); i++) {
							if (inviteList.get(i).getNickName().equals(inviteUser.getNickName())) {
								inviteList.remove(i);
							}
						}
					}

					if (chatUserList.get(roomNum).size() >= myRoomInfo.getMaxNumbers()) {
						oos.writeObject(new SendData(OperationCodeServer.INVITE_CHAT_FAIL));
						oos.flush();
						oos.reset();
						flag = false;

					}
					if (flag) {
						for (int roomNums : chatUserList.keySet()) {
							if (roomNums == roomNum) {
								if (chatUserList.get(roomNums).size() <= myRoomInfo.getMaxNumbers()) {
									chatBroadcast(myUserInfo.getNickName() + "님이 입장하셨습니다", roomNum);
									chatUserList.get(roomNums).put(myUserInfo, oos);
									Vector<UserInfo> chatList = new Vector<>(chatUserList.get(roomNums).keySet());
									Vector<UserInfo> waitList = new Vector<>(waitingUserList.keySet());
									myRoomInfo.setCurrentNum(chatUserList.get(roomNums).size());
									synchronized (waitingUserList) {
										for (int i = 0; i < waitList.size(); i++) {
											if (waitList.get(i).getNickName().equals(myUserInfo.getNickName())) {
												waitingUserList.remove(waitList.get(i));
											}
										}
										waitList = new Vector<>(waitingUserList.keySet());
									}
									oos.writeObject(new SendData(OperationCodeServer.INVITE_UPDATE, chatList,
											myRoomInfo, headerNickName));
									oos.flush();
									oos.reset();

									setChatUserUpdate(new SendData(OperationCodeServer.CHATTING_USER_UPDATE, chatList,
											myRoomInfo), roomNum);

									updateWaitingRoom(new SendData(OperationCodeServer.ALL_WAITING_ROOM_UPDATE,
											waitList, myRoomInfo));
								}
							}
						}
					}
					// 초대 거부
				} else if (data.getCode() == OperationCodeClient.INVITE_RESPONSE_NO) {
					String inviteId = (String) data.getData()[0];
					int roomNum = (int) data.getData()[1];
					String myId = (String) data.getData()[2];
					UserInfo inviteUser = (UserInfo) data.getData()[3];
					UserInfo invUser = null;
					String nickName = null;
					for (User user : allUserList) {
						if (inviteId.equals(user.getId())) {
							invUser = new UserInfo(user.isGender(), user.getNickName());
						}
						if (myId.equals(user.getId())) {
							nickName = user.getNickName();
						}
					}
					synchronized (inviteList) {
						for (int i = 0; i < inviteList.size(); i++) {
							if (inviteList.get(i).getNickName().equals(inviteUser.getNickName())) {
								inviteList.remove(i);
							}
						}
					}
					
					onlyChatSendData(new SendData(OperationCodeServer.INVITE_CHAT_FAIL2, nickName), roomNum, invUser);

					// 귓속말 송신
				} else if (data.getCode() == OperationCodeClient.WHISPER_CHAT) {
					String whisperChat = (String) data.getData()[0];
					String id = (String) data.getData()[1];
					String mynickName = "";
					boolean flag = true;
					UserInfo whisperUser = null;
					int to = 0;
					int from = 0;
					String nickName = "";
					String chat = "";
					Vector<UserInfo> waitList = new Vector<>(waitingUserList.keySet());

					try {
						if (whisperChat == null || !(whisperChat.substring(0, 3).equals("/to"))) {
							oos.writeObject(new SendData(OperationCodeServer.WHISPER_CHAT_FAIL2, "귓말 형식이 적합하지 않습니다."));
							oos.flush();
							oos.reset();
							flag = false;
						}
					} catch (StringIndexOutOfBoundsException e) {
						oos.writeObject(new SendData(OperationCodeServer.WHISPER_CHAT_FAIL2, "귓말 형식이 적합하지 않습니다."));
						oos.flush();
						oos.reset();
						flag = false;
					}
					if (flag) {
						try {
							to = whisperChat.indexOf(" ");
							from = whisperChat.indexOf(" ", to + 1);
							nickName = whisperChat.substring(to + 1, from);
							chat = whisperChat.substring(from + 1);
						} catch (StringIndexOutOfBoundsException e) {
							oos.writeObject(new SendData(OperationCodeServer.WHISPER_CHAT_FAIL2, "귓말 형식이 적합하지 않습니다."));
							oos.flush();
							oos.reset();
							flag = false;
						}
					}
					for (User user : allUserList) {
						if (user.getId().equals(id)) {
							mynickName = user.getNickName();
						}
						if (user.getNickName().equals(nickName)) {
							whisperUser = new UserInfo(user.isGender(), user.getNickName());
						}
					}
					// 자기자신한테 x, 유저존재x
					if (flag) {
						if (mynickName.equals(nickName) || nickName.equals("") || whisperUser == null) {
							oos.writeObject(new SendData(OperationCodeServer.WHISPER_CHAT_FAIL,
									"유저가 존재하지 않거나 자기자신한테 귓말을 보낼 수 없습니다."));
							oos.flush();
							oos.reset();
							flag = false;
						}
					}
					if (flag) {
						boolean who = true;
						for (UserInfo users : waitList) {
							if (users.getNickName().equals(nickName)) {
								whisperUser = users;
								oos.writeObject(new SendData(OperationCodeServer.WHISPER_CHAT_SUCCESS,
										"[" + whisperUser.getNickName() + "님에게 귓속말을 보냈습니다.", chat));
								onlyWaitSendData(new SendData(OperationCodeServer.WHISPER_CHAT_OK,
										"[" + mynickName + "님이 귓속말을 보냈습니다]", chat), whisperUser);
								who = false;
							}
						}
						if (who) {
							oos.writeObject(new SendData(OperationCodeServer.WHISPER_CHAT_SUCCESS,
									"[" + whisperUser.getNickName() + "님에게 귓속말을 보냈습니다.", chat));
							onlyChatSendData(new SendData(OperationCodeServer.WHISPER_CHAT_OK,
									"[" + mynickName + "님이 귓속말을 보내셨습니다]", chat), whisperUser);
						}
					}
					// 채팅방 검색
				} else if (data.getCode() == OperationCodeClient.SEARCH_REQUEST) {
					String title = (String) data.getData()[0];
					boolean flag = true;
					for (RoomInfo rooms : roomList) {
						if (rooms.getTitle().equals(title)) {
							oos.writeObject(new SendData(OperationCodeServer.SEARCH_RESULT, rooms));
							oos.flush();
							oos.reset();
							flag = false;
						}
					}
					if (flag) {
						oos.writeObject(new SendData(OperationCodeServer.SEARCH_RESULT_FAIL));
						oos.flush();
						oos.reset();
					}
					// 채팅방 검색결과 들어가기
				} else if (data.getCode() == OperationCodeClient.SEARCH_RESPONSE_YES) {
					RoomInfo room = (RoomInfo) data.getData()[0];
					int roomNum = room.getNumberRoom();
					String id = (String) data.getData()[1];
					UserInfo myUserInfo = null;
					String headerNickName = "";

					for (User user : allUserList) {
						if (user.getId().equals(id)) {
							myUserInfo = new UserInfo(user.isGender(), user.getNickName());
						}
					}
					for (int i = 0; i < allChatRoomList.size(); i++) {
						if (allChatRoomList.get(i).getNumberRoom() == roomNum) {
							headerNickName = allChatRoomList.get(i).getHeader();
						}
					}
					for (int roomNums : chatUserList.keySet()) {
						// 공개방일때
						if (roomNums == roomNum) {
							if (room.getPassword().equals("")) {
								boolean flag = true;
								if (chatUserList.get(roomNums).size() >= room.getMaxNumbers()) {
									oos.writeObject(new SendData(OperationCodeServer.ROOM_FAIL));
									oos.flush();
									oos.reset();
									flag = false;
								}
								if (flag) {
									chatBroadcast(myUserInfo.getNickName() + "님이 입장하셨습니다", roomNum);
									chatUserList.get(roomNums).put(myUserInfo, oos);
									Vector<UserInfo> chatList = new Vector<>(chatUserList.get(roomNum).keySet());
									Vector<UserInfo> waitList = new Vector<>(waitingUserList.keySet());
									room.setCurrentNum(chatUserList.get(roomNums).size());
									synchronized (waitingUserList) {
										for (int i = 0; i < waitList.size(); i++) {
											if (waitList.get(i).getNickName().equals(myUserInfo.getNickName())) {
												waitingUserList.remove(waitList.get(i));
											}
										}
										waitList = new Vector<>(waitingUserList.keySet());
									}
									oos.writeObject(
											new SendData(OperationCodeServer.ROOM_OK, chatList, room, headerNickName));
									oos.flush();
									oos.reset();
									setChatUserUpdate(
											new SendData(OperationCodeServer.CHATTING_USER_UPDATE, chatList, room),
											roomNum);

									updateWaitingRoom(
											new SendData(OperationCodeServer.ALL_WAITING_ROOM_UPDATE, waitList, room));
									// 비밀방일떄
								}
							} else {
								boolean flag = true;
								String pw = (String) data.getData()[2];
								if (pw.equals(room.getPassword())) {
									if (chatUserList.get(roomNums).size() >= room.getMaxNumbers()) {
										oos.writeObject(new SendData(OperationCodeServer.ROOM_FAIL));
										oos.flush();
										oos.reset();
										flag = false;
									}
									if (flag) {
										chatBroadcast(myUserInfo.getNickName() + "님이 입장하셨습니다", roomNum);
										chatUserList.get(roomNums).put(myUserInfo, oos);
										Vector<UserInfo> chatList = new Vector<>(chatUserList.get(roomNum).keySet());
										Vector<UserInfo> waitList = new Vector<>(waitingUserList.keySet());
										room.setCurrentNum(chatUserList.get(roomNums).size());
										synchronized (waitingUserList) {
											for (int i = 0; i < waitList.size(); i++) {
												if (waitList.get(i).getNickName().equals(myUserInfo.getNickName())) {
													waitingUserList.remove(waitList.get(i));
												}
											}
											waitList = new Vector<>(waitingUserList.keySet());
										}
										oos.writeObject(new SendData(OperationCodeServer.ROOM_OK, chatList, room,
												headerNickName));
										oos.flush();
										oos.reset();
										setChatUserUpdate(
												new SendData(OperationCodeServer.CHATTING_USER_UPDATE, chatList, room),
												roomNum);

										updateWaitingRoom(new SendData(OperationCodeServer.ALL_WAITING_ROOM_UPDATE,
												waitList, room));
									}
								}
							}
						}
					}
				}
			}
		} catch (

		Exception e) {
		}
	}

	private void updateWaitingRoom(SendData sd) {
		synchronized (waitingUserList) {
			Collection<ObjectOutputStream> collection = waitingUserList.values();
			Iterator<ObjectOutputStream> iter = collection.iterator();
			while (iter.hasNext()) {
				try {
					ObjectOutputStream oos = iter.next();
					oos.writeObject(sd);
					oos.flush();
					oos.reset();
				} catch (IOException e) {
				}
			}
		}
	}

	private void onlyWaitSendData(SendData sd, UserInfo user) {
		Object obj = waitingUserList.get(user);
		if (obj != null) {
			try {
				ObjectOutputStream oos = (ObjectOutputStream) obj;
				oos.writeObject(sd);
				oos.flush();
				oos.reset();
			} catch (IOException e) {
			}
		}
	}

	private void onlyChatSendData(SendData sd, int roomNumber, UserInfo user) {
		Object obj = chatUserList.get(roomNumber).get(user);
		if (obj != null) {
			try {
				ObjectOutputStream oos = (ObjectOutputStream) obj;
				oos.writeObject(sd);
				oos.flush();
				oos.reset();
			} catch (IOException e) {
			}
		}
	}

	private void onlyChatSendData(SendData sd, UserInfo user) {
		synchronized (chatUserList) {
			boolean flag = true;
			Collection<HashMap<UserInfo, ObjectOutputStream>> collection = chatUserList.values();
			Iterator<HashMap<UserInfo, ObjectOutputStream>> iter = collection.iterator();

			while (iter.hasNext()) {
				HashMap<UserInfo, ObjectOutputStream> map = iter.next();

				for (UserInfo users : map.keySet()) {
					if (users.getNickName().equals(user.getNickName()) && flag) {
						ObjectOutputStream oos = (ObjectOutputStream) map.get(user);
						try {
							oos.writeObject(sd);
							oos.flush();
							oos.reset();
							flag = false;
						} catch (IOException e) {
						}
					}
				}
			}
		}

	}

	private void setMandate(SendData sd, int roomNumber, UserInfo user) {
		synchronized (chatUserList) {
			for (UserInfo info : chatUserList.get(roomNumber).keySet()) {
				if (info.equals(user)) {
				} else {
					try {
						ObjectOutputStream oos = chatUserList.get(roomNumber).get(info);
						oos.writeObject(sd);
						oos.flush();
						oos.reset();
					} catch (Exception e) {
					}
				}
			}
		}
	}

	private void setChatUserUpdate(SendData sd, int roomNumber) {
		synchronized (chatUserList) {
			Collection<ObjectOutputStream> collection = chatUserList.get(roomNumber).values();
			Iterator<ObjectOutputStream> iter = collection.iterator();
			while (iter.hasNext()) {
				try {
					ObjectOutputStream oos = iter.next();
					oos.writeObject(sd);
					oos.flush();
					oos.reset();
				} catch (Exception e) {
				}
			}
		}
	}

	private void moveChatBroadcast(String path, int roomNumber, String nickName) {
		synchronized (chatUserList) {
			Collection<ObjectOutputStream> collection = chatUserList.get(roomNumber).values();
			Iterator<ObjectOutputStream> iter = collection.iterator();
			while (iter.hasNext()) {
				try {
					ObjectOutputStream oos = iter.next();
					oos.writeObject(new SendData(OperationCodeServer.CHATROOM_EMOT_CHAT_MOVE_OK, path, nickName));
					oos.flush();
					oos.reset();
				} catch (Exception e) {
				}
			}
		}
	}

	private void chatBroadcast(ImageIcon i, int roomNumber, String nickName) {
		synchronized (chatUserList) {
			Collection<ObjectOutputStream> collection = chatUserList.get(roomNumber).values();
			Iterator<ObjectOutputStream> iter = collection.iterator();
			while (iter.hasNext()) {
				try {
					ObjectOutputStream oos = iter.next();
					oos.writeObject(new SendData(OperationCodeServer.CHATROOM_EMOT_CHAT_OK, i, nickName));
					oos.flush();
					oos.reset();
				} catch (Exception e) {
				}
			}
		}
	}

	private void chatBroadcast(String msg, int roomNumber) {
		synchronized (chatUserList) {
			Collection<ObjectOutputStream> collection = chatUserList.get(roomNumber).values();
			Iterator<ObjectOutputStream> iter = collection.iterator();
			while (iter.hasNext()) {
				try {
					ObjectOutputStream oos = iter.next();
					oos.writeObject(new SendData(OperationCodeServer.CHATROOM_CHAT_OK, msg));
					oos.flush();
					oos.reset();
				} catch (Exception e) {
				}
			}
		}
	}

	private void chatBroadcast(String msg, int roomNumber, String nickName) {
		synchronized (chatUserList) {
			Collection<ObjectOutputStream> collection = chatUserList.get(roomNumber).values();
			Iterator<ObjectOutputStream> iter = collection.iterator();
			while (iter.hasNext()) {
				try {
					ObjectOutputStream oos = iter.next();
					oos.writeObject(new SendData(OperationCodeServer.CHATROOM_CHAT_OK2, msg, nickName));
					oos.flush();
					oos.reset();
				} catch (Exception e) {
				}
			}
		}
	}

	private void setWaitingUpdate(SendData sd) {
		synchronized (waitingUserList) {
			Collection<ObjectOutputStream> collection = waitingUserList.values();
			Iterator<ObjectOutputStream> iter = collection.iterator();
			while (iter.hasNext()) {
				try {
					ObjectOutputStream oos = iter.next();
					oos.writeObject(sd);
					oos.flush();
					oos.reset();
				} catch (IOException e) {
				}
			}
		}
	}

	private void broadcast(String msg, String nickName) {
		synchronized (waitingUserList) {
			Collection<ObjectOutputStream> collection = waitingUserList.values();
			Iterator<ObjectOutputStream> iter = collection.iterator();
			while (iter.hasNext()) {
				try {
					ObjectOutputStream oos = iter.next();
					oos.writeObject(new SendData(OperationCodeServer.WAITINGROOM_CHAT_OK, msg, nickName));
					oos.flush();
					oos.reset();
				} catch (IOException e) {
				}
			}
		}

	}

	private void broadcast(ImageIcon i, String nickName) {
		synchronized (waitingUserList) {
			Collection<ObjectOutputStream> collection = waitingUserList.values();
			Iterator<ObjectOutputStream> iter = collection.iterator();
			while (iter.hasNext()) {
				try {
					ObjectOutputStream oos = iter.next();
					oos.writeObject(new SendData(OperationCodeServer.WAITINGROOM_EMOT_CHAT_OK, i, nickName));
					oos.flush();
					oos.reset();
				} catch (IOException e) {
				}
			}
		}
	}

	private void movebroadcast(String path, String nickName) {
		synchronized (waitingUserList) {
			Collection<ObjectOutputStream> collection = waitingUserList.values();
			Iterator<ObjectOutputStream> iter = collection.iterator();
			while (iter.hasNext()) {
				try {
					ObjectOutputStream oos = iter.next();
					oos.writeObject(new SendData(OperationCodeServer.WAITINGROOM_EMOT_CHAT_MOVE_OK, path, nickName));
					oos.flush();
					oos.reset();
				} catch (IOException e) {
				}
			}
		}
	}

}
