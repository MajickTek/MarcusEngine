package com.marcusman;

public class TickBasedGameLoop extends GameLoop {
	private final Runnable update;
	private final Runnable render;
	private Timer timer;
	
	public TickBasedGameLoop(Runnable update, Runnable render) {
		this.update=update;
		this.render=render;
	}
	
	@Override
	protected void processGameLoop() {
		this.timer = getTimer();
		
		while(isGameRunning()) 
		{
			this.timer.advanceTime();
			for(int i = 0; i < this.timer.ticks; ++i) {
				update.run();
			}
			
			render.run();
			
		}
	}

	private Timer getTimer() {
		return timer == null ? timer = new Timer(60) : timer;
	}

}
