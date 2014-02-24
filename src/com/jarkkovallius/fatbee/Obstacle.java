package com.jarkkovallius.fatbee;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Obstacle implements Poolable {

	public float x;
	public float y;
	public boolean passed = false;

	public float speed;

	@Override
	public void reset() {

	}

	public void init(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Rectangle up;
	public Rectangle down;
	public TextureRegion region;

}
