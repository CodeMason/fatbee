package com.jarkkovallius.fatbee;

import com.badlogic.gdx.utils.TimeUtils;

public class SimpleTimer {
	private long timeNanoSeconds = 0; // how long timer runs
	private long resetTime = 0;
	private boolean expired = false;
	// private long timeLeft = 0;
	private long tmp = 0;
	private boolean pause = false;
	public int id;

	public void start() {
		this.pause = false;
		resetTime = TimeUtils.nanoTime() - tmp;

	}

	/**
	 * Pauses timer
	 */
	public void stop() {
		this.pause = true;
		tmp = TimeUtils.nanoTime() - resetTime;
	}

	public boolean isPause() {
		return pause;
	}

	public SimpleTimer(int timeMilliSeconds) {
		super();
		this.timeNanoSeconds = convertMilliToNano(timeMilliSeconds);
		this.resetTime = TimeUtils.nanoTime();
		// this.timeLeft = this.timeNanoSeconds ;
		// tmp = TimeUtils.nanoTime() ;
	}

	public SimpleTimer() {
		super();
		this.timeNanoSeconds = convertMilliToNano(1000);
		this.resetTime = TimeUtils.nanoTime();
		this.pause = true;
		// this.timeLeft = this.timeNanoSeconds ;
		// tmp = TimeUtils.nanoTime() ;
	}

	public void setTime(int milliSeconds) {
		this.timeNanoSeconds = convertMilliToNano(milliSeconds);
		// this.timeLeft = this.timeNanoSeconds ;
	}

	public void reset() {
		this.resetTime = TimeUtils.nanoTime();
		// this.timeLeft = this.timeNanoSeconds ;
	}

	public boolean timeExpired() {
		if (pause) {
			if (tmp > timeNanoSeconds) {
				expired = true;
			} else {
				expired = false;
			}
		} else {
			if (TimeUtils.nanoTime() - resetTime > timeNanoSeconds) {
				expired = true;
			} else {
				expired = false;
			}
		}
		return expired;
	}

	/*
	 * returns time left in milliseconds
	 */
	public long getTimeLeft() {
		long time = 0;
		if (pause) {
			time = convertNanoToMilli(timeNanoSeconds - tmp);
		} else {
			time = convertNanoToMilli(timeNanoSeconds
					- (TimeUtils.nanoTime() - resetTime));
		}

		if (time < 0) {
			time = 0;
		}
		return time;
	}

	/**
	 * 
	 * @return 0 - 1
	 */
	public float getTimeLeftPercent() {
		float percent = 0;
		long start = convertNanoToMilli(timeNanoSeconds);
		long left = getTimeLeft();
		percent = (float) left / (float) start;
		return percent;
	}

	private long convertNanoToMilli(long nanoSeconds) {
		return nanoSeconds / 1000000;
	}

	private long convertMilliToNano(int milliSeconds) {
		return (long) milliSeconds * 1000000;
	}

}
