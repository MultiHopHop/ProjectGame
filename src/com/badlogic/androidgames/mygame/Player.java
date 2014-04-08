package com.badlogic.androidgames.mygame;

public class Player {
	public static final int UP = 0;
	public static final int LEFT = 1;
	public static final int DOWN = 2;
	public static final int RIGHT = 3;
	public static final int DEFAULT = 4;
	
	public int lastX, lastY, x, y, direction;
	PowerUpType powerUp;
	
	public Player(int x, int y) {
		this.x = x;
		this.y = y;
//		this.powerUp = powerUp;		
		this.direction = DEFAULT;
	}
	
	public void moveUp() {
		direction = UP; 
	}
	
	public void moveLeft() {
		direction = LEFT;
	}
	
	public void moveDown() {
		direction = DOWN;
	}
	
	public void moveRight() {
		direction = RIGHT;
	}
	
	public void advance() {
		if (direction == UP) {
			y -= 1;
		}
		if (direction == LEFT) {
			x -= 1;
		}
		if (direction == DOWN) {
			y += 1;
		}
		if (direction == RIGHT) {
			x += 1;
		}
		direction = DEFAULT;
		
		if (x < 0) {
			x = 9;
		}
		if (x > 9) {
			x = 0;
		}
		if (y < 0) {
			y = 9;
		}
		if (y > 9) {
			y = 0;
		}
	}
}
