package com.sandstonelabs.mimi;

import java.util.List;

import android.location.Location;

public interface RestaurantListener {

	public void onRestaurantsLoaded(List<Restaurant> restaurants, Location location, int startIndex);
	
}
