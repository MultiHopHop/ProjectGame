package com.badlogic.androidgames.mygame;


import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

public class Player {
	public static final int UP = 0;
	public static final int LEFT = 1;
	public static final int DOWN = 2;
	public static final int RIGHT = 3;
	public static final int DEFAULT = 4;
	private int min_x = 0;
	private int max_x = 9;
	private int min_y = 0;
	private int max_y = 9;
	
	public int lastX, lastY, x, y, direction;
	List<PowerUpType> powerUpList = new LinkedList<PowerUpType>();

	
	public Player(int x, int y) {
		this.x = x;
		this.y = y;
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
