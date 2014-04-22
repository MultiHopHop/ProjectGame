package com.badlogic.androidgames.mygame;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Graphics.PixmapFormat;
import com.badlogic.androidgames.framework.Screen;

public class LoadingScreen extends Screen {
	public LoadingScreen(Game game) {
		super(game);
	}
	
	public void update(float deltaTime) {
		Graphics g = game.getGraphics();
		Assets.background = g.newPixmap("background.png", PixmapFormat.RGB565);
	    Assets.logo = g.newPixmap("logo.png", PixmapFormat.ARGB4444);
	    Assets.mainMenu = g.newPixmap("mainmenu.png", PixmapFormat.ARGB4444);
	    Assets.buttons = g.newPixmap("buttons.png", PixmapFormat.ARGB4444);
	    Assets.help1 = g.newPixmap("help1.png", PixmapFormat.ARGB4444);
	    Assets.help2 = g.newPixmap("help2.png", PixmapFormat.ARGB4444);
	    Assets.help3 = g.newPixmap("help3.png", PixmapFormat.ARGB4444);
	    Assets.numbers = g.newPixmap("numbers.png", PixmapFormat.ARGB4444);
	    Assets.ready = g.newPixmap("ready.png", PixmapFormat.ARGB4444);
	    Assets.pause = g.newPixmap("pausemenu.png", PixmapFormat.ARGB4444);
	    Assets.gameOver = g.newPixmap("gameover.png", PixmapFormat.ARGB4444);
	    Assets.headUp = g.newPixmap("headup.png", PixmapFormat.ARGB4444);
	    Assets.headLeft = g.newPixmap("headleft.png", PixmapFormat.ARGB4444);
	    Assets.headDown = g.newPixmap("headdown.png", PixmapFormat.ARGB4444);
	    Assets.headRight = g.newPixmap("headright.png", PixmapFormat.ARGB4444);
	    Assets.tail = g.newPixmap("tail.png", PixmapFormat.ARGB4444);
	    Assets.T2w = g.newPixmap("t2white.png", PixmapFormat.ARGB4444);
	    Assets.T3w = g.newPixmap("t3white.png", PixmapFormat.ARGB4444);
	    Assets.T4w = g.newPixmap("t4white.png", PixmapFormat.ARGB4444);
	    Assets.T5w = g.newPixmap("t5white.png", PixmapFormat.ARGB4444);
	    Assets.T2b = g.newPixmap("t2black.png", PixmapFormat.ARGB4444);
	    Assets.T3b = g.newPixmap("t3black.png", PixmapFormat.ARGB4444);
	    Assets.T4b = g.newPixmap("t4black.png", PixmapFormat.ARGB4444);
	    Assets.T5b = g.newPixmap("t5black.png", PixmapFormat.ARGB4444);
	    Assets.stun = g.newPixmap("stun.png", PixmapFormat.ARGB4444);
	    Assets.bomb = g.newPixmap("bomb.png", PixmapFormat.ARGB4444);
	    Assets.speedup = g.newPixmap("speedup.png", PixmapFormat.ARGB4444);
	    Assets.stunned = g.newPixmap("stunned.png", PixmapFormat.ARGB4444);
	    Assets.serverclient = g.newPixmap("serverclient.png", PixmapFormat.ARGB4444);
	    Assets.server = g.newPixmap("server.png", PixmapFormat.ARGB4444);
	    Assets.client = g.newPixmap("client.png", PixmapFormat.ARGB4444);
	    Assets.numberpad = g.newPixmap("numberpad.png", PixmapFormat.ARGB4444);
	    Assets.click = game.getAudio().newSound("click.ogg");
	    Assets.eat = game.getAudio().newSound("eat.ogg");
	    Assets.bitten = game.getAudio().newSound("bitten.ogg");
	    Settings.load(game.getFileIO());
	    game.setScreen(new MainMenuScreen(game));
	}

	@Override
	public void present(float deltaTime) {
		// TODO Auto-generated method stub
		
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
	
    
}
