package com.marcusman.logic;

import com.marcusman.utils.GameObject;
import com.marcusman.utils.Rectangle;
import com.marcusman.graphics.RenderHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;

public class Map
{
	Tiles tileSet;
	private int fillTileID = -1;

	private ArrayList<MappedTile> mappedTiles = new ArrayList<MappedTile>();
	private Chunk[][] chunks;
	private int chunkStartX, chunkStartY;

	private int chunkWidth = 6;
	private int chunkHeight = 6;
	private int chunkPixelWidth = chunkWidth * 16;
	private int chunkPixelHeight = chunkHeight * 16;

	private HashMap<Integer, String> comments = new HashMap<Integer, String>();

	private File mapFile;

	int numLayers;

	public Map(File mapFile, Tiles tileSet)
	{
		this.mapFile = mapFile;
		this.tileSet = tileSet;
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		try (Scanner scanner = new Scanner(mapFile)) 
		{
			int currentLine = 0;
			while(scanner.hasNextLine()) 
			{
				String line = scanner.nextLine();
				if(!line.startsWith("//"))
				{
					if(line.contains(":")) 
					{
						String[] splitString = line.split(":");
						if(splitString[0].equalsIgnoreCase("Fill"))
						{
							fillTileID = Integer.parseInt(splitString[1]);
							continue;
						}
					}


					String[] splitString = line.split(",");
					if(splitString.length >= 4)
					{
						MappedTile mappedTile = new MappedTile(Integer.parseInt(splitString[0]),
															   Integer.parseInt(splitString[1]),
															   Integer.parseInt(splitString[2]),
															   Integer.parseInt(splitString[3]));
						if(mappedTile.x < minX)
							minX = mappedTile.x;
						if(mappedTile.y < minY)
							minY = mappedTile.y;
						if(mappedTile.x > maxX)
							maxX = mappedTile.x;
						if(mappedTile.x > maxY)
							maxY = mappedTile.y;

						if(numLayers <= mappedTile.layer)
							numLayers = mappedTile.layer + 1;


						mappedTiles.add(mappedTile);
					}
				}
				else
				{
					comments.put(currentLine, line);
				}
				currentLine++;
			}

			if(mappedTiles.size() == 0) {
				minX = -chunkWidth;
				minY = -chunkHeight;
				maxX = chunkWidth;
				maxY = chunkHeight;
			} 

			chunkStartX = minX;
			chunkStartY = minY;
			int chunkSizeX = (maxX + chunkWidth) - minX;
			int chunkSizeY = (maxY + chunkHeight) - minY;
			chunks = new Chunk[chunkSizeX][chunkSizeY];

			//Loop through all mappedTiles in the entire level and add them to the chunks.
			for(int i = 0; i < mappedTiles.size(); i++) {
				MappedTile mappedTile = mappedTiles.get(i);
				int chunkX = (mappedTile.x - minX)/chunkWidth;
				int chunkY = (mappedTile.y - minY)/chunkHeight;
				assert(chunkX >= 0 && chunkX < chunks.length && chunkY >= 0 && chunkY < chunks[0].length);

				if(chunks[chunkX][chunkY] == null)
					chunks[chunkX][chunkY] = new Chunk(this);

				chunks[chunkX][chunkY].addTile(mappedTile);
			}

		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch chunk
			e.printStackTrace();
		}
	}

	public MappedTile getTile(int layer, int tileX, int tileY) {
		int chunkX = (tileX - chunkStartX)/chunkWidth;
		int chunkY = (tileY - chunkStartY)/chunkHeight;

		if(chunkX < 0 || chunkX >= chunks.length || chunkY < 0 || chunkY >= chunks[0].length)
			return null;

		Chunk chunk = chunks[chunkX][chunkY];

		if(chunk == null)
			return null;

		return chunk.getTile(layer, tileX, tileY);
	}

