package com.sandstonelabs.mimi;

import android.os.Bundle;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RestaurantMapFragment extends MapFragment implements GoogleMap.OnMapClickListener, RestaurantListener, OnMapReadyCallback {

    private GoogleMap map;

    private Set<Restaurant> allRestaurants = new HashSet<Restaurant>();

    private LatLng currentLocation;

    public RestaurantMapFragment() {
        GoogleMapOptions options = new GoogleMapOptions();
        CameraPosition cameraPosition = new CameraPosition(new LatLng(51.49336f, -0.16644f), 13, 0, 0);
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false)
                .zoomControlsEnabled(true)
                .camera(cameraPosition);
        Bundle var2 = new Bundle();
        var2.putParcelable("MapOptions", options);
        setArguments(var2);
    }

//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.map, container, false);
//    }


    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setMyLocationEnabled(true);
        displayMarkers();
    }

    @Override
    public void onRestaurantsLoaded(List<Restaurant> restaurants, LatLng location, int startIndex) {
        if (startIndex == 0)
        {
            allRestaurants.clear();
        }
        allRestaurants.addAll(restaurants);
        currentLocation = location;
        displayMarkers();
    }

    private void displayMarkers() {
        if (map != null) {

            LatLngBounds.Builder bc = new LatLngBounds.Builder();

            for (Restaurant restaurant : allRestaurants) {
                LatLng position = new LatLng(restaurant.latitude, restaurant.longitude);
                map.addMarker(new MarkerOptions()
                        .title(restaurant.name)
                        .snippet(restaurant.description)
                        .position(position));
                bc.include(position);
            }
            bc.include(currentLocation);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bc.build(), 100);
            map.moveCamera(cameraUpdate);
        }
    }
}
