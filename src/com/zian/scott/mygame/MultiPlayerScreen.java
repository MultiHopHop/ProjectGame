package com.zian.scott.mygame;

import java.util.List;

import com.zian.scott.framework.Game;
import com.zian.scott.framework.Graphics;
import com.zian.scott.framework.Screen;
import com.zian.scott.framework.Input.TouchEvent;

public class MultiPlayerScreen extends Screen {
	
	// booleans of each authentication t, if true it is selected, if false it is not
	private boolean t2 = true;
	private boolean t3 = false;
	private boolean t4 = false;
	private boolean t5 = false;
	
	private Integer t = 2;	// Integer of the authentication that was selected

	public MultiPlayerScreen(Game game) {
		super(game);
	}

	/*The update method processes the touch inputs of the user*/
	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		Graphics g = game.getGraphics();
		
		int len = touchEvents.size();
		for (int i=0; i<len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (inBounds(event, 0, 416, 120, 48)) { // back button
					game.setScreen(new MainMenuScreen(game));
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
					return;
				}
				
				if (inBounds(event, g.getWidth()/2-150, g.getHeight()/2-150, 120, 48)) { // server button
					if(t!=null)
						game.setScreen(new ServerScreen(game,t));
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
				}
				if (inBounds(event, g.getWidth()/2+30, g.getHeight()/2-150, 120, 48)) { // client button
					if(t!=null)
						game.setScreen(new ClientScreen(game,t));
					if (Settings.soundEnabled) {
						Assets.click.play(1);
					}
				}
				
				// choose authentication method: T2, T3, T4 or T5
				if (inBounds(event, g.getWidth()/2-95, g.getHeight()/2, 40, 40)) { // T2 button
					t2 = true; t3 = false; t4 = false; t5 = false;
					t = 2;
					if (Settings.soundEnabled) {
						Assets.click1.play(1);
					}
				}
				if (inBounds(event, g.getWidth()/2-45, g.getHeight()/2, 40, 40)) { // T3 button
					t2 = false; t3 = true; t4 = false; t5 = false;
					t = 3;
					if (Settings.soundEnabled) {
						Assets.click1.play(1);
					}
				}
				if (inBounds(event, g.getWidth()/2+5, g.getHeight()/2, 40, 40)) { // T4 button
					t2 = false; t3 = false; t4 = true; t5 = false;
					t = 4;
					if (Settings.soundEnabled) {
						Assets.click1.play(1);
					}
				}
				if (inBounds(event, g.getWidth()/2+55, g.getHeight()/2, 40, 40)) { // T5 button
					t2 = false; t3 = false; t4 = false; t5 = true;
					t = 5;
					if (Settings.soundEnabled) {
						Assets.click1.play(1);
					}
				}
			}
		}
	}

	/* present draws the required graphics onto the game screen */
	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();
		g.drawPixmap(Assets.background, 0, 0);
		g.drawPixmap(Assets.mochi, g.getWidth()/2-150+60-16, g.getHeight()/2-150-32, 0, 0, 32, 32); // Mochi image
		g.drawPixmap(Assets.serverclient, g.getWidth()/2-150, g.getHeight()/2-150, 0, 0, 120, 48); // Mochi Text image
		g.drawPixmap(Assets.bird, g.getWidth()/2+30+60-16, g.getHeight()/2-150-32, 0, 0, 32, 32); // Bird image
		g.drawPixmap(Assets.serverclient, g.getWidth()/2+30, g.getHeight()/2-150, 0, 48, 120, 48); // Bird Text image
		g.drawPixmap(Assets.serverclient, 0, 416, 0, 96, 120, 48); // Back button image
		
		// T2 button image
		if(t2) g.drawPixmap(Assets.T2b, g.getWidth()/2-95, g.getHeight()/2, 0, 0, 40, 40);
		else g.drawPixmap(Assets.T2w, g.getWidth()/2-95, g.getHeight()/2, 0, 0, 40, 40);
		
		// T3 button image
		if(t3) g.drawPixmap(Assets.T3b, g.getWidth()/2-45, g.getHeight()/2, 0, 0, 40, 40);
		else g.drawPixmap(Assets.T3w, g.getWidth()/2-45, g.getHeight()/2, 0, 0, 40, 40);
				
		// T4 button image
		if(t4) g.drawPixmap(Assets.T4b, g.getWidth()/2+5, g.getHeight()/2, 0, 0, 40, 40);
		else g.drawPixmap(Assets.T4w, g.getWidth()/2+5, g.getHeight()/2, 0, 0, 40, 40);
		
		// T5 button image
		if(t5) g.drawPixmap(Assets.T5b, g.getWidth()/2+55, g.getHeight()/2, 0, 0, 40, 40);
		else g.drawPixmap(Assets.T5w, g.getWidth()/2+55, g.getHeight()/2, 0, 0, 40, 40);
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
	
	/*returns true if the TouchEvent occurs within the speified box described by x,y,width and height
	  else returns false*/
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
