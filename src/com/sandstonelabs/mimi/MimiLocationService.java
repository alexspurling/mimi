package com.sandstonelabs.mimi;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class MimiLocationService {

	private static final long LOCATION_AVAILABLE_CHECK_TIME = 60000; // In ms
	private static final long MIN_LOCATION_REFRESH_TIME = 60000; // In ms
	private static final float MIN_LOCATION_REFRESH_DISTANCE = 100; // In m
	
	private LocationChangeListener locationChangeListener;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private String provider;
	
	//For testing only
	private Handler handler;

	public MimiLocationService(Context context, LocationChangeListener locationChangeListener) {
		this.locationChangeListener = locationChangeListener;
		setUpLocationManager(context);
	}

	private void setUpLocationManager(Context context) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		locationListener = new LocationListener() {
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

		if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER) && 
			locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
			Log.i(MimiLog.TAG, "Requesting location updates from provider " + provider);
			// Register the listener with the LocationManager to receive location updates
			locationManager.requestLocationUpdates(provider, MIN_LOCATION_REFRESH_TIME, MIN_LOCATION_REFRESH_DISTANCE, locationListener);
			setTimeLimitForLocation();
		}else{
			Log.i(MimiLog.TAG, "Network provider is currently unavailable");
			locationChangeListener.onLocationUnavailable();
		}
	}

	public Location getLastKnownLocation() {
		if (provider != null) {
			return locationManager.getLastKnownLocation(provider);
		}else{
			//Used for testing
			return getRandomLocation();
		}
	}
	
	public void refreshLocationManager(Context context) {
		setUpLocationManager(context);
	}

	private void setTimeLimitForLocation() {
		handler = new Handler();
		//Check if a location has been found after 60s. If not, location manager.
		handler.postDelayed(new LocationAvailabilityCheck(), LOCATION_AVAILABLE_CHECK_TIME);
	}

	private class LocationAvailabilityCheck implements Runnable {
		@Override
		public void run() {
			Location location = getLastKnownLocation();
			if (location == null) {
				locationManager.removeUpdates(locationListener);
				locationChangeListener.onLocationUnavailable();
			}
		}
	}
	
	public Location getRandomLocation() {
		Location fakeLocation = new Location("RandomLocation");
		float maxLatitude = 51.57405f;
		float minLongitude = -0.10752f;
		float minLatitude = 51.57346f;
		float maxLongitude = -0.10655f;
		
		float latitude = (float) (Math.random() * (maxLatitude - minLatitude) + minLatitude);
		float longitude = (float) (Math.random() * (maxLongitude - minLongitude) + minLongitude);
		
//		latitude = 51.49292f;
//		longitude = -0.16699f;
		
		fakeLocation.setLatitude(latitude);
		fakeLocation.setLongitude(longitude);
		
		Log.i(MimiLog.TAG, "Generated random location: " + fakeLocation);
		
		return fakeLocation;
	}
}
