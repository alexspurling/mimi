package com.sandstonelabs.mimi;

import java.util.List;
import java.util.Random;

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

	public RestaurantSearchArrayAdapter(Context context, List<Restaurant> restaurants) {
		super(context, R.layout.result, restaurants);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Context context = getContext();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.result, parent, false);

		Restaurant restaurant = getItem(position);
		
		//Set the restaurant name
		TextView nameTextView = (TextView) rowView.findViewById(R.id.search_restaurant_name);
		nameTextView.setText(getRestaurantName(restaurant));

		//Set the images for the restaurant rating
		ImageView starsImageView = (ImageView) rowView.findViewById(R.id.search_restaurant_stars);
		setRestaurantStarsImageView(restaurant, starsImageView);
		
		//Set the restaurant details
		TextView detailsTextView = (TextView) rowView.findViewById(R.id.search_restaurant_details);
		detailsTextView.setText(getRestaurantDetails(restaurant));
		
		//Set the restaurant description
		TextView descriptionTextView = (TextView) rowView.findViewById(R.id.search_restaurant_description);
		descriptionTextView.setText(getRestaurantDescription(restaurant));
		
		return rowView;
	}
	
	private String getRestaurantName(Restaurant restaurant) {
		return restaurant.name;
	}
	
	private String getRestaurantDetails(Restaurant restaurant) {
		return restaurant.cuisine + " - " + restaurant.foodPrice;
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