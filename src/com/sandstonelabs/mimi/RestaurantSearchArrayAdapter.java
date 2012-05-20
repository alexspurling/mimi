package com.sandstonelabs.mimi;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

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
		RestaurantRating rating = restaurant.rating;

		View previousElement = relativeLayout.getChildAt(relativeLayout.getChildCount()-1);
		
		int ratingImageResource = getRatingImageResource(rating);
		
		for (int i = 0; i < rating.ratingValue; i++) {
			ImageView starImage = new ImageView(getContext());
			starImage.setId(ViewId.getInstance().getUniqueId()); //Need to set the id manually
			starImage.setImageResource(ratingImageResource);
			
			RelativeLayout.LayoutParams imageLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			imageLayout.addRule(RelativeLayout.RIGHT_OF, previousElement.getId());
			imageLayout.addRule(RelativeLayout.CENTER_VERTICAL);
			relativeLayout.addView(starImage, imageLayout);
			
			//Take a note of this image so we can refer to it in the next loop
			previousElement = starImage;
		}
	}

	private int getRatingImageResource(RestaurantRating rating) {
		switch(rating.ratingType) {
		case COMFORTABLE:
			return R.drawable.comfortable;
		case PLEASANT:
			return R.drawable.pleasant;
		case MICHELIN_STAR:
			return R.drawable.michelinstar;
		case PUB:
			return R.drawable.pub;
		case HOTEL:
			return R.drawable.hotel;
		}
		return -1;
	}
}