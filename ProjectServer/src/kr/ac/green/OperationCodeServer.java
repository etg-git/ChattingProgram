package kr.ac.green;

import java.io.Serializable;

public class OperationCodeServer implements Serializable {
	//리스트 갱신
	public static final int ALL_WAITING_ROOM_UPDATE = 501;
	public static final int ALL_WAITING_ROOM_UPDATE_ERROR = 511;
	public static final int CHATTING_USER_UPDATE = 502;
	public static final int CHATTING_USER_UPDATE_ERROR = 512;
	public static final int WAITING_USER_UPDATE = 503;
	public static final int WAITING_USER_UPDATE_ERROR = 513;
	public static final int CHATTING_LIST_UPDATE = 504;
	
	//회원가입
	public static final int SIGN_UP_OK = 1000;
	public static final int SIGN_UP_FAIL = 1001;
	public static final int FIND_ID_OK = 1100;
	public static final int FIND_ID_FAIL1 = 1101;
	public static final int FIND_ID_FAIL2 = 1102;
	public static final int PW_SEARCH = 1200;
	public static final int PW_SEARCH_FAIL1 = 1201;
	public static final int PW_SEARCH_FAIL2 = 1202;
	public static final int PW_SEARCH_FAIL3 = 1203;
	public static final int PW_SEARCH_FAIL4 = 1204;
	public static final int PW_CHANGE = 1300;
	public static final int WITHDRAWAL_FAIL = 1501;
	public static final int WITHDRAWAL_OK = 1500;
	
	//로그인
	public static final int CONNECT_FAIL = 2001;
	public static final int CONNECT_FAIL2 = 2002;
	public static final int CONNECT_UPDATE = 2003;
	//검색
	public static final int SEARCH_RESULT = 2501;
	public static final int SEARCH_RESULT_OK = 2502;
	public static final int SEARCH_RESULT_FAIL = 2503;
	
	//방만들기
	public static final int CREATE_FAIL = 3001;
	
	//방들어가기
	public static final int ROOM_FAIL = 3100;
	public static final int ROOM_FAIL2 = 3102;
	public static final int ROOM_OK = 3101;
	
	//방나가기
	public static final int ROOM_EXIT_OK = 3150;
		
	//방장 랜덤위임
	public static final int LEADER_DISPEAR_OK = 3250;
	
	//방설정
	public static final int VIEW_OK = 3260;
	public static final int ROOM_SETTING_SUCCESS = 3300;
	public static final int ROOM_SETTING_UPDATE = 3301;
	public static final int CHAT_ROOM_UPDATE = 3310;
	public static final int CHAT_ROOM_UPDATE_ERROR = 3311;
	public static final int ROOM_SETTING_FAIL = 3302;
	public static final int ROOM_SETTING_FAIL2 = 3303;
	
	//방장
	public static final int HEAD_UPDATE = 3400;
	public static final int HEAD_UPDATE_OK = 3405;
	public static final int HEAD_UPDATE_SHOW = 3401;
	public static final int HEAD_MANDATE_FAIL = 3402;
	public static final int HEAD_MANDATE_FAIL2 = 3403;
	
	public static final int KICK_OUT_OK = 3500;
	public static final int KICK_OUT_FAIL = 3502;
	
	//채팅
	public static final int WAITINGROOM_CHAT_OK = 4000;
	public static final int WAITINGROOM_EMOT_CHAT_OK = 4100;
	public static final int WAITINGROOM_EMOT_CHAT_MOVE_OK = 4110;
	public static final int CHATROOM_CHAT_OK = 4200;
	public static final int CHATROOM_CHAT_OK2 = 4250;
	public static final int CHATROOM_EMOT_CHAT_OK = 4300;
	public static final int CHATROOM_EMOT_CHAT_MOVE_OK = 4310;
	public static final int WHISPER_CHAT_OK = 4400;
	public static final int WHISPER_CHAT_SUCCESS = 4403;
	public static final int WHISPER_CHAT_FAIL = 4401;
	public static final int WHISPER_CHAT_FAIL2 = 4402;
	//초대보내기
	public static final int INVITE_CHAT_OK = 5000;
	public static final int INVITE_CHAT_FAIL = 5001;
	public static final int INVITE_CHAT_FAIL2 = 5002;
	public static final int INVITE_CHAT_FAIL3 = 5003;
	public static final int INVITE_UPDATE = 5100;
	public static final int INVITE_CHAT_CONFIRM_FAIL = 5101;
	public static final int INVITE_CHAT_CONFIRM_FAIL2 = 5102;
	public static final int INVITE_NOPE = 5200;
}
