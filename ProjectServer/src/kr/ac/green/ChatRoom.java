package kr.ac.green;

import java.io.Serializable;
import java.util.Vector;

public class ChatRoom implements Serializable{
	private String title;
	private int maxNumbers;
	private String password;
	private int numberRoom;
	private Vector<UserInfo> chatUserList;
	private String header;
	private RoomInfo roomInfo;
	
	public ChatRoom(String title, int maxNumbers, String password, int numberRoom, Vector<UserInfo> chatUserList,
			String header, RoomInfo roomInfo) {
		super();
		this.title = title;
		this.maxNumbers = maxNumbers;
		this.password = password;
		this.numberRoom = numberRoom;
		this.chatUserList = chatUserList;
		this.header = header;
		this.roomInfo = roomInfo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getMaxNumbers() {
		return maxNumbers;
	}

	public void setMaxNumbers(int maxNumbers) {
		this.maxNumbers = maxNumbers;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getNumberRoom() {
		return numberRoom;
	}

	public void setNumberRoom(int numberRoom) {
		this.numberRoom = numberRoom;
	}

	public Vector<UserInfo> getChatUserList() {
		return chatUserList;
	}

	public void setChatUserList(Vector<UserInfo> chatUserList) {
		this.chatUserList = chatUserList;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public RoomInfo getRoomInfo() {
		return roomInfo;
	}

	public void setRoomInfo(RoomInfo roomInfo) {
		this.roomInfo = roomInfo;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof ChatRoom || obj instanceof RoomInfo)) {
			return false;
		}
		RoomInfo info = (RoomInfo) obj;
		
		return title.equals(info.getTitle());
	}
	@Override
	public String toString() {
		return numberRoom + "";
	}
}
