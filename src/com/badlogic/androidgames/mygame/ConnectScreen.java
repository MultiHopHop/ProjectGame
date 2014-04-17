package com.badlogic.androidgames.mygame;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.os.Handler;
import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.Screen;

public class ConnectScreen extends Screen {

	ServerManagement sm;
	ClientManagement cm;

	// For testing only
	// Will enable IP input method later
	String serverIp = "172.16.10.162";

	static String ip;
	static int numPlayers;

	Thread serverThread;

	Handler updateConversationHandler;
	Handler playerConnectedHandler;

	public ConnectScreen(Game game) {
		super(game);
		ip = null;
		// default
		numPlayers = 1;

		this.serverThread = new Thread(new ServerThread());
		this.serverThread.start();
		Log.d("CreateServer", "CreateServerThread");
	}

	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();

		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (inBounds(event, 0, 416, 64, 64)) {					// get IP (bottom left button)
					// Log.d("GetIp", "Pressed" + getLocalIpAddress());
					ConnectScreen.ip = getLocalIpAddress();
					if (ConnectScreen.ip != null) {
						Log.d("GetIp", "show ip: " + ConnectScreen.ip);
					}
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					return;
				}

				// connect to server (second button on the left)
				if (inBounds(event, 64, 416, 64, 64)) {
					new Thread(new ClientThread()).start();
				}

				// start (bottom right button)
				if (inBounds(event, 192, 416, 64, 64)) {
					sm.write("startgame");
					Log.d("ServerWrite", "gamestart");
					sm.write(String.valueOf(numPlayers));
					game.setScreen(new GameScreenServer(game, sm));

				}
			}
		}
	}

	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();

		g.drawPixmap(Assets.background, 0, 0);
		g.drawPixmap(Assets.mainMenu, 64, 20, 0, 42, 196, 42);

		// draw IP
		if (ip != null) {
			// Log.d("GetIp", ip);
			drawText(g, ip, 20, 100);
		}

		// draw number of players connected
		drawText(g, String.valueOf(numPlayers), 20, 160);
		// draw get IP button
		g.drawPixmap(Assets.buttons, 0, 416, 64, 64, 64, 64);
		// draw connect button
		g.drawPixmap(Assets.buttons, 64, 416, 64, 64, 64, 64);
		// draw game start button
		g.drawPixmap(Assets.buttons, 192, 416, 0, 64, 64, 64);

	}

	/*
	 * This method takes a string of number (with decimal point) and converts
	 * the string to a graph using Assets.numbers
	 */
	public void drawText(Graphics g, String line, int x, int y) {
		int len = line.length();
		for (int i = 0; i < len; i++) {
			char character = line.charAt(i);

			if (character == ' ') {
				x += 20;
				continue;
			}

			int srcX = 0;
			int srcWidth = 0;
			if (character == '.') {
				srcX = 200;
				srcWidth = 10;
			} else {
				srcX = (character - '0') * 20;
				srcWidth = 20;
			}

			g.drawPixmap(Assets.numbers, x, y, srcX, 0, srcWidth, 32);
			x += srcWidth;
		}
	}

	/**
	 * The thread handles connection to the server
	 * 
	 * @author zianli
	 * 
	 */
	class ServerThread implements Runnable {

		public void run() {

			Log.d("ServerAccept", "Befor accept");
			sm = new ServerManagement();
			boolean accepted = sm.accept();
			if (accepted) {
				numPlayers++;
				sm.singleWrite(String.valueOf(numPlayers - 1), numPlayers - 2);
				Log.d("ServerAccept", "Send to " + (numPlayers - 2));
			} else {
				Log.d("ServerAccept", "Fail to accept");

			}
		}

	}

	/**
	 * This thread handle connection from client Upon successful connection, it
	 * receives an ID from server indicating its player-index, ranging from 1 to
	 * 3.
	 * The second message it receives will start the game
	 * 
	 * @author zianli
	 * 
	 */
	class ClientThread implements Runnable {
		public void run() {
			cm = new ClientManagement(serverIp);
			Log.d("ClientRequest", "request");
			String input;
			input = cm.read();
			Log.d("TestClient", input);
			if (input.matches("[1-3]")) {
				cm.clientIndex = Integer.parseInt(input);
			}

			input = cm.read();
			if (input.equals("startgame")) {
				numPlayers = Integer.parseInt(cm.read());
				game.setScreen(new GameScreen(game, cm, numPlayers));
			}

		}
	}

	/**
	 * This method gets the IP address of device
	 * 
	 * @return a string of IP address
	 */
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					String ipv4;
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils
									.isIPv4Address(ipv4 = inetAddress
											.getHostAddress())) {

						return ipv4;
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("GetIp", ex.toString());
		}
		return null;
	}

	/**
	 * This method checks if a touchEvent lies in specified region
	 * 
	 * @param event
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	private boolean inBounds(TouchEvent event, int x, int y, int width,
			int height) {
		if (event.x > x && event.x < x + width - 1 && event.y > y
				&& event.y < y + height - 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
