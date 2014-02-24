package com.jarkkovallius.fatbee;

import com.badlogic.gdx.InputAdapter;

public class InputHandler extends InputAdapter {

	GameScreen game;

	public InputHandler(GameScreen game) {
		this.game = game;
	}

	public boolean keyPressed = false;

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (!keyPressed) {
			keyPressed = true;
			game.handleButtonPress();
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		keyPressed = false;
		return super.touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public boolean keyDown(int keycode) {
		if (!keyPressed) {
			keyPressed = true;
			game.handleButtonPress();
		}
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		keyPressed = false;
		return super.keyUp(keycode);
	}

}
