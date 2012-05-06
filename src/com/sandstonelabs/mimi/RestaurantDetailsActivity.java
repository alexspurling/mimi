package com.sandstonelabs.mimi;

import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RestaurantDetailsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);

		TextView nameText = (TextView) findViewById(R.id.name);
		TextView descriptionText = (TextView) findViewById(R.id.description);
		TextView cuisineText = (TextView) findViewById(R.id.cuisine);
		TextView foodPriceText = (TextView) findViewById(R.id.food_price);
		Button locationButton = (Button) findViewById(R.id.location);
		Button emailButton = (Button) findViewById(R.id.email);
		Button callPhoneButton = (Button) findViewById(R.id.call);

		RestaurantData restaurantData = getIntent().getParcelableExtra("restaurant");
		final Restaurant restaurant = restaurantData.getRestaurant();

		nameText.setText(restaurant.name);
		descriptionText.setText(restaurant.description);
		cuisineText.setText(restaurant.cuisine);
		foodPriceText.setText(restaurant.foodPrice);
		emailButton.setText(getLengthRestrictedString(restaurant.email, 35));
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
