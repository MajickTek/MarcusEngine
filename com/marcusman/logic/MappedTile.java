package com.marcusman.logic;

//Tile ID in the tileSet and the position of the tile in the map
class MappedTile
{
	public int layer, id, x, y;

	public MappedTile(int layer, int id, int x, int y)
	{
		this.layer = layer;
		this.id = id;
		this.x = x;
		this.y = y;
	}
}