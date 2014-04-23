package com.badlogic.androidgames.mygame;

import java.util.List;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.Screen;

public class MultiPlayerScreen extends Screen {
	
	private boolean t2 = false;
	private boolean t3 = false;
	private boolean t4 = false;
	private boolean t5 = false;
	
	private Integer t = null;	

	public MultiPlayerScreen(Game game) {
		super(game);
	}

	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		Graphics g = game.getGraphics();
		
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
					if(t!=null)
						game.setScreen(new ServerScreen(game,t));
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
				}
				if (inBounds(event, 200, 0, 120, 48)) { // client button
					if(t!=null)
						game.setScreen(new ClientScreen(game,t));
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
				}
				
				// choose authentication method: T2, T3, T4 or T5
				if (inBounds(event, 0, g.getHeight()/2-40, 40, 40)) { // T2 button
					t2 = true; t3 = false; t4 = false; t5 = false;
					t = 2;
					if (Settings.soundEnabled) {
						Assets.click1.play(1);
					}
				}
				if (inBounds(event, 50, g.getHeight()/2-40, 40, 40)) { // T3 button
					t2 = false; t3 = true; t4 = false; t5 = false;
					t = 3;
					if (Settings.soundEnabled) {
						Assets.click1.play(1);
					}
				}
				if (inBounds(event, 100, g.getHeight()/2-40, 40, 40)) { // T4 button
					t2 = false; t3 = false; t4 = true; t5 = false;
					t = 4;
					if (Settings.soundEnabled) {
						Assets.click1.play(1);
					}
				}
				if (inBounds(event, 150, g.getHeight()/2-40, 40, 40)) { // T5 button
					t2 = false; t3 = false; t4 = false; t5 = true;
					t = 5;
					if (Settings.soundEnabled) {
						Assets.click1.play(1);
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
		
		// T2 button image
		if(t2) g.drawPixmap(Assets.T2b, 0, g.getHeight()/2-40, 0, 0, 40, 40);
		else g.drawPixmap(Assets.T2w, 0, g.getHeight()/2-40, 0, 0, 40, 40);
		
		// T3 button image
		if(t3) g.drawPixmap(Assets.T3b, 50, g.getHeight()/2-40, 0, 0, 40, 40);
		else g.drawPixmap(Assets.T3w, 50, g.getHeight()/2-40, 0, 0, 40, 40);
				
		// T4 button image
		if(t4) g.drawPixmap(Assets.T4b, 100, g.getHeight()/2-40, 0, 0, 40, 40);
		else g.drawPixmap(Assets.T4w, 100, g.getHeight()/2-40, 0, 0, 40, 40);
		
		// T5 button image
		if(t5) g.drawPixmap(Assets.T5b, 150, g.getHeight()/2-40, 0, 0, 40, 40);
		else g.drawPixmap(Assets.T5w, 150, g.getHeight()/2-40, 0, 0, 40, 40);
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
