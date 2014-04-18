package com.badlogic.androidgames.mygame;

import java.util.List;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.Screen;

public class MultiPlayerScreen extends Screen {

	public MultiPlayerScreen(Game game) {
		super(game);
	}

	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		
		int len = touchEvents.size();
		for (int i=0; i<len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (inBounds(event, 200, 416, 120, 48)) { // back button
					game.setScreen(new MainMenuScreen(game));
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					return;
				}
				
				if (inBounds(event, 0, 0, 120, 48)) { // server button
					game.setScreen(new ServerScreen(game));
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
				}
				if (inBounds(event, 200, 0, 120, 48)) { // client button
					game.setScreen(new ClientScreen(game));
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
				}
			}
		}
	}

	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();
		g.drawPixmap(Assets.background, 0, 0);
		g.drawPixmap(Assets.serverclient, 0, 0, 0, 0, 120, 48); // Server button image
		g.drawPixmap(Assets.serverclient, 200, 0, 0, 48, 120, 48); // Client button image
		g.drawPixmap(Assets.serverclient, 200, 416, 0, 96, 120, 48); // Back button image
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
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

}
