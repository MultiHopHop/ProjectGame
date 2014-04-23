package com.badlogic.androidgames.mygame;

import java.util.List;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.Screen;

public class HelpScreen extends Screen {
	
	private int page = 0;

	public HelpScreen(Game game) {
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
				if (event.x > 256 && event.y > 416) {
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					if(page < 5){
						page++;
					} else{
						page = 0;
						game.setScreen(new MainMenuScreen(game));
						return;
					}
				}
				if (inBounds(event, 0, 416, 120, 48)) {
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					if(page > 0){
						page--;
					} else{
						game.setScreen(new MainMenuScreen(game));
						return;
					}
				}
			}
		}
	}

	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();
		g.drawPixmap(Assets.background, 0, 0);
		
		// draw page
		switch(page){
		case 0:
			g.drawPixmap(Assets.help0, 32, 20);
			break;
		case 1:
			g.drawPixmap(Assets.help1, 32, 20);
			break;
		case 2:
			g.drawPixmap(Assets.help2, 32, 20);
			break;
		case 3:
			g.drawPixmap(Assets.help3, 32, 20);
			break;
		case 4:
			g.drawPixmap(Assets.help4, 32, 20);
			break;
		case 5:
			g.drawPixmap(Assets.help5, 32, 20);
			break;
		}
		
		g.drawPixmap(Assets.buttons, 256, 416, 0, 64, 64, 64); // forward button
		g.drawPixmap(Assets.buttons, 0, 416, 64, 64, 64, 64); // backward button
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
