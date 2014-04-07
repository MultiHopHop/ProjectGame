package com.badlogic.androidgames.mygame;

public class PowerUp {
	public static final int SPEEDUP = 0;
	public static final int STUN = 1;
	public int x, y;
	public int type;
	
	public PowerUp(int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}
}
