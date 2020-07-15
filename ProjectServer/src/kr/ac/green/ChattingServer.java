package kr.ac.green;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ChattingServer implements Serializable {
	private HashMap<UserInfo, ObjectOutputStream> waitingUserList;
	private Vector<ChatRoom> allChatRoomList;
	private HashMap<Integer, HashMap<UserInfo, ObjectOutputStream>> chatUserList;
	private Vector<User> allUserList;
	private Vector<RoomInfo> roomList;
	private Vector<UserInfo> connectUserList;
	private Vector<UserInfo> inviteList;

	public ChattingServer() {
		try {
			FileReader fr = new FileReader("..\\users\\user.txt");
			BufferedReader br = new BufferedReader(fr);

			String info = null;

			ServerSocket socket = new ServerSocket(10001);
			System.out.println("접속을 시작합니다");
			waitingUserList = new HashMap<>();
			allChatRoomList = new Vector<>();
			chatUserList = new HashMap<>();
			allUserList = new Vector<>();
			roomList = new Vector<>();
			connectUserList = new Vector<>();
			inviteList = new Vector<>();
			while ((info = br.readLine()) != null) {
				StringTokenizer tk = new StringTokenizer(info);
				String id = tk.nextToken();
				String pw = tk.nextToken();
				String nickName = tk.nextToken();
				String myphoneNumber = tk.nextToken();
				String myName = tk.nextToken();
				String question = tk.nextToken();
				String answer = tk.nextToken();
				String gender = tk.nextToken();
				boolean mygender = true;
				if (gender.equals("false")) {
					mygender = false;
				}
				allUserList.add(new User(id, pw, mygender, myName, nickName, myphoneNumber, question, answer));

			}
			while (true) {
				Socket sock = socket.accept();
				ChatThread chatThread = new ChatThread(sock, waitingUserList, allChatRoomList, chatUserList,
						allUserList, roomList, connectUserList, inviteList);
				chatThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new ChattingServer();
	}
}
