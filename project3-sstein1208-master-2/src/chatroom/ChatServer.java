package chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.List;
import java.util.ArrayList;


/**
 * Shaina Stein
 */
public class ChatServer extends ChatWindow 
{

	//private ClientHandler handler;
	private List <ClientHandler> handlers; //list of all clients called handlers


	public ChatServer(){
		super();
		this.setTitle("Chat Server"); //title of the window
		this.setLocation(80,80);
		handlers = new ArrayList<ClientHandler>(); //handlers is now an arraylist

		try {
			// Create a listening service for connections
			// at the designated port number.
			ServerSocket srv = new ServerSocket(2113);
			printMsg("Waiting for a connection");
			while (true) {
				// The method accept() blocks until a client connects.
				
				//when a new client joins
				Socket socket = srv.accept();
				ClientHandler handler = new ClientHandler(socket);
				handlers.add(handler); //add to the list

				Thread server = new Thread(handler);
				server.start(); //start thread
				
			}

		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/** This innter class handles communication to/from one client. */
	class ClientHandler implements Runnable //must implement runnable to have threads
	{
		private PrintWriter writer;
		private BufferedReader reader;
		private String clientName;

		public ClientHandler(Socket socket) {
			try {
				InetAddress serverIP = socket.getInetAddress();
				printMsg("Connection made to " + serverIP);
				writer = new PrintWriter(socket.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			catch (IOException e){
					printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
				}
		}
		public void handleConnection() {
			try {
				while(true) {
					// read a message from the client
					readMsg();

				}
			}
			catch (IOException e){
				printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
			}
		}

		/** Receive and display a message */
		public void readMsg() throws IOException {
			String command = reader.readLine();

			if(command.equalsIgnoreCase("/CONNECT"))
			{
				clientName = reader.readLine(); //get the client's name
				printMsg(clientName + " has joined"); //print that they have joined
				sendToAll(clientName + " has joined"); //send that message to all of the ppl in the chat
				sendMsg("Hi " + clientName); //says hello to the person who joined

			}
			else if(command.equalsIgnoreCase("/MESSAGE"))
			{
				String s = reader.readLine();
				printMsg(clientName + ": " + s); //prints out they message they typed
				sendToAll(clientName + ": " + s); //sends the message to everyone's screen

			}
			else if(command.equalsIgnoreCase("/NAME_CHANGE"))
			{
				String s = clientName + " changed name to ";
				clientName = reader.readLine();
				printMsg(s + clientName); //shows message that you changed names
				sendToAll(s + clientName); //shows everyone else you changed names
				sendMsg("Hi " + clientName); //says hi to the person who changed their nakme

			}
			
		}
		/** Send a string */
		public void sendMsg(String s){
			writer.println(s);
		}
		
		public void run(){
			handleConnection();
		}

	}
	public void sendToAll(String s) //sends a message to all clients in the list
	{
		for(ClientHandler handler : handlers)
		{
			handler.sendMsg(s);
		}
	}


	public static void main(String args[]){
		new ChatServer();
	}
}
