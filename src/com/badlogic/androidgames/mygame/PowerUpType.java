package com.badlogic.androidgames.mygame;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum PowerUpType
{
	SPEEDUP, // should allow player to move twice for limited time
	STUN, // should force other players to halt movement for limited time
	BOMB, // should change tiles surrounding player to his color
	SCORE_MULTIPLIER; // should have save a multiplier that affect final scores

	private static final List<PowerUpType> VALUES = Collections
			.unmodifiableList(Arrays.asList(values()));
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();

	public static PowerUpType randomPowerup()
	{
		return VALUES.get(RANDOM.nextInt(SIZE));
	}
}
