package com.zian.scott.mygame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

import com.zian.scott.authentication.Authentication;
import com.zian.scott.authentication.T2ClientAuthentication;
import com.zian.scott.authentication.T3ClientAuthentication;
import com.zian.scott.authentication.T4ClientAuthentication;
import com.zian.scott.authentication.T5ClientAuthentication;

/**
 * This class handles client-side connection
 * 
 * @author zianli
 * 
 */
public class ClientManagement {

	private static final int SERVERPORT = 6000; // default server port
	private final String SERVER_IP; // IP address of server
	private InetAddress serverAddr;
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	public int clientIndex = 0; // default 0, stores client's player index
	int authenticationType = 2; // ranging from 2 to 5
	private Authentication authentication; // stores authentication object

	public ClientManagement(String serverip) {
		this.SERVER_IP = serverip;

		try {
			this.serverAddr = InetAddress.getByName(SERVER_IP);
			this.socket = new Socket(serverAddr, SERVERPORT);
			this.reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			this.writer = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())), true);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}


	/**
	 * Initialize mutual authentication using protocol t
	 * 
	 * @param t
	 * @return true if authentication is successful
	 */
	public boolean initializeAuthenticate(int t) {
		this.authenticationType = t;
		try {
			switch(authenticationType){
				case 2:
					authentication = new T2ClientAuthentication(socket, "HelloWorld");;
					return authentication.initialize();
				case 3:
					authentication = new T3ClientAuthentication(socket, "HelloWorld");
					return authentication.initialize();
				case 4:
					authentication = new T4ClientAuthentication(socket, "HelloWorld");
					return authentication.initialize();
				case 5:
					authentication = new T5ClientAuthentication(socket);
					return authentication.initialize();
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}
	
	/**
	 * read lines in bufferReader
	 * 
	 * @return
	 */
	public String read(){
		if (authenticationType != 2) {
			try {
				return authentication.safeRead();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
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

	/**
	 * check if bufferReader is ready
	 * 
	 * @return
	 */
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


	/**
	 * Write a message to printWriter
	 * @param msg
	 */
	public void write(String msg){
		if (authenticationType != 2) {
			try {
				authentication.safeWrite(msg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		writer.println(msg);
		writer.flush();
	}

	/**
	 * close bufferReader, printWriter and the socket
	 */
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
