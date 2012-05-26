package com.sandstonelabs.mimi;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RestaurantSearchArrayAdapter extends ArrayAdapter<Restaurant> {

	private final Location location;

	public RestaurantSearchArrayAdapter(Context context, List<Restaurant> restaurants, Location location) {
		super(context, R.layout.results, restaurants);
		this.location = location;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Context context = getContext();
		
		View rowView;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.results, parent, false);
		}else{
			rowView = convertView;
		}

		Restaurant restaurant = getItem(position);

		RestaurantDisplay restaurantDisplay = new RestaurantDisplay(restaurant);
		//Set the restaurant name
		TextView nameTextView = (TextView) rowView.findViewById(R.id.search_restaurant_name);
		nameTextView.setText(restaurant.name);

		//Set the images for the restaurant rating
		LinearLayout ratingLayout = (LinearLayout) rowView.findViewById(R.id.search_restaurant_rating);
		restaurantDisplay.setRatingImageView(context, ratingLayout);
		
		//Set the restaurant details
		TextView summaryTextView = (TextView) rowView.findViewById(R.id.search_restaurant_summary);
		summaryTextView.setText(restaurantDisplay.getCuisine());
		
		//Set the restaurant distance
		TextView distanceTextView = (TextView) rowView.findViewById(R.id.search_restaurant_distance);
		distanceTextView.setText(restaurantDisplay.getDistance(location));
		
		//Set the restaurant description
		TextView descriptionTextView = (TextView) rowView.findViewById(R.id.search_restaurant_description);
		descriptionTextView.setText(restaurant.description);
		
		return rowView;
	}
}