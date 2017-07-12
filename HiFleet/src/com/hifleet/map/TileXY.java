package com.hifleet.map;

public class TileXY {
	private int tilex, tiley;

	public TileXY() {

	}

	public TileXY(int x, int y) {
		tilex = x;
		tiley = y;
	}

	/**
	 * @return the tilex
	 */
	public int getTilex() {
		return tilex;
	}

	/**
	 * @param tilex the tilex to set
	 */
	public void setTilex(int tilex) {
		this.tilex = tilex;
	}

	/**
	 * @return the tiley
	 */
	public int getTiley() {
		return tiley;
	}

	/**
	 * @param tiley the tiley to set
	 */
	public void setTiley(int tiley) {
		this.tiley = tiley;
	}

}
