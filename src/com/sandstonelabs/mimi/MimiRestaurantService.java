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

	private final Context context;
	private final List<RestaurantListener> restaurantListeners;
	
	private RestaurantService restaurantService;


	public MimiRestaurantService(Context context, List<RestaurantListener> restaurantListeners) throws IOException {
		this.context = context;
		this.restaurantListeners = restaurantListeners;
		restaurantService = setupRestaurantService(context);
	}

	private RestaurantService setupRestaurantService(Context context) throws IOException {
		RestaurantJsonParser jsonParser = new RestaurantJsonParser();

		// Create the cache
		File cacheDir = context.getCacheDir();
		RestaurantJsonCache restaurantJsonCache = new RestaurantJsonCache(cacheDir, jsonParser);

		Log.i(MimiLog.TAG, "Using cache directory: " + cacheDir.getCanonicalPath());
		
		// Return a new restaurant service using the new cache object
		ApiRestaurantSearch restaurantApiSearch = new ApiRestaurantSearch();
		return new RestaurantService(restaurantApiSearch, restaurantJsonCache);
	}

	public void loadRestaurantsForLocation(Location location, int startIndex, int numResults) {
		Log.i(MimiLog.TAG, "Getting restaurants for location: " + location.toString());

		try {
			float latitude = (float) location.getLatitude();
			float longitude = (float) location.getLongitude();
			
			//First try to get the results from the cache
			RestaurantResults restaurantResults = restaurantService.getCachedRestaurantsAtLocation(latitude, longitude, startIndex, numResults);
			
			Log.i(MimiLog.TAG, "Got " + restaurantResults.restaurants.size() + " results from cache from index " + startIndex + ". Full results: " + restaurantResults.fullResults);

			for (Restaurant restaurant : restaurantResults.restaurants) {
				Log.i(MimiLog.TAG, "Got restaurant " + restaurant.name);
			}
			
			//If we did not get the full results for this location,
			//lookup the remaining results from the API
			if (!restaurantResults.fullResults && isNetworkAvailable()) {
				//We haven't got all the possible results from the cache
				//call the remote api if it is available
				fetchRestaurantsFromApi(location, startIndex);
			}else{
				//Display the restaurants loaded from the cache (whether they are full or not)
				for (RestaurantListener restaurantListener : restaurantListeners) {
					restaurantListener.onRestaurantsLoaded(restaurantResults.restaurants, location, startIndex);
				}
			}
		}catch(IOException e) {
			//TODO handle error properly
			throw new RuntimeException("Error getting results from cache", e);
		}
	}
	
	private boolean isNetworkAvailable() {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
	
	private void fetchRestaurantsFromApi(Location location, int startIndex) {
		Log.i(MimiLog.TAG, "Getting restaurants from api from index " + startIndex);
		new FetchRestaurantsTask().execute(location, startIndex);
	}

	private class FetchRestaurantsTask extends AsyncTask<Object, Void, List<Restaurant>> {

		private Location location;
		private int startIndex;
		
		@Override
	    protected List<Restaurant> doInBackground(Object... params) {
			location = (Location)params[0];
			startIndex = (Integer)params[1];
			float latitude = (float) location.getLatitude();
			float longitude = (float) location.getLongitude();
        	try {
				return restaurantService.getApiRestaurantsAtLocation(latitude, longitude, startIndex);
			} catch (IOException e) {
				//TODO handle error properly
				throw new RuntimeException("Error downloading results from api", e);
			}
	    }

	    @Override
	    protected void onPostExecute(List<Restaurant> restaurants) {
			for (RestaurantListener restaurantListener : restaurantListeners) {
				restaurantListener.onRestaurantsLoaded(restaurants, location, startIndex);
			}
	    }
	    
	}
	
}
