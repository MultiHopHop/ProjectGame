package com.zian.scott.mygame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.util.Log;

public class World {
	protected final int WORLD_WIDTH = 10; // default width of board
	protected int WORLD_HEIGHT = 10; // default height of board

	public PowerUp powerUp; // PowerUp instance
	public List<PowerUp> powerUpList; // stores power ups on the board
	public boolean gameOver = false;
	public int numPlayers; // stores number of players in the game
	public List<Player> players = new ArrayList<Player>(); // stores list of Player object
	public int board[][]; // 2D array of board

	private Random random = new Random();
	
	public List<Integer> playerPowerUpTime = new LinkedList<Integer>();

	/**
	 * Initialize world instance
	 * @param num
	 */
	public World(int num) {
		board = new int[WORLD_WIDTH][WORLD_HEIGHT]; // initialize board
				
		for(int i=0;i<num;i++){
			playerPowerUpTime.add(0);
		}
		
		// initialize players according to number of players connected to the game
		switch (num) {
		case 1:
			players.add(new Player(0, 0));
			break;
		case 2:
			players.add(new Player(0, 0));
			players.add(new Player(WORLD_WIDTH-1, WORLD_HEIGHT-1));
			break;
		case 3:
			players.add(new Player(0, 0));
			players.add(new Player(WORLD_WIDTH-1, WORLD_HEIGHT-1));
			players.add(new Player(0, WORLD_HEIGHT-1));
			break;
		case 4:
			players.add(new Player(0, 0));
			players.add(new Player(WORLD_WIDTH-1, WORLD_HEIGHT-1));
			players.add(new Player(0, WORLD_HEIGHT-1));
			players.add(new Player(WORLD_WIDTH-1, 0));
			break;		
		}
		
		numPlayers = players.size();
		
		powerUpList = new LinkedList<PowerUp>();

		// set each grid to be 0
		for (int i = 0; i < WORLD_WIDTH; i++) {
			for (int j = 0; j < WORLD_HEIGHT; j++) {
				board[i][j] = 0;
			}
		}

		// set each player's grid to their respective (player index + 1)
		for (int i=0; i<numPlayers; i++) {
			board[players.get(i).x][players.get(i).y] = i+1;
		}
		Log.d("WorldTest", "World Created");

	}

	/**
	 * Generate a random power up at random location
	 * This method can only called by server
	 */
	public void placePowerUp() {
		int powerX = random.nextInt(WORLD_WIDTH);
		int powerY = random.nextInt(WORLD_HEIGHT);
		boolean foundSlot = false;
		boolean emptySlot = false;
		
		while (!foundSlot) {
			emptySlot = true;
			for (Player player: players) { // check if the slot is occupied by any player
				if (powerX == player.x && powerY == player.y) {
					emptySlot = false;
					break;
				}			
			}
			
			for (PowerUp powerUp: powerUpList) { // check if the slot is occupied by other power ups
				if (powerUp.x == powerX && powerUp.y == powerY) {
					emptySlot = false;
					break;
				}
			}
			
			if (!emptySlot) { // if not empty, increment current coordinates by 1
				powerX += 1;
				if (powerX >= WORLD_WIDTH) {
					powerX = 0;
					powerY += 1;
					if (powerY >= WORLD_HEIGHT) {
						powerY = 0;
					}
				}
			}
			else {
				powerUp = new PowerUp(powerX, powerY, PowerUpType.randomPowerup()); // generate a random power up
				powerUpList.add(powerUp); // add the power up to powerUpList
				foundSlot = true; // stop while loop
			}
		}
	}
	
	public void placePowerUp(int powerX, int powerY, PowerUpType type) { // place a power up of specific type at specific location
		powerUpList.add(new PowerUp(powerX, powerY, type));
	}

	public void update() {
		if (gameOver) { // stop if game over
			return;
		}

		for (int i=0; i<numPlayers; i++) {			
			players.get(i).advance(); // each player advances in his current direction
			board[players.get(i).x][players.get(i).y] = i+1; // change the grid to his (player index+1)
			Log.d("World", "player"+i+" x:"+players.get(i).x+" y:"+players.get(i).y );
			
			if(players.get(i).step == 2){ // if speedup is activated, the player advance one more step
				Log.d("World", "step: "+players.get(i).step);
				int x = players.get(i).x;
				Log.d("World", "x: "+x);
				int lastX = players.get(i).lastX;
				Log.d("World", "lastX: "+lastX);
				int y = players.get(i).y;
				Log.d("World", "y: "+y);
				int lastY = players.get(i).lastY;
				Log.d("World", "lastY: "+lastY);
				if(y>lastY){ 
					board[x][lastY+1] = i+1;
				}
				if(y<lastY){
					board[x][lastY-1] = i+1;
				}
				if(x>lastX){
						board[lastX+1][y] = i+1;
				}
				if(x<lastX){
					board[lastX-1][y] = i+1;
				}
									
			}
			
			if (!powerUpList.isEmpty()) {
				for (PowerUp powerUp: powerUpList) {
					if (players.get(i).x == powerUp.x && players.get(i).y == powerUp.y) { // check if the player comes to a power up location
						if(players.get(i).powerUpList.size()<4){ // pick up the power up if less than 4 power ups are taken
							players.get(i).powerUpList.add(powerUp.type);
						}
						powerUpList.remove(powerUp);
						break;
					}
				}					
			}
		}
		
		// handle duration of a power up
		for(int index=0;index<playerPowerUpTime.size();index++){
			if(playerPowerUpTime.get(index) > 0){
				playerPowerUpTime.set(index, playerPowerUpTime.get(index)-1);
				
			} else if(playerPowerUpTime.get(index) == 0){ // return to normal state
				players.get(index).step = 1; 
				players.get(index).stunned = false;
			}
			//Log.d("World", "powerUpTime :"+playerPowerUpTime );
		}
		
		
	}

	// activate speedup for players.playerIndex
	public void speedup(int playerIndex){
		players.get(playerIndex).step = 2;
		playerPowerUpTime.set(playerIndex, 5);	
	}
	// activate stun for players.playerIndex
	public void stun(int playerIndex){
		for(int i=0;i<players.size();i++){
			if(i != playerIndex){ // stun all other players
				Log.d("World", "player "+i+" got stunned");
				players.get(i).step = 0;
				players.get(i).stunned = true;
				players.get(i).stunnedsound = true;
				playerPowerUpTime.set(i, 3);
			}
		}		
	}
	
	// activate bomb for players.playerIndex
	public void bomb(int playerIndex){
		int x = players.get(playerIndex).x;
		int y = players.get(playerIndex).y;
		if(y-1>=0){
			if(x-1>=0)
				board[x-1][y-1] = playerIndex+1;
			board[x][y-1] = playerIndex+1;
			if(x+1<10)
				board[x+1][y-1] = playerIndex+1;
		}
		if(x-1>=0)
			board[x-1][y] = playerIndex+1;
		board[x][y] = playerIndex+1;
		if(x+1<10)
			board[x+1][y] = playerIndex+1;
		if(y+1<10){
			if(x-1>=0)
				board[x-1][y+1] = playerIndex+1;
			board[x][y+1] = playerIndex+1;
			if(x+1<10)
				board[x+1][y+1] = playerIndex+1;
		}			
	}
}
