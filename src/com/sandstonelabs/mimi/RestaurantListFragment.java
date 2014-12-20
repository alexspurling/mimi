package com.sandstonelabs.mimi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * This activity displays the results of a location based restaurant search.
 * Displays search results triggered by the search dialog and handles actions
 * from search suggestions.
 */
public class RestaurantListFragment extends Fragment implements OnScrollListener, LocationChangeListener, RestaurantListener {

	private List<Restaurant> restaurantList = new ArrayList<Restaurant>();
	
	private TextView mTextView;
	private ListView mListView;
	private View footerView;
	private RestaurantSearchArrayAdapter listAdapter;
	private MimiLocationService locationService;
	private MimiRestaurantService restaurantService;

	private static final int NUM_RESULTS_PER_PAGE = 20;
	private static final int MAX_RESULTS = 100;
	
	private AtomicBoolean loadingResults = new AtomicBoolean(false);

	private Location location;

	private PopupWindow aboutPopup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Activity activity = getActivity();
//		activity.setContentView(R.layout.listfragment);

		try {
			restaurantService = new MimiRestaurantService(activity, this);
		} catch (IOException e) {
			throw new RuntimeException("Could not instantiate restaurant service", e);
		}

		locationService = new MimiLocationService(activity, this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mainView = inflater.inflate(R.layout.listfragment, container, false);

		Activity activity = getActivity();
		mTextView = (TextView) mainView.findViewById(R.id.text);

		listAdapter = new RestaurantSearchArrayAdapter(activity, restaurantList);
		mListView = (ListView) mainView.findViewById(R.id.list);
		mListView.setOnScrollListener(this);

		mListView.setAdapter(listAdapter);

		footerView = inflater.inflate(R.layout.listfooter, container, false);

		addLoadingFooter();

		setupAboutPopup(inflater);

		initialiseResults();

		return mainView;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private void setupAboutPopup(LayoutInflater inflater) {
//		getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		int popupWidth = display.getWidth() - 40;
		int popupHeight = 400;
		aboutPopup = new PopupWindow(inflater.inflate(R.layout.about, null, false), popupWidth, popupHeight, true);
		aboutPopup.setBackgroundDrawable(new BitmapDrawable());
		aboutPopup.setOutsideTouchable(true);
//		Button okButton = aboutPopup.
//		okButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				aboutPopup.dismiss();
//			}
//		});
	}
//
//	@Override
//	protected void onNewIntent(Intent intent) {
//		// Because this activity has set launchMode="singleTop", the system calls this method
//		// to deliver the intent if this activity is currently the foreground activity when
//		// invoked again (when the user executes a search from this activity, we don't create
//		// a new instance of this activity, so the system delivers the search intent here)
//		initialiseResults();
//	}

	private void initialiseResults() {
		mTextView.setText("Loading your current location...");
		// Get the last available location to display results
		Location location = locationService.getLastKnownLocation();
		if (location != null) {
			onLocationChanged(location);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		this.location = location;
		//If we have a new location then refresh any results that are already loaded
		loadRestaurants(0, NUM_RESULTS_PER_PAGE);
	}

	private void loadRestaurants(int startIndex, int numResults) {
		if (restaurantService != null && location != null) {
			//Check mutex to avoid loading restaurants more than once
			if (loadingResults.compareAndSet(false, true)) {
				Log.i(MimiLog.TAG, "About to load " + numResults + " results from index " + startIndex);
				mTextView.setText("Loading results ...");
				restaurantService.loadRestaurantsForLocation(location, startIndex, numResults);
			}
		}
	}

	@Override
	public void onRestaurantsLoaded(List<Restaurant> restaurants, Location location, int startIndex) {
		displayResults(restaurants, location, startIndex);
		Log.i(MimiLog.TAG, "Loaded " + restaurantList.size() + " results");
		mTextView.setText("Loaded " + restaurantList.size() + " results");
		loadingResults.set(false); //Unset mutex
	}

	private void displayResults(List<Restaurant> restaurants, Location location, int startIndex) {

		updateItemsInListAdapter(restaurants, location, startIndex);
		
		Log.i(MimiLog.TAG, "List has " + listAdapter.getCount() + " elements");

		// Define the on-click listener for the list items
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Build the Intent used to open WordActivity with a specific
				// word Uri
				/*
				Intent restaurantIntent = new Intent(getActivity().getApplicationContext(), RestaurantDetailsFragment.class);

				Restaurant restaurant = listAdapter.getItem(position);

				restaurantIntent.putExtra("restaurant", new RestaurantData(restaurant));
				startActivity(restaurantIntent);
				*/

				RestaurantDetailsFragment restaurantDetailsFragment = new RestaurantDetailsFragment();

				FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();

				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				// Replace whatever is in the fragment_container view with this fragment,
				// and add the transaction to the back stack so the user can navigate back
				transaction.replace(R.id.fragment_container, restaurantDetailsFragment);
				transaction.addToBackStack(null);

				// Commit the transaction
				transaction.commit();
			}
		});
		
		if (listAdapter.getCount() >= MAX_RESULTS) {
			removeLoadingFooter();
		}else{
			addLoadingFooter();
		}
	}

	private void addLoadingFooter() {
		if (mListView.getFooterViewsCount() == 0) {
			//mListView.addFooterView(footerView);
			mListView.setOnScrollListener(this);
		}
	}

	private void removeLoadingFooter() {
		mListView.removeFooterView(footerView);
		mListView.setOnScrollListener(null);
	}

	private void updateItemsInListAdapter(List<Restaurant> restaurants, Location location, int startIndex) {
		//Remove any existing items in the list from the insert index onwards
		
		startIndex = startIndex + 1;
		Log.i(MimiLog.TAG, "Updating list with " + restaurants.size() + " restaurants starting at index " + startIndex);
		
		if (restaurantList.size() > startIndex) {
			ListIterator<Restaurant> iter = restaurantList.listIterator(restaurantList.size());
			while(restaurantList.size() > startIndex && iter.hasPrevious()) {
				Restaurant restaurant = iter.previous();
				Log.i(MimiLog.TAG, "Removing restaurant: " + restaurant.name);
				iter.remove();
			}
		}
		
		restaurantList.addAll(restaurants);
		//Set the location so the distance to each restaurant can be calculated
		listAdapter.setLocation(location);
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem + visibleItemCount >= totalItemCount) {
			//Load a page of new results onto the end of the existing list
			int startIndex = restaurantList.size();
			loadRestaurants(startIndex, NUM_RESULTS_PER_PAGE);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onLocationUnavailable() {
		Log.i(MimiLog.TAG, "Location unavailable");
		mTextView.setText("Current location unavailable");
		removeLoadingFooter();
	}

    private void showMap() {
        Intent intent = new Intent(getActivity(), MapActivity.class);
        startActivity(intent);
    }

	private void showAbout() {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("Mimi Restaurant Search")
			.setMessage("Happy birthday, Mum! May Mimi guide you always to good food and happy times.")
			.setIcon(R.drawable.ic_launcher)
			.setNeutralButton("OK", null)
			.show();
	}

	private void refresh() {
		locationService.refreshLocationManager(getActivity());
		listAdapter.clear();
		listAdapter.notifyDataSetChanged();
		initialiseResults();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_refresh:
	            refresh();
	            return true;
	        case R.id.menu_about:
	            showAbout();
	            return true;
            case R.id.menu_map:
                showMap();
                return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
