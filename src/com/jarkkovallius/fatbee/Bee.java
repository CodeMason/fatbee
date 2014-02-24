package com.jarkkovallius.fatbee;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Bee {

	public Animation flyingAnimation;
	public TextureRegion[] flyingKeyFrames;
	public TextureRegion dead;

	public enum State {
		STAND_BY, FLYING, ON_GROUND, DEAD
	}

	public State state;

	public float x;
	public float y;
	public float width;
	public float height;
	public float velocityY;
	public float velocityX;
	public float terminalVelocityY;

	public Rectangle bounds;

	public void update(float delta) {

		if (state != State.STAND_BY) {
			velocityY = MathUtils.clamp(velocityY, -terminalVelocityY,
					terminalVelocityY);
			y += velocityY * delta;
		}

		x += velocityX * delta;
		bounds.setCenter(x, y);
	}
}
