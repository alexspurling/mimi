package com.sandstonelabs.mimi;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
	
	public String getDistance(Location location) {
		LatLng searchLocation = new LatLng(location.getLatitude(), location.getLongitude());
		LatLng restaurantLocation = new LatLng(restaurant.latitude, restaurant.longitude);
		double distance = LatLngTool.distance(searchLocation, restaurantLocation, LengthUnit.KILOMETER);
		DecimalFormat df = new DecimalFormat("#.#");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(distance) + "km";
	}

	public void setRatingWithDescription(RestaurantRating rating, Context context, ViewGroup ratingLayout, ViewGroup ratingView, TextView ratingDescriptionText) {
		if (rating == null) {
			ratingLayout.setVisibility(View.GONE);
			return;
		}
		
		setRatingView(rating, context, ratingView);
		ratingDescriptionText.setText(rating.description);
	}
	
	public void setRatingImageView(Context context, ViewGroup viewGroup) {
		//Display the quality rating if it exists, otherwise show the comfort rating
		RestaurantRating rating = restaurant.qualityRating != null ? restaurant.qualityRating : restaurant.comfortRating;
		setRatingView(rating, context, viewGroup);
	}
	
	private void setRatingView(RestaurantRating rating, Context context, ViewGroup viewGroup) {
		//Nothing to set
		if (rating == null) return;
		
		viewGroup.removeAllViews(); //Clear out any existing views from this group
		View previousView = null;
		
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
			viewGroup.addView(starImage, imageLayout);
			
			//Take a note of this image so we can refer to it in the next loop
			previousView = starImage;
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

	public String getComfortRatingDescription() {
		if (restaurant.comfortRating != null) {
			return restaurant.comfortRating.description;
		}
		return "";
	}

	public String getQualityRatingDescription() {
		if (restaurant.qualityRating != null) {
			return restaurant.qualityRating.description;
		}
		return "";
	}
}
