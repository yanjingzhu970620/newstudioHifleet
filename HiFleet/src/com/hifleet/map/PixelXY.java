package com.hifleet.map;

public class PixelXY {
private int px,py;

public PixelXY(){}

public PixelXY(int x,int y){
	px = x;py=y;
}

/**
 * @return the px
 */
public int getPx() {
	return px;
}

/**
 * @param px the px to set
 */
public void setPx(int px) {
	this.px = px;
}

/**
 * @return the py
 */
public int getPy() {
	return py;
}

/**
 * @param py the py to set
 */
public void setPy(int py) {
	this.py = py;
}

}
