package com.badlogic.androidgames.mygame;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.Screen;

public class ServerScreen extends Screen {
	
	ServerManagement sm;
	static int numPlayers;

	Thread serverThread = null;
	
	boolean connected = false;
	boolean authenticated = false;
	
	private int t;

	public ServerScreen(Game game, Integer t) {
		super(game);
		this.t = t;
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
		for (int i=0; i<len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (inBounds(event, 0, 416, 120, 48)) { // back button
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					game.setScreen(new MultiPlayerScreen(game));
					sm.stop();
					// test back, close sm
					return;
				}
				
				if (inBounds(event, 200, 416, 120, 48)) { // play button
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
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
		g.drawPixmap(Assets.serverclient, g.getWidth()/2 -60, 0, 0, 0, 120, 48); // Server image
		g.drawPixmap(Assets.serverclient, 0, 416, 0, 96, 120, 48); // Back button image
		g.drawPixmap(Assets.serverclient, 200, 416, 0, 144, 120, 48); // Play button image
		
		String ip = getLocalIpAddress(); // get IP
		if(ip == null) drawText(g, "123", 0, 80); // get IP failed
		else drawText(g, ip, 0, 80); // display IP
		
		if(connected) {
			// draw number of players connected
			drawText(g, String.valueOf(numPlayers), 20, 150);
		}
		
		if(authenticated) 
			g.drawPixmap(Assets.client, 0, 200, 0, 50, 160, 50); // Autenticated image
		
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
	
	public boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
		if (event.x > x && event.x < x + width - 1 &&
				event.y > y && event.y < y + height - 1) {
			return true;
		} 
		else {
			return false;
		}
	}
	
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
			
			sm = new ServerManagement();
			System.out.println("before connection");

			boolean accepted = sm.accept();
			if(accepted) {
				System.out.println("client accpeted");
				connected = true;
			}
			
			authenticated = sm.initializeAuthenticate(t);
			
			if (accepted) {
				numPlayers++;
				sm.singleWrite(String.valueOf(numPlayers - 1), numPlayers - 2);
				Log.d("ServerAccept", "Send to " + (numPlayers - 2));
			} else {
				Log.d("ServerAccept", "Fail to accept");

			}
			
		}
	}

}
