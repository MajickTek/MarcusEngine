package com.marcusman;

public enum GameInstance {
	INSTANCE;
	Game game;
	
	public void set(Game game) {
		if(this.game!=null) {
			throw new UnsupportedOperationException("The game instance has already been set");
		}
		this.game=game;
	}
	
	public Game get() {
		return game;
	}
	
	public static Game getInstance() {
		if(INSTANCE.game==null) throw new UnsupportedOperationException("The game instance has not been set.");
		return INSTANCE.get();
	}
	
	public static void setInstance(Game game) {
		INSTANCE.set(game);
	}
}
