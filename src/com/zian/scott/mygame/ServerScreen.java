package com.zian.scott.mygame;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.util.Log;

import com.zian.scott.framework.Game;
import com.zian.scott.framework.Graphics;
import com.zian.scott.framework.Screen;
import com.zian.scott.framework.Input.TouchEvent;

public class ServerScreen extends Screen {
	
	ServerManagement sm; //sm manages the socket connections
	static int numPlayers; //number of players in the game

	Thread serverThread = null; //Thread to initiate server
	
	boolean connected = false; //check if client has been connected to server
	boolean authenticated = false; //check if authentication passes or fails
	
	private int t; //Authentication type number

	public ServerScreen(Game game, Integer t) {
		super(game);
		this.t = t;
		// default
		numPlayers = 1;
		
		//Initiate server
		this.serverThread = new Thread(new ServerThread());
		this.serverThread.start();
		Log.d("CreateServer", "CreateServerThread");

	}

	/*The update method processes the touch inputs of the user*/
	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		
		int len = touchEvents.size();
		for (int i=0; i<len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (inBounds(event, 0, 416, 120, 48)) { // back button
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					// test back, close sm
					game.setScreen(new MultiPlayerScreen(game));
					sm.stop();
					return;
				}
				
				if (inBounds(event, 200, 416, 120, 48)) { // play button
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					sm.write("start"); //inform clients to start game
					Log.d("ServerWrite", "gamestart");
					sm.write(String.valueOf(numPlayers)); //inform clients the number of players
					game.setScreen(new GameScreenServer(game, sm)); //go to game screen

				}
			}
		}
	}

	/* present draws the required graphics onto the game screen */
	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();
		g.drawPixmap(Assets.background, 0, 0); // background image
		g.drawPixmap(Assets.serverclient, g.getWidth()/2 -60, 0, 0, 0, 120, 48); // Server image
		g.drawPixmap(Assets.serverclient, 0, 416, 0, 96, 120, 48); // Back button image
		g.drawPixmap(Assets.serverclient, 200, 416, 0, 144, 120, 48); // Play button image
		g.drawPixmap(Assets.mochi, g.getWidth() - 40, 5, 0, 0, 32, 32); // mochi image
		
		String ip = getLocalIpAddress(); // get IP
		if(ip == null) drawText(g, "123", 0, 80); // get IP failed
		else drawText(g, ip, 0, 80); // display IP
		
		if(connected) {
			// draw number of players connected
			drawText(g, String.valueOf(numPlayers), 20, 150);
		}
		
		if(authenticated) 
			g.drawPixmap(Assets.client, 0, 200, 0, 50, 160, 50); // Authenticated image
		
		//T image
		switch(t){
			case 2:
				g.drawPixmap(Assets.T2w, 0, 0, 0, 0, 40, 40);
				break;
			case 3:
				g.drawPixmap(Assets.T3w, 0, 0, 0, 0, 40, 40);
				break;
			case 4:
				g.drawPixmap(Assets.T4w, 0, 0, 0, 0, 40, 40);
				break;
			case 5:
				g.drawPixmap(Assets.T5w, 0, 0, 0, 0, 40, 40);
				break;
			
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
	
	/*returns true if the TouchEvent occurs within the speified box described by x,y,width and height
	  else returns false*/
	public boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
		if (event.x > x && event.x < x + width - 1 &&
				event.y > y && event.y < y + height - 1) {
			return true;
		} 
		else {
			return false;
		}
	}
	
	/* getLocalIpAddress returns the Local Ip Address of this device if available */
	public String getLocalIpAddress() {
	    try {
	    	for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                String ipv4;
					if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = inetAddress.getHostAddress())) {
	                	
	                	return ipv4;
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        //Log.e(LOG_TAG, ex.toString());
	    }
	    return null;
	}
	
	/* drawText draws numbers which are represented in a String input to a specific x and y position*/
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
	
	class ServerThread implements Runnable {

		public void run() {
			
			sm = new ServerManagement(); // creates an instance of ServerManagement
			System.out.println("before connection");

			boolean accepted = sm.accept(); // tries to accept a client
			if(accepted) {
				System.out.println("client accpeted");
				connected = true; // client connected
			} else {
				Log.d("ServerAccept", "Fail to accept");
			}
			

			if (!(authenticated = sm.initializeAuthenticate(t))) { // initialize Authentication
				//if Authentication fails
//				sm.stop();
				return;
			}
			
			if (accepted) { // if client was accepted
				numPlayers++; //increase number of players
				sm.singleWrite(String.valueOf(numPlayers - 1), numPlayers - 2); //inform players their number
				Log.d("ServerAccept", "Send to " + (numPlayers - 2));
			} else {
				Log.d("ServerAccept", "Fail to accept");

			}
			
		}
	}

}
