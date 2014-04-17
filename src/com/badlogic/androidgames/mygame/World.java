package com.badlogic.androidgames.mygame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.util.Log;

public class World {
	static final int WORLD_WIDTH = 10;
	static final int WORLD_HEIGHT = 10;
	static final float TICK_INITIAL = 1f;
	static final float TICK_DECREMENT = 0.05f;
	static final float TICK_POWERUP = 10f;

	public Player player, bot;
	public PowerUp powerUp;
	public boolean gameOver = false;
	public int numPlayer;
	public List<Player> players = new ArrayList<Player>();
	public int board[][] = new int[WORLD_WIDTH][WORLD_HEIGHT];

	float tickTime = 0;
	// tick is the interval between each move. 
	// This part should be deleted for actual implementation
	float tick = TICK_INITIAL;
	float counter = 0;
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
		
		powerUp = null;

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

	private void placePowerUp() {

//		int powerX = random.nextInt(WORLD_WIDTH);
//		int powerY = random.nextInt(WORLD_HEIGHT);
//		boolean found = false;
//
//		while (!found) {
//			for (Player player: players) {
//				if (powerX == player.x || powerY == player.y) {
//					break;
//				}
//			}			
//			powerX += 1;
//			if (powerX >= WORLD_WIDTH) {
//				powerX = 0;
//				powerY += 1;
//				if (powerY >= WORLD_HEIGHT) {
//					powerY = 0;
//				}
//			}
//		}
//		powerUp = new PowerUp(powerX, powerY, random.nextInt(2));

	}

	public void update(float deltaTime) {
		if (gameOver) {
			return;
		}

		tickTime += deltaTime;
		counter += deltaTime;

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
				
				if (powerUp != null) {
					if (players.get(i).x == powerUp.x && players.get(i).y == powerUp.y) { 
						players.get(i).powerUp = powerUp.type;
						powerUp = null;
						counter = 0;
					}
				}
			}
			tickTime -= tick;
//			Log.d("checkWorld", "coord0: "+players.get(1).x+";"+players.get(1).y);
//			Log.d("checkWorld", "coord1: "+players.get(1).x+";"+players.get(1).y);

			
//			player.advance();
//			Log.d("OnlyWorldTest", "After advance");
//			for (int i=0; i<numPlayer; i++) {
//				board[player.x][player.y] = 1+i;
//			}
//
//			if (powerUp != null) {
//				if (player.x == powerUp.x && player.y == powerUp.y) {
//					player.powerUp = powerUp.type;
//					powerUp = null;
//				}
//			}
//			
//			tickTime -= tick;

		}
	}

	public void updateSquare(int x, int y) {
		board[x][y] = 1;
	}
}
