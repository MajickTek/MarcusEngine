package com.marcusman.utils;

public class UnitConverter {

	public static final int TILE_SIZE = 16;
	public static final float UNITS_PER_PIXEL = TILE_SIZE / TILE_SIZE;
	
	
	public static float worldUnits(float val) {
		return val * TILE_SIZE;
	}
	
	public static float worldToPixel(float worldUnits) {
		return worldUnits / UNITS_PER_PIXEL;
	}
	
	public static float pixelToWorld(int pixel) {
		return pixel * UNITS_PER_PIXEL;
	}
}
