package com.marcusman;

public class Timer {
	private static final long NS_PER_SECOND = (long) 1e9;
	private static final long MAX_NS_PER_UPDATE = (long) 1e9;
	private static final int MAX_TICKS_PER_UPDATE = 100;
	
	private final float ticksPerSecond;
	
	private long lastTime = System.nanoTime();
	
	public float timeScale=1.0f;
	
	public float fps = 0f;
	
	public float passedTime = 0f;
	
	public int ticks;
	
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
	
	private static long clamp(long val, long min, long max) {
		if(min > max) throw new IllegalArgumentException(min + " > " + max);
		
		return Math.min(max, Math.max(val, min));
	}
}
