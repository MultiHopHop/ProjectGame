package com.badlogic.androidgames.mygame;

import java.util.List;

import android.graphics.Color;
import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.Pixmap;
import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.mygame.GameScreenServer.GameState;

public class GameScreen extends Screen {
	enum GameState {
		Ready, Running, Paused, GameOver
	}

	GameState state = GameState.Ready;
	World world;
	String score = "0";
	private static float timer;
	private ClientManagement cm;
	Parser parser;

	public GameScreen(Game game, ClientManagement cm, int numPlayers) {
		super(game);
		this.cm = cm;
		Log.d("CreateWorld", "num: "+numPlayers);
		world = new World(numPlayers);
		timer = 0;
		parser = new Parser(world);
	}

	@Override
	public void update(float deltaTime) {
		timer += deltaTime;
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();

		if (state == GameState.Ready)
			updateReady(touchEvents);
		if (state == GameState.Running)
			updateRunning(touchEvents, deltaTime);
		if (state == GameState.Paused)
			updatePaused(touchEvents);
		if (state == GameState.GameOver)
			updateGameOver(touchEvents);
	}

	/**
	 * change state to GameState.Running when server issues 'ready
	 * 
	 * @param touchEvents
	 */
	private void updateReady(List<TouchEvent> touchEvents) {
//		Log.d("ClientReadgState", "timer: "+timer);
		
		if (cm.ready() && cm.read().equals("ready")) {
			state = GameState.Running;
			timer = 0;
		}
	}

