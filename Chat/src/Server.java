import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Server 
{
	private static List<ServerThread> list; 
	
	//아이디 리스트 <아이디(key), 메시지>
	private static Map<String, ObjectOutputStream> IDList;
	//방 리스트 <방 번호(key), RoomGaem생성자>
	private static Map<String, RoomGame> RoomList;
	
	// 클라이언트에게 보내기
	public static void main(String[] args) 
	{ 
		list = new ArrayList<>();
		ServerSocket server;
		IDList = new HashMap<>();
		RoomList = new HashMap<>();
		
		try 
		{
			server = new ServerSocket(8888);
			while(true)
			{
				try 
				{
					Socket socket = server.accept();
					Thread sth = new ServerThread(socket, IDList, RoomList);
					sth.start();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		} 
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
	}
}

//클라이언트에게 받기
class ServerThread extends Thread
{
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Map<String, ObjectOutputStream> IDList;
	private Map<String, RoomGame> Roomlist;
	private Object lock = new Object();
	
	
	private boolean On = true;

	public ServerThread
	(Socket socket, 
	Map<String, ObjectOutputStream> IDList, 
	Map<String, RoomGame> RoomList)
	
	{
		this.socket = socket;
		this.IDList = IDList;
		this.Roomlist = RoomList;
		
		try 
		{
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}	
	}
	
	@Override
	public void run() 
	{
		
		while(On)
		{
			try 
			{
				MyData m;
				try 
				{
					m = (MyData) ois.readObject();
					ProtocolCheck (m);
				} 
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
				}
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	
	//오브젝트 약속확인 메소드
	public void ProtocolCheck(MyData my) throws ClassNotFoundException, IOException
	{
		
		if(my.getPurpose() == MyProtocol.ID_CHECK)
		{
			IdCheck(my.getID());
		}
		
		if(my.getPurpose() == MyProtocol.ROOM_CHECK)
		{
			RoomCheck(my.getID(), my.getTitle(), my.getRoomgame(), my.getUserlist());
		}
		
		if(my.getPurpose() == MyProtocol.ID_ADD)
		{
			UpdateUser();
			UpdateRoom();
		}
		
		if(my.getPurpose() == MyProtocol.ROOM_JOIN)
		{
			JoinRoom(my.getID(), my.getTitle(), my.getUser());
		}
		
		if(my.getPurpose() == MyProtocol.MESSAGE)
		{
			sendMessage(my.getID(), my.getTitle(), my.getMessage());
		}
		
		if(my.getPurpose() == MyProtocol.ROOM_USER_LIST)
		{
			RoomUser(my.getID(), my.getTitle());
		}
		
		if(my.getPurpose() == MyProtocol.CLOSE)
		{
			ClientClose(my.getID(),my.getTitle());
			UpdateUser();
			On = false;
		}
	}
	
	public void sendMessage(String ID, String Title, String Message) throws IOException
	{
		
		
		Iterator iter1 = IDList.entrySet().iterator();
		
		Map<String, ObjectOutputStream> map = new HashMap<>();
		
		while(iter1.hasNext())
		{
			Entry<String, ObjectOutputStream> entry = (Entry)iter1.next();
			if(Roomlist.get(Title).userlist.contains(entry.getKey()))
			{
				map.put(entry.getKey(), entry.getValue());
			}
		}	
		
		Iterator<String> iter = map.keySet().iterator();
		System.out.println(map.size());
		while(iter.hasNext())
		{
			String key = iter.next();
			System.out.println(key + "에게 메세지 " + ID + " : " + Message);
			map.get(key).writeObject(new MyData(ID, MyProtocol.MESSAGE, Title, Message));
			map.get(key).flush();
		}
	}
	
	
	public void UpdateUser() throws IOException
	{
		synchronized (lock) 
		{
			List<String> list = new ArrayList<>(IDList.keySet());
			Iterator<String> keys = IDList.keySet().iterator();
			while(keys.hasNext())
			{
				String key = keys.next();
				IDList.get(key).writeObject(new MyData(MyProtocol.ID_UPDATE, list));	
				IDList.get(key).flush();
			}	
		}
	}
	
	public void RoomCheck(String ID, String title, RoomGame RoomGame, List<User>userlist) throws IOException
	{
		synchronized (lock)
		{
			if(!Roomlist.containsKey(title))
			{
				CreateRoom(ID, title, RoomGame);
				UpdateRoom();
				oos.writeObject(new MyData(ID, MyProtocol.ROOM_YES, title, RoomGame, userlist));
				oos.flush();
			}
			else
			{
				oos.writeObject(new MyData(ID, MyProtocol.ROOM_NO, title));
				oos.flush();
			}
			
		}
	}
	
	
	public void CreateRoom(String ID, String Title, RoomGame RoomGame) throws IOException
	{
		synchronized (lock) 
		{
			Roomlist.put(Title, RoomGame);
			System.out.println("만들어진 방 : " + Title + "\n");
		}
	}
	
	public void JoinRoom(String ID, String Title, User user) throws IOException
	{
		synchronized (lock)
		{
			System.out.println("들어갈려는 방 : " + Title + "\n");
			
			if(!Roomlist.get(Title).userlist.contains(ID))
			{
				Roomlist.get(Title).userlist.add(ID);
				Roomlist.get(Title).RoomUserList.add(user);
				Roomlist.get(Title).addUser();
				RoomUser(ID, Title);

				Iterator<String> iter = IDList.keySet().iterator();
				
				while(iter.hasNext())
				{
					String key = iter.next();
					if(Roomlist.get(Title).userlist.contains(key))
					{
						System.out.println(Title + "에 들어있는 아이디" + key);
						System.out.println(key +"에게 " +user.ID + "을 "+ Title + "에 입장을 보냄" + "\n");
						List<User> userlist = new ArrayList<>(Roomlist.get(Title).RoomUserList);
						Roomlist.get(Title).setRoomUserList(userlist);
						IDList.get(key).writeObject(new MyData(key, MyProtocol.ROOM_JOIN, Title, 
						Roomlist.get(Title), Roomlist.get(Title).RoomUserList));
						
						IDList.get(key).flush();
					}
				}

			}
			else
			{
				System.out.println(ID + "님은 이미 입장된 상태입니다.");
			}
		}
	}
	
	public void UpdateRoom() throws IOException
	{
		synchronized (lock)
		{
			
			Iterator<String> userlist = IDList.keySet().iterator();
			while(userlist.hasNext())
			{
				String key = userlist.next();
				List<String> list = new ArrayList<>(Roomlist.keySet());
				IDList.get(key).writeObject(new MyData(MyProtocol.ROOM_LIST, list));
				System.out.println(key + "에게 방목록 : " + list.toString() + "출력");
				IDList.get(key).flush();		
			}
		}
	}
	
	
	
	public void RoomUser(String ID, String Title) throws IOException
	{
		synchronized (lock)
		{
			Iterator<String> iter = IDList.keySet().iterator();
			while(iter.hasNext())
			{
				String key = iter.next();
				List<String> list = new ArrayList<>(Roomlist.get(Title).userlist);
				IDList.get(key).writeObject
				(new MyData(Title, MyProtocol.ROOM_USER_LIST, list));
				System.out.println
				(key + "에게 "+ Title + "방 인원 : " + Roomlist.get(Title).userlist +"을 보내라" + "\n");
				IDList.get(key).flush();
			}
		}
	}

	public void ClientClose(String ID, String Title) throws IOException
	{	
		synchronized (lock)
		{
			if(Title == null)
			{		
				IDList.remove(ID);
			}
			else
			{
				int a = Roomlist.get(Title).userlist.indexOf(ID);
				Roomlist.get(Title).userlist.remove(a);
				Roomlist.get(Title).RoomUserList.remove(a);
				System.out.println(ID + "님 종료");	
				IDList.remove(ID);
				RoomUser(ID, Title);
			}

		}	
	}
	
	
	public void IdCheck(String ID) throws IOException
	{
		synchronized (lock) 
		{
			
			if(IDList.containsKey(ID))
			{
				oos.writeObject(new MyData(ID, MyProtocol.ID_NO));
				oos.flush();				
			}
			else
			{
				oos.writeObject(new MyData(ID, MyProtocol.ID_YES));
				oos.flush();			
				IDList.put(ID, oos);
			}
		}
	}
}