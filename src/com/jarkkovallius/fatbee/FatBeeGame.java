package com.jarkkovallius.fatbee;

import com.badlogic.gdx.Game;

public class FatBeeGame extends Game {

	GameScreen gameScreen;

	@Override
	public void dispose() {
		gameScreen.dispose();
		AudioManager.getInstance().dispose();
		Assets.getInstance().dispose();
		super.dispose();
	}

	@Override
	public void create() {
		Assets.getInstance().init();
		AudioManager.getInstance().init();
		gameScreen = new GameScreen();
		this.setScreen(gameScreen);
	}

}
