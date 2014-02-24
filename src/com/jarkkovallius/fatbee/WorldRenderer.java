package com.jarkkovallius.fatbee;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class WorldRenderer {

	ShapeRenderer renderer;
	GameScreen game;
	SpriteBatch batch;

	TextureRegion moon;
	Array<Cloud> clouds;
	Array<Star> stars;

	public WorldRenderer(GameScreen screen) {
		this.game = screen;
		renderer = new ShapeRenderer();
		batch = new SpriteBatch();

		moon = Assets.getInstance().atlas.findRegion(Assets.MOON);

		stars = new Array<Star>();
		buildStars();

		clouds = new Array<Cloud>();
		Cloud c = constructCloud(100, 60);
		c.speed = 5;
		clouds.add(c);

	}

	private void buildStars() {
		int maxTwinkleStars = 20;
		int twinkleCount = 0;
		for (int y = 0; y < game.camera.viewportHeight; y += 5) {
			int xPos = MathUtils.random(0, (int) game.camera.viewportWidth);

			Star s = new Star();
			s.x = xPos;
			s.y = y;
			s.region = Assets.getInstance().atlas
					.findRegion(Assets.WHITE_PIXEL);
			s.alpha = MathUtils.random();

			// randomize twinkle
			if (MathUtils.randomBoolean() && twinkleCount <= maxTwinkleStars) {
				s.twinkle = true;
				twinkleCount++;
			}

			stars.add(s);

		}

	}

	/*
	 * private static int[] randomizeStarsXPositions(int amount, int minX, int
	 * maxX) { int[] positions = new int[amount] ;
	 * java.util.Arrays.fill(positions, -1); int ran = -1 ; boolean duplicate =
	 * true ; for (int i=0; i<amount; i++) { duplicate = true ; while
	 * (duplicate) { ran = MathUtils.random(minX, maxX); duplicate = false ; for
	 * (int j=0; j<positions.length; j++) { if (positions[j] == ran) { duplicate
	 * = true ; break ; } } } positions[i] = ran ; }
	 * java.util.Arrays.sort(positions); return positions ; }
	 */

	private Cloud constructCloud(float xOffset, float yOffset) {
		Cloud c = new Cloud();
		c.region = Assets.getInstance().atlas.findRegion(Assets.CLOUD);
		c.x = game.camera.viewportWidth / 2 - xOffset;
		c.y = game.camera.viewportHeight / 2 - yOffset;
		return c;
	}

	public void render(float delta) {
		Gdx.gl20.glClearColor(6 / 255f, 33 / 255f, 95 / 255f, 1);
		Gdx.gl20.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.enableBlending();
		renderer.setProjectionMatrix(game.camera.combined);
		batch.setProjectionMatrix(game.camera.combined);

		drawStars();
		drawMoon();

		drawClouds(delta);
		drawObstacles();

		drawGround();
		drawBee();

	}

	private void drawStars() {
		batch.begin();

		for (Star s : stars) {
			if (s.twinkle) {
				batch.setColor(1, 1, 1, MathUtils.random());
			} else {
				batch.setColor(1, 1, 1, s.alpha);
			}

			batch.draw(s.region, game.camera.position.x
					- game.camera.viewportWidth / 2 + s.x,
					game.camera.position.y - game.camera.viewportHeight / 2
							+ s.y);
		}

		batch.end();
		batch.setColor(1, 1, 1, 1);
	}

	private void drawClouds(float delta) {

		batch.begin();

		for (Cloud c : clouds) {

			if (c.x > -(game.camera.viewportWidth / 2 + c.region
					.getRegionWidth())) {
				c.x -= c.speed * delta;
				batch.draw(c.region, game.camera.position.x + c.x,
						game.camera.position.y + c.y);
			} else {
				c.x = game.camera.viewportWidth / 2;
			}
		}

		batch.end();

	}

	private void drawMoon() {
		batch.begin();
		batch.draw(moon, game.camera.position.x + 40,
				game.camera.position.y + 90);
		batch.end();
	}

	private void drawObstacles() {

		for (Obstacle o : game.obstacles) {

			batch.begin();

			if (o.region.isFlipY()) {
				o.region.flip(false, true);
			}

			// LOWER
			batch.draw(o.region, o.x, o.y - o.region.getRegionHeight());

			o.region.flip(false, true);

			// UPPER
			batch.draw(o.region, o.x, o.y + game.OBSTACLE_HEIGHT);

			batch.end();

			if (GameScreen.DEBUG_LINES) {
				// DEBUG LINES
				renderer.begin(ShapeType.Filled);
				renderer.setColor(Color.RED);
				if (game.bee.bounds.overlaps(o.up)) {
					renderer.rect(o.up.x, o.up.y, o.up.width, o.up.height);
				}

				if (game.bee.bounds.overlaps(o.down)) {
					renderer.rect(o.down.x, o.down.y, o.down.width,
							o.down.height);
				}

				renderer.end();
			}

		}

	}

	float stateTime = 0;

	float rotation = 0;

	private void drawBee() {
		stateTime += Gdx.graphics.getDeltaTime();
		batch.begin();

		if (game.bee.state != Bee.State.DEAD) {
			if (game.bee.velocityY > 0) {
				rotation = 5f;
			} else {
				rotation = -5f;
			}

			TextureRegion reg = game.bee.flyingAnimation.getKeyFrame(stateTime,
					true);

			batch.draw(reg, game.bee.x - 20, game.bee.y - 15, 20, 20,
					reg.getRegionWidth(), reg.getRegionHeight(), 1.0f, 1.0f,
					rotation);
		} else {

			batch.draw(game.bee.dead,
					game.bee.x - game.bee.dead.getRegionWidth() / 2, game.bee.y
							- game.bee.dead.getRegionHeight() / 2);
		}

		batch.end();

	}

	private void drawGround() {
		batch.begin();

		for (Ground g : game.grounds) {
			batch.draw(g.region, g.x, g.y);
		}

		batch.end();
	}
}
