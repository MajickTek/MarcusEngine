package com.marcusman;

public class Timer {
	private static final long NS_PER_SECOND = (long) 1e9;
	private static final long MAX_NS_PER_UPDATE = (long) 1e9;
	private static final int MAX_TICKS_PER_UPDATE = 100;
	
	private final float ticksPerSecond;
	
	private long lastTime = System.nanoTime();
	
	/**
	 * Scale the tick speed
	 */
	public float timeScale=1.0f;
	
	public float fps = 0f;
	
	/**
	 * Time passed since game update (essentially,delta)
	 */
	public float passedTime = 0f;
	
	public int ticks;
	
	/**
	 * Overflow of current tick, caused by casting the past time to an integer
	 */
	public float partialTicks;
	
	public Timer(float tps) {
		this.ticksPerSecond=tps;
	}
	
	public void advanceTime() {
		long now = System.nanoTime();
		long passedNS = now - this.lastTime;
		this.lastTime=now;
		
		passedNS = clamp(passedNS, 0, MAX_NS_PER_UPDATE);
		
		this.fps = (float) (NS_PER_SECOND / passedNS);
		
		this.passedTime += passedNS * this.timeScale * this.ticksPerSecond / NS_PER_SECOND;
		this.ticks = (int) this.passedTime;
		
		this.ticks = Math.min(MAX_TICKS_PER_UPDATE, this.ticks);
		
		this.passedTime -= this.ticks;
		this.partialTicks = this.passedTime;
	}
	
	//TODO: Move or remove
	private static long clamp(long val, long min, long max) {
		if(min > max) throw new IllegalArgumentException(min + " > " + max);
		
		return Math.min(max, Math.max(val, min));
	}
	
}
