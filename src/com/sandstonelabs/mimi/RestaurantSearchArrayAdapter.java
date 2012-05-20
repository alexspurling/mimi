package com.sandstonelabs.mimi;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.javadocmd.simplelatlng.LatLng;

public class RestaurantSearchArrayAdapter extends ArrayAdapter<Restaurant> {

	private final LatLng curLocation;

	public RestaurantSearchArrayAdapter(Context context, List<Restaurant> restaurants, LatLng curLocation) {
		super(context, R.layout.results, restaurants);
		this.curLocation = curLocation;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Context context = getContext();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.results, parent, false);

		Restaurant restaurant = getItem(position);

		RestaurantDisplay restaurantDisplay = new RestaurantDisplay(restaurant);
		//Set the restaurant name
		TextView nameTextView = (TextView) rowView.findViewById(R.id.search_restaurant_name);
		nameTextView.setText(restaurant.name);

		//Set the images for the restaurant rating
		RelativeLayout relativeLayout = (RelativeLayout) rowView.findViewById(R.id.search_restaurant_title);
		restaurantDisplay.setRatingImageView(getContext(), relativeLayout);
		
		//Set the restaurant details
		TextView summaryTextView = (TextView) rowView.findViewById(R.id.search_restaurant_summary);
		summaryTextView.setText(restaurantDisplay.getCuisine());
		
		//Set the restaurant distance
		TextView distanceTextView = (TextView) rowView.findViewById(R.id.search_restaurant_distance);
		distanceTextView.setText(restaurantDisplay.getDistance(curLocation));
		
		//Set the restaurant description
		TextView descriptionTextView = (TextView) rowView.findViewById(R.id.search_restaurant_description);
		descriptionTextView.setText(restaurant.description);
		
		return rowView;
	}
}