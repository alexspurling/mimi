package com.sandstonelabs.mimi;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This activity displays the results of a location based restaurant search.
 * Displays search results triggered by the search dialog and handles actions
 * from search suggestions.
 */
public class RestaurantSearchResults extends Activity implements LocationChangeListener, RestaurantListener {

	/** The number of restaurants to find before updating the display */
	private static final int MIN_REFRESH_COUNT = 0;

	private TextView mTextView;
	private ListView mListView;
	private Set<Restaurant> currentRestaurantsSet;
	private MimiLocationService locationService;
	private MimiRestaurantService restaurantService;

	//Number of results to limit the search to
	private int numResults = 200;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mTextView = (TextView) findViewById(R.id.text);
		mListView = (ListView) findViewById(R.id.list);
		
		locationService = new MimiLocationService(this, this);

		try {
			restaurantService = new MimiRestaurantService(this, this);
		} catch (IOException e) {
			throw new RuntimeException("Could not instantiate restaurant service", e);
		}

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// Because this activity has set launchMode="singleTop", the system calls this method
		// to deliver the intent if this activity is currently the foreground activity when
		// invoked again (when the user executes a search from this activity, we don't create
		// a new instance of this activity, so the system delivers the search intent here)
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		Log.i(MimiLog.TAG, "Doing something with intent: " + intent);
		showLoadingScreen();

		// Get the last available location to display results
		Location location = locationService.getLastKnownLocation();
		if (location != null) {
			onLocationChanged(location);
		}
	}

	private void showLoadingScreen() {
		mTextView.setText("Loading your current location...");
	}

	@Override
	public void onLocationChanged(Location location) {
		restaurantService.loadRestaurantsForLocation(location, numResults);
	}

	@Override
	public void onRestaurantsLoaded(List<Restaurant> restaurants, Location location) {
		int newRestaurants = countNewRestaurants(restaurants);

		// Only refresh if we have a sufficient number of new items to display
		if (newRestaurants >= MIN_REFRESH_COUNT) {
			displayResults(restaurants, location);
		}
	}

	// Returns the number of restaurants in the given list that are not already
	// currently being displayed
	private int countNewRestaurants(List<Restaurant> restaurants) {
		if (currentRestaurantsSet == null || currentRestaurantsSet.isEmpty()) {
			return restaurants.size();
		}
		int count = 0;
		for (Restaurant restaurant : restaurants) {
			if (currentRestaurantsSet.contains(restaurant)) {
				count++;
			}
		}
		return count;
	}

	private void displayResults(List<Restaurant> restaurants, Location location) {

		// Display the number of results
		mTextView.setText(restaurants.size() + " results for your location");

		Log.i(MimiLog.TAG, "List has " + restaurants.size() + " elements");
		final ArrayAdapter<Restaurant> adapter = new RestaurantSearchArrayAdapter(this, restaurants, location);

		mListView.setAdapter(adapter);

		// Define the on-click listener for the list items
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mTextView.setText("Selected " + id);
				// Build the Intent used to open WordActivity with a specific
				// word Uri
				Intent restaurantIntent = new Intent(getApplicationContext(), RestaurantDetailsActivity.class);

				Restaurant restaurant = adapter.getItem(position);

				restaurantIntent.putExtra("restaurant", new RestaurantData(restaurant));

				startActivity(restaurantIntent);
			}
		});

		currentRestaurantsSet = new HashSet<Restaurant>(restaurants);
	}
}
