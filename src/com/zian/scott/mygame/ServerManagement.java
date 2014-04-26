package com.zian.scott.mygame;

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

import com.zian.scott.authentication.Authentication;
import com.zian.scott.authentication.T2ServerAuthentication;
import com.zian.scott.authentication.T3ServerAuthentication;
import com.zian.scott.authentication.T4ServerAuthentication;
import com.zian.scott.authentication.T5ServerAuthentication;

/**
 * This class stores all the client sockets connected to server
 * 
 * Methods: write, singleWrite, read, ready, close
 * 
 * @author zianli
 * 
 */
public class ServerManagement {
	private ServerSocket serverSocket;
	public static final int SERVERPORT = 6000; // default server port
	private PrintWriter writer;
	private BufferedReader reader;
	public List<Socket> sockets; // stores list of connected sockets
	public static int counter; // count number of sockets connected

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
			counter++;
			sockets.add(socket);
			Log.d("ServerAccept", String.valueOf(sockets.size()));
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Initialize mutual authentication using protocol t
	 * 
	 * @param t
	 * @return true if authentication is successful
	 */
	public boolean initializeAuthenticate(int t) {
		this.authenticationType = t;
		boolean ans = false;
		for (Socket socket: sockets) {
			try {
				switch(authenticationType){
					case 2:
						authentication = new T2ServerAuthentication(socket, "HelloWorld");
						break;
					case 3:
						authentication = new T3ServerAuthentication(socket, "HelloWorld");
						break;
					case 4:
						authentication = new T4ServerAuthentication(socket, "HelloWorld");
						break;
					case 5:
						authentication = new T5ServerAuthentication(socket);
						break;
					
				}
				
				ans = authentication.initialize();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ans;
	}

	/**
	 * This method writes to all clients
	 * 
	 * @param msg
	 */
	public void write(String msg) {
		if (sockets.isEmpty()) {
			return;
		}
		
		if (authenticationType != 2) {
			try {
				authentication.safeWrite(msg);
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
				Log.d("ServerWrite", msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method sends message to a particular client
	 * 
	 * @param msg
	 * @param index
	 */
	public void singleWrite(String msg, int index) {
		if (authenticationType != 2) {
			try {
				authentication.safeWrite(msg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		
		try {
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					sockets.get(index).getOutputStream())), true);
			writer.println(msg);
			writer.flush();
			Log.d("SingleServerWrite", msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method can handle the last messages from all its clients.
	 * 
	 * @return a string of all messages (separated by lines)
	 */

	public String read(){		
		StringBuilder builder = new StringBuilder();
		String input = "";
		if (sockets.isEmpty()) {
			return null;
		}
		
		if (authenticationType != 2) {
			try {
				return authentication.safeRead();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (Socket socket: sockets) {
			try {
				reader = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				while (reader.ready()) {
					input = reader.readLine();
				}
				Log.d("ServerRead", "reader.checkReady");
				if (!input.equals("")) {
					Log.d("ServerRead", "input: " + input);
					builder.append(input);
				}
				input = "";
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
	 * 
	 * @return
	 */
	public boolean ready() {
		boolean ready = false;
		for (Socket socket : sockets) {
			try {
				reader = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
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
