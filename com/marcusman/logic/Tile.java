package com.marcusman.logic;

import com.marcusman.graphics.Sprite;

public class Tile 
{
	public String tileName;
	public Sprite sprite;
	public boolean collidable = false;
	public int collisionType = -1;

	public Tile(String tileName, Sprite sprite) 
	{
		this.tileName = tileName;
		this.sprite = sprite;
	}
}