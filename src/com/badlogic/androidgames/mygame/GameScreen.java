package com.badlogic.androidgames.mygame;

import java.util.List;
import java.util.Random;

import android.graphics.Color;
import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.Pixmap;
import com.badlogic.androidgames.framework.Screen;

public class GameScreen extends Screen {
	enum GameState {
		Ready,
		Running,
		Paused,
		GameOver
	}
	
	GameState state = GameState.Ready;
	World world;
	String score = "0";

	
	public GameScreen(Game game) {
		super(game);
		world = new World();
	}

	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		
		if(state == GameState.Ready)
            updateReady(touchEvents);
        if(state == GameState.Running)
            updateRunning(touchEvents, deltaTime);
        if(state == GameState.Paused)
            updatePaused(touchEvents);
        if(state == GameState.GameOver)
            updateGameOver(touchEvents);  		
	}

	private void updateReady(List<TouchEvent> touchEvents) {
		if (touchEvents.size() > 0) {
			state = GameState.Running;
		}
	}
	
	private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {
		int len = touchEvents.size();
		for (int i=0; i<len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (event.x < 64 && event.y < 64) {
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					state = GameState.Paused;
					return;
				}
			}
			if (event.type == TouchEvent.TOUCH_DOWN) {
				if (inBounds(event, 256, 416, 64, 64)) {
					world.players.get(0).moveRight();
				}
				if (inBounds(event, 192, 416, 64, 64)) {
					world.players.get(0).moveDown();
				}
				if (inBounds(event, 128, 416, 64, 64)) {
					world.players.get(0).moveLeft();
				}
				if (inBounds(event, 192, 352, 64, 64)) {
					world.players.get(0).moveUp();
				}
			}		
		}
		
		// for robot
		Random random = new Random();
		int botMove = random.nextInt(4);
		switch (botMove) {
		case 0:
			world.players.get(1).moveDown();
			break;
		case 1:
			world.players.get(1).moveLeft();
			break;
		case 2:
			world.players.get(1).moveRight();
			break;
		case 3:
			world.players.get(1).moveUp();
			break;
		}
		
		world.update(deltaTime);
		if (world.gameOver) {
			if (Settings.soundEnabled) {
				Assets.bitten.play(1);
			}
			state = GameState.GameOver;
		}
	}
	
	private void updatePaused(List<TouchEvent> touchEvents) {
		int len = touchEvents.size();
		for (int i=0; i<len; i++) {
			TouchEvent event = touchEvents.get(i);
			if(event.type == TouchEvent.TOUCH_UP) {
                if(event.x > 80 && event.x <= 240) {
                    if(event.y > 100 && event.y <= 148) {
                        if(Settings.soundEnabled)
                            Assets.click.play(1);
                        state = GameState.Running;
                        return;
                    }                    
                    if(event.y > 148 && event.y < 196) {
                        if(Settings.soundEnabled)
                            Assets.click.play(1);
                        game.setScreen(new MainMenuScreen(game)); 
                        return;
                    }
                }
            }
		}
	}
	
	private void updateGameOver(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_UP) {
                if(event.x >= 128 && event.x <= 192 &&
                   event.y >= 200 && event.y <= 264) {
                    if(Settings.soundEnabled)
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
		if(state == GameState.Ready) 
            drawReadyUI();
        if(state == GameState.Running)
            drawRunningUI();
        if(state == GameState.Paused)
            drawPausedUI();
        if(state == GameState.GameOver)
            drawGameOverUI();
        
		drawText(g, score, 64, g.getHeight() - 42);
	}

	private void drawWorld(World world) {
		Graphics g = game.getGraphics();
		List<Player> players = world.players;
		PowerUp powerUp = world.powerUp;
		int x, y;
		int numPlayer = world.numPlayer;
		
		for (int i=0; i<world.WORLD_WIDTH; i++) {
			for (int j=0; j<world.WORLD_HEIGHT; j++) {
				switch (world.board[i][j]) {			
				case 1:
					g.drawRect(i*32, j*32, 32, 32, Color.RED);
					break;
				case 2:
					g.drawRect(i*32, j*32, 32, 32, Color.GREEN);
					break;
				case 3:
					g.drawRect(i*32, j*32, 32, 32, Color.BLUE);
					break;
				case 4:
					g.drawRect(i*32, j*32, 32, 32, Color.YELLOW);
					break;
				default:
					g.drawRect(i*32, j*32, 32, 32, Color.GRAY);
				}
//				if (world.board[i][j] == 0) {
//					g.drawRect(i*32, j*32, 32, 32, Color.GRAY);
//				}
//				else {
//					g.drawPixmap(Assets.tail, i*32, j*32);
//				}
			}
		}
		
		
		Pixmap powerUpPixmap = null;
		if (powerUp != null) {
			if (powerUp.type == PowerUpType.SPEEDUP) {
				powerUpPixmap = Assets.stain1;
			}
			if (powerUp.type == PowerUpType.STUN) {
				powerUpPixmap = Assets.stain3;
			}
			Log.d("DrawWorldTest","powerup");
			x = powerUp.x * 32;
			y = powerUp.y * 32;
			g.drawPixmap(powerUpPixmap, x, y);
		}		
		
		
		for (Player player: players) {
			Pixmap headPixmap = Assets.tail;
//			if (player.direction == Player.UP) {
//				headPixmap = Assets.headUp;
//			}
//			if (player.direction == Player.LEFT) {
//				headPixmap = Assets.headLeft;
//			}
//			if (player.direction == Player.DOWN) {
//				headPixmap = Assets.headDown;
//			}
//			if (player.direction == Player.RIGHT) {
//				headPixmap = Assets.headRight;
//			}
			x = player.x * 32 + 16;
			y = player.y * 32 + 16;
			g.drawPixmap(headPixmap, x - headPixmap.getWidth()/2, y - headPixmap.getHeight()/2);
			StringBuilder builder = new StringBuilder();
			builder.append("x" + player.x);
			builder.append("y" + player.y);
			Log.d("DrawWorldTest", builder.toString());
		}
		
	}
	
	private void drawReadyUI() {
        Graphics g = game.getGraphics();
        
        g.drawPixmap(Assets.ready, 47, 100);
        g.drawLine(0, 320, 480, 320, Color.BLACK);
    }
    
    private void drawRunningUI() {
        Graphics g = game.getGraphics();

        for (int i=1; i<world.WORLD_WIDTH-1; i++) {
        	g.drawLine(i*32, 0, i*32, 320, Color.BLACK );
        }
        for (int j=1; j<world.WORLD_HEIGHT; j++) {
    		g.drawLine(0, j*32, 320, j*32, Color.BLACK);
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
			state = GameState.Paused;
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

	private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
		if (event.x > x && event.x < x + width - 1 &&
				event.y > y && event.y < y + height - 1) {
			return true;
		} 
		else {
			return false;
		}
	}
}
