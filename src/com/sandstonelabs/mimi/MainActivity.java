package com.sandstonelabs.mimi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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

    private RestaurantMapFragment restaurantMapFragment;
    private RestaurantListFragment restaurantListFragment;
    private MenuItem mapListMenuItem;
    private AtomicBoolean mapVisible = new AtomicBoolean(false);

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
        restaurantListFragment = new RestaurantListFragment();

        // In case this activity was started with special instructions from an Intent,
        // pass the Intent's extras to the fragment as arguments
        restaurantListFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager().beginTransaction().add(R.id.fragment_container, restaurantListFragment).commit();

        //Set up the map fragment
        restaurantMapFragment = new RestaurantMapFragment();
        restaurantMapFragment.getMapAsync(restaurantMapFragment);

        List<RestaurantListener> listeners = new ArrayList<RestaurantListener>();
        listeners.add(restaurantListFragment);
        listeners.add(restaurantMapFragment);

        try {
            restaurantService = new MimiRestaurantService(this, listeners);
        } catch (IOException e) {
            throw new RuntimeException("Could not instantiate restaurant service", e);
        }

        locationService = new MimiLocationService(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        mapListMenuItem = menu.findItem(R.id.menu_map_list);
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
        loadRestaurants(0);
    }

    public void loadRestaurants(int startIndex) {
        if (restaurantService != null && location != null) {
            //Check mutex to avoid loading restaurants more than once
            if (loadingResults.compareAndSet(false, true)) {
                Log.i(MimiLog.TAG, "About to load " + NUM_RESULTS_PER_PAGE + " results from index " + startIndex);
                //mTextView.setText("Loading results ...");
                restaurantService.loadRestaurantsForLocation(location, startIndex, NUM_RESULTS_PER_PAGE);
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

    private void toggleMapListView() {
        if (mapVisible.compareAndSet(false, true)) {
            mapListMenuItem.setIcon(R.drawable.ic_action_view_as_list);
            mapListMenuItem.setTitle(R.string.view_as_list);
            showMap();
        }else if (mapVisible.compareAndSet(true, false)) {
            mapListMenuItem.setIcon(R.drawable.ic_action_map);
            mapListMenuItem.setTitle(R.string.view_as_map);
            showList();
        }
    }

    private void showMap() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, restaurantMapFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showList() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, restaurantListFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

//	private void refresh() {
//		locationService.refreshLocationManager(getActivity());
//		listAdapter.clear();
//		listAdapter.notifyDataSetChanged();
//		initialiseResults();
//	}

    private void showAbout() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Mimi Restaurant Search")
                .setMessage("Merry Christmas, Mum! May Mimi guide you always to good food and happy times.")
                .setIcon(R.drawable.ic_launcher)
                .setNeutralButton("OK", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_refresh:
//	            refresh();
                return true;
            case R.id.menu_about:
                showAbout();
                return true;
            case R.id.menu_map_list:
                toggleMapListView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
