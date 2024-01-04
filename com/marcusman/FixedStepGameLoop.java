package com.marcusman;

public class FixedStepGameLoop extends GameLoop {
	
	private final Runnable update,render;
	
	public FixedStepGameLoop(Runnable update, Runnable render) {
		this.update=update;
		this.render=render;
	}
	
	@Override
	protected void processGameLoop() {
		long lastTime = System.nanoTime(); //long 2^63
		double nanoSecondConversion = 1000000000.0 / 60; //60 frames per second
		double changeInSeconds = 0;

		while(isGameRunning()) 
		{
			long now = System.nanoTime();

			changeInSeconds += (now - lastTime) / nanoSecondConversion;
			while(changeInSeconds >= 1) {
				update.run();
				changeInSeconds--;
			}

			render.run();
			lastTime = now;
		}
	}

}
