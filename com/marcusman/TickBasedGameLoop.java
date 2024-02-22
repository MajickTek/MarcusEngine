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
		int frames=0;
		long lastTime = System.currentTimeMillis();
		while(isGameRunning()) 
		{
			this.timer.advanceTime();
			for(int i = 0; i < this.timer.ticks; ++i) {
				update.run();
			}
			
			render.run();
			
			frames++;
			while(System.currentTimeMillis() >= lastTime+1000L) {
				System.out.println("FPS: "+frames);
				lastTime+=1000L;
				frames=0;
			}
			
		}
	}

	private Timer getTimer() {
		return timer == null ? timer = new Timer(60) : timer;
	}

}
