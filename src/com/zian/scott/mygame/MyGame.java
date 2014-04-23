package com.zian.scott.mygame;

import com.zian.scott.framework.Screen;
import com.zian.scott.framework.impl.AndroidGame;

public class MyGame extends AndroidGame {
	public Screen getStartScreen() {
		return new LoadingScreen(this);
	}
}
