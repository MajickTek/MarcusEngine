package com.marcusman.logic;

import java.util.ArrayList;

import com.marcusman.graphics.RenderHandler;

//Chunk represents a 6/6 area of tiles
 class Chunk
{
	
	private final Map map;
	
	public ArrayList<ArrayList<MappedTile>> mappedTilesByLayer;

	public Chunk(Map map) 
	{
		this.map = map;
		
		mappedTilesByLayer = new ArrayList<>(this.map.numLayers);
		for(int i = 0; i < this.map.numLayers; i++) {
			
			mappedTilesByLayer.add( new ArrayList<MappedTile>());
		}
	}

	public void render(RenderHandler renderer, int layer, int tileWidth, int tileHeight, int xZoom, int yZoom)
	 {
		if(mappedTilesByLayer.size() > layer) 
		{
			ArrayList<MappedTile> mappedTiles = mappedTilesByLayer.get(layer);
			for(int tileIndex = 0; tileIndex < mappedTiles.size(); tileIndex++)
			{
				MappedTile mappedTile = mappedTiles.get(tileIndex);
				this.map.tileSet.renderTile(mappedTile.id, renderer, mappedTile.x * tileWidth, mappedTile.y * tileHeight, xZoom, yZoom);
			}
		}
	}

	public void addTile(MappedTile tile) {
		if(mappedTilesByLayer.size() <= tile.layer) 
		{
			
			ArrayList<ArrayList<MappedTile>> newTilesByLayer = new ArrayList<>(tile.layer+1);
			int i = 0;
			for(i = 0; i < mappedTilesByLayer.size(); i++)
				newTilesByLayer.set(i, mappedTilesByLayer.get(i));
			for(; i < newTilesByLayer.size(); i++)
				newTilesByLayer.set(i, new ArrayList<MappedTile>());

			mappedTilesByLayer = newTilesByLayer;
		}
		mappedTilesByLayer.get(tile.layer).add(tile);
	}

	public void removeTile(MappedTile tile) {
		mappedTilesByLayer.get(tile.layer).remove(tile);
	}

	public MappedTile getTile(int layer, int tileX, int tileY) 
	{
		for(MappedTile tile : mappedTilesByLayer.get(layer)) 
		{
			if(tile.x == tileX && tile.y == tileY)
				return tile;
		}
		return null;
	}
}