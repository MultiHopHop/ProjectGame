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

import com.badlogic.androidgame.authentication.T2ClientAuthentication;
import com.badlogic.androidgame.authentication.T3ClientAuthentication;
import com.badlogic.androidgame.authentication.T4ClientAuthentication;
import com.badlogic.androidgame.authentication.T5ClientAuthentication;

import android.util.Log;

/**
 * This class handles client-side connection
 * 
 * @author zianli
 *
 */
public class ClientManagement {

	private static final int SERVERPORT = 6000;
	private final String SERVER_IP;
	private InetAddress serverAddr;
	private Socket socket;
	private  BufferedReader reader;
	private  PrintWriter writer;
	public int clientIndex = 0; // default
	private int authenticationType = 2; // 2-5

	public ClientManagement(String serverip) {
		this.SERVER_IP = serverip;
		
		try {
			this.serverAddr = InetAddress.getByName(SERVER_IP);
			this.socket = new Socket(serverAddr, SERVERPORT);
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.writer = new PrintWriter(new BufferedWriter(
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
	
	public boolean initializeAuthenticate(int t) {
		this.authenticationType = t;
		try {
			switch(authenticationType){
				case 2:
					T2ClientAuthentication clientAuth2 = new T2ClientAuthentication(socket, "HelloWorld");
					return clientAuth2.t2Authentication();
				case 3:
					T3ClientAuthentication clientAuth3 = new T3ClientAuthentication(socket, "HelloWorld");
					return clientAuth3.t3Authentication();
				case 4:
					T4ClientAuthentication clientAuth4 = new T4ClientAuthentication(socket, "HelloWorld");
					return clientAuth4.t4Authentication();
				case 5:
					return false;
					/*T5ClientAuthentication clientAuth5 = new T5ClientAuthentication(socket, "HelloWorld");
					return clientAuth5.t5Authentication();*/
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}
	
	public String read(){
		StringBuilder builder = new StringBuilder();
		try {
			do {
				builder.append(reader.readLine() + "\n");
			} while (reader.ready());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String output = builder.toString();
		Log.d("ClientRead", output);
		return output;
	}
	
	public boolean ready() {
		boolean output = false;
		try {
			output = reader.ready();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}
	
	public void write(String msg){
		writer.println(msg);
		writer.flush();
	}

	public void stop() {
		writer.close();
		try {
			reader.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
