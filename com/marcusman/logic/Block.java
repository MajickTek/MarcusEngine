package com.marcusman.logic;

import java.util.ArrayList;

import com.marcusman.graphics.RenderHandler;

//Block represents a 6/6 block of tiles
@SuppressWarnings("unchecked") class Block
{
	/**
	 * 
	 */
	private final Map map;
	public ArrayList<MappedTile>[] mappedTilesByLayer;

	public Block(Map map) 
	{
		this.map = map;
		mappedTilesByLayer = new ArrayList[this.map.numLayers];
		for(int i = 0; i < mappedTilesByLayer.length; i++)
			mappedTilesByLayer[i] = new ArrayList<MappedTile>();
	}

	public void render(RenderHandler renderer, int layer, int tileWidth, int tileHeight, int xZoom, int yZoom)
	 {
		if(mappedTilesByLayer.length > layer) 
		{
			ArrayList<MappedTile> mappedTiles = mappedTilesByLayer[layer];
			for(int tileIndex = 0; tileIndex < mappedTiles.size(); tileIndex++)
			{
				MappedTile mappedTile = mappedTiles.get(tileIndex);
				this.map.tileSet.renderTile(mappedTile.id, renderer, mappedTile.x * tileWidth, mappedTile.y * tileHeight, xZoom, yZoom);
			}
		}
	}

	public void addTile(MappedTile tile) {
		if(mappedTilesByLayer.length <= tile.layer) 
		{
			ArrayList<MappedTile>[] newTilesByLayer = new ArrayList[tile.layer + 1];

			int i = 0;
			for(i = 0; i < mappedTilesByLayer.length; i++)
				newTilesByLayer[i] = mappedTilesByLayer[i];
			for(; i < newTilesByLayer.length; i++)
				newTilesByLayer[i] = new ArrayList<MappedTile>();

			mappedTilesByLayer = newTilesByLayer;
		}
		mappedTilesByLayer[tile.layer].add(tile);
	}

	public void removeTile(MappedTile tile) {
		mappedTilesByLayer[tile.layer].remove(tile);
	}

	public MappedTile getTile(int layer, int tileX, int tileY) 
	{
		for(MappedTile tile : mappedTilesByLayer[layer]) 
		{
			if(tile.x == tileX && tile.y == tileY)
				return tile;
		}
		return null;
	}
}