package com.badlogic.androidgames.mygame;

import android.R.integer;

public class PowerUp {
	public static final int SPEEDUP = 0; // should allow player to move in single direction twice for limited time
	public static final int STUN = 1; // should force other players to halt movement for limited time
	public static final int BOMB = 2; // should change tiles surrounding player to his color
	public static final int SCORE_MULTIPLIER = 3; // should have save a multiplier that affect final scores
	public int x, y;
	public int type;
	
	public PowerUp(int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}
}
