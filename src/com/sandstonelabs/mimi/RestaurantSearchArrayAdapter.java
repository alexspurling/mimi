package com.sandstonelabs.mimi;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

public class RestaurantSearchArrayAdapter extends ArrayAdapter<Restaurant> {

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

		Log.i(MimiLog.TAG, "Setting up view for restaurant: " + restaurant);
		
		//Set the restaurant name
		TextView nameTextView = (TextView) rowView.findViewById(R.id.search_restaurant_name);
		nameTextView.setText(getRestaurantName(restaurant));

		//Set the images for the restaurant rating
		RelativeLayout relativeLayout = (RelativeLayout) rowView.findViewById(R.id.search_restaurant_title);
		setRestaurantStarsImageView(restaurant, relativeLayout);
		
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
	
	private void setRestaurantStarsImageView(Restaurant restaurant, RelativeLayout relativeLayout) {
		int starRating = random.nextInt(5) + 1;

		Log.i(MimiLog.TAG, "Setting star images for rating " + starRating);
		
		View previousElement = relativeLayout.getChildAt(0);
		
		for (int i = 0; i < starRating; i++) {
			ImageView starImage = new ImageView(getContext());
			starImage.setId(ViewId.getInstance().getUniqueId()); //Need to set the id manually
			starImage.setImageResource(R.drawable.redforkandspoon_small_1);
			
			RelativeLayout.LayoutParams imageLayout = new RelativeLayout.LayoutParams(20, 30);
			imageLayout.addRule(RelativeLayout.RIGHT_OF, previousElement.getId());
			imageLayout.addRule(RelativeLayout.CENTER_VERTICAL);
			relativeLayout.addView(starImage, imageLayout);
			
			//Take a note of this image so we can refer to it in the next loop
			previousElement = starImage;
		}
	}
}