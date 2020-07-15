package kr.ac.green;

import java.io.Serializable;

public class OperationCodeClient  implements Serializable{
	public static final int SIGN_UP =100;
	public static final int FIND_ID = 110;
	public static final int PW_SEARCH = 120;
	public static final int PW_CHANGE = 130;
	public static final int WITHDRAWAL = 150;
	public static final int LOGIN = 200;
	public static final int SEARCH_REQUEST = 250;
	public static final int CREATE_ROOM_REQUEST = 300;
	public static final int ROOM_REQUEST = 310;
	public static final int ROOM_EXIT = 320;
	public static final int LEADER_DISPEAR = 325;
	public static final int VIEW_ALL_USER = 326;
	public static final int ROOM_SETTING = 330;
	public static final int HEAD_MANDATE = 340;
	public static final int KICK_OUT = 350;
	public static final int LOGOUT = 360;
	public static final int WAITING_ROOM_CHAT = 400;
	public static final int WAITING_ROOM_EMOTICON_CHAT = 410;
	public static final int WAITING_ROOM_EMOTICON_CHAT_MOVE = 411;
	public static final int ROOM_CHAT = 420;
	public static final int ROOM_EMOTICON_CHAT = 430;
	public static final int ROOM_EMOTICON_CHAT_MOVE = 431;
	public static final int WHISPER_CHAT = 440;
	public static final int INVITE_CHAT = 500;
	public static final int INVITE_RESPONSE_YES = 510;
	public static final int INVITE_RESPONSE_NO = 520;
	public static final int SEARCH_RESPONSE_YES = 530;
}
