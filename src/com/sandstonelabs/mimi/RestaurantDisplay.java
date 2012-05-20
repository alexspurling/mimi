package com.sandstonelabs.mimi;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

/**
 * Methods to convert a Restaurant into objects that Android can display
 * on the screen
 *
 */
public class RestaurantDisplay {

	private final Restaurant restaurant;

	public RestaurantDisplay(Restaurant restaurant) {
		this.restaurant = restaurant;
	}
	
	public String getCuisine() {
		char firstLetter = restaurant.cuisine.charAt(0);
		//TODO: DO this transformation when parsing the raw data!
		if (Character.isLowerCase(firstLetter)) {
			return Character.toUpperCase(firstLetter) + restaurant.cuisine.substring(1);
		}
		return restaurant.cuisine;
	}
	
	public String getDistance(LatLng curLocation) {
		LatLng restaurantLocation = new LatLng(restaurant.latitude, restaurant.longitude);
		double distance = LatLngTool.distance(curLocation, restaurantLocation, LengthUnit.KILOMETER);
		DecimalFormat df = new DecimalFormat("#.#");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(distance) + "km";
	}
	
	public void setRatingImageView(Context context, RelativeLayout relativeLayout, View displayAfterView, View displayBeforeView) {
		RestaurantRating rating = restaurant.rating;

		//Nothing to set
		if (rating == null) return;
		
		View previousView = displayAfterView;
		
		int ratingImageResource = getRatingImageResource(rating);
		
		for (int i = 0; i < rating.ratingValue; i++) {
			ImageView starImage = new ImageView(context);
			starImage.setId(ViewId.getInstance().getUniqueId()); //Need to set the id manually
			starImage.setImageResource(ratingImageResource);
			
			RelativeLayout.LayoutParams imageLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if (previousView != null) {
				imageLayout.addRule(RelativeLayout.RIGHT_OF, previousView.getId());
			}
			imageLayout.addRule(RelativeLayout.CENTER_VERTICAL);
			relativeLayout.addView(starImage, imageLayout);
			
			//Take a note of this image so we can refer to it in the next loop
			previousView = starImage;
		}
		
		if (displayBeforeView != null && previousView != null) {
//			RelativeLayout.LayoutParams previousImageLayout = (RelativeLayout.LayoutParams) previousView.getLayoutParams();
//			previousImageLayout.addRule(RelativeLayout.LEFT_OF, displayBeforeView.getId());
//			
			RelativeLayout.LayoutParams beforeImageLayout = (RelativeLayout.LayoutParams) displayBeforeView.getLayoutParams();
			beforeImageLayout.addRule(RelativeLayout.RIGHT_OF, previousView.getId());
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
