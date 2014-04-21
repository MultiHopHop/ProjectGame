package com.badlogic.androidgames.mygame;

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
	private List<PowerUpType> powerUpList;
	
	public int lastX, lastY, x, y, direction;

	
	public Player(int x, int y) {
		this.x = x;
		this.y = y;
		powerUpList = new ArrayList<PowerUpType>();
		this.direction = DEFAULT;
	}
	
	public void storePowerUps(PowerUpType type)
	{
		powerUpList.add(type);
	}
	
	// TODO Scott, where do we put this?
	
	public List<PowerUpType> getPowerUps()
	{
		return powerUpList;
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
