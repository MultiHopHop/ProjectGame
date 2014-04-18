package com.badlogic.androidgames.mygame;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.Screen;

public class ServerScreen extends Screen {
	
	ServerManagement sm;

	Thread serverThread = null;
	
	boolean connected = false;

	public ServerScreen(Game game) {
		super(game);

		this.serverThread = new Thread(new ServerThread());
		this.serverThread.start();
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
					game.setScreen(new MultiPlayerScreen(game));
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					return;
				}
				
				if (inBounds(event, 200, 416, 120, 48)) { // play button
					//game.setScreen(new ServerScreen(game));
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
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
		
		if(connected) g.drawPixmap(Assets.server, 0, 150, 0, 0, 300, 50); // Player connected image
		
		
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
			
			boolean accepted = sm.accept();
			if(accepted) connected = true;
			
			/*CommunicationThread commThread = new CommunicationThread();
			new Thread(commThread).start();*/
			
			ServerManagement.write("Connected to server.");
    	    
			
			
			/*while (!Thread.currentThread().isInterrupted()) {
				if(numOfPlayers >2) break;

				try {

					Socket newSocket = serverSocket.accept();
					Socketlist.add(newSocket);

					writer = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(newSocket.getOutputStream())),
							true);
		    	    
					writer.println("Connected to server.");
					
					connected.setText(connected.getText().toString() + "\n" + numOfPlayers + " Player connected");

					CommunicationThread commThread = new CommunicationThread(newSocket);
					new Thread(commThread).start();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}*/
		}
	}

	/*class CommunicationThread implements Runnable {

		public void run() {

			while (!Thread.currentThread().isInterrupted()) {

				String read = ServerManagement.read();

				updateConversationHandler.post(new updateUIThread(read));

			}
		}

	}*/


}