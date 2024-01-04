package com.marcusman.event;

public abstract class AbstractEvent implements Event {

	@Override
	public Class<? extends Event> getType() {
		return getClass();
	}

}
