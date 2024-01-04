package com.marcusman.event;

public interface Handler<E extends Event> {
	void onEvent(E event);
}