	private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {
//		Log.d("gameState", "running");
		
		// handle touch input
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (event.x < 64 && event.y < 64) {
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					cm.write("pause");
//					state = GameState.Paused;
				}
			}
			if (event.type == TouchEvent.TOUCH_DOWN) {
				if (inBounds(event, 256, 416, 64, 64)) {
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					cm.write("Player" + cm.clientIndex + " move right");
				}
				if (inBounds(event, 192, 416, 64, 64)) {
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					cm.write("Player" + cm.clientIndex + " move down");
				}
				if (inBounds(event, 128, 416, 64, 64)) {
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					cm.write("Player" + cm.clientIndex + " move left");
				}
				if (inBounds(event, 192, 352, 64, 64)) {
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					cm.write("Player" + cm.clientIndex + " move up");
				}
			}
		}

		// handle request from server
		if (cm.ready()) {
			String serverRequest = cm.read();			
			Log.d("ServerRequest", serverRequest);
			
			// check if it is 'pause'
			if (serverRequest.equals("pause")) {
				cm.write(serverRequest);
				state = GameState.Paused;
				return;
			}
			
			// chech if it is 'endGame'
			if (serverRequest.equals("endGame")) {
				cm.stop();
				state = GameState.GameOver;
			}
			
			String[] requests = serverRequest.split("\n");
			for (String request : requests) {
				parser.parse(request);
			}
		}
		

		
		world.update(deltaTime);
	}

	private void updatePaused(List<TouchEvent> touchEvents) {
		if (cm.ready()) {
			String input = cm.read();
			if (input.equals("resume")) {
				state = GameState.Running;
				return;
			}
			else if (input.equals("endGame")) {
				cm.stop();
				game.setScreen(new MainMenuScreen(game));
				return;
			}
		}
		
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (inBounds(event, 80, 100, 160, 48)) {
					if (Settings.soundEnabled)
						Assets.click.play(1);
					cm.write("resume");
//					state = GameState.Running;
					return;
				}
				if (inBounds(event, 80, 148, 160, 48)) {
					if (Settings.soundEnabled)
						Assets.click.play(1);
					cm.write("endGame");
					return;
				}				
			}
		}
	}

	private void updateGameOver(List<TouchEvent> touchEvents) {
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (event.x >= 128 && event.x <= 192 && event.y >= 200
						&& event.y <= 264) {
					if (Settings.soundEnabled)
						Assets.click.play(1);
					game.setScreen(new MainMenuScreen(game));
					return;
				}
			}
		}
	}

	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();

		g.drawPixmap(Assets.background, 0, 0);
		drawWorld(world);
		if (state == GameState.Ready)
			drawReadyUI();
		if (state == GameState.Running)
			drawRunningUI();
		if (state == GameState.Paused)
			drawPausedUI();
		if (state == GameState.GameOver)
			drawGameOverUI();

		drawText(g, score, 64, g.getHeight() - 42);
	}

	private void drawWorld(World world) {
		Graphics g = game.getGraphics();
		List<Player> players = world.players;
		PowerUp powerUp = world.powerUp;
		int x, y;

		for (int i = 0; i < world.WORLD_WIDTH; i++) {
			for (int j = 0; j < world.WORLD_HEIGHT; j++) {
				switch (world.board[i][j]) {
				case 1:
					g.drawRect(i * 32, j * 32, 32, 32, Color.RED);
					break;
				case 2:
					g.drawRect(i * 32, j * 32, 32, 32, Color.GREEN);
					break;
				case 3:
					g.drawRect(i * 32, j * 32, 32, 32, Color.BLUE);
					break;
				case 4:
					g.drawRect(i * 32, j * 32, 32, 32, Color.YELLOW);
					break;
				default:
					g.drawRect(i * 32, j * 32, 32, 32, Color.GRAY);
				}
				// if (world.board[i][j] == 0) {
				// g.drawRect(i*32, j*32, 32, 32, Color.GRAY);
				// }
				// else {
				// g.drawPixmap(Assets.tail, i*32, j*32);
				// }
			}
		}

		Pixmap powerUpPixmap = null;
		if (powerUp != null) {
			if (powerUp.type == PowerUp.SPEEDUP) {
				powerUpPixmap = Assets.stain1;
			}
			if (powerUp.type == PowerUp.STUN) {
				powerUpPixmap = Assets.stain3;
			}
			Log.d("DrawWorldTest", "powerup");
			x = powerUp.x * 32;
			y = powerUp.y * 32;
			g.drawPixmap(powerUpPixmap, x, y);
		}

		for (Player player : players) {
			Pixmap headPixmap = Assets.tail;
			// if (player.direction == Player.UP) {
			// headPixmap = Assets.headUp;
			// }
			// if (player.direction == Player.LEFT) {
			// headPixmap = Assets.headLeft;
			// }
			// if (player.direction == Player.DOWN) {
			// headPixmap = Assets.headDown;
			// }
			// if (player.direction == Player.RIGHT) {
			// headPixmap = Assets.headRight;
			// }
			x = player.x * 32 + 16;
			y = player.y * 32 + 16;
			g.drawPixmap(headPixmap, x - headPixmap.getWidth() / 2, y
					- headPixmap.getHeight() / 2);
			StringBuilder builder = new StringBuilder();
			builder.append("x" + player.x);
			builder.append("y" + player.y);
			// Log.d("DrawWorldTest", builder.toString());
		}

	}

	private void drawReadyUI() {
		Graphics g = game.getGraphics();
		if (timer < 1) {
			g.drawPixmap(Assets.numbers, 150, 100, 60, 0, 20, 32);
		}
		else if (timer < 2) {
			g.drawPixmap(Assets.numbers, 150, 100, 40, 0, 20, 32);
		}
		else if (timer < 3) {
			g.drawPixmap(Assets.numbers, 150, 100, 20, 0, 20, 32);
		}
//		g.drawPixmap(Assets.ready, 47, 100);
		g.drawLine(0, 320, 480, 320, Color.BLACK);
	}

	private void drawRunningUI() {
		Graphics g = game.getGraphics();

		for (int i = 1; i < world.WORLD_WIDTH - 1; i++) {
			g.drawLine(i * 32, 0, i * 32, 320, Color.BLACK);
		}
		for (int j = 1; j < world.WORLD_HEIGHT; j++) {
			g.drawLine(0, j * 32, 320, j * 32, Color.BLACK);
		}
		g.drawPixmap(Assets.buttons, 0, 0, 64, 128, 64, 64);
		g.drawLine(0, 320, 480, 320, Color.BLACK);

		g.drawPixmap(Assets.buttons, 192, 416, 64, 64, 64, 64);
		g.drawPixmap(Assets.buttons, 128, 416, 64, 64, 64, 64);
		g.drawPixmap(Assets.buttons, 192, 352, 0, 64, 64, 64);
		g.drawPixmap(Assets.buttons, 256, 416, 0, 64, 64, 64);
	}

	private void drawPausedUI() {
		Graphics g = game.getGraphics();

		g.drawPixmap(Assets.pause, 80, 100);
		g.drawLine(0, 320, 480, 320, Color.BLACK);
	}

	private void drawGameOverUI() {
		Graphics g = game.getGraphics();

		g.drawPixmap(Assets.gameOver, 62, 100);
		g.drawPixmap(Assets.buttons, 128, 200, 0, 128, 64, 64);
		g.drawLine(0, 320, 480, 320, Color.BLACK);
	}

	public void drawText(Graphics g, String line, int x, int y) {
		int len = line.length();
		for (int i = 0; i < len; i++) {
			char character = line.charAt(i);

			if (character == ' ') {
				x += 20;
				continue;
			}

			int srcX = 0;
			int srcWidth = 0;
			if (character == '.') {
				srcX = 200;
				srcWidth = 10;
			} else {
				srcX = (character - '0') * 20;
				srcWidth = 20;
			}

			g.drawPixmap(Assets.numbers, x, y, srcX, 0, srcWidth, 32);
			x += srcWidth;
		}
	}

	@Override
	public void pause() {
		if (state == GameState.Running) {
			cm.write("pause");
//			state = GameState.Paused;
		}
		if (world.gameOver) {
			Settings.addScore(110);
			Settings.save(game.getFileIO());
		}
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}


	/**
	 * This method checks if a touchEvent lies in specified region
	 * 
	 * @param event
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	private boolean inBounds(TouchEvent event, int x, int y, int width,
			int height) {
		if (event.x > x && event.x < x + width - 1 && event.y > y
				&& event.y < y + height - 1) {
			return true;
		} else {
			return false;
		}
	}
}
