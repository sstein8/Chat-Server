package chatroom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Shaina Stein
 */
public class ChatClient extends ChatWindow {

	// Inner class used for networking
	private Communicator comm;

	// GUI Objects
	private JTextField serverTxt;
	private JTextField nameTxt;
	private JButton connectB;
	private JTextField messageTxt;
	private JButton sendB;

	public ChatClient(){
		super();
		this.setTitle("Chat Client"); //title of the user's chat box
		printMsg("Chat Client Started."); //first message in chat screen
		printMsg("To change name, type into name field and press enter.");

		// GUI elements at top of window
		// Need a Panel to store several buttons/text fields
		serverTxt = new JTextField("localhost");
		serverTxt.setColumns(15);
		nameTxt = new JTextField("Name"); 
		nameTxt.setColumns(10);
		nameTxt.setActionCommand("Name"); //enter name here
		connectB = new JButton("Connect"); //press to connect
		JPanel topPanel = new JPanel();
		topPanel.add(serverTxt);
		topPanel.add(nameTxt);
		topPanel.add(connectB);
		contentPane.add(topPanel, BorderLayout.NORTH);

		// GUI elements and panel at bottom of window
		messageTxt = new JTextField("");
		messageTxt.setColumns(40);
		sendB = new JButton("Send");
		JPanel botPanel = new JPanel();
		botPanel.add(messageTxt);
		botPanel.add(sendB);
		contentPane.add(botPanel, BorderLayout.SOUTH);

		// Resize window to fit all GUI components
		this.pack();

		// Setup the communicator so it will handle the connect button
		Communicator comm = new Communicator();
		connectB.addActionListener(comm);
		sendB.addActionListener(comm);
		nameTxt.addActionListener(comm); //added action listener for names
		messageTxt.addActionListener(comm); //added action listener for messages
		//add action listener to message text to clear out mesage

	}

	/** This inner class handles communication with the server. */
	class Communicator implements ActionListener
	{
		private Socket socket;
		private PrintWriter writer;
		private BufferedReader reader;
		private int port = 2113;
		private Thread socketReader; //thread for client

		@Override
		public void actionPerformed(ActionEvent actionEvent) 
		{
			//connect button
			if(actionEvent.getActionCommand().compareTo("Connect") == 0) 
			{
				//establish connection to the server
				connect(); 
			}
			//send button
			else if(actionEvent.getActionCommand().compareTo("Send") == 0) 
			{
				//send a message to the server
				sendMsg("/MESSAGE", messageTxt.getText());

				//clear message after pressing send
				messageTxt.setText(" ");
			}
			else if(actionEvent.getActionCommand().compareTo("Name") == 0){
				sendMsg("/NAME_CHANGE", nameTxt.getText()); //name change when Name is pressed(enter)
			}
		}

		/** Connect to the remote server and setup input/output streams. */
		public void connect(){
			try {
				socket = new Socket(serverTxt.getText(), port);
				InetAddress serverIP = socket.getInetAddress();
				printMsg("Connection made to " + serverIP);
				writer = new PrintWriter(socket.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				socketReader = new Thread(new SocketReader(reader)); //create a new client thread
				socketReader.start(); //start the thread

				sendMsg("/CONNECT", nameTxt.getText()); //displays name of who is connecting

			}
			catch(IOException e) {
				printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
			}
		}
		
		/** Send a string */
		public void sendMsg(String command, String s){ //takes command and string
			//send command to server
			writer.println(command);

			if(s != null && s.length() > 0) //make sure string has contents
			{
				writer.println(s); 
			}
			
		}
		class SocketReader implements Runnable //subclass for new thread
		{
			private BufferedReader reader;

			public SocketReader(BufferedReader reader)
			{
				this.reader = reader; //initialize reader
			}
			public void readMsg() 
			{ 
				while(true)
				{
					try{
						String s = reader.readLine(); //read in message
						printMsg(s); //print out message
					}
					catch(IOException e){
						printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
					}
						
				}
			}
			public void run()
			{
				readMsg(); //thread runs and reads messages
			}
		}
	}
	


	public static void main(String args[]){
		new ChatClient();
	}

}
