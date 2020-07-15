package kr.ac.green;

import java.io.Serializable;

public class UserInfo implements Serializable {
	private boolean gender;
	private String nickName;
	
	public UserInfo(boolean gender, String nickName) {
		super();
		this.gender = gender;
		this.nickName = nickName;
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
	@Override
	public int hashCode() {
		int result = nickName.hashCode() % 3;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof UserInfo)) {
			return false;
		}
		UserInfo user = (UserInfo) obj;
		
		return user.getNickName().equals(nickName);
	}
	@Override
	public String toString() {
		return nickName;
	}
}
