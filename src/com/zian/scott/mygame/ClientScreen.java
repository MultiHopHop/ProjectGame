	package com.zian.scott.mygame;

import java.util.List;

import android.util.Log;

import com.zian.scott.framework.Game;
import com.zian.scott.framework.Graphics;
import com.zian.scott.framework.Screen;
import com.zian.scott.framework.Input.TouchEvent;

public class ClientScreen extends Screen {

	private ClientManagement cm; //cm manages the socket connection
	private static String SERVER_IP = ""; //IP of server
	private int numPlayers; //number of players in the game

	boolean connected = false; //check if client has been connected to server
	boolean authenticated = false; //check if authentication passes or fails
	
	private int t; //Authentication type number
	private boolean pressed = false;  //check if connect button was pressed

	public ClientScreen(Game game, Integer t) {
		super(game);
		this.t = t;
	}

	/*The update method processes the touch inputs of the user*/
	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();

		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
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
					if(!pressed){ // connect button was pressed
						pressed = true;
						new Thread(new ClientThread()).start(); //initialize Client
					}
				}
			}

			//hides the numberpad if connected
			if(!connected){
				if (event.type == TouchEvent.TOUCH_DOWN) { //code for numbpad
					if (inBounds(event, game.getGraphics().getWidth()/2-81, 150, 52, 50)) {  // 1
						SERVER_IP += "1";
					}
					if (inBounds(event, game.getGraphics().getWidth()/2-81+58, 150, 52, 50)) { // 2
						SERVER_IP += "2";
					}
					if (inBounds(event, game.getGraphics().getWidth()/2-81+114, 150, 52, 50)) { // 3
						SERVER_IP += "3";
					}
					if (inBounds(event, game.getGraphics().getWidth()/2-81, 150+58, 52, 50)) {  // 4
						SERVER_IP += "4";
					}
					if (inBounds(event, game.getGraphics().getWidth()/2-81+58, 150+58, 52, 50)) { // 5
						SERVER_IP += "5";
					}
					if (inBounds(event, game.getGraphics().getWidth()/2-81+114, 150+58, 52, 50)) { // 6
						SERVER_IP += "6";
					}
					if (inBounds(event, game.getGraphics().getWidth()/2-81, 150+114, 52, 50)) {  // 7
						SERVER_IP += "7";
					}
					if (inBounds(event, game.getGraphics().getWidth()/2-81+58, 150+114, 52, 50)) { // 8
						SERVER_IP += "8";
					}
					if (inBounds(event, game.getGraphics().getWidth()/2-81+114, 150+114, 52, 50)) { // 9
						SERVER_IP += "9";
					}
					if (inBounds(event, game.getGraphics().getWidth()/2-81, 150+170, 52, 50)) {  // .
						SERVER_IP += ".";
					}
					if (inBounds(event, game.getGraphics().getWidth()/2-81+58, 150+170, 52, 50)) { // 0
						SERVER_IP += "0";
					}
					if (inBounds(event, game.getGraphics().getWidth()/2-81+114, 150+170, 52, 50)) { // backspace
						if(SERVER_IP != null)
							if(SERVER_IP.length() > 0)
								SERVER_IP = SERVER_IP.substring(0, SERVER_IP.length()-1);
					}
				}
			}
		}
	}

	/* present draws the required graphics onto the game screen */
	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();

		g.drawPixmap(Assets.background, 0, 0);
		g.drawPixmap(Assets.serverclient, g.getWidth()/2 -60, 0, 0, 48, 120, 48); // Client image
		g.drawPixmap(Assets.serverclient, 0, 416, 0, 96, 120, 48); // Back button image
		g.drawPixmap(Assets.serverclient, 200, 416, 0, 192, 120, 48); // Connect button image
		g.drawPixmap(Assets.bird, g.getWidth() - 40, 5, 0, 0, 32, 32); // bird image
		
		if(!connected)
			g.drawPixmap(Assets.numberpad, g.getWidth()/2-81, 150, 0, 0, 163, 219); // numberpad image
		
		if(SERVER_IP == null);
		else drawText(g, SERVER_IP, 0, 80); // display IP
		
		if(connected) g.drawPixmap(Assets.client, 0, 110, 0, 0, 160, 50); // Player connected image
		if(authenticated) g.drawPixmap(Assets.client, 0, 110+50, 0, 50, 160, 50); // Autenticated image
		
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
	public boolean inBounds(TouchEvent event, int x, int y, int width,
			int height) {
		if (event.x > x && event.x < x + width - 1 && event.y > y
				&& event.y < y + height - 1) {
			return true;
		} else {
			return false;
		}
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

	class ClientThread implements Runnable {

		public void run() {

			cm = new ClientManagement(SERVER_IP); // creates an instance of ClientManagement
			Log.d("ClientRequest", "request");


			if (!(authenticated = cm.initializeAuthenticate(t))) { // initialize Authentication
				//if Authentication fails
				cm.stop(); //stop ClientManagement
				pressed = false; //allow press on Connect button
				return;
			}
			
			String input;
			input = cm.read(); //reads from socket
			String[] requests = input.split("\n"); // split message into lines
			Log.d("TestClient", input);
			for (String request : requests) { //examine every line
				if (request.matches("[1-3]")) { // check if line contains player number
					cm.clientIndex = Integer.parseInt(request); //set player number
					connected = true; //assert connection to true
				}
				else{ //line is invalid
					cm.stop(); //stop CM
					pressed = false; //allow press on Connect button
					return;					
				}
			}

			input = cm.read(); //reads from socket
			if (input.contains("start")) {//examine if input contains start
				numPlayers = Integer.parseInt(cm.read().substring(0, 1)); //set number of players
				game.setScreen(new GameScreenClient(game, cm, numPlayers)); //go to game screen
			}
			

		}

	}
}

