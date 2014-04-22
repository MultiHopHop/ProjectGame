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

import android.util.Log;

import com.badlogic.androidgame.authentication.Authentication;
import com.badlogic.androidgame.authentication.T2ClientAuthentication;
import com.badlogic.androidgame.authentication.T5ClientAuthentication;

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
	private BufferedReader reader;
	private PrintWriter writer;
	public int clientIndex = 0; // default
	int authenticationType = 2; // 2-5
	public Authentication clientAuthentication;

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

	public boolean initializeAuthentication() {
		switch (authenticationType) {
		case 2:
			clientAuthentication = new T2ClientAuthentication(socket,
					"HelloWorld");
			break;
		case 5:
			clientAuthentication = new T5ClientAuthentication(socket);
			break;
		}

		boolean authenticate = false;
		try {
			authenticate = clientAuthentication.initialize();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return authenticate;
	}

	public String read() {
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

	public void write(String msg) {
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
