package com.jarkkovallius.fatbee;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

public class AudioManager implements Disposable {

	public final float MUSIC_VOLUME = 0.4f;
	public final float FLYING_VOLUME = 0.8f;
	public final float DEATH_VOLUME = 0.6f;
	public final float JUMP_VOLUME = 0.4f;

	Music music;
	Sound flying;
	Sound jump;
	Sound death;

	private static final AudioManager INSTANCE = new AudioManager();

	public static AudioManager getInstance() {
		return INSTANCE;
	}

	/**
	 * 
	 */
	private AudioManager() {
	};

	public void init() {
		flying = Assets.getInstance().getAssetManager()
				.get(Assets.SOUND_FLYING, Sound.class);

		death = Assets.getInstance().getAssetManager()
				.get(Assets.SOUND_DEATH, Sound.class);
		jump = Assets.getInstance().getAssetManager()
				.get(Assets.SOUND_JUMP, Sound.class);

		music = Assets.getInstance().getAssetManager()
				.get(Assets.MUSIC, Music.class);

	}

	public void playFlying() {
		flying.loop(FLYING_VOLUME);
	}

	public void stopFlying() {
		flying.stop();
	}

	public void playDeath() {
		death.play(DEATH_VOLUME);
	}

	public void playJump() {
		jump.play(JUMP_VOLUME);
	}

	public void playTheme() {
		if (!music.isPlaying()) {
			music.setVolume(MUSIC_VOLUME);
			music.setLooping(true);
			music.play();
		}
	}

	public void stopTheme() {
		music.stop();
	}

	@Override
	public void dispose() {
		stopTheme();
		stopFlying();
		music.dispose();
		jump.dispose();
		death.dispose();
		flying.dispose();

	}

}
