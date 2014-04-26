package com.zian.scott.mygame;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class Parser {
	// Example String input: "Player1 move up",
	// "Server spawnpowerup 23 4 stun", "Player2 activate speedup"

	private String agent, command, argument;
	private int playerIndex; // index of player in world.players
	private World world; // instance of world

	public Parser(World world) {
		this.world = world;
	}

	public void parse(String input) {
		lexer(input);
		if (agent == null || command == null)
			return;
		// Log.d("Parser", "Agent: "+agent);

		if (agent.matches("Player[0-3]")) { // check if agent is player
			// find playerIndex of agent
			int position = agent.length() - 1;
			char temp = agent.charAt(position);
			playerIndex = temp - '0'; // convert char to int
			// Log.d("Parser", "command: "+command);
			// Log.d("Parser", "argument: "+argument);
			
			if (command.equals("move")) { // handle player's move
				if (argument.equals("up")) {
					Log.d("Parser", "player" + playerIndex + " move up");
					world.players.get(playerIndex).moveUp();
				} else if (argument.equals("down")) {
					world.players.get(playerIndex).moveDown();
				} else if (argument.equals("right")) {
					world.players.get(playerIndex).moveRight();
				} else if (argument.equals("left")) {
					world.players.get(playerIndex).moveLeft();
				}
			}

			else if (command.equals("activate")) { // handle activation of power-up
				if (argument.equals("SPEEDUP")) {
					world.speedup(playerIndex);
				} else if (argument.equals("STUN")) {
					world.stun(playerIndex);
				} else if (argument.equals("BOMB")) {
					world.bomb(playerIndex);
				}
			}
		}

		if (agent.equals("Server")) { // check if agent is server
			if (command.equals("update")) { // update world object
				world.update();
			}
			if (command.equals("spawnpowerup")) { // generate a certain power-up at certain location
				String[] xy = argument.split(" ");
				int x = Integer.parseInt(xy[0]);
				int y = Integer.parseInt(xy[1]);
				if (xy[2].equals("speedup")) {
					world.placePowerUp(x, y, PowerUpType.SPEEDUP);
				} else if (xy[2].equals("stun")) {
					world.placePowerUp(x, y, PowerUpType.STUN);
				} else if (xy[2].equals("bomb")) {
					world.placePowerUp(x, y, PowerUpType.BOMB);
				}
			}
		}
	}

	/**
	 * Find out the agent, command and argument in the input string
	 * 
	 * @param input
	 */
	private void lexer(String input) {
		Pattern patterns = Pattern
				.compile("((Player[0-3])|Server)|"
						+ "(move|spawnpowerup|activate|update)|"
						+ "(up|down|right|left|(SPEEDUP|STUN|BOMB)|([0-9]+ [0-9]+ (speedup|stun|bomb)))|");
		Matcher matcher = patterns.matcher(input);
		while (matcher.find()) {
			if (matcher.group().matches("(Player[0-3])|Server")) {
				agent = matcher.group();
			} else if (matcher.group().matches(
					"(move|spawnpowerup|activate|update)")) {
				command = matcher.group();
			} else if (matcher
					.group()
					.matches(
							"(up|down|right|left|(SPEEDUP|STUN|BOMB)|([0-9]+ [0-9]+ (speedup|stun|bomb)))")) {
				argument = matcher.group();
			}
		}
	}
}