package com.sandstonelabs.mimi;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends Activity implements LocationChangeListener {

    List<Restaurant> restaurantList = new ArrayList<Restaurant>();
    AtomicBoolean loadingResults = new AtomicBoolean(false);
    Location location;

    private MimiLocationService locationService;

    private MimiRestaurantService restaurantService;
    private static final int NUM_RESULTS_PER_PAGE = 20;
    private static final int MAX_RESULTS = 100;
    private Restaurant selectedRestaurant;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }

        // Create an instance of ExampleFragment
        RestaurantListFragment restaurantListFragment = new RestaurantListFragment();

        // In case this activity was started with special instructions from an Intent,
        // pass the Intent's extras to the fragment as arguments
        restaurantListFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager().beginTransaction().add(R.id.fragment_container, restaurantListFragment).commit();

        try {
            restaurantService = new MimiRestaurantService(this, restaurantListFragment);
        } catch (IOException e) {
            throw new RuntimeException("Could not instantiate restaurant service", e);
        }

        locationService = new MimiLocationService(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        initialiseResults();
    }

    private void initialiseResults() {
        //mTextView.setText("Loading your current location...");
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
                //mTextView.setText("Loading results ...");
                restaurantService.loadRestaurantsForLocation(location, startIndex, numResults);
            }
        }
    }

    @Override
    public void onLocationUnavailable() {
        Log.i(MimiLog.TAG, "Location unavailable");
//        mTextView.setText("Current location unavailable");
//        removeLoadingFooter();
    }

    public void setSelectedRestaurant(Restaurant selectedRestaurant) {
        this.selectedRestaurant = selectedRestaurant;
    }

    public Restaurant getSelectedRestaurant() {
        return selectedRestaurant;
    }
}
