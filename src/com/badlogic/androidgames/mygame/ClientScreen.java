package com.badlogic.androidgames.mygame;

import java.util.List;

import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.Screen;

public class ClientScreen extends Screen {
	
	private ClientManagement cm;
	private static String SERVER_IP = "";
	private int numPlayers;
	
	boolean connected = false;

	public ClientScreen(Game game) {
		super(game);
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
					return;
				}
				
				if (inBounds(event, 200, 416, 120, 48)) { // connect button
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					new Thread(new ClientThread()).start();
				}
			}
			if(!connected){
				if (event.type == TouchEvent.TOUCH_DOWN) { //code for numbpad
					if (inBounds(event, 0, 150, 52, 50)) {  // 1
						SERVER_IP += "1";
					}
					if (inBounds(event, 58, 150, 52, 50)) { // 2
						SERVER_IP += "2";
					}
					if (inBounds(event, 114, 150, 52, 50)) { // 3
						SERVER_IP += "3";
					}
					if (inBounds(event, 0, 150+58, 52, 50)) {  // 4
						SERVER_IP += "4";
					}
					if (inBounds(event, 58, 150+58, 52, 50)) { // 5
						SERVER_IP += "5";
					}
					if (inBounds(event, 114, 150+58, 52, 50)) { // 6
						SERVER_IP += "6";
					}
					if (inBounds(event, 0, 150+114, 52, 50)) {  // 7
						SERVER_IP += "7";
					}
					if (inBounds(event, 58, 150+114, 52, 50)) { // 8
						SERVER_IP += "8";
					}
					if (inBounds(event, 114, 150+114, 52, 50)) { // 9
						SERVER_IP += "9";
					}
					if (inBounds(event, 0, 150+170, 52, 50)) {  // .
						SERVER_IP += ".";
					}
					if (inBounds(event, 58, 150+170, 52, 50)) { // 0
						SERVER_IP += "0";
					}
					if (inBounds(event, 114, 150+170, 52, 50)) { // backspace
						if(SERVER_IP != null)
							if(SERVER_IP.length() > 0)
								SERVER_IP = SERVER_IP.substring(0, SERVER_IP.length()-1);
					}
				}
			}	
		}
	}

	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();
		g.drawPixmap(Assets.background, 0, 0);
		g.drawPixmap(Assets.serverclient, g.getWidth()/2 -60, 0, 0, 48, 120, 48); // Client image
		g.drawPixmap(Assets.serverclient, 0, 416, 0, 96, 120, 48); // Back button image
		g.drawPixmap(Assets.serverclient, 200, 416, 0, 192, 120, 48); // Connect button image
		
		if(!connected)
			g.drawPixmap(Assets.numberpad, 0, 150, 0, 0, 163, 219); // numberpad image
		
		if(SERVER_IP == null);
		else drawText(g, SERVER_IP, 0, 80); // display IP
		
		if(connected) g.drawPixmap(Assets.client, 0, 110, 0, 0, 160, 50); // Player connected image
		
		
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
	
	class ClientThread implements Runnable {

		//@Override
		public void run() {

//			cm = new ClientManagement(SERVER_IP);
//			
//			CommunicationThread commThread = new CommunicationThread();
//			new Thread(commThread).start();

			cm = new ClientManagement(SERVER_IP);
			Log.d("ClientRequest", "request");
			String input;
			input = cm.read();
			String[] requests = input.split("\n");
			Log.d("TestClient", input);
			for (String request : requests) {
				if (request.matches("[1-3]")) {
					cm.clientIndex = Integer.parseInt(request);
					connected = true;
				}
			}

			input = cm.read();
			if (input.contains("startgame")) {
				numPlayers = Integer.parseInt(cm.read().substring(0, 1));
				game.setScreen(new GameScreenClient(game, cm, numPlayers));
			}

		}

	}
	
//	class CommunicationThread implements Runnable {
//
//		public void run() {
//
//			while (!Thread.currentThread().isInterrupted()) {
//
//				String read = ClientManagement.read();
//
//				if(read != null) connected = true;
//			}
//		}
//		
//	}


}
