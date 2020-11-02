import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Client extends JDialog implements ActionListener {
	
	JPanel idPanel;
	JLabel idLabel;
	JTextField idField;
	JButton enterButton;
	
	String ID;
	
	LoadingRoom loading;
	
	ObjectInputStream ois;
	ObjectOutputStream oos;
	Socket socket;
	
	Client() 
	{
		setLocationRelativeTo(null);
		idPanel = new JPanel();
		idLabel = new JLabel("사용하실 아이디를 입력해주세요.");
		idField = new JTextField(10);
		enterButton = new JButton("입장");
		enterButton.addActionListener(this);
		
		idPanel.add(idLabel);
		idPanel.add(idField);
		idPanel.add(enterButton);
		add(idPanel);
		
		showGUI();
	}
	

	
	
	public void showGUI() 
	{
		setTitle("아이디 입력");
		setSize(300, 100);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	public static void main(String[] args)
	{
		Client idCheck = new Client();
	}
	
	public void Connect()
	{
		try 
		{
			socket = new Socket("localhost",8888);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} 
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	//아이디 체크
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == enterButton) 
		{
			Connect();
			try 
			{
				oos.writeObject(new MyData(idField.getText(), MyProtocol.ID_CHECK));
				oos.flush();
				MyData m = (MyData) ois.readObject();
					if(m.getPurpose() == MyProtocol.ID_YES)
					{
						this.setVisible(false);
						ID = m.getID();
						oos.writeObject(new MyData(ID,MyProtocol.ID_ADD));
						oos.flush();		
						JOptionPane.showMessageDialog(idPanel, ID + "생성");
						loading = new LoadingRoom(m.getID(), socket, ois, oos);
						
						loading.setTitle(ID);
						
						
					}
					else
					{
						JOptionPane.showMessageDialog(idPanel, ID + "를 누가 사용중");
					}
				
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			catch (ClassNotFoundException e1)
			{
				e1.printStackTrace();
			}		
		}
	}
}