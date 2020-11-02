import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

class User extends JPanel
{
	JLabel userID;
	boolean host, readyB;
	JButton ready;
	String ID;

	public String getID() 
	{
		return ID;
	}

	public void setID(String iD) 
	{
		this.ID = iD;
	}

	public boolean isHost() 
	{
		return host;
	}

	public void setHost(boolean host)
	{
		this.host = host;
	}

	public boolean isReadyB() 
	{
		return readyB;
	}

	public void setReadyB(boolean readyB) 
	{
		this.readyB = readyB;
	}

	public User(String name)
	{
		this.readyB = false;
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(100,50));
		setMinimumSize(new Dimension(100,50));
		setMaximumSize(new Dimension(100,50));
		userID = new JLabel(name);
		this.ID = userID.getText();
		userID.setForeground(new Color(255,255,255));
		userID.setHorizontalAlignment(JLabel.CENTER);
		ready = new JButton("준비");
		
		Random rd = new Random();
		int r = rd.nextInt(255);
		int g = rd.nextInt(255);
		int b = rd.nextInt(255);
		
		setBackground(new Color(r,g,b));
		add(userID, BorderLayout.CENTER);
		add(ready, BorderLayout.SOUTH);
		
		ready.addActionListener(new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(e.getActionCommand() == "준비") 
				{
					readyB = true;
					ready.setText("준비완료");
				}
				else
				{
					ready.setText("준비");
					readyB = false;
				}
			}
		});
	}
}


public class RoomGame extends JFrame
{
	List<User> RoomUserList;
	List<String> userlist;
	
	JButton start, exit;
	JPanel backpanel, eastpanel, westpanel, centerpanel,
	toppanel, timepanel, startpanel, exitpanel;
	JLabel timer;
	JTextArea textArea;
	JTextField textField;
	JScrollPane scroll;
	BoxLayout box;
	Timer time;



	public List<String> getUserlist() {
		return userlist;
	}

	public void setUserlist(List<String> userlist) {
		this.userlist = userlist;
	}

	public List<User> getRoomUserList() 
	{
		return RoomUserList;
	}

	public void setRoomUserList(List<User> roomUserList)
	{
		RoomUserList = roomUserList;
	}

	public JTextArea getTextArea() 
	{
		return textArea;
	}

	public void setTextArea(JTextArea textArea)
	{
		this.textArea = textArea;
	}

	public RoomGame(String Title)
	{
		userlist = new ArrayList<>();
		RoomUserList = new ArrayList<>();
		setTitle(Title);	
		
		backpanel = new JPanel(new BorderLayout());
		
		toppanel = new JPanel(new GridLayout(0,3));
		timepanel = new JPanel();
		startpanel = new JPanel();
		exitpanel = new JPanel();
		
		eastpanel = new JPanel();
		eastpanel.setLayout(new BoxLayout(eastpanel, BoxLayout.Y_AXIS));
		
		westpanel = new JPanel();
		westpanel.setLayout(new BoxLayout(westpanel, BoxLayout.Y_AXIS));
		
		centerpanel = new JPanel(new BorderLayout());
		
		eastpanel.setPreferredSize(new Dimension(100,150));
		westpanel.setPreferredSize(new Dimension(100,150));
		
		start = new JButton("시작");
		timer = new JLabel("0 : 0");
		exit = new JButton("나가기");
		
		timer.setFont(new Font("DIALOG",Font.BOLD, 20));
		
		startpanel.add(start);
		timepanel.add(timer);
		exitpanel.add(exit);
		
		toppanel.add(startpanel);
		toppanel.add(timepanel);
		toppanel.add(exitpanel);
		
		backpanel.add(toppanel, BorderLayout.NORTH);

		JLabel eastlabel = new JLabel("유저");
		eastlabel.setHorizontalAlignment(JLabel.CENTER);
		eastlabel.setFont(new Font("DIALOG", Font.BOLD, 20));
		JLabel westlabel = new JLabel("유저");
		westlabel.setHorizontalAlignment(JLabel.CENTER);
		westlabel.setFont(new Font("DIALOG", Font.BOLD, 20));
		
		eastpanel.add(eastlabel);
		westpanel.add(westlabel);
		
		textArea = new JTextArea(30,30);
		textField = new JTextField(20);
		scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		centerpanel.add(scroll, BorderLayout.CENTER);
		centerpanel.add(textField, BorderLayout.SOUTH);
		
		backpanel.add(eastpanel, BorderLayout.EAST);
		backpanel.add(centerpanel, BorderLayout.CENTER);
		backpanel.add(westpanel, BorderLayout.WEST);

		add(backpanel);
		pack();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(false);
		timeRun();
	}
	
	int ss;
	int se;
	
	public void timeRun()
	{
		ss = 0;
		se = 0;
		time = new Timer(1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				timer.setText(ss + "." + se);
				if(se == 59)
				{
					se = 00;
					ss++;
				}
				se++;
			}
		});
		//time.start();
	}
	
	
	//접속하는 사람 패널에 배치
	public void addUser()
	{	
		for(int i = 0; i < RoomUserList.size(); i++)
		{
			
			if(i % 2 != 0)
			{
				westpanel.add(RoomUserList.get(RoomUserList.size()-1));	
				westpanel.add(Box.createVerticalGlue());
			}
			else
			{
				eastpanel.add(RoomUserList.get(RoomUserList.size()-1));
				eastpanel.add(Box.createVerticalGlue());
			}			
		}
		pack();
	}
	
}



