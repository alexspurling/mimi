package com.sandstonelabs.mimi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class RestaurantDetailsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        TextView name = (TextView) findViewById(R.id.name);
        TextView description = (TextView) findViewById(R.id.description);
        TextView cuisine = (TextView) findViewById(R.id.cuisine);
        TextView foodPrice = (TextView) findViewById(R.id.food_price);

        RestaurantData restaurantData = getIntent().getParcelableExtra("restaurant");
        Restaurant restaurant = restaurantData.getRestaurant();
        
        name.setText(restaurant.name);
        description.setText(restaurant.description);
        cuisine.setText(restaurant.cuisine);
        foodPrice.setText(restaurant.foodPrice);
    }
}
