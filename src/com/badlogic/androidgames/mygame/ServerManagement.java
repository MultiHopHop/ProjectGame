package com.badlogic.androidgames.mygame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerManagement {
	private ServerSocket serverSocket;
	public static final int SERVERPORT = 6000;
	private static PrintWriter writer;
	private static BufferedReader input;

	public ServerManagement() {
		try {
			serverSocket = new ServerSocket(SERVERPORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean accept(){
		try {
			Socket socket = serverSocket.accept();
			writer = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())),
					true);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static void write(String msg){		
		writer.println(msg);		
	}
	
	public static String read(){		
		String read = null;
		try {
			read = input.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return read;
	}
	
	public void stop(){
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
