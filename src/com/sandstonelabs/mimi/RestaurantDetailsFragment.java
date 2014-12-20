package com.sandstonelabs.mimi;

import java.net.URLEncoder;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RestaurantDetailsFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Activity activity = getActivity();

		RestaurantData restaurantData = activity.getIntent().getParcelableExtra("restaurant");

		if (restaurantData != null) {

			TextView nameText = (TextView) activity.findViewById(R.id.details_name);
			ViewGroup comfortRatingLayout = (ViewGroup) activity.findViewById(R.id.details_comfort_rating_layout);
			ViewGroup qualityRatingLayout = (ViewGroup) activity.findViewById(R.id.details_quality_rating_layout);
			ViewGroup comfortRatingView = (ViewGroup) activity.findViewById(R.id.details_comfort_rating);
			ViewGroup qualityRatingView = (ViewGroup) activity.findViewById(R.id.details_quality_rating);
			TextView comfortRatingDescriptionText = (TextView) activity.findViewById(R.id.details_comfort_rating_description);
			TextView qualityRatingDescriptionText = (TextView) activity.findViewById(R.id.details_quality_rating_description);
			TextView descriptionText = (TextView) activity.findViewById(R.id.details_description);
			TextView cuisineText = (TextView) activity.findViewById(R.id.details_cuisine);
			TextView foodPriceText = (TextView) activity.findViewById(R.id.details_food_price);
			TextView addressText = (TextView) activity.findViewById(R.id.details_address);
			Button locationButton = (Button) activity.findViewById(R.id.details_location);
			Button emailButton = (Button) activity.findViewById(R.id.details_email);
			Button callPhoneButton = (Button) activity.findViewById(R.id.details_call);

			final Restaurant restaurant = restaurantData.getRestaurant();

			RestaurantDisplay restaurantDisplay = new RestaurantDisplay(restaurant);

			nameText.setText(restaurant.name);

			restaurantDisplay.setRatingWithDescription(restaurant.comfortRating, activity, comfortRatingLayout, comfortRatingView, comfortRatingDescriptionText);
			restaurantDisplay.setRatingWithDescription(restaurant.qualityRating, activity, qualityRatingLayout, qualityRatingView, qualityRatingDescriptionText);

			descriptionText.setText(restaurant.description);
			cuisineText.setText(restaurant.cuisine);
			setFoodPrice(foodPriceText, restaurant);
			addressText.setText(restaurant.oneLineAddress);

			setEmail(emailButton, restaurant);
			setPhone(callPhoneButton, restaurant);

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

		return inflater.inflate(R.layout.details, container, false);
	}

	private void setFoodPrice(TextView foodPriceText, Restaurant restaurant) {
		if (restaurant.foodPrice != null) {
			String cleanFoodPriceString = restaurant.foodPrice.replaceAll("([0-9]+)£", "£$1");
			cleanFoodPriceString = cleanFoodPriceString.replaceAll("([0-9]+)€", "€$1");
			cleanFoodPriceString = cleanFoodPriceString.replaceAll("/", "-");
			foodPriceText.setText(cleanFoodPriceString);
		}
	}

	private void setEmail(Button emailButton, Restaurant restaurant) {
		if (restaurant.email != null && restaurant.email.length() > 0) {
			emailButton.setText(getLengthRestrictedString(restaurant.email, 27));
			emailButton.setEnabled(true);
		}
	}

	private void setPhone(Button callPhoneButton, Restaurant restaurant) {
		if (restaurant.phoneNumber != null && restaurant.phoneNumber.length() > 0) {
			callPhoneButton.setText(restaurant.phoneNumber);
			callPhoneButton.setEnabled(true);
		}
	}

	private String getLengthRestrictedString(String string, int maxLength) {
		if (string != null && string.length() > maxLength) {
			return string.substring(0, maxLength-3) + "...";
		}
		return string;
	}
}
