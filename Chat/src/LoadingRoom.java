import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;


public class LoadingRoom extends JFrame implements ActionListener {

	JPanel panel;
	JList<String> room_mokrock, room_Inwon, wait_Inwon;
	JButton make_button, enter_button, exit_button;
	JScrollPane sp_roommokrock, sp_roomInwon, sp_waitInfo;
	String selectedRoom;

	ObjectInputStream ois;
	ObjectOutputStream oos;
	Socket socket;

	boolean On = true;

	String ID;
	String JoinRoomTitle;
	RoomGame RoomGame;

	DefaultListModel<String> IDlist;
	DefaultListModel<String> Roomlist;
	DefaultListModel<String> RoomUserlist;

	
	public LoadingRoom(String ID, Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
		this.ID = ID;
		this.socket = socket;
		this.ois = ois;
		this.oos = oos;

		IDlist = new DefaultListModel<>();
		Roomlist = new DefaultListModel<>();
		RoomUserlist = new DefaultListModel<>();

		room_mokrock = new JList<String>();
		room_mokrock.setBorder(new TitledBorder("방 목록"));

		room_Inwon = new JList<String>();
		room_Inwon.setBorder(new TitledBorder("인원정보"));

		wait_Inwon = new JList<String>();
		wait_Inwon.setBorder(new TitledBorder("대기실정보"));

		sp_roommokrock = new JScrollPane(room_mokrock);
		sp_roomInwon = new JScrollPane(room_Inwon);
		sp_waitInfo = new JScrollPane(wait_Inwon);

		sp_roommokrock.setBounds(10, 10, 300, 300);
		sp_roomInwon.setBounds(320, 10, 150, 300);
		sp_waitInfo.setBounds(10, 320, 300, 130);

		make_button = new JButton("방 만들기");
		enter_button = new JButton("방 입장");
		exit_button = new JButton("나가기 ");

		make_button.setBackground(Color.YELLOW);
		enter_button.setBackground(Color.MAGENTA);
		exit_button.setBackground(Color.ORANGE);

		panel = new JPanel();

		make_button.setBounds(320, 320, 150, 40);
		enter_button.setBounds(320, 365, 150, 40);
		exit_button.setBounds(320, 410, 150, 39);

		panel.setLayout(null);
		panel.add(make_button);
		panel.add(enter_button);
		panel.add(exit_button);
		panel.add(sp_roommokrock);
		panel.add(sp_roomInwon);
		panel.add(sp_waitInfo);

		add(panel);
		setSize(500, 500);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		event();
		Thread th = new ClientTh();
		th.setDaemon(true);
		th.start();

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Close();
			}
		});
		
		room_mokrock.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
					try 
					{
						String title = room_mokrock.getSelectedValue();
						System.out.println(ID + "님" + title + "선택");
						oos.writeObject(new MyData(ID, MyProtocol.ROOM_USER_LIST, title));
						oos.flush();
					} 
					catch (IOException e1) 
					{
						e1.printStackTrace();
					}
				
			}
		});

	}

	private void event() {
		make_button.addActionListener(this);
		enter_button.addActionListener(this);
		exit_button.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		
		if (e.getSource() == make_button) 
		{// 방만들기 요청
			String title = JOptionPane.showInputDialog(this, "방제목:");
			// sendMsg("21|"+title);
			RoomCheck(ID, title, new RoomGame(title));// 서버에 전달
			System.out.println(title + "방만들기요청" + "\n");
		}

		if (e.getSource() == enter_button) 
		{// 방들어가기 요청
			selectedRoom = room_mokrock.getSelectedValue();
			if (selectedRoom.equals(null)) 
			{
				JOptionPane.showMessageDialog(this, "방을 선택하세요~");
			}
			else
			{
				try 
				{
					System.out.println(ID + "님이" + selectedRoom + "방에 입장요청" + "\n");
					Join(ID, selectedRoom);
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		} 
		if (e.getSource() == exit_button)
		{// 나가기(프로그램종료)요청
			System.out.println("종료하기확인");
			Close();// 현재프로그램종료하기
		}

	}

	// 방 중복확인
		public void RoomCheck(String ID, String Title, RoomGame RoomGame) 
		{
			try 
			{
				oos.writeObject(new MyData(ID, MyProtocol.ROOM_CHECK, Title, RoomGame, RoomGame.getRoomUserList()));
				oos.flush();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	
	
	
	// 방 입장하기
	public void Join(String ID, String Title) throws IOException
	{
		System.out.println(Title + "방 들어가기" + "\n");
		oos.writeObject(new MyData(ID, MyProtocol.ROOM_JOIN, Title, new User(ID)));
		oos.flush();
		this.JoinRoomTitle = Title;
	}
	
	

	// 종료
	public void Close() 
	{
		System.out.println("종료해라");
		try 
		{
			On = false;
			oos.writeObject(new MyData(ID, MyProtocol.CLOSE, JoinRoomTitle));
			oos.flush();
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		finally
		{
			System.exit(0);
		}

	}

	class ClientTh extends Thread 
	{

		public ClientTh() 
		{

		}

		public void run() 
		{
			while (On) 
			{
				try 
				{
					MyData m = (MyData) ois.readObject();
					ProtocolCheck(m);
				} 
				catch (ClassNotFoundException | IOException e) 
				{
					e.printStackTrace();
				}
			}
		}

		// 클라이언트 프로토콜 확인하기
		public void ProtocolCheck(MyData m) throws IOException
		{
			if (m.getPurpose() == MyProtocol.ID_UPDATE)
			{
				IDlist(m.getList());
			}

			if (m.getPurpose() == MyProtocol.ROOM_LIST) 
			{
				Roomlist(m.getList());
			}

			if (m.getPurpose() == MyProtocol.ROOM_NO) 
			{
				JOptionPane.showMessageDialog(panel, "이미 있는방입니다.");
			}
			
			if(m.getPurpose() == MyProtocol.ROOM_YES)
			{
				System.out.println("방시작");
				//RoomStart(m.getID(), m.getRoomgame());
				//RoomJoin(m.getID(), m.getRoomgame(), m.getUserlist());
				Join(m.getID(), m.getTitle());
			}

			if (m.getPurpose() == MyProtocol.ROOM_USER_LIST)
			{
				Room(m.getTitle(), m.getList());
			}
			
			if(m.getPurpose() == MyProtocol.ROOM_JOIN)
			{
				RoomGame = m.getRoomgame();
				RoomJoin(m.getID(), m.getUserlist());
			}
			if(m.getPurpose() == MyProtocol.MESSAGE)
			{
				ReceiveMessage(m.getID(), m.getTitle(), m.getMessage());
			}
		}
		
		/*//새로운 방 만들기
		public void RoomStart(String ID, RoomGame RoomGame)
		{
			RoomGame.setVisible(true);
		}*/
		
		//채팅내용 서버에서 받기
		public void ReceiveMessage(String ID, String Title, String Message)
		{
			if(Title.equals(RoomGame.getTitle()))
			{
				RoomGame.textArea.append(ID + " : " + Message + "\n");				
			}
		}
		
		
		//방에 입장하기
		public void RoomJoin(String ID , List<User> userlist)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run() 
				{		
					
					System.out.println("방 제목" + RoomGame.getTitle());
					System.out.println("방 인원" + userlist.size() + "\n");
					RoomGame.setRoomUserList(userlist);
					RoomGame.addUser();
					for(int i = 0; i < userlist.size(); i++)
					{
						System.out.println(userlist.get(i).ID + "\n");
					}
					RoomGame.start.addActionListener(new ActionListener()
					{
						
						@Override
						public void actionPerformed(ActionEvent e)
						{
							startbtn(RoomGame);
						}
					});
				}
			});
			
			RoomGame.textField.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					try 
					{
						JOptionPane.showMessageDialog(panel, ID + "가 보냄");
						oos.writeObject(new MyData(ID, MyProtocol.MESSAGE, RoomGame.getTitle(), RoomGame.textField.getText()));
						oos.flush();
					} 
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
			});
			RoomGame.setVisible(true);
			
		}
		
		public void startbtn(RoomGame RoomGame)
		{
			System.out.println("방 인원" + RoomGame.userlist.size());
			for(int i = 0; i < RoomGame.userlist.size(); i++)
			{
				System.out.println("인원 : " + RoomGame.RoomUserList.get(i));
			}
		}
		
		
		
		
		// 선택한 방 보여주기
		public void Room(String Title, List<String> roomUserlist) 
		{	
			
			if(Title.equals(room_mokrock.getSelectedValue()))
			{
				RoomUserlist.clear();
				Iterator<String> iter = roomUserlist.iterator();

				while (iter.hasNext()) 
				{
					RoomUserlist.addElement(iter.next());
				}
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run() 
					{
						wait_Inwon.setModel(RoomUserlist);
					}
				});
			}
			else
			{
				System.out.println("방제가 다르다");
			}
		}

		// 전체 접속인원 보여주기
		public void IDlist(List<String> IDList) 
		{
			IDlist.clear();
			Iterator<String> iter = IDList.iterator();

			while (iter.hasNext()) 
			{
				IDlist.addElement(iter.next());
			}
			SwingUtilities.invokeLater(new Runnable() 
			{
				@Override
				public void run() {
					room_Inwon.setModel(IDlist);
				}
			});
		}

		// 방이 생성될때마다 정보 받기
		public void Roomlist(List<String> RoomList) 
		{
			SwingUtilities.invokeLater(new Runnable() 
			{
				@Override
				public void run() {
					Roomlist.clear();
					Iterator<String> iter = RoomList.iterator();

					while (iter.hasNext()) 
					{
						Roomlist.addElement(iter.next());
					}
					room_mokrock.setModel(Roomlist);
				}
			});
		}

	}
}