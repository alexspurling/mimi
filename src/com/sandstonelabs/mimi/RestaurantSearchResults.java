/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sandstonelabs.mimi;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.javadocmd.simplelatlng.LatLng;

/**
 * This activity displays the results of a location based restaurant search.
 * Displays search results triggered by the search dialog and handles
 * actions from search suggestions.
 */
public class RestaurantSearchResults extends Activity {

    private TextView mTextView;
    private ListView mListView;
    private List<Restaurant> restaurantList;
    private LatLng currentLocation;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mTextView = (TextView) findViewById(R.id.text);
        mListView = (ListView) findViewById(R.id.list);

        currentLocation = new LatLng(51.492713, -0.166243);
        RestaurantJsonParser jsonParser = new RestaurantJsonParser();
		try {
			StaticRestaurantLoader staticRestaurantLoader = new StaticRestaurantLoader(this, jsonParser);
			restaurantList = staticRestaurantLoader.loadRestaurants();
		} catch (Exception e) {
			throw new RuntimeException("Error in the restaurant cache search", e);
		}

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this activity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
    	Log.i(MimiLog.TAG, "Doing something with intent: " + intent);
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // handles a click on a search suggestion; launches activity to show word
            Intent wordIntent = new Intent(this, RestaurantDetailsActivity.class);
            wordIntent.setData(intent.getData());
            startActivity(wordIntent);
        } else {
            showResults();
        }
    }

    /**
     * Searches the dictionary and displays results for the given query.
     * @param query The search query
     */
    private void showResults() {
        // Display the number of results
        mTextView.setText("20 results for (your location)");
        
        Log.i(MimiLog.TAG, "List has " + restaurantList.size() + " elements");
		final ArrayAdapter<Restaurant> adapter = new RestaurantSearchArrayAdapter(this, restaurantList, currentLocation);
        
        mListView.setAdapter(adapter);

        // Define the on-click listener for the list items
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	mTextView.setText("Selected " + id);
            	// Build the Intent used to open WordActivity with a specific word Uri
                Intent restaurantIntent = new Intent(getApplicationContext(), RestaurantDetailsActivity.class);
                
                Restaurant restaurant = adapter.getItem(position);
                
                restaurantIntent.putExtra("restaurant", new RestaurantData(restaurant));
				
                startActivity(restaurantIntent);
            }
        });
    }
}
