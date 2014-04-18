package com.badlogic.androidgames.mygame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientManagement {

	private static final int SERVERPORT = 6000;
	private final String SERVER_IP;
	private InetAddress serverAddr;
	private Socket socket;
	private static BufferedReader input;
	private static PrintWriter writer;

	public ClientManagement(String serverip) {
		this.SERVER_IP = serverip;
		
		try {
			this.serverAddr = InetAddress.getByName(SERVER_IP);
			this.socket = new Socket(serverAddr, SERVERPORT);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())),
					true);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
	
	public static void write(String msg){
		writer.println(msg);
	}


}
