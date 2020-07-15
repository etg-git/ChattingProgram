package kr.ac.green;

import java.io.Serializable;

public class RoomInfo implements Serializable {
	private String title;
	private String subject;
	private int maxNumbers;
	private int currentNum;
	private int numberRoom;
	private String password;
	
	public RoomInfo(String title, String subject, int maxNumbers, int currentNum, int numberRoom, String password) {
		super();
		this.title = title;
		this.subject = subject;
		this.maxNumbers = maxNumbers;
		this.currentNum = currentNum;
		this.numberRoom = numberRoom;
		this.password = password;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getMaxNumbers() {
		return maxNumbers;
	}

	public void setMaxNumbers(int maxNumbers) {
		this.maxNumbers = maxNumbers;
	}

	public int getCurrentNum() {
		return currentNum;
	}

	public void setCurrentNum(int currentNum) {
		this.currentNum = currentNum;
	}

	public int getNumberRoom() {
		return numberRoom;
	}

	public void setNumberRoom(int numberRoom) {
		this.numberRoom = numberRoom;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof RoomInfo)) {
			return false;
		}
		RoomInfo info = (RoomInfo) obj;
		
		return title.equals(info.getTitle());
	}
}