	public boolean checkCollision(Rectangle rect, int layer, int xZoom, int yZoom) {
		int tileWidth = 16 * xZoom;
		int tileHeight = 16 * yZoom;

		//Coordinates to check all tiles in a radius of 4 around the player
		int topLeftX = (rect.x - 64)/tileWidth;
		int topLeftY = (rect.y - 64)/tileHeight;
		int bottomRightX = (rect.x + rect.w + 64)/tileWidth;
		int bottomRightY = (rect.y + rect.h + 64)/tileHeight;

		//Starting at the top left tile and going to the bottom right
		for(int x = topLeftX; x < bottomRightX; x++)
			for(int y = topLeftY; y < bottomRightY; y++) {
				MappedTile tile = getTile(layer, x, y);
				if(tile != null) {
					int collisionType = tileSet.collisionType(tile.id);

					//Full tile collision
					if(collisionType == 0) {
						Rectangle tileRectangle = new Rectangle(tile.x*tileWidth, tile.y*tileHeight, tileWidth, tileWidth);
						if(tileRectangle.intersects(rect))
							return true;

					//Top of tile collision
					} else if(collisionType == 1) {
						Rectangle tileRectangle = new Rectangle(tile.x*tileWidth, tile.y*tileHeight, tileWidth, 16);
						if(tileRectangle.intersects(rect))
							return true;

					//Left of tile collision
					} else if(collisionType == 2) {
						Rectangle tileRectangle = new Rectangle(tile.x*tileWidth, tile.y*tileHeight, 16, tileHeight);
						if(tileRectangle.intersects(rect))
							return true;

					//Bottom of tile collision
					} else if (collisionType == 3) {
						Rectangle tileRectangle = new Rectangle(tile.x*tileWidth, tile.y*tileHeight + tileHeight - 16, tileWidth, 16);
						Rectangle adjustedRect = new Rectangle(rect.x, rect.y + rect.h, rect.w, 1);
						if(tileRectangle.intersects(adjustedRect))
							return true;

					//Right of tile collision
					} else if (collisionType == 4) {
						Rectangle tileRectangle = new Rectangle(tile.x*tileWidth + tileWidth - 16, tile.y*tileHeight, 16, tileHeight);
						if(tileRectangle.intersects(rect))
							return true;
					}



				}
			}

		return false;
	}

	public void setTile(int layer, int tileX, int tileY, int tileID)
	{
		if(layer >= numLayers)
			numLayers = layer + 1;

		for(int i = 0; i < mappedTiles.size(); i++)
		{
			MappedTile mappedTile = mappedTiles.get(i);
			if(mappedTile.x == tileX && mappedTile.y == tileY) {
				mappedTile.id = tileID;
				return;
			}
		}

		MappedTile mappedTile = new MappedTile(layer, tileID, tileX, tileY);
		mappedTiles.add(mappedTile);

		//Add to chunks
		int chunkX = (tileX - chunkStartX)/chunkWidth;
		int chunkY = (tileY - chunkStartY)/chunkHeight;
		if(chunkX >= 0 && chunkY >= 0 && chunkX < chunks.length && chunkY < chunks[0].length) 
		{
			if(chunks[chunkX][chunkY] == null)
				chunks[chunkX][chunkY] = new Chunk(this);

			chunks[chunkX][chunkY].addTile(mappedTile);
		} 
		else 
		{
			int newMinX = chunkStartX;
			int newMinY = chunkStartY;
			int newLengthX = chunks.length;
			int newLengthY = chunks[0].length;

			if(chunkX < 0) 
			{
				int increaseAmount = chunkX * -1;
				newMinX = chunkStartX - chunkWidth*increaseAmount;
				newLengthX = newLengthX + increaseAmount;
			} else if(chunkX >= chunks.length)
				newLengthX = chunks.length + chunkX;

			if(chunkY < 0) 
			{
				int increaseAmount = chunkY * -1;
				newMinY = chunkStartY - chunkHeight*increaseAmount;
				newLengthY = newLengthY + increaseAmount;
			} else if(chunkY >= chunks[0].length)
				newLengthY = chunks[0].length + chunkY;

			Chunk[][] newchunks = new Chunk[newLengthX][newLengthY];

			for(int x = 0; x < chunks.length; x++)
				for(int y = 0; y < chunks[0].length; y++)
					if(chunks[x][y] != null) 
					{
						newchunks[x + (chunkStartX - newMinX)/chunkWidth][y + (chunkStartY - newMinY)/chunkHeight] = chunks[x][y];
					}

			chunks = newchunks;
			chunkStartX = newMinX;
			chunkStartY = newMinY;
			chunkX = (tileX - chunkStartX)/chunkWidth;
			chunkY = (tileY - chunkStartY)/chunkHeight;
			if(chunks[chunkX][chunkY] == null)
				chunks[chunkX][chunkY] = new Chunk(this);
			chunks[chunkX][chunkY].addTile(mappedTile);
		}
	}

