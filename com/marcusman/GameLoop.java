package com.marcusman;

public abstract class GameLoop {
	protected volatile GameStatus status;
	
	protected GameLoop() {
		status = GameStatus.STOPPED;
	}
	
	public void run() {
		status = GameStatus.RUNNING;
		Thread gameThread = new Thread(this::processGameLoop);
		gameThread.start();
	}
	
	public void stop() {
		status = GameStatus.STOPPED;
	}
	
	public boolean isGameRunning() {
		return status.equals(GameStatus.RUNNING);
	}
	
	protected abstract void processGameLoop();
}
