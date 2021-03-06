package com.zian.scott.mygame;

import java.util.List;
import java.util.Random;

import android.graphics.Color;
import android.util.Log;

import com.zian.scott.framework.Game;
import com.zian.scott.framework.Graphics;
import com.zian.scott.framework.Pixmap;
import com.zian.scott.framework.Screen;
import com.zian.scott.framework.Input.TouchEvent;

/**
 * This is the game screen for server It handles all the requests from clients
 * and broadcast the update message
 * 
 * @author zianli
 * 
 */
public class GameScreenServer extends Screen {
	
	//Game State types
	enum GameState {
		Ready, Running, Paused, GameOver
	}

	GameState state = GameState.Ready; //state of game
	World world; //instance of world
	String score = "0";
	private static float timer; // The timer indicates state of game, unit is second
	private static float powerUpTimer; // timer for powerup generation
	private final int powerUpIntervalLow = 5; // lower limit for powerup generation
	private final int powerUpIntervalUp = 8; // upper limit for powerup generation
	private final int endTime = 60; //total game time
	private static final float TICK_INITIAL = 0.4f; // time interval for every update
	private ServerManagement sm; //instance of ServerManagement to manage socket connection
	Parser parser; //instance of parser to parse messages
	Random random = new Random(); // To generate random time for powerup generation
	
	private static float worldTimer; // time interval to update the world
	private static float tick = TICK_INITIAL; // time interval for every update
	
	int maxNumPowerUps = 5; // Maximum number of powerups + 1
	int gridCount[]; //stores number of tiles occupied by each player starting with player0
	private static String tempString = ""; 
	
	boolean ai = false; // if AI is required
	private AI bot; //instance of AI

	public GameScreenServer(Game game, ServerManagement sm) {
		super(game);

		this.sm = sm;
		timer = 0; // set timer to 0
		
		//If Single Player mode
		if(sm.sockets.size() == 0){
			world = new World(2); //initialize world with number of players
			parser = new Parser(world); //initialize parser with world
			ai = true; //ai required
			bot = new AI(world,parser); //initialize AI
		}
		//MultiPlayer mode
		else
			world = new World(sm.sockets.size() + 1); //initialize world with number of players
			parser = new Parser(world); //initialize parser with world
		
		
	}

	/*The update method increases the timer if game is not paused
	 *then calls the correct method based on the current game state
	 *to process the touch inputs of the player*/
	@Override
	public void update(float deltaTime) {
		if (state != GameState.Paused) {
			timer += deltaTime;
		}
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();

		if (state == GameState.Ready)
			updateReady(touchEvents);
		if (state == GameState.Running)
			updateRunning(touchEvents, deltaTime);
		if (state == GameState.Paused)
			updatePaused(touchEvents);
		if (state == GameState.GameOver){
			gridCount = countGrids();
			updateGameOver(touchEvents);
		}
	}

	/* change state to GameState.Running when server issues 'ready */
	private void updateReady(List<TouchEvent> touchEvents) {
		if (timer > 3) {
			Log.d("ServerReadgState", "timer: " + timer);
			sm.write("ready");
			state = GameState.Running;
			timer = 0;
			powerUpTimer = 0;
		}
	}

	/* updates when the game state is running
	 * 1) Increase timers
	 * 2) processes the touch inputs of the player
	 * 3) handle request from clients
	 * 4) spawn power up
	 * 5) Handles AI
	 * 6) update world
	 * 7) handles end of game
	 * 4) calls for stun sound if player is stunned
	 */
	private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {
		
		//increase Timers
		powerUpTimer += deltaTime;
		worldTimer += deltaTime;

		// handle touch input
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (event.x < 64 && event.y < 64) { //pause
					if (Settings.soundEnabled) {
						Assets.click1.play(1);
					}
					sm.write("pause");
					state = GameState.Paused;
					return;
				}
			}
			
