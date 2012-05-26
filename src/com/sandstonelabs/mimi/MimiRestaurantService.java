package com.sandstonelabs.mimi;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class MimiRestaurantService {

    private static final int maxDistance = 10000; //10km
    
	private Context context;
	private final RestaurantListener restaurantListener;
	
	private RestaurantService restaurantService;

	public MimiRestaurantService(Context context, RestaurantListener restaurantListener) throws IOException {
		this.context = context;
		this.restaurantListener = restaurantListener;
		restaurantService = setupRestaurantService(context);
	}

	private RestaurantService setupRestaurantService(Context context) throws IOException {
		RestaurantJsonParser jsonParser = new RestaurantJsonParser();

		// Create the cache
		File cacheFile = new File(context.getCacheDir(), "mimi-cache.txt");
		RestaurantJsonCache restaurantJsonCache = new RestaurantJsonCache(cacheFile, jsonParser);

		// Load some static data and put it in the cache
		StaticRestaurantLoader staticRestaurantLoader = new StaticRestaurantLoader(context, jsonParser);
		List<String> restaurantJson = staticRestaurantLoader.loadRestaurantJson();
		restaurantJsonCache.storeResultsInCache(restaurantJson);

		// Return a new restaurant service using the new cache object
		ApiRestaurantSearch restaurantApiSearch = new ApiRestaurantSearch();
		return new RestaurantService(restaurantApiSearch, restaurantJsonCache);
	}

	public void loadRestaurantsForLocation(Location location) {
		Log.i(MimiLog.TAG, "Getting restaurants for location: " + location.toString());

		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			Log.i(MimiLog.TAG, "Getting restaurants from network");
			new FetchRestaurantsTask().execute(location);
		} else {
			Log.i(MimiLog.TAG, "Getting restaurants from cache");
			fetchRestaurantsFromCache(location);
		}
	}
	
	private void fetchRestaurantsFromCache(Location location) {
		float latitude = (float) location.getLatitude();
		float longitude = (float) location.getLongitude();
		try {
			List<Restaurant> cachedRestaurants = restaurantService.getCachedRestaurantsAtLocation(latitude, longitude, maxDistance);
			restaurantListener.onRestaurantsLoaded(cachedRestaurants, location);
		}catch (IOException e) {
			//TODO handle error properly
			throw new RuntimeException("Error downloading results from cache", e);
		}
	}

	// Implementation of AsyncTask used to download XML feed from stackoverflow.com.
	private class FetchRestaurantsTask extends AsyncTask<Location, Void, List<Restaurant>> {

		private Location location;
		
		@Override
	    protected List<Restaurant> doInBackground(Location... locations) {
			location = locations[0];
			float latitude = (float) location.getLatitude();
			float longitude = (float) location.getLongitude();
        	try {
				return restaurantService.getApiRestaurantsAtLocation(latitude, longitude, maxDistance);
			} catch (IOException e) {
				//TODO handle error properly
				throw new RuntimeException("Error downloading results from api", e);
			}
	    }

	    @Override
	    protected void onPostExecute(List<Restaurant> restaurants) {  
	    	restaurantListener.onRestaurantsLoaded(restaurants, location);
	    }
	    
	}
	
}
