package com.jarkkovallius.fatbee;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class GameScreen implements Screen {

	public Stage stageMenu;
	public Stage stageGameOver;

	private int score = 0;
	private int hiScore = 0;

	public final static boolean DEBUG_LINES = false;

	public final static int VIRTUAL_WIDTH = 200; // 240
	public final static int VIRTUAL_HEIGHT = 320; // 384

	public enum State {
		STAND_BY, RUNNING, GAMEOVER
	}

	public State state = State.STAND_BY;

	float OBSTACLE_HEIGHT = 80;
	float OBSTACLE_WIDTH = 0;
	float OBSTACLE_INTERVAL = 160;
	float SPEED = 180;
	float OBSTACLE_MIN_Y_DISTANCE = 50;

	float BEE_X_POS = 72;
	float BEE_Y_POS = 230;

	float BEE_JUMP_FORCE = 300;
	float BEE_MAX_VELOCITY = 300;
	float GRAVITY = -1000f;
	float GRAVITY_DEAD = -2000f;

	private int GROUND_WIDTH;
	int groundX = 0;
	public float obstacleXSpawnPosition = 0;
	WorldRenderer worldRenderer;
	InputHandler inputHandler;
	public OrthographicCamera camera;

	private BitmapFont font8px;
	private BitmapFont font24px;
	private TextureRegion logo;
	private Color colorYellow = new Color(224 / 255f, 204 / 255f, 27 / 255f, 1f);
	private Color colorWhite = new Color(227 / 255f, 227 / 255f, 227 / 255f, 1f);
	private SimpleTimer blinkTimer;
	private SimpleTimer coolDownTimer;

	public Bee bee;

	public Array<Ground> grounds = new Array<Ground>();
	public Pool<Ground> poolGrounds = new Pool<Ground>() {
		@Override
		protected Ground newObject() {
			Ground g = new Ground();
			g.region = Assets.getInstance().atlas.findRegion(Assets.GROUND);
			return g;
		}

		@Override
		public Ground obtain() {
			Ground g = super.obtain();
			g.y = 0;
			g.x = groundX;
			return g;
		}
	};

	public Array<Obstacle> obstacles = new Array<Obstacle>();
	public Pool<Obstacle> poolObstacles = new Pool<Obstacle>() {
		@Override
		protected Obstacle newObject() {
			Obstacle o = new Obstacle();
			o.region = Assets.getInstance().atlas.findRegion(Assets.PLANT);
			return o;
		}

		@Override
		public Obstacle obtain() {
			Obstacle o = super.obtain();
			initObstacle(o);
			return o;
		}
	};

	private void initObstacle(Obstacle o) {
		o.init(obstacleXSpawnPosition,
				MathUtils.random(OBSTACLE_MIN_Y_DISTANCE, VIRTUAL_HEIGHT
						- OBSTACLE_HEIGHT - OBSTACLE_MIN_Y_DISTANCE));
		if (o.down == null) {
			o.down = new Rectangle(o.x, 0, o.region.getRegionWidth(), o.y);
		} else {
			o.down.set(o.x, 0, o.region.getRegionWidth(), o.y);
		}

		if (o.up == null) {
			o.up = new Rectangle(o.x, o.y + OBSTACLE_HEIGHT,
					o.region.getRegionWidth(), VIRTUAL_HEIGHT - o.y);
		} else {
			o.up.set(o.x, o.y + OBSTACLE_HEIGHT, o.region.getRegionWidth(),
					VIRTUAL_HEIGHT - o.y);
		}
		o.passed = false;
	}

	@Override
	public void show() {
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		camera.viewportWidth = (VIRTUAL_HEIGHT / (float) Gdx.graphics
				.getHeight()) * Gdx.graphics.getWidth();

		worldRenderer = new WorldRenderer(this);
		inputHandler = new InputHandler(this);
		Gdx.input.setInputProcessor(inputHandler);

		font8px = Assets.getInstance().getAssetManager()
				.get(Assets.FONT_8PX, BitmapFont.class);
		font24px = Assets.getInstance().getAssetManager()
				.get(Assets.FONT_24PX, BitmapFont.class);
		logo = Assets.getInstance().atlas.findRegion(Assets.LOGO);

		buildMenuStage();
		buildGameOverStage();

		buildBee();

		OBSTACLE_WIDTH = (Assets.getInstance().atlas.findRegion(Assets.PLANT))
				.getRegionWidth();
		GROUND_WIDTH = Assets.getInstance().atlas.findRegion(Assets.GROUND)
				.getRegionWidth();

		initGrounds();

		blinkTimer = new SimpleTimer(500);
		blinkTimer.start();

		coolDownTimer = new SimpleTimer(1500);
		coolDownTimer.start();

		hiScore = getHighScore();

		initMenuScreen();

		AudioManager.getInstance().playTheme();
	}

	Label labelMenuTapToPlay;
	Label labelMenuHiScore;
	Label labelGameOverHiScore;
	Label labelGameOverScore;
	private final String PREFERENCES_FILE_NAME = "com.creampixel.fatbee.Preferences";
	private final String PREFERENCES_HIGH_SCORE = "highscore";
	private boolean saveHighScore = false;
	private Label labelGameOverTapTo;

	private void buildMenuStage() {
		stageMenu = new Stage();

		Table t = new Table();
		t.setFillParent(true);

		Table header = new Table();
		header.add(new Image(logo)).expand();
		header.add(UIFactory.constructLabel("1.0", font8px)).left().bottom()
				.padLeft(2).padBottom(-2);
		header.row();
		header.add(UIFactory.constructLabel("by Jarkko Vallius", font8px))
				.colspan(2).left();
		// header.debug();
		t.add(header).right().top().padTop(80).padRight(5);
		t.row();
		Label labelInfo = UIFactory
				.constructLabel(
						"Made for\nFlappyJam 2014\n\nMusic\nLadybug Castle\nby Rolemusic\n\nHail to dongatory",
						font8px, colorWhite);
		labelInfo.setAlignment(Align.center);
		t.add(labelInfo).expand().top().padTop(20);
		t.row();
		labelMenuTapToPlay = UIFactory.constructLabel("TAP TO FLY!", font24px,
				colorYellow);
		t.add(labelMenuTapToPlay).padBottom(5);
		t.row();
		labelMenuHiScore = UIFactory.constructLabel("Hi-Score 0", font8px,
				Color.WHITE);
		t.add(labelMenuHiScore).padBottom(10);
		t.debug();
		stageMenu.addActor(t);

	}

	private void buildGameOverStage() {
		stageGameOver = new Stage();
		Table t = new Table();
		t.setFillParent(true);

		t.add(UIFactory.constructLabel("GAME OVER", font24px, colorYellow));
		t.row();

		labelGameOverScore = UIFactory.constructLabel("", font8px, colorWhite);
		t.add(labelGameOverScore);
		t.row();

		labelGameOverHiScore = UIFactory
				.constructLabel("", font8px, colorWhite);
		t.add(labelGameOverHiScore);
		t.row();
		labelGameOverTapTo = UIFactory.constructLabel("TAP IT!", font24px,
				colorYellow);
		t.add(labelGameOverTapTo).bottom().padBottom(5);

		stageGameOver.addActor(t);
	}

	private void initGameOverScreen(boolean newHighScore) {
		labelGameOverScore.setText("Score " + score);

		if (!newHighScore) {
			labelGameOverHiScore.getStyle().font.setColor(colorWhite);
			labelGameOverHiScore.setText("Hi-Score " + hiScore);
		} else {
			LabelStyle style = new LabelStyle(labelGameOverHiScore.getStyle());
			style.fontColor.set(colorYellow);
			labelGameOverHiScore.setStyle(style);
			labelGameOverHiScore.setText("NEW HIGH SCORE!");
		}

		labelGameOverTapTo.setVisible(false);
	}

	private void initMenuScreen() {

		if (hiScore > 0) {
			labelMenuHiScore.setText("Hi-Score " + hiScore);
			labelMenuHiScore.setVisible(true);
		} else {
			labelMenuHiScore.setVisible(false);
		}
	}

	@Override
	public void render(float delta) {

		update(delta);
		worldRenderer.render(delta);

		if (state == State.STAND_BY) {
			stageMenu.act(delta);
			stageMenu.draw();
		} else if (state == State.GAMEOVER) {
			stageGameOver.act(delta);
			stageGameOver.draw();
		}

		// Table.drawDebug(stage);

	}

	public void reset() {
		if (bee.state == Bee.State.DEAD) {

			bee.velocityX = SPEED;

			bee.state = Bee.State.STAND_BY;
			bee.y = BEE_Y_POS;
			state = State.STAND_BY;
			bee.bounds.setCenter(bee.x, bee.y);
			bee.x = BEE_X_POS;

			camera.position.x = bee.x;
			obstacleXSpawnPosition = camera.position.x + camera.viewportWidth;
			obstacles.clear();

			labelGameOverTapTo.setVisible(false);

			initGrounds();
			score = 0;

		}
	}

	private void update(float delta) {

		if (blinkTimer.timeExpired()) {
			labelMenuTapToPlay.setVisible(!labelMenuTapToPlay.isVisible());

			if (coolDownTimer.timeExpired()) {
				labelGameOverTapTo.setVisible(!labelGameOverTapTo.isVisible());
			} else {
				labelGameOverTapTo.setVisible(false);
			}

			blinkTimer.reset();
		}

		if (state == State.RUNNING) {
			for (int i = 0; i < obstacles.size; i++) {
				Obstacle o = obstacles.get(i);

				// set bee to dead if collide with obstacle
				if (o.up.overlaps(bee.bounds) || o.down.overlaps(bee.bounds)) {
					setBeeToDeath();
				}

				// obstacle passed, +1 to score
				if (bee.state != Bee.State.DEAD) {
					if (!o.passed) {
						if (bee.x > o.down.x + o.down.width) {
							o.passed = true;
							score += 1;
							System.out.println("SCORE " + score);
						}
					}
				}

				// free obstacle to pool
				if (o.x < camera.position.x - camera.viewportWidth) {
					poolObstacles.free(obstacles.removeIndex(i));
				}
			}

			// create new obstacle
			if (camera.position.x + camera.viewportWidth / 2 >= obstacleXSpawnPosition) {
				obstacleXSpawnPosition += OBSTACLE_INTERVAL + OBSTACLE_WIDTH;
				obstacles.add(poolObstacles.obtain());
			}

		}

		// grounds
		if (camera.position.x + camera.viewportWidth / 2 >= groundX) {
			grounds.add(poolGrounds.obtain());
			groundX += GROUND_WIDTH;
		}

		if (state != State.STAND_BY) {
			if (bee.state == Bee.State.DEAD) {
				bee.velocityY += GRAVITY_DEAD * delta;
			} else {
				bee.velocityY += GRAVITY * delta;
			}

		}

		if (bee.state == Bee.State.DEAD) {

			if (state == State.RUNNING) {

				if (score > hiScore) {
					hiScore = score;
					saveHighScore = true;
					initGameOverScreen(true);
					initMenuScreen();
				} else {
					initGameOverScreen(false);
				}

				state = State.GAMEOVER;

			}

		}

		if (state == State.RUNNING) {
			if (bee.y < -10 || bee.y > VIRTUAL_HEIGHT + 50) {

				setBeeToDeath();
			}
		}

		bee.update(delta);

		camera.position.x = bee.x + BEE_X_POS;
		camera.update();

	}

	private void setBeeToDeath() {
		bee.state = Bee.State.DEAD;
		bee.velocityY = BEE_JUMP_FORCE * 2;
		AudioManager.getInstance().playDeath();
		AudioManager.getInstance().stopFlying();
		coolDownTimer.reset();
	}

	public void handleButtonPress() {

		if (state == State.STAND_BY) {
			state = State.RUNNING;
			bee.state = Bee.State.FLYING;
			obstacleXSpawnPosition = camera.position.x + camera.viewportWidth;

			bee.velocityY = BEE_JUMP_FORCE;

			AudioManager.getInstance().playFlying();

		}

		if (state == State.RUNNING) {
			bee.velocityY = BEE_JUMP_FORCE;
			AudioManager.getInstance().playJump();
		}

		if (state == State.GAMEOVER) {

			if (coolDownTimer.timeExpired()) {
				if (saveHighScore) {

					setHighScore(hiScore);
					saveHighScore = false;
				}
				reset();
				state = State.STAND_BY;
			}

		}

	}

	@Override
	public void resize(int width, int height) {

		camera.viewportWidth = (VIRTUAL_HEIGHT / (float) height) * width;

		camera.position.x = camera.viewportWidth / 2;
		camera.position.y = camera.viewportHeight / 2;
		camera.update();

		obstacleXSpawnPosition = camera.position.x + camera.viewportWidth;

		if (obstacles == null) {
			obstacles = new Array<Obstacle>();
		}

		stageMenu.setViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, true);
		stageGameOver.setViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, true);

	}

	@Override
	public void hide() {
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

	private void buildBee() {
		bee = new Bee();
		bee.x = BEE_X_POS;
		bee.y = BEE_Y_POS;
		bee.velocityX = SPEED;

		bee.flyingKeyFrames = new TextureRegion[2];
		bee.flyingKeyFrames[0] = Assets.getInstance().atlas
				.findRegion(Assets.BEE);
		bee.flyingKeyFrames[1] = Assets.getInstance().atlas
				.findRegion(Assets.BEE2);
		bee.flyingAnimation = new Animation(0.05f, bee.flyingKeyFrames);

		bee.dead = Assets.getInstance().atlas.findRegion(Assets.BEE_DEAD);
		bee.dead.flip(false, true);

		bee.bounds = new Rectangle(0, 0, 28, 25);
		bee.bounds.setCenter(bee.x, bee.y);
		bee.state = Bee.State.FLYING;
		bee.velocityY = 0;
		bee.terminalVelocityY = BEE_MAX_VELOCITY;
	}

	private void initGrounds() {
		grounds.clear();
		groundX = (int) (camera.position.x - camera.viewportWidth);

		for (int i = 0; i < 2; i++) {
			grounds.add(poolGrounds.obtain());
			groundX += GROUND_WIDTH;
		}
	}

	public int getHighScore() {
		Preferences pref = Gdx.app.getPreferences(this.PREFERENCES_FILE_NAME);
		int hiScore = pref.getInteger(PREFERENCES_HIGH_SCORE, 0);
		return hiScore;
	}

	public void setHighScore(int highScore) {
		Preferences pref = Gdx.app.getPreferences(this.PREFERENCES_FILE_NAME);
		pref.putInteger(PREFERENCES_HIGH_SCORE, highScore);
		pref.flush();
	}

}
