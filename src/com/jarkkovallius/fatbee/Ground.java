package com.jarkkovallius.fatbee;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Ground implements Poolable {

	public TextureRegion region;
	public int y = 0;
	public int x;

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
