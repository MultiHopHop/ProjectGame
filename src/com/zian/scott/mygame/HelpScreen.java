package com.zian.scott.mygame;

import java.util.List;

import com.zian.scott.framework.Game;
import com.zian.scott.framework.Graphics;
import com.zian.scott.framework.Screen;
import com.zian.scott.framework.Input.TouchEvent;

public class HelpScreen extends Screen {
	
	private int page = 0; //int to store the current page number

	public HelpScreen(Game game) {
		super(game);
	}

	/*The update method processes the touch inputs of the user*/
	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		
		int len = touchEvents.size();
		for (int i=0; i<len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (event.x > 256 && event.y > 416) { //forward button
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
				if (inBounds(event, 0, 416, 120, 48)) { //backward button
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

	/* present draws the required graphics onto the game screen */
	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();
		g.drawPixmap(Assets.background, 0, 0);
		
		// draw page
		switch(page){
		case 0:
			g.drawPixmap(Assets.help0, 32, 20); //page 0
			break;
		case 1:
			g.drawPixmap(Assets.help1, 32, 20); //page 1
			break;
		case 2:
			g.drawPixmap(Assets.help2, 32, 20); //page 2
			break;
		case 3:
			g.drawPixmap(Assets.help3, 32, 20); //page 3
			break;
		case 4:
			g.drawPixmap(Assets.help4, 32, 20); //page 4
			break;
		case 5:
			g.drawPixmap(Assets.help5, 32, 20); //page 5
			break;
		}
		
		g.drawPixmap(Assets.buttons, 256, 416, 0, 64, 64, 64); // forward button image
		g.drawPixmap(Assets.buttons, 0, 416, 64, 64, 64, 64); // backward button image
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
