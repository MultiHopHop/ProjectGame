package com.badlogic.androidgames.mygame;

import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.AndroidGame;

public class MyGame extends AndroidGame {
	public Screen getStartScreen() {
		return new LoadingScreen(this);
	}
}
