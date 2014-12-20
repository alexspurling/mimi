package com.sandstonelabs.mimi;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.*;
import android.app.Activity;

public class RestaurantMapFragment extends MapFragment implements GoogleMap.OnMapClickListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    private void showResults() {
        Intent intent = new Intent(this, RestaurantListFragment.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_list:
                showResults();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
