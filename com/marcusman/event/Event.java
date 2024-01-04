package com.marcusman.event;

public interface Event {
	
	Class<? extends Event> getType();
}
