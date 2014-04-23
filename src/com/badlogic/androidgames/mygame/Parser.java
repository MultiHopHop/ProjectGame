package com.badlogic.androidgames.mygame;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class Parser {
	// Example String input: "Player1 move up 0",
	// "Server spawnpowerup 23 4", "Player2 move down -1"

//	private final int REQUEST = 0;
//	private final int DENY = -1;
//	private final int APPROVE = 1;

	private String agent, command, argument;
	private int flag = 10;

	private String output = null;
	private int playerIndex;
//	private List<Player> players;
	private World world;
//	private boolean checkMove;

	public Parser (World world) {
//		this.players = players;
		this.world = world;
	}

	public void parse(String input) {
		lexer(input);
		if(agent== null || command==null) return;
//		if (flag == -1) {
//			return;
//		}
		Log.d("Parser", "Agent: "+agent);

		if (agent.matches("Player[0-3]")) {
			int position = agent.length() - 1;
			//Log.d("Parser", "position: "+position);
			char temp = agent.charAt(position);
			//Log.d("Parser", "temp: "+temp);
			playerIndex = temp - '0';
			//Log.d("Parser", "index: "+playerIndex);
			//Log.d("Parser", "input: "+input);
			//Log.d("Parser", "command: "+command);
			//Log.d("Parser", "argument: "+argument);
			if (command.equals("move")) {
				if (argument.equals("up")) {
					Log.d("Parser", "player"+playerIndex+" move up");
					world.players.get(playerIndex).moveUp();
				} else if (argument.equals("down")) {
					world.players.get(playerIndex).moveDown();
				} else if (argument.equals("right")) {
					world.players.get(playerIndex).moveRight();
				} else if (argument.equals("left")) {
					world.players.get(playerIndex).moveLeft();
				}
			}
			
			else if (command.equals("activate")) {
				if (argument.equals("SPEEDUP")) {
					world.speedup(playerIndex);
				}else if (argument.equals("STUN")) {
					world.stun(playerIndex);
				}else if (argument.equals("BOMB")) {
					world.bomb(playerIndex);
				}
			}
		}

		if (agent.equals("Server")) {
			if (command.equals("update")) {
				world.update();
			}
			if (command.equals("spawnpowerup")) {
				String[] xy = argument.split(" ");
				int x = Integer.parseInt(xy[0]);
				int y = Integer.parseInt(xy[1]);
				if (xy[2].equals("speedup")) {
					world.placePowerUp(x, y, PowerUpType.SPEEDUP);
				}
				else if (xy[2].equals("stun")) {
					world.placePowerUp(x, y, PowerUpType.STUN);
				}
				else if (xy[2].equals("bomb")) {
					world.placePowerUp(x, y, PowerUpType.BOMB);
				}
 			}
		}

		// if (command.equals("spawnpowerup") && agent.equals("Server")) {
		// String[] xy = argument.split(" ");
		// int x = Integer.parseInt(xy[0]);
		// int y = Integer.parseInt(xy[1]);
		// board.spawnPowerup(new Coord(x, y));
		// }
	}

	public boolean isRequest() {
		return (flag == 0);
	}

	public String getOutput() {
		return this.output;
	}

	private void lexer(String input) {
		Pattern patterns = Pattern.compile("((Player[0-3])|Server)|"
				+ "(move|spawnpowerup|activate|update)|"
				+ "(up|down|right|left|(SPEEDUP|STUN|BOMB)|([0-9]+ [0-9]+ (speedup|stun|bomb)))|");
		Matcher matcher = patterns.matcher(input);
		while (matcher.find()) {
			if (matcher.group().matches("(Player[0-3])|Server")) {
				agent = matcher.group();
			} else if (matcher.group().matches("(move|spawnpowerup|activate|update)")) {
				command = matcher.group();
			} else if (matcher.group().matches(
					"(up|down|right|left|(SPEEDUP|STUN|BOMB)|([0-9]+ [0-9]+ (speedup|stun|bomb)))")) {
				argument = matcher.group();
			} 
//			else if (matcher.group().matches("-?[01]")) {
//				System.out.println("Flag: " + matcher.group());
//				flag = Integer.parseInt(matcher.group());
//			}
		}
	}
}