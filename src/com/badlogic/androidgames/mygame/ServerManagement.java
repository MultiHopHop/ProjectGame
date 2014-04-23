package com.badlogic.androidgames.mygame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.badlogic.androidgame.authentication.Authentication;
import com.badlogic.androidgame.authentication.T5ServerAuthentication;

/**
 * This class stores all the client sockets
 * 
 * Methods:
 * write, singleWrite, read, ready, close
 * 
 * @author zianli
 *
 */
public class ServerManagement {
	private ServerSocket serverSocket;
	public static final int SERVERPORT = 6000;
	private PrintWriter writer;
	private  BufferedReader reader;
	public List<Socket> sockets;
	public static int counter;
	private int authenticationType = 2; // 2-5
	private Authentication authentication;

	public ServerManagement() {
		try {
			serverSocket = new ServerSocket(SERVERPORT);
			sockets = new ArrayList<Socket>();
			counter = 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean accept() {
		try {
			Socket socket = serverSocket.accept();
			counter ++;
			sockets.add(socket);
			Log.d("ServerAccept", String.valueOf(sockets.size()));
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean initializeAuthenticate(int t) {
		this.authenticationType = t;
		boolean ans = false;
		for (Socket socket: sockets) {
			try {
				switch(authenticationType){
					case 5:
						authentication = new T5ServerAuthentication(socket);
						ans = authentication.initialize();
//						T2ServerAuthentication serverAuth2 = new T2ServerAuthentication(socket, "HelloWorld");
//						ans = serverAuth2.t2Authentication();
						break;
//					case 3:
//						T3ServerAuthentication serverAuth3 = new T3ServerAuthentication(socket, "HelloWorld");
//						ans = serverAuth3.t3Authentication();
//						break;
//					case 4:
//						T4ServerAuthentication serverAuth4 = new T4ServerAuthentication(socket, "HelloWorld");
//						ans = serverAuth4.t4Authentication();
//						break;
//					case 5:
//						ans = false;
//						break;
						/*T5ServerAuthentication clientAuth5 = new T5ServerAuthentication(socket, "HelloWorld");
						return serverAuth5.t5Authentication();*/
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ans;
	}
	
	/**
	 * This method writes to all clients
	 * @param msg
	 */
	public void write(String msg) {
		if (sockets.isEmpty()) {
			return;
		}
		
		if (authenticationType == 5) {
			try {
				authentication.send(msg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		
		for (Socket socket : sockets) {
			try {
				
				writer = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				writer.println(msg);		
				writer.flush();
//				writer.close();
				Log.d("ServerWrite", msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method sends message to a particular client
	 * @param msg
	 * @param index
	 */
	public void singleWrite(String msg, int index) {
		if (authenticationType == 5) {
			try {
				authentication.send(msg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		
		try {
			writer = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(sockets.get(index).getOutputStream())), true);
			writer.println(msg);
			writer.flush();
//			writer.close();
			Log.d("SingleServerWrite", msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method can handle latest messages from all its clients.
	 * @return a string of all messages (separated by lines)
	 */
	public String read(){		
		Log.d("ServerRead", "initialize");
		StringBuilder builder = new StringBuilder();
		String input = "";
		if (sockets.isEmpty()) {
			return null;
		}
		
		if (authenticationType == 5) {
			try {
				return authentication.receive();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (Socket socket: sockets) {
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				while (reader.ready()) {
					input = reader.readLine();
				}
				Log.d("ServerRead", "reader.checkReady");
				if (!input.equals("")) {
					Log.d("ServerRead", "input: "+input);
					builder.append(input);
				}
				input = "";
//				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.d("ServerRead", "builder: " + builder.toString());
		return builder.toString();
	}
	
	/**
	 * This method check if there is any message from clients.
	 * @return
	 */
	public boolean ready() {
		boolean ready = false;
		for (Socket socket: sockets) {
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				if (reader.ready()) {
					ready = true;
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		return ready;
	}

	/**
	 * This method terminates all client sockets and server socket.
	 */
	public void stop() {
		try {
			for (Socket socket : sockets) {
				sockets.remove(socket);
				socket.close();
			}
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
