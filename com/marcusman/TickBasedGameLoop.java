package com.marcusman;

import java.util.function.Consumer;

public class TickBasedGameLoop extends GameLoop {
	private final Runnable update;
	private final Consumer<Float> render;
	private Timer timer;
	
	public TickBasedGameLoop(Runnable update, Consumer<Float> render) {
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
			
			render.accept(this.timer.partialTicks);
			
		}
	}

	private Timer getTimer() {
		return timer == null ? timer = new Timer(60) : timer;
	}

}
