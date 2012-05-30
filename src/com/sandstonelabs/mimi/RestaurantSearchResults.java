package com.sandstonelabs.mimi;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This activity displays the results of a location based restaurant search.
 * Displays search results triggered by the search dialog and handles actions
 * from search suggestions.
 */
public class RestaurantSearchResults extends Activity implements OnScrollListener, LocationChangeListener, RestaurantListener {

	/** The number of restaurants to find before updating the display */
	private static final int MIN_REFRESH_COUNT = 0;

	private TextView mTextView;
	private ListView mListView;
	private RestaurantSearchArrayAdapter listAdapter;
	private Set<Restaurant> currentRestaurantsSet;
	private MimiLocationService locationService;
	private MimiRestaurantService restaurantService;

	//Number of results to limit the search to
	private int numResults = 20;
	private Location location;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		locationService = new MimiLocationService(this, this);

		try {
			restaurantService = new MimiRestaurantService(this, this);
		} catch (IOException e) {
			throw new RuntimeException("Could not instantiate restaurant service", e);
		}

		mTextView = (TextView) findViewById(R.id.text);
		
		listAdapter = new RestaurantSearchArrayAdapter(this);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setOnScrollListener(this);
		
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View footerView = inflater.inflate(R.layout.listfooter, null);
		Log.i(MimiLog.TAG, "Got footer view: " + footerView);
		mListView.addFooterView(footerView);
		mListView.setAdapter(listAdapter);
		
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
		this.location = location;
		loadRestaurants();
	}

	private void loadRestaurants() {
		if (restaurantService != null && location != null) {
			restaurantService.loadRestaurantsForLocation(location, numResults);
		}
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

		addItemsToListAdapter(restaurants, location);
		
		Log.i(MimiLog.TAG, "List has " + listAdapter.getCount() + " elements");
		mTextView.setText(listAdapter.getCount() + " results for your location");

		// Define the on-click listener for the list items
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mTextView.setText("Selected " + id);
				// Build the Intent used to open WordActivity with a specific
				// word Uri
				Intent restaurantIntent = new Intent(getApplicationContext(), RestaurantDetailsActivity.class);

				Restaurant restaurant = listAdapter.getItem(position);

				restaurantIntent.putExtra("restaurant", new RestaurantData(restaurant));

				startActivity(restaurantIntent);
			}
		});

		currentRestaurantsSet = new HashSet<Restaurant>(restaurants);
	}

	private void addItemsToListAdapter(List<Restaurant> restaurants, Location location) {
		for (Restaurant restaurant : restaurants) {
			listAdapter.add(restaurant);
		}
		listAdapter.setLocation(location);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
		loadMore &= numResults <= totalItemCount;
		if (loadMore) {
			numResults = totalItemCount + 20;
			Log.i(MimiLog.TAG, "Loading " + numResults + " items");
			//TODO load a new set of restaurants to add to the list
			loadRestaurants();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}
