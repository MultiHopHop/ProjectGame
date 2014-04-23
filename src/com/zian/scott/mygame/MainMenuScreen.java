package com.zian.scott.mygame;

import java.util.List;

import com.zian.scott.framework.Game;
import com.zian.scott.framework.Graphics;
import com.zian.scott.framework.Screen;
import com.zian.scott.framework.Input.TouchEvent;

public class MainMenuScreen extends Screen {
	public MainMenuScreen(Game game) {
		super(game);
	}
	
	public void update(float deltaTime) {
		Graphics g = game.getGraphics();
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		
		int len = touchEvents.size();
		for (int i=0; i<len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (inBounds(event, 0, g.getHeight() - 64, 64, 64)) { //sound button
					Settings.soundEnabled = !Settings.soundEnabled;
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
				}
				if (inBounds(event, 64, 220, 192, 42)) {  // play button
					game.setScreen(new MultiPlayerScreen(game));
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
				}
				if (inBounds(event, 64, 220 + 42, 192, 42)) { // highscore button
					game.setScreen(new HighscoreScreen(game));
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
				}
				if (inBounds(event, 64, 220 + 84, 192, 42)) { //help button
					game.setScreen(new HelpScreen(game));
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
				}
				
				return;
			}
		}
	}
	
	public boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
		if (event.x > x && event.x < x + width - 1 &&
				event.y > y && event.y < y + height - 1) {
			return true;
		} 
		else {
			return false;
		}
	}
	
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();
		
		g.drawPixmap(Assets.background, 0, 0);
		g.drawPixmap(Assets.logo, 10, 20);
		g.drawPixmap(Assets.mainMenu, 64, 220); //set of main menu buttons image
		if (Settings.soundEnabled) {
			g.drawPixmap(Assets.buttons, 0,  416, 0, 0, 64, 64);
		}
		else {
			g.drawPixmap(Assets.buttons, 0, 416, 64, 0, 64, 64);
		}
	}
	
	public void pause() {
		Settings.save(game.getFileIO());
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	
}