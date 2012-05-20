package com.sandstonelabs.mimi;

import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RestaurantDetailsActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);

		TextView nameText = (TextView) findViewById(R.id.details_name);
		ViewGroup comfortRatingLayout = (ViewGroup) findViewById(R.id.details_comfort_rating_layout);
		ViewGroup qualityRatingLayout = (ViewGroup) findViewById(R.id.details_quality_rating_layout);
		ViewGroup comfortRatingView = (ViewGroup) findViewById(R.id.details_comfort_rating);
		ViewGroup qualityRatingView = (ViewGroup) findViewById(R.id.details_quality_rating);
		TextView comfortRatingDescriptionText = (TextView) findViewById(R.id.details_comfort_rating_description);
		TextView qualityRatingDescriptionText = (TextView) findViewById(R.id.details_quality_rating_description);
		TextView descriptionText = (TextView) findViewById(R.id.details_description);
		TextView cuisineText = (TextView) findViewById(R.id.details_cuisine);
		TextView foodPriceText = (TextView) findViewById(R.id.details_food_price);
		Button locationButton = (Button) findViewById(R.id.details_location);
		Button emailButton = (Button) findViewById(R.id.details_email);
		Button callPhoneButton = (Button) findViewById(R.id.details_call);

		RestaurantData restaurantData = getIntent().getParcelableExtra("restaurant");
		final Restaurant restaurant = restaurantData.getRestaurant();

		RestaurantDisplay restaurantDisplay = new RestaurantDisplay(restaurant);
		
		nameText.setText(restaurant.name);
		
		restaurantDisplay.setRatingWithDescription(restaurant.comfortRating, this, comfortRatingLayout, comfortRatingView, comfortRatingDescriptionText);
		restaurantDisplay.setRatingWithDescription(restaurant.qualityRating, this, qualityRatingLayout, qualityRatingView, qualityRatingDescriptionText);
		
		descriptionText.setText(restaurant.description);
		cuisineText.setText(restaurantDisplay.getCuisine());
		foodPriceText.setText(restaurant.foodPrice);
		emailButton.setText(getLengthRestrictedString(restaurant.email, 28));
		callPhoneButton.setText(restaurant.phoneNumber);
		
		locationButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				String latLngString = restaurant.latitude + "," + restaurant.longitude;
				String queryUrl = "?q=" + URLEncoder.encode(restaurant.name + ", " + restaurant.zipCode);
				String zoom = "&z=16";
				Uri uri = Uri.parse("geo:" + latLngString + queryUrl + zoom);
				Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(mapIntent);
			}

		});
		
		emailButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Uri uri = Uri.parse("mailto:" + restaurant.email);
				Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
				sendIntent.setData(uri);
				startActivity(Intent.createChooser(sendIntent, "Send email")); 
			}

		});

		callPhoneButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Uri phoneUri = Uri.parse("tel:" + restaurant.phoneNumber);
				Intent intent = new Intent(Intent.ACTION_CALL, phoneUri);
				startActivity(intent);
			}

		});

	}

	private String getLengthRestrictedString(String string, int maxLength) {
		if (string.length() > maxLength) {
			return string.substring(0, maxLength-3) + "...";
		}
		return string;
	}
}
