package com.sandstonelabs.mimi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.content.res.Resources;

public class StaticRestaurantLoader {

	private final Context context;
	private final RestaurantJsonParser restaurantJsonParser;
	
	public StaticRestaurantLoader(Context context, RestaurantJsonParser restaurantJsonParser) {
		this.context = context;
		this.restaurantJsonParser = restaurantJsonParser;
	}
	
	public List<String> loadRestaurantJson() throws IOException {
        Resources resources = context.getResources();
        
        InputStream inputStream = resources.openRawResource(R.raw.staticrestaurants);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        List<String> restaurantJson = new ArrayList<String>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
            	restaurantJson.add(line);
            }
        } finally {
            reader.close();
        }
        return restaurantJson;
	}
	
	public List<Restaurant> loadRestaurants() throws IOException, JSONException {
		
		List<String> restaurantJson = loadRestaurantJson();
		
        List<Restaurant> restaurants = new ArrayList<Restaurant>();
        
        for (String json : restaurantJson) {
        	restaurants.add(restaurantJsonParser.parseRestaurantSearchResultsFromJson(json));
        }
        return restaurants;
	}
	
}
