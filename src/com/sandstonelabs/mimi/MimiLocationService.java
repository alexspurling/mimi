package com.sandstonelabs.mimi;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class MimiLocationService {

	private static final long MIN_LOCATION_REFRESH_TIME = 0; // In ms
	private static final float MIN_LOCATION_REFRESH_DISTANCE = 0; // In m
	
	private LocationChangeListener locationChangeListener;
	private LocationManager locationManager;
	private String provider;
	
	//For testing only
	private Handler handler;

	public MimiLocationService(Context context, LocationChangeListener locationChangeListener) {
		this.locationChangeListener = locationChangeListener;
		setUpLocationManager(context);
		//Should only be used for testing purposes - otherwise rely on the LocationManager refresh behaviour
		setUpRegularLocationRefresh();
	}

	private void setUpLocationManager(Context context) {
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location
				// provider.
				locationChangeListener.onLocationChanged(location);
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
			}
		};

		Log.i(MimiLog.TAG, "All location providers: " + locationManager.getAllProviders());

		provider = getLocationProvider();

		// Register the listener with the LocationManager to receive location updates
		locationManager.requestLocationUpdates(provider, MIN_LOCATION_REFRESH_TIME, MIN_LOCATION_REFRESH_DISTANCE, locationListener);
	}

	private String getLocationProvider() {
		String provider = LocationManager.NETWORK_PROVIDER;
		if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
		} else {
			provider = setupFakeLocationProvider(locationManager);
		}

		Log.i(MimiLog.TAG, "Requesting location updates from provider " + provider);
		return provider;
	}

	private String setupFakeLocationProvider(LocationManager locationManager) {
		String providerName = "TestProvider";
		if (locationManager.getProvider(providerName) == null) {
			locationManager.addTestProvider(providerName, true, false, true, false, true, false, false, Criteria.POWER_HIGH, Criteria.ACCURACY_LOW);
		}
		locationManager.setTestProviderEnabled(providerName, true);
		Location fakeLocation = new Location(providerName);
		float maxLatitude = 51.484f;
		float minLongitude = -0.189f;
		float minLatitude = 51.546f;
		float maxLongitude = -0.08f;

		float randomLatitude = (float) (Math.random() * (maxLatitude - minLatitude) + minLatitude);
		float randomLongitude = (float) (Math.random() * (maxLongitude - minLongitude) + minLongitude);

		fakeLocation.setLatitude(randomLatitude);
		fakeLocation.setLongitude(randomLongitude);

		Log.i(MimiLog.TAG, "Got random location: " + fakeLocation);

		locationManager.setTestProviderLocation(providerName, fakeLocation);
		return providerName;
	}

	public Location getLastKnownLocation() {
		return locationManager.getLastKnownLocation(provider);
	}

	private void setUpRegularLocationRefresh() {
		handler = new Handler();
		handler.postDelayed(new LocationUpdater(), 60000);
	}

	private class LocationUpdater implements Runnable {
		@Override
		public void run() {
			setupFakeLocationProvider(locationManager);
			Location location = getLastKnownLocation();
			locationChangeListener.onLocationChanged(location);
			handler.postDelayed(new LocationUpdater(), 10000);
		}
	}
}
