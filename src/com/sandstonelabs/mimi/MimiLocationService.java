package com.sandstonelabs.mimi;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class MimiLocationService {

	private static final long MIN_LOCATION_REFRESH_TIME = 60000; // In ms
	private static final float MIN_LOCATION_REFRESH_DISTANCE = 100; // In m
	
	private LocationChangeListener locationChangeListener;
	private LocationManager locationManager;
	private String provider;
	
	//For testing only
	private Handler handler;

	public MimiLocationService(Context context, LocationChangeListener locationChangeListener) {
		this.locationChangeListener = locationChangeListener;
		setUpLocationManager(context);
		//Should only be used for testing purposes - otherwise rely on the LocationManager refresh behaviour
		//setUpRegularLocationRefresh();
	}

	private void setUpLocationManager(Context context) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
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

		if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
			Log.i(MimiLog.TAG, "Requesting location updates from provider " + provider);
			
			// Register the listener with the LocationManager to receive location updates
			locationManager.requestLocationUpdates(provider, MIN_LOCATION_REFRESH_TIME, MIN_LOCATION_REFRESH_DISTANCE, locationListener);
		}else{
			//TODO Handle no network location provider
			//
		}
	}

	public Location getLastKnownLocation() {
		if (provider != null) {
			return locationManager.getLastKnownLocation(provider);
		}
		return null;
	}

	private void setUpRegularLocationRefresh() {
		handler = new Handler();
		handler.post(new LocationUpdater());
	}

	private class LocationUpdater implements Runnable {
		@Override
		public void run() {
			Location location = getRandomLocation();
			locationChangeListener.onLocationChanged(location);
			handler.postDelayed(new LocationUpdater(), MIN_LOCATION_REFRESH_TIME);
		}
	}
	
	public Location getRandomLocation() {
		Location fakeLocation = new Location("RandomLocation");
		float maxLatitude = 51.484f;
		float minLongitude = -0.189f;
		float minLatitude = 51.546f;
		float maxLongitude = -0.08f;
		
		float randomLatitude = (float) (Math.random() * (maxLatitude - minLatitude) + minLatitude);
		float randomLongitude = (float) (Math.random() * (maxLongitude - minLongitude) + minLongitude);
		
		fakeLocation.setLatitude(randomLatitude);
		fakeLocation.setLongitude(randomLongitude);
		
		Log.i(MimiLog.TAG, "Generated random location: " + fakeLocation);
		
		return fakeLocation;
	}
}
