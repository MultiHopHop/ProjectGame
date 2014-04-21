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
	public int numPlayer;
	public List<Player> players = new ArrayList<Player>();
	public int board[][] = new int[WORLD_WIDTH][WORLD_HEIGHT];

	float tickTime = 0;
	// tick is the interval between each move. 
	// This part should be deleted for actual implementation
	float tick = TICK_INITIAL;
	float powerupCounter = 0;
	Random random = new Random();

	public World(int num) {
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
		
		numPlayer = players.size();
		
//		powerUp = null;
		powerUpList = new LinkedList<PowerUp>();

		for (int i = 0; i < WORLD_WIDTH; i++) {
			for (int j = 0; j < WORLD_HEIGHT; j++) {
				board[i][j] = 0;
			}
		}

		for (int i=0; i<numPlayer; i++) {
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

	public void update(float deltaTime) {
		if (gameOver) {
			return;
		}

		tickTime += deltaTime;
		powerupCounter += deltaTime;

		
//		if (counter > TICK_POWERUP && powerUp == null) {
//			placePowerUp();
//			if (tick - TICK_DECREMENT > 0) {
//				tick -= TICK_DECREMENT;
//			}
//		}

		while (tickTime > tick) {
//			Log.d("checkWolrd", "timer: "+tickTime);
//			Log.d("checkWorld", "tick: "+tick);
//			
//			Log.d("checkWorld", "dir0: "+players.get(0).direction);
//			Log.d("checkWorld", "dir1: "+players.get(1).direction);

			for (int i=0; i<numPlayer; i++) {
				players.get(i).advance();
				board[players.get(i).x][players.get(i).y] = i+1;
				
				if (!powerUpList.isEmpty()) {
					for (PowerUp powerUp: powerUpList) {
						if (players.get(i).x == powerUp.x && players.get(i).y == powerUp.y) {
							if(players.get(i).powerUpList.size()<5){
								players.get(i).powerUpList.add(powerUp.type);
							}
							powerUpList.remove(powerUp);
							break;
						}
					}					
				}
			}
			tickTime -= tick;
//			Log.d("checkWorld", "coord0: "+players.get(1).x+";"+players.get(1).y);
//			Log.d("checkWorld", "coord1: "+players.get(1).x+";"+players.get(1).y);

		}
	}

	public void updateSquare(int x, int y) {
		board[x][y] = 1;
	}
}
