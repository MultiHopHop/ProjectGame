package com.zian.scott.mygame;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public enum PowerUpType
{
	SPEEDUP, // should allow player to move twice for limited time
	STUN, // should force other players to halt movement for limited time
	BOMB; // should change tiles surrounding player to his color
	
	/**
	 * Returns a randomized power-up as defined in the enum list
	 * 
	 * @return A random power-up
	 */
	public static PowerUpType randomPowerup()
	{
		return Collections.unmodifiableList(Arrays.asList(values())).get(
				(new Random()).nextInt(Collections.unmodifiableList(
						Arrays.asList(values())).size()));
	}
}
