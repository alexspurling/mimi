package com.sandstonelabs.mimi;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends Activity implements LocationChangeListener, AdapterView.OnItemSelectedListener {

    List<Restaurant> restaurantList = new ArrayList<Restaurant>();
    AtomicBoolean loadingResults = new AtomicBoolean(false);
    LatLng location;

    private MimiLocationService locationService;

    private MimiRestaurantService restaurantService;
    private static final int NUM_RESULTS_PER_PAGE = 20;
    private static final int MAX_RESULTS = 100;
    private Restaurant selectedRestaurant;

    private RestaurantMapFragment restaurantMapFragment;
    private RestaurantListFragment restaurantListFragment;

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

        List<RestaurantListener> listeners = setupFragments();

        setupServices(listeners);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setDisplayShowTitleEnabled(false);
        List<String> list = Arrays.asList("List", "Map");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        actionBar.setListNavigationCallbacks(adapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                if (itemPosition == 0) {
                    showList();
                } else {
                    showMap();
                }
                return true;
            }
        });

        //setupListMapTabs();
    }

    private List<RestaurantListener> setupFragments() {
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
        return listeners;
    }

    private void setupServices(List<RestaurantListener> listeners) {
        try {
            restaurantService = new MimiRestaurantService(this, listeners);
        } catch (IOException e) {
            throw new RuntimeException("Could not instantiate restaurant service", e);
        }

        locationService = new MimiLocationService(this, this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        switch (position) {
            case 0:
                showList();
                break;
            case 1:
                showMap();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            int changes = 0;

            @Override
            public boolean onQueryTextChange(String s) {

//                if (changes >= 4 || s.endsWith(" ")) {
//                    findLocationByName(s);
//                    changes = 0;
//                } else
//                    ++changes;
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                findLocationByName(query);
                searchView.clearFocus();
                searchMenuItem.collapseActionView();
                searchView.setQuery("", false);
                return true;
            }

        });

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
            onLocationChanged(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    @Override
    public void onLocationChanged(LatLng location) {
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
        Toast toast = Toast.makeText(this, "Location unavailable", Toast.LENGTH_SHORT);
        toast.show();
//        removeLoadingFooter();
    }

    public void setSelectedRestaurant(Restaurant selectedRestaurant) {
        this.selectedRestaurant = selectedRestaurant;
    }

    public Restaurant getSelectedRestaurant() {
        return selectedRestaurant;
    }

    private void findLocationByName(String locationName) {
        try {
            List<Address> addresses = new Geocoder(this).getFromLocationName(locationName, 10);
            if (addresses.isEmpty())
            {
                Toast toast = Toast.makeText(this, "No results found for " + locationName, Toast.LENGTH_SHORT);
                toast.show();
            }
            else
            {
                Address firstAddress = addresses.get(0);
                onLocationChanged(new LatLng(firstAddress.getLatitude(), firstAddress.getLongitude()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMap() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, restaurantMapFragment);
        fragmentTransaction.commit();
    }

    private void showList() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, restaurantListFragment);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
