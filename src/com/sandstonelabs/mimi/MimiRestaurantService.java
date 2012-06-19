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
import android.widget.TextView;

public class MimiRestaurantService {

	private final Context context;
	private final TextView statusTextView;
	private final RestaurantListener restaurantListener;
	
	private RestaurantService restaurantService;


	public MimiRestaurantService(Context context, TextView statusTextView, RestaurantListener restaurantListener) throws IOException {
		this.context = context;
		this.statusTextView = statusTextView;
		this.restaurantListener = restaurantListener;
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
			//Calculate the page number based on the number of results
			int page = (startIndex)/20+1; //TODO don't calculate the page number here - pass the index directly to the library
			
			//First try to get the results from the cache
			statusTextView.setText("Getting restaurants from cache");
			RestaurantResults restaurantResults = restaurantService.getCachedRestaurantsAtLocation(latitude, longitude, page);
			
			Log.i(MimiLog.TAG, "Got " + restaurantResults.restaurants.size() + " results from cache for page " + page + ". Full results: " + restaurantResults.fullResults);

			for (Restaurant restaurant : restaurantResults.restaurants) {
				Log.i(MimiLog.TAG, "Got restaurant " + restaurant.name);
			}
			
			
			statusTextView.setText("Loaded " + restaurantResults.restaurants.size() + " restaurants from cache");
			//If we did not get the full results for this location,
			//lookup the remaining results from the API
			if (!restaurantResults.fullResults && isNetworkAvailable()) {
				//We haven't got all the possible results from the cache
				//call the remote api if it is available
				statusTextView.setText("Loading restaurants from website");
				fetchRestaurantsFromApi(location, startIndex, page);
			}else{
				//Display the restaurants loaded from the cache (whether they are full or not)
				restaurantListener.onRestaurantsLoaded(restaurantResults.restaurants, location, startIndex);
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
	
	private void fetchRestaurantsFromApi(Location location, int startIndex, int page) {
		Log.i(MimiLog.TAG, "Getting restaurants from api at page " + page);
		new FetchRestaurantsTask().execute(location, startIndex, page);
	}

	private class FetchRestaurantsTask extends AsyncTask<Object, Void, List<Restaurant>> {

		private Location location;
		private int startIndex;
		
		@Override
	    protected List<Restaurant> doInBackground(Object... params) {
			location = (Location)params[0];
			startIndex = (Integer)params[1];
			int page = (Integer)params[2];
			float latitude = (float) location.getLatitude();
			float longitude = (float) location.getLongitude();
        	try {
				return restaurantService.getApiRestaurantsAtLocation(latitude, longitude, page);
			} catch (IOException e) {
				//TODO handle error properly
				throw new RuntimeException("Error downloading results from api", e);
			}
	    }

	    @Override
	    protected void onPostExecute(List<Restaurant> restaurants) {
			statusTextView.setText("Loaded " + restaurants.size() + " restaurants from website");
	    	restaurantListener.onRestaurantsLoaded(restaurants, location, startIndex);
	    }
	    
	}
	
}
