package com.badlogic.androidgames.mygame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.util.Log;

public class World {
	static final int WORLD_WIDTH = 10;
	static final int WORLD_HEIGHT = 10;
	static final float TICK_INITIAL = 1f;
	static final float TICK_DECREMENT = 0.05f;
	static final float TICK_POWERUP = 5f;

	public Player player, bot;
	public PowerUp powerUp;
	public List<PowerUp> powerUpList;
	public boolean gameOver = false;
	public int numPlayers;
	public List<Player> players = new ArrayList<Player>();
	public int board[][] = new int[WORLD_WIDTH][WORLD_HEIGHT];

	float tickTime = 0;
	// tick is the interval between each move. 
	// This part should be deleted for actual implementation
	float tick = TICK_INITIAL;
	float powerupCounter = 0;
	Random random = new Random();
	
	public List<Integer> playerPowerUpTime = new LinkedList<Integer>();

	public World(int num) {
		for(int i=0;i<num;i++){
			playerPowerUpTime.add(0);
		}
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
		
//		powerUp = null;
		powerUpList = new LinkedList<PowerUp>();

		for (int i = 0; i < WORLD_WIDTH; i++) {
			for (int j = 0; j < WORLD_HEIGHT; j++) {
				board[i][j] = 0;
			}
		}

		for (int i=0; i<numPlayers; i++) {
			board[players.get(i).x][players.get(i).y] = i+1;
		}
		Log.d("WorldTest", "World Created");

	}

	/**
	 * This method can only called by server
	 */
	public void placePowerUp() {
		int powerX = random.nextInt(WORLD_WIDTH);
		int powerY = random.nextInt(WORLD_HEIGHT);
		boolean foundSlot = false;
		boolean emptySlot = false;
		
		while (!foundSlot) {
			emptySlot = true;
			for (Player player: players) {
				if (powerX == player.x && powerY == player.y) {
					emptySlot = false;
					break;
				}			
			}
			
			for (PowerUp powerUp: powerUpList) {
				if (powerUp.x == powerX && powerUp.y == powerY) {
					emptySlot = false;
					break;
				}
			}
			
			if (!emptySlot) {
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
				powerUp = new PowerUp(powerX, powerY, PowerUpType.randomPowerup());
				powerUpList.add(powerUp);
				foundSlot = true;
			}
		}
	}
	
	public void placePowerUp(int powerX, int powerY, PowerUpType type) {
		powerUpList.add(new PowerUp(powerX, powerY, type));
	}

	public void update() {
		if (gameOver) {
			return;
		}

		for (int i=0; i<numPlayers; i++) {
			
			players.get(i).advance();
			board[players.get(i).x][players.get(i).y] = i+1;
			Log.d("World", "player"+i+" x:"+players.get(i).x+" y:"+players.get(i).y );
			
			if(players.get(i).step == 2){
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
					if (players.get(i).x == powerUp.x && players.get(i).y == powerUp.y) {
						if(players.get(i).powerUpList.size()<4){
							players.get(i).powerUpList.add(powerUp.type);
						}
						powerUpList.remove(powerUp);
						break;
					}
				}					
			}
		}
		
		for(int index=0;index<playerPowerUpTime.size();index++){
			if(playerPowerUpTime.get(index) > 0){
				playerPowerUpTime.set(index, playerPowerUpTime.get(index)-1);
				
			} else if(playerPowerUpTime.get(index) == 0){
				players.get(index).step = 1;
				players.get(index).stunned = false;
			}
			//Log.d("World", "powerUpTime :"+playerPowerUpTime );
		}
		
		
	}

	public void updateSquare(int x, int y) {
		board[x][y] = 1;
	}
	
	public void speedup(final int playerIndex){
		players.get(playerIndex).step = 2;
		playerPowerUpTime.set(playerIndex, 5);
		
		
	}
	public void stun(int playerIndex){
		for(int i=0;i<players.size();i++){
			if(i != playerIndex){
				Log.d("World", "player "+i+" got stunned");
				players.get(i).step = 0;
				players.get(i).stunned = true;
				players.get(i).stunnedsound = true;
				playerPowerUpTime.set(i, 3);
			}
		}
		
	}
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
