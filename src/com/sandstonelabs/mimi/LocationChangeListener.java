package com.sandstonelabs.mimi;

import com.google.android.gms.maps.model.LatLng;

public interface LocationChangeListener {

	public void onLocationChanged(LatLng location);
	public void onLocationUnavailable();
	
}
