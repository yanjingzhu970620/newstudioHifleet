package com.hifleet.map;

public interface IMapLocationListener {
	void locationChanged(double newLatitude, double newLongitude, Object source);

}