			if (event.type == TouchEvent.TOUCH_DOWN) {
				if (inBounds(event, 256, 416, 64, 64)) { //right
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					world.players.get(0).moveRight();
					tempString = "Player0 move right";
				}
				if (inBounds(event, 192, 416, 64, 64)) { //down
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					world.players.get(0).moveDown();
					tempString = "Player0 move down";
				}
				if (inBounds(event, 128, 416, 64, 64)) { //left
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					world.players.get(0).moveLeft();
					tempString = "Player0 move left";
				}
				if (inBounds(event, 192, 352, 64, 64)) { //up
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					world.players.get(0).moveUp();
					tempString = "Player0 move up";
				}		
				if (inBounds(event, 0, 352, 40, 40)) { //first power up
					if(!world.players.get(0).powerUpList.isEmpty()){
			        	List<PowerUpType> list = world.players.get(0).powerUpList;
			        	String s = "Player0" + " activate "+list.get(0).toString();
			        	sm.write(s+"\n");
			        	parser.parse(s);
			        	PowerUpType p = list.get(0);
			        	if (Settings.soundEnabled){
			        		switch(p){
				        		case SPEEDUP:
			        				Assets.fastbounce.play(1);
			        				break;
			        			case STUN:
			        				Assets.eat.play(2);
			        				break;
			        			case BOMB:
			        				Assets.explode.play(1);
			        				break;
			        		}
			        	}	
			        	list.remove(0);
					}
				}
				if (inBounds(event, 40, 352, 40, 40)) { //second power up
					if(world.players.get(0).powerUpList.size() > 1){
			        	List<PowerUpType> list = world.players.get(0).powerUpList;
			        	String s = "Player0" + " activate "+list.get(1).toString();
			        	sm.write(s+"\n");
			        	parser.parse(s);
			        	PowerUpType p = list.get(1);
			        	if (Settings.soundEnabled){
			        		switch(p){
				        		case SPEEDUP:
			        				Assets.fastbounce.play(1);
			        				break;
			        			case STUN:
			        				Assets.eat.play(2);
			        				break;
			        			case BOMB:
			        				Assets.explode.play(1);
			        				break;
			        		}
			        	}	
			        	list.remove(1);
					}
				}
				if (inBounds(event, 80, 352, 40, 40)) { //third power up
					if(world.players.get(0).powerUpList.size() > 2){
			        	List<PowerUpType> list = world.players.get(0).powerUpList;
			        	String s = "Player0" + " activate "+list.get(2).toString();
			        	sm.write(s+"\n");
			        	parser.parse(s);
			        	PowerUpType p = list.get(2);
			        	if (Settings.soundEnabled){
			        		switch(p){
				        		case SPEEDUP:
			        				Assets.fastbounce.play(1);
			        				break;
			        			case STUN:
			        				Assets.eat.play(2);
			        				break;
			        			case BOMB:
			        				Assets.explode.play(1);
			        				break;
			        		}
			        	}	
			        	list.remove(2);
					}
				}
				if (inBounds(event, 120, 352, 40, 40)) { //forth power up
					if(world.players.get(0).powerUpList.size() > 3){
			        	List<PowerUpType> list = world.players.get(0).powerUpList;
			        	String s = "Player0" + " activate "+list.get(3).toString();
			        	sm.write(s+"\n");
			        	parser.parse(s);
			        	PowerUpType p = list.get(3);
			        	if (Settings.soundEnabled){
			        		switch(p){
				        		case SPEEDUP:
			        				Assets.fastbounce.play(1);
			        				break;
			        			case STUN:
			        				Assets.eat.play(2);
			        				break;
			        			case BOMB:
			        				Assets.explode.play(1);
			        				break;
			        		}
			        	}	
			        	list.remove(3);
					}
				}
			}
		}

		// handle request from clients
		if (sm.ready()) {
			String clientInput = sm.read();
			Log.d("clientInput", clientInput);

			String[] requests = clientInput.split("\n");

			for (String request : requests) {
				// check if input is 'pause'
				if (request.equals("pause")) {
					sm.write(clientInput);
					state = GameState.Paused;
					return;
				}
				parser.parse(request);
				sm.write(request);
			}
		}

		// spawn power up
		float randomTime = random.nextFloat()
				* (powerUpIntervalUp - powerUpIntervalLow) + powerUpIntervalLow; //creates a random time
		//check if it is possible to spawn a powerup
		if (powerUpTimer > randomTime && world.powerUpList.size() < maxNumPowerUps) {
			world.placePowerUp();
			String message = "";
			switch (world.powerUp.type) {
			case SPEEDUP:
				message = "Server spawnpowerup "+world.powerUp.x+" "+world.powerUp.y+" speedup";
				break;
			case STUN:
				message = "Server spawnpowerup "+world.powerUp.x+" "+world.powerUp.y+" stun";
				break;
			case BOMB:
				message = "Server spawnpowerup "+world.powerUp.x+" "+world.powerUp.y+" bomb";
				break;
			}
			sm.write(message);
			Log.d("CheckPowerUp", "message: "+message);
			powerUpTimer -= randomTime; //decrease timer as powerup has been spawned
		}

		// Handles AI
		if(ai){
			if (worldTimer > tick) {
				bot.move();
				bot.activate();
			}
		}
		
		// update world
		while (worldTimer > tick) {
			// broadcast move of player 0
			if (!tempString.equals("")) {
				sm.write(tempString);
				tempString = "";
			}
			sm.write("Server update"); //tell players to update world
			world.update();
			Log.d("ServerWrite", "Server update");
			worldTimer -= tick;
		}
		
		// end of game
		if (timer > endTime) {
			if (Settings.soundEnabled) {
				Assets.bitten.play(1);
			}
			sm.write("endGame");
			sm.stop();
			state = GameState.GameOver;
		}
		
		//stun sound
		if(world.players.get(0).stunnedsound){
			if (Settings.soundEnabled){
				Assets.gothit.play(2);
			}
			world.players.get(0).stunnedsound = false;
		}
	}

	/*Processes the touch inputs of the user and
	 * messages from Clients if game is in the paused state*/
	private void updatePaused(List<TouchEvent> touchEvents) {
		
		// Process messages from Clients
		if (sm.ready()) {
			String input = sm.read();
			if (input.equals("resume")) { //Client wants to resume game
				sm.write("resume");
				state = GameState.Running;
				return;
			} else if (input.equals("endGame")) { //Client wants to end game
				sm.write(input);
				sm.stop();
				game.setScreen(new MainMenuScreen(game));
				return;
			}
		}

		// Touch inputs
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (inBounds(event, 80, 100, 160, 48)) { //Server wants to resume game
					if (Settings.soundEnabled)
						Assets.click.play(1);
					sm.write("resume");
					state = GameState.Running;
					return;
				}
				if (inBounds(event, 80, 148, 160, 48)) { //Server wants to end game
					if (Settings.soundEnabled)
						Assets.click.play(1);
					sm.write("endGame");
					sm.stop();
					game.setScreen(new MainMenuScreen(game));
					return;
				}
			}
		}
	}

	/*Processes the touch inputs of the user and
	 *records highscore if game is in the Game Over state*/
	private void updateGameOver(List<TouchEvent> touchEvents) {
		// record high score
		if (world.players.size() > 1) {
			for (int i = 0; i < 5; i++) {
				if (gridCount[1] > Settings.highscores[i]) {					
					Settings.addScore(gridCount[0]);
				}
			}
		}
		
		// Touch inputs
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (event.x >= 128 && event.x <= 192 && event.y >= 200
						&& event.y <= 264) {
					if (Settings.soundEnabled)
						Assets.click.play(1);
					game.setScreen(new MainMenuScreen(game));
					return;
				}
			}
		}
	}

	/* present draws the required graphics onto the game screen */
	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();

		g.drawPixmap(Assets.background, 0, 0); //background image
		
		//Draws the game world
		drawWorld(world);
		
		//Draws the specified UI based on the game state
		if (state == GameState.Ready)
			drawReadyUI();
		if (state == GameState.Running)
			drawRunningUI();
		if (state == GameState.Paused)
			drawPausedUI();
		if (state == GameState.GameOver)
			drawGameOverUI();
		
		// draws timer
		if(timer<61) drawText(g, ((Integer) Math.round(timer)).toString(), 64, g.getHeight() - 42);
		else drawText(g, "60", 64, g.getHeight() - 42);
	}

	/* Draws the game world */
	private void drawWorld(World world) {
		Graphics g = game.getGraphics();
		List<Player> players = world.players;
		List<PowerUp> powerUpList = world.powerUpList;
		int x, y;

		// Colours the tiles in the board
		for (int i = 0; i < world.WORLD_WIDTH; i++) {
			for (int j = 0; j < world.WORLD_HEIGHT; j++) {
				switch (world.board[i][j]) {
				case 1:
					g.drawRect(i * 32, j * 32, 32, 32, Color.RED);
					break;
				case 2:
					g.drawRect(i * 32, j * 32, 32, 32, Color.BLUE);
					break;
				case 3:
					g.drawRect(i * 32, j * 32, 32, 32, Color.GREEN);
					break;
				case 4:
					g.drawRect(i * 32, j * 32, 32, 32, Color.YELLOW);
					break;
				default:
					g.drawRect(i * 32, j * 32, 32, 32, Color.WHITE);
				}

			}
		}

		// draw powerup(s)
		Pixmap powerUpPixmap = null;
		if (!powerUpList.isEmpty()) {
			for (PowerUp powerUp : powerUpList) {
				if (powerUp.type == PowerUpType.BOMB) {
					powerUpPixmap = Assets.bomb;
				}
				if (powerUp.type == PowerUpType.SPEEDUP) {
					powerUpPixmap = Assets.speedup;
				}
				if (powerUp.type == PowerUpType.STUN) {
					powerUpPixmap = Assets.stun;
				}
				// Log.d("PowerUpTest", "powerup drawn");
				x = powerUp.x * 32;
				y = powerUp.y * 32;
				g.drawPixmap(powerUpPixmap, x, y);
			}
		}


		// Draws player icon: bird or mochi
		int index = 0;
		for (Player player : players) {
			Pixmap headPixmap;
			if(index == 0)
				headPixmap = Assets.mochi;
			else 
				headPixmap = Assets.bird;
			x = player.x * 32 + 16;
			y = player.y * 32 + 16;
			g.drawPixmap(headPixmap, x - headPixmap.getWidth() / 2, y
					- headPixmap.getHeight() / 2);
			StringBuilder builder = new StringBuilder();
			builder.append("x" + player.x);
			builder.append("y" + player.y);
			// Log.d("DrawWorldTest", builder.toString());
			index++;
		}

	}

	/* Draws the UI for ready state */
	private void drawReadyUI() {
		Graphics g = game.getGraphics();
		//draw count down to start
		if (timer < 1) {
			g.drawPixmap(Assets.numbers, 150, 100, 60, 0, 20, 32);
		} else if (timer < 2) {
			g.drawPixmap(Assets.numbers, 150, 100, 40, 0, 20, 32);
		} else if (timer < 3) {
			g.drawPixmap(Assets.numbers, 150, 100, 20, 0, 20, 32);
		}
		// g.drawPixmap(Assets.ready, 47, 100);
		g.drawLine(0, 320, 480, 320, Color.BLACK);
	}
	
	/* Draws the UI for running state */
	private void drawRunningUI() {
		Graphics g = game.getGraphics();

		// Draws the lines for the grid
		for (int i = 1; i < world.WORLD_WIDTH; i++) {
			g.drawLine(i * 32, 0, i * 32, 320, Color.BLACK);
		}
		for (int j = 1; j < world.WORLD_HEIGHT; j++) {
			g.drawLine(0, j * 32, 320, j * 32, Color.BLACK);
		}
		g.drawPixmap(Assets.buttons, 0, 0, 64, 128, 64, 64); // pause button
		g.drawLine(0, 320, 480, 320, Color.BLACK);

		g.drawPixmap(Assets.buttons, 192, 416, 64, 192, 64, 64); //down
        g.drawPixmap(Assets.buttons, 128, 416, 64, 64, 64, 64); //left
        g.drawPixmap(Assets.buttons, 192, 352, 0, 192, 64, 64); //up
        g.drawPixmap(Assets.buttons, 256, 416, 0, 64, 64, 64); //right
        
      //Update power up
        if(!world.players.get(0).powerUpList.isEmpty()){
        	List<PowerUpType> list = world.players.get(0).powerUpList;
        	Pixmap powerUpPixmap = null;
        	int x = 0;
			int y = 352;
        	for (PowerUpType powerUp : list) {
 				if (powerUp == PowerUpType.BOMB) {
 					powerUpPixmap = Assets.bomb;
 				}
 				if (powerUp == PowerUpType.SPEEDUP) {
 					powerUpPixmap = Assets.speedup;
 				}
 				if (powerUp == PowerUpType.STUN) {
 					powerUpPixmap = Assets.stun;
 				}
 				g.drawPixmap(powerUpPixmap, x, y);
 				x += 40;
 			}
        }
        
        //stunned image
        if(world.players.get(0).stunned){
        		g.drawPixmap(Assets.stunned, g.getWidth()/2-50,  g.getHeight()/2-200, 0, 0, 100, 100);
        }
	}

	/* Draws the UI for paused state */
	private void drawPausedUI() {
		Graphics g = game.getGraphics();

		g.drawPixmap(Assets.pause, 80, 100); // paused options
		g.drawLine(0, 320, 480, 320, Color.BLACK);
	}

	/* Draws the UI for Game Over state */
	private void drawGameOverUI() {
		Graphics g = game.getGraphics();

		//Draw the number of tiles for each player starting with player0
		String s = "";		
		for(int num :gridCount){
			s += num+".";
		}
		drawText(g,s.substring(0, s.length()-1),g.getWidth()/2-20,150);
		
		//Draw Win, lose or Draw
		if (world.players.size() > 1) {
			int p0 = gridCount[0];
			int p1 = gridCount[1];
			if(p0>p1)
				g.drawPixmap(Assets.winlose, g.getWidth()/2-120, 50, 0, 0, 240, 50);
			else if(p0==p1)
				g.drawPixmap(Assets.winlose, g.getWidth()/2-120, 50, 0, 100, 240, 50);
			else
				g.drawPixmap(Assets.winlose, g.getWidth()/2-120, 50, 0, 50, 240, 50);
		}
				
		g.drawPixmap(Assets.gameOver, 62, 100); //GameOver image
		g.drawPixmap(Assets.buttons, 128, 200, 0, 128, 64, 64); // X button image
		g.drawLine(0, 320, 480, 320, Color.BLACK);
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

	@Override
	public void pause() {
		if (state == GameState.Running) {
			sm.write("pause");
			state = GameState.Paused;
		}
		if (world.gameOver) {
			Settings.addScore(110);
			Settings.save(game.getFileIO());
		}
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
	
	/*Counts and returns the number of tiles for each player*/
	private int[] countGrids(){
		int ans[] = new int[world.numPlayers];
		for(int i=0;i<world.board.length;i++){
			for(int j=0;j<world.board.length;j++){
				if(world.board[i][j] > 0){
					ans[world.board[i][j]-1] += 1;
				}
			}
		}
		
		return ans;
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
}
