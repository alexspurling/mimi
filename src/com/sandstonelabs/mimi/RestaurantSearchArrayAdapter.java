package com.sandstonelabs.mimi;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RestaurantSearchArrayAdapter extends ArrayAdapter<Restaurant> {

	private static final int[] redStarImages = new int[] {
			R.drawable.redforkandspoon_small_1,
			R.drawable.redforkandspoon_small_2,
			R.drawable.redforkandspoon_small_3,
			R.drawable.redforkandspoon_small_4,
			R.drawable.redforkandspoon_small_5};
	
	private final Random random = new Random();

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
		
		//Set the restaurant name
		TextView nameTextView = (TextView) rowView.findViewById(R.id.search_restaurant_name);
		nameTextView.setText(getRestaurantName(restaurant));

		//Set the images for the restaurant rating
		ImageView starsImageView = (ImageView) rowView.findViewById(R.id.search_restaurant_stars);
		setRestaurantStarsImageView(restaurant, starsImageView);
		
		//Set the restaurant details
		TextView summaryTextView = (TextView) rowView.findViewById(R.id.search_restaurant_summary);
		summaryTextView.setText(getRestaurantSummary(restaurant));
		
		//Set the restaurant distance
		TextView distanceTextView = (TextView) rowView.findViewById(R.id.search_restaurant_distance);
		distanceTextView.setText(getRestaurantDistance(restaurant));
		
		//Set the restaurant description
		TextView descriptionTextView = (TextView) rowView.findViewById(R.id.search_restaurant_description);
		descriptionTextView.setText(getRestaurantDescription(restaurant));
		
		return rowView;
	}

	private String getRestaurantName(Restaurant restaurant) {
		return restaurant.name;
	}
	
	private String getRestaurantSummary(Restaurant restaurant) {
		char firstLetter = restaurant.cuisine.charAt(0);
		//TODO: DO this transformation when parsing the raw data!
		if (Character.isLowerCase(firstLetter)) {
			return Character.toUpperCase(firstLetter) + restaurant.cuisine.substring(1);
		}
		return restaurant.cuisine;
	}
	
	private String getRestaurantDistance(Restaurant restaurant) {
		LatLng restaurantLocation = new LatLng(restaurant.latitude, restaurant.longitude);
		double distance = LatLngTool.distance(curLocation, restaurantLocation, LengthUnit.KILOMETER);
		DecimalFormat df = new DecimalFormat("#.#");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(distance) + "km";
	}
	
	private String getRestaurantDescription(Restaurant restaurant) {
		return restaurant.description;
	}
	
	private void setRestaurantStarsImageView(Restaurant restaurant, ImageView starsImageView) {
		int starRating = random.nextInt(5) + 1;

		starsImageView.setImageResource(redStarImages[starRating-1]);
		starsImageView.getLayoutParams().width = 20 * starRating;
	}
}