package com.sandstonelabs.mimi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;

public class StaticRestaurantLoader {

	private final Context context;
	private final RestaurantJsonParser restaurantJsonParser;
	
	public StaticRestaurantLoader(Context context, RestaurantJsonParser restaurantJsonParser) {
		this.context = context;
		this.restaurantJsonParser = restaurantJsonParser;
	}
	
	public List<Restaurant> loadRestaurants() throws IOException {
        Resources resources = context.getResources();
        
        InputStream inputStream = resources.openRawResource(R.raw.staticrestaurants);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        List<Restaurant> restaurants = new ArrayList<Restaurant>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
            	restaurants.add(restaurantJsonParser.parseRestaurantSearchResultsFromJson(line));
            }
        } finally {
            reader.close();
        }
        return restaurants;
	}
	
}