	public void removeTile(int layer, int tileX, int tileY)
	{
		for(int i = 0; i < mappedTiles.size(); i++)
		{
			MappedTile mappedTile = mappedTiles.get(i);
			if(mappedTile.layer == layer && mappedTile.x == tileX && mappedTile.y == tileY) {
				mappedTiles.remove(i);

				//Remove from chunk
				int chunkX = (tileX - chunkStartX)/chunkWidth;
				int chunkY = (tileY - chunkStartY)/chunkHeight;
				assert(chunkX >= 0 && chunkY >= 0 && chunkX < chunks.length && chunkY < chunks[0].length);
				chunks[chunkX][chunkY].removeTile(mappedTile);
			}
		}
	}

	public void saveMap()
	{
		try
		{
			int currentLine = 0;
			if(mapFile.exists()) 
				mapFile.delete();
			mapFile.createNewFile();

			PrintWriter printWriter = new PrintWriter(mapFile);

			if(fillTileID >= 0) {
				if(comments.containsKey(currentLine)) 
				{
					printWriter.println(comments.get(currentLine));
					currentLine++;
				}
				printWriter.println("Fill:" + fillTileID);
			}

			for(int i = 0; i < mappedTiles.size(); i++) 
			{
				if(comments.containsKey(currentLine))
					printWriter.println(comments.get(currentLine));

				MappedTile tile = mappedTiles.get(i);
				printWriter.println(tile.layer + "," + tile.id + "," + tile.x + "," + tile.y);
				currentLine++;
			}

			printWriter.close();
		} 
		catch (java.io.IOException e)
		{
			e.printStackTrace();
		}
	}

	public void render(RenderHandler renderer, GameObject[] objects, int xZoom, int yZoom)
	{
		int tileWidth = 16 * xZoom;
		int tileHeight = 16 * yZoom;

		if(fillTileID >= 0)
		{
			Rectangle camera = renderer.getCamera();

			for(int y = camera.y - tileHeight - (camera.y % tileHeight); y < camera.y + camera.h; y+= tileHeight)
			{
				for(int x = camera.x - tileWidth - (camera.x % tileWidth); x < camera.x + camera.w; x+= tileWidth)
				{
					tileSet.renderTile(fillTileID, renderer, x, y, xZoom, yZoom);
				}
			}
		}

		for(int layer = 0; layer < numLayers; layer++) 
		{
			int topLeftX = renderer.getCamera().x;
			int topLeftY = renderer.getCamera().y;
			int bottomRightX = renderer.getCamera().x + renderer.getCamera().w;
			int bottomRightY = renderer.getCamera().y + renderer.getCamera().h;

			int leftchunkX = (topLeftX/tileWidth - chunkStartX - 16)/chunkWidth;
			int chunkX = leftchunkX;
			int chunkY = (topLeftY/tileHeight - chunkStartY - 16)/chunkHeight;
			int pixelX = topLeftX;
			int pixelY = topLeftY;

			while(pixelX < bottomRightX && pixelY < bottomRightY)
			{

				if(chunkX >= 0 && chunkY >= 0 && chunkX < chunks.length && chunkY < chunks[0].length) 
				{
					if(chunks[chunkX][chunkY] != null)
						chunks[chunkX][chunkY].render(renderer, layer, tileWidth, tileHeight, xZoom, yZoom);
				}

				chunkX++;
				pixelX += chunkPixelWidth;

				if(pixelX > bottomRightX) 
				{
					pixelX = topLeftX;
					chunkX = leftchunkX;
					chunkY++;
					pixelY += chunkPixelHeight;
					if(pixelY > bottomRightY)
						break;
				}
			}

			for(int i = 0; i < objects.length; i++)
				if(objects[i].getLayer() == layer)
					objects[i].render(renderer, xZoom, yZoom);
				else if(objects[i].getLayer() + 1 == layer) 
				{
					Rectangle rect = objects[i].getRectangle();

					int tileBelowX = rect.x/tileWidth;
					int tileBelowX2 = (int) Math.floor((rect.x + rect.w/2*xZoom*1.0)/tileWidth);
					int tileBelowX3 = (int) Math.floor((rect.x + rect.w*xZoom*1.0)/tileWidth);

					int tileBelowY = (int) Math.floor((rect.y + rect.h*yZoom*1.0)/tileHeight);

					if(getTile(layer, tileBelowX, tileBelowY) == null && 
					   getTile(layer, tileBelowX2, tileBelowY) == null && 
					   getTile(layer, tileBelowX3, tileBelowY) == null)
						objects[i].render(renderer, xZoom, yZoom);
				}
		}

		for(int i = 0; i < objects.length; i++)
			if(objects[i].getLayer() == Integer.MAX_VALUE)
				objects[i].render(renderer, xZoom, yZoom);

	}
	
	public int getNumLayers() {
		return numLayers;
	}
}
