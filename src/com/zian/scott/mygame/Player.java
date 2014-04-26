package com.zian.scott.mygame;


import java.util.LinkedList;
import java.util.List;

public class Player {
	public static final int UP = 0;
	public static final int LEFT = 1;
	public static final int DOWN = 2;
	public static final int RIGHT = 3;
	public static final int DEFAULT = 4; // no direction indicated
	private int min_x = 0;
	private int max_x = 9;
	private int min_y = 0;
	private int max_y = 9;
	int step = 1; // step-size of each move
	boolean stunned = false; // player's stun status
	boolean stunnedsound = false;
	
	public int lastX, lastY, x, y, direction;
	List<PowerUpType> powerUpList = new LinkedList<PowerUpType>();

	/**
	 * Initialize player
	 * 
	 * @param x
	 * @param y
	 */
	public Player(int x, int y) {
		this.x = x;
		this.y = y;
		this.direction = DEFAULT;
	}
	
	public void moveUp() { // change direction to up
		direction = UP; 
	}
	
	public void moveLeft() { // change direction to left
		direction = LEFT;
	}
	
	public void moveDown() { // change direction to down
		direction = DOWN;
	}
	
	public void moveRight() { // change direction to right
		direction = RIGHT;
	}
	
	/**
	 * Player moves one step in his current direction
	 */
	public void advance() {
		if (direction == UP) {
			lastY = y;
			lastX = x;
			y -= step;
		}
		if (direction == LEFT) {
			lastX = x;
			lastY = y;
			x -= step;
		}
		if (direction == DOWN) {
			lastY = y;
			lastX = x;
			y += step;
		}
		if (direction == RIGHT) {
			lastX = x;
			lastY = y;
			x += step;
		}
		direction = DEFAULT;
		
		if (x < min_x) {
			x = min_x;
		}
		if (x > max_x) {
			x = max_x;
		}
		if (y < min_y) {
			y = min_y;
		}
		if (y > max_y) {
			y = max_y;
		}
	}
	
}
