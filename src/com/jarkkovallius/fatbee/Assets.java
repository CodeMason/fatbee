package com.jarkkovallius.fatbee;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable {

	public static final String BEE = "bee";
	public static final String BEE2 = "bee2";
	public static final String BEE_DEAD = "bee_dead";
	public static final String PLANT = "plant";
	public static final String CLOUD = "cloud";
	public static final String GROUND = "ground";
	public static final String MOON = "moon";
	public static final String LOGO = "logo";
	public static final String WHITE_PIXEL = "whitepixel";

	public static final String FONT_8PX = "fonts/8px.fnt";
	public static final String FONT_24PX = "fonts/24px.fnt";

	public static final String SOUND_FLYING = "sounds/flying.ogg";
	public static final String SOUND_JUMP = "sounds/jump.ogg";
	public static final String SOUND_DEATH = "sounds/death.ogg";

	public static final String MUSIC = "sounds/music.ogg";

	private static final Assets INSTANCE = new Assets();

	public final static Assets getInstance() {
		return INSTANCE;
	}

	private AssetManager assetManager;

	public AssetManager getAssetManager() {
		return assetManager;
	}

	private Assets() {
	}

	public void init() {
		assetManager = new AssetManager();
		assetManager.load("data/fatbee.pack", TextureAtlas.class);
		assetManager.load(FONT_8PX, BitmapFont.class);
		assetManager.load(FONT_24PX, BitmapFont.class);
		assetManager.load(SOUND_DEATH, Sound.class);
		assetManager.load(SOUND_FLYING, Sound.class);
		assetManager.load(SOUND_JUMP, Sound.class);
		assetManager.load(MUSIC, Music.class);

		assetManager.finishLoading();
		atlas = assetManager.get("data/fatbee.pack", TextureAtlas.class);
	}

	@Override
	public void dispose() {
		atlas.dispose();
		assetManager.dispose();
	}

	public TextureAtlas atlas;

}
