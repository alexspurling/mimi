package com.sandstonelabs.mimi;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

	private TextView mTextView;
	private ListView mListView;
	private RestaurantSearchArrayAdapter listAdapter;
	private MimiLocationService locationService;
	private MimiRestaurantService restaurantService;

	//Number of results to limit the search to
	private int numResultsLoaded = 0;
	private int numResultsToLoad = 20;
	private AtomicBoolean loadingResults = new AtomicBoolean(false);

	private Location location;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mTextView = (TextView) findViewById(R.id.text);
		
		locationService = new MimiLocationService(this, this);

		try {
			restaurantService = new MimiRestaurantService(this, mTextView, this);
		} catch (IOException e) {
			throw new RuntimeException("Could not instantiate restaurant service", e);
		}
		
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
			//Check mutex to avoid loading restaurants more than once
			if (loadingResults.compareAndSet(false, true)) {
				Log.i(MimiLog.TAG, "About to load up to " + numResultsToLoad + " results");
				restaurantService.loadRestaurantsForLocation(location, numResultsToLoad);
			}
		}
	}

	@Override
	public void onRestaurantsLoaded(List<Restaurant> restaurants, Location location) {
		numResultsLoaded += restaurants.size();
		displayResults(restaurants, location);
		Log.i(MimiLog.TAG, "Loaded " + numResultsLoaded + " results");
		loadingResults.set(false); //Unset mutex
	}

	private void displayResults(List<Restaurant> restaurants, Location location) {

		addItemsToListAdapter(restaurants, location);
		
		Log.i(MimiLog.TAG, "List has " + listAdapter.getCount() + " elements");

		// Define the on-click listener for the list items
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Build the Intent used to open WordActivity with a specific
				// word Uri
				Intent restaurantIntent = new Intent(getApplicationContext(), RestaurantDetailsActivity.class);

				Restaurant restaurant = listAdapter.getItem(position);

				restaurantIntent.putExtra("restaurant", new RestaurantData(restaurant));

				startActivity(restaurantIntent);
			}
		});
	}

	private void addItemsToListAdapter(List<Restaurant> restaurants, Location location) {
		for (Restaurant restaurant : restaurants) {
			listAdapter.add(restaurant);
		}
		listAdapter.setLocation(location);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem + visibleItemCount >= totalItemCount) {
			numResultsToLoad = numResultsLoaded + 20;
			loadRestaurants();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}
