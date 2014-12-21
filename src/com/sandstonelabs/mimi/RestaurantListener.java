package com.sandstonelabs.mimi;

import java.util.List;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;

public interface RestaurantListener {

	public void onRestaurantsLoaded(List<Restaurant> restaurants, LatLng location, int startIndex);
	
}
