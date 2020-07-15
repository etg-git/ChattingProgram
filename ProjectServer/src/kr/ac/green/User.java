package kr.ac.green;

import java.io.Serializable;

public class User implements Serializable {
	public static final int WAITING = 0;
	public static final int CHATTING = 1;
	private String id;
	private String pw;
	private boolean gender;
	private String nickName;
	private String phoneNumber;
	private String name;
	private String question;
	private String answer;
	
	private int status = WAITING;
	
	
	public User(String id, String pw, boolean gender, String name, String nickName,  String phoneNumber, String question,
			String answer) {
		super();
		this.id = id;
		this.pw = pw;
		this.gender = gender;
		this.name = name;
		this.nickName = nickName;
		this.phoneNumber = phoneNumber;
		this.question = question;
		this.answer = answer;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public boolean isGender() {
		return gender;
	}

	public void setGender(boolean gender) {
		this.gender = gender;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	/*
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof User)) {
			return false;
		}
		User user = (User) obj;
		return id.equals(user.getId()) ||
				phoneNumber.equals(user.getPhoneNumber()) || 
				nickName.equals(user.getNickName()) || 
				name.equals(user.getName()) ||
				question.equals(user.getQuestion());
	}
	*/
	@Override
	public String toString() {
		return id;
	}
}
