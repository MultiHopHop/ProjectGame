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

	public World() {
		player = new Player(0, 0);
		bot = new Player(WORLD_WIDTH-1, WORLD_HEIGHT-1);
		players.add(player);
		players.add(bot);
		numPlayer = players.size();
		Log.d("OnlyWorldTest", "Create World");
		System.out.println("World Created");

		powerUp = null;

		for (int i = 0; i < WORLD_WIDTH; i++) {
			for (int j = 0; j < WORLD_HEIGHT; j++) {
				board[i][j] = 0;
			}
		}

		board[player.x][player.y] = 1;
		board[bot.x][bot.y] = 2;
	}

	private void placePowerUp() {

		int powerX = random.nextInt(WORLD_WIDTH);
		int powerY = random.nextInt(WORLD_HEIGHT);

		while (true) {
			if (powerX != player.x || powerY != player.y) {
				break;
			}
			powerX += 1;
			if (powerX >= WORLD_WIDTH) {
				powerX = 0;
				powerY += 1;
				if (powerY >= WORLD_HEIGHT) {
					powerY = 0;
				}
			}
		}
		powerUp = new PowerUp(powerX, powerY, PowerUpType.randomPowerup());

	}

	public void update(float deltaTime) {
		if (gameOver) {
			return;
		}

		tickTime += deltaTime;
		counter += deltaTime;

		if (counter > TICK_POWERUP && powerUp == null) {
			placePowerUp();
			if (tick - TICK_DECREMENT > 0) {
				tick -= TICK_DECREMENT;
			}
		}

		while (tickTime > tick) {
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
