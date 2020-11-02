import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

interface MyProtocol {

	//@---서버---@
	public static final int ID_CHECK = 0; //아이디 중복 확인하기
	public static final int ID_YES = 1; //아이디 생성 성공
	public static final int ID_NO = 2; //아이디 생성 실패
	public static final int ID_ADD = 3; // 생성한 아이디 추가
	public static final int ID_UPDATE = 4; // 생성한 아이디 다른 유저들에게 업데이트 해주기
	
	
	//public static final int JOIN_LIST = 10; //전체 접속 인원 리스트 주기
	//public static final int GIVE_HOST = 11; //방장주기
	
	public static final int CREATE_ROOM = 21; //방 만들기
	
	public static final int ROOM_LIST = 22; //방 목록 보내주기
	
	public static final int ROOM_CHECK = 24; //방 중복 체크
	public static final int ROOM_NO = 8; //방 생성 실패
	public static final int ROOM_YES = 9; //방 생성 성공
	
	
	
//	public static final int ROOM_COUNT = 23; //방 인원 확인하기
//	public static final int ROOM_REMOVE = 30; //방 인원 확인 후 아무도 없으면 방 지우기
	
	public static final int MESSAGE = 31; //일반 채팅
	
	//방장
//	public static final int HINT = 14; //힌트 받고 다른 클라이언트에게 보내기 
//	public static final int QUESTION = 13; //랜덤적 문제 방장에게 보내주기 
////	public static final int GIVE_HOST = 11; //선택된 유저에게 방장권한 넘겨주기 
//	public static final int KICK = 19; //입력받은 아이디 방에서 강퇴하기
	
	//@---클라이언트---@
	
	public static final int ROOM_JOIN = 7; //방 입장하기  
	
	
//	public static final int ROOM_NEW_JOIN = 6; //방 입장하기 
	
//	public static final int JOIN_LIST = 10; //전체 접속 인원 리스트 받기 
	public static final int HOST_CHECK = 20; //방장 여부 확인 
//	public static final int ROOM_LIST =22; //방 목록 받기 
//	public static final int CREATE_ROOM = 21; //방 만들기
//	public static final int MESSAGE = 31; //일반 채팅
	
	
	public static final int CLOSE = 99; //종료하기 
	//방장
//	public static final int HINT = 14; //힌트 보내기 (/힌트 ID : %%%%)  
//	public static final int GIVE_HOST = 11; //방장 권한 넘겨주기 (/방장 ID)
	public static final int GET_HOST = 15; //방장 권한 받기 
	
	//@---Room(생성자)---@
//	public static final int GET_HOST = 12; //방장권한 받기 
//	public static final int QUESTION = 13; //문제 받기 
//	public static final int KICK =19 //강퇴유저 방에서 제외시키기 
	public static final int ROOM_USER_LIST = 25; //방 접속한 유저 목록 받기 
	public static final int SELECT_ROOM_USER_LIST = 26; //방 접속한 유저 목록 받기 
	public static final int ROOM_EXIT = 27; //방에서 나가기 
//	public static final int MESSAGE = 31; //유저들의 메시지를 받기
	
	//서버에 주기
	public static final int HOST_NAME = 17; //방장 이름  
	public static final int KICK_CHOICE = 18; //강퇴유저 이름 (/강퇴 ID) 
//	public static final int MESSAGE = 31; //유저들의 메시지 보내기 
//	public static final int ROOM_USER_LIST = 25; //방 접속 유저 목록 보내기 
//	public static final int ROOM_COUNT = 23; //방 접속 유저 카운트 보내기 
	public static final int ROOM_EXIT_USER = 28; //방에서 나간 유저 이름 
	
}

public class MyData implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	//아이디
	private String ID;
	//목적
	private int purpose;
	
	private String title;
	private String message;
	
	//전체 접속 인원 리스트
	private List<String> list;
	private List<User> userlist;
	
	private RoomGame roomgame;
	private User user;
	
	
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<User> getUserlist() {
		return userlist;
	}

	public void setUserlist(List<User> userlist) {
		this.userlist = userlist;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public RoomGame getRoomgame() 
	{
		return roomgame;
	}

	public void setRoomgame(RoomGame roomgame) 
	{
		this.roomgame = roomgame;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public String getID()
	{
		return ID;
	}

	public void setID(String iD)
	{
		ID = iD;
	}

	public int getPurpose() 
	{
		return purpose;
	}

	public void setPurpose(int purpose) 
	{
		this.purpose = purpose;
	}

	public MyData()
	{
		
	}
	
	
	
	public MyData(String ID, int purpose, String title, String message)
	{
		this.ID = ID;
		this.purpose = purpose;
		this.title = title;
		this.message = message;
	}
	
	public MyData(String ID, int purpose)
	{
		this.ID = ID;
		this.purpose = purpose;
	}
	
	
	public MyData(String ID, int purpose, String title)
	{
		this.ID = ID;
		this.purpose = purpose;
		this.title = title;
	}
	
	public MyData(int purpose, List<String> list)
	{
		this.purpose = purpose;
		this.list = list;
	}
	
	public MyData(String title, int purpose, List<String> list)
	{
		this.title = title;
		this.purpose = purpose;
		this.list = list;
	}
	
	public MyData(String ID, int purpose, String title, RoomGame roomgame)
	{
		this.ID = ID;
		this.purpose = purpose;
		this.title = title;
		this.roomgame = roomgame;
	}
	
	public MyData(String ID, int purpose, String title, User user, RoomGame roomgame)
	{
		this.ID = ID;
		this.purpose = purpose;
		this.title = title;
		this.user = user;
		this.roomgame = roomgame;
	}
	
	
	public MyData(String ID, int purpose, String title, User user)
	{
		this.ID = ID;
		this.purpose = purpose;
		this.title = title;
		this.user = user;
	}
	
	
	public MyData(String ID, int purpose, RoomGame roomgame, User user)
	{
		this.ID = ID;
		this.purpose = purpose;
		this.roomgame = roomgame;
		this.user = user;
	}
	
	public MyData(String ID, int purpose, String title, RoomGame roomgame, List<User> userlist)
	{
		this.ID = ID;
		this.purpose = purpose;
		this.title = title;
		this.roomgame = roomgame;
		this.userlist = userlist;
	}

	
}