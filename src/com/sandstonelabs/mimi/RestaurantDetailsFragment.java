package com.sandstonelabs.mimi;

import java.net.URLEncoder;

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

		MainActivity activity = (MainActivity) getActivity();

		View detailsView = inflater.inflate(R.layout.details, container, false);

		final Restaurant restaurant = activity.getSelectedRestaurant();

		if (restaurant != null) {

			TextView nameText = (TextView) detailsView.findViewById(R.id.details_name);
			ViewGroup comfortRatingLayout = (ViewGroup) detailsView.findViewById(R.id.details_comfort_rating_layout);
			ViewGroup qualityRatingLayout = (ViewGroup) detailsView.findViewById(R.id.details_quality_rating_layout);
			ViewGroup comfortRatingView = (ViewGroup) detailsView.findViewById(R.id.details_comfort_rating);
			ViewGroup qualityRatingView = (ViewGroup) detailsView.findViewById(R.id.details_quality_rating);
			TextView comfortRatingDescriptionText = (TextView) detailsView.findViewById(R.id.details_comfort_rating_description);
			TextView qualityRatingDescriptionText = (TextView) detailsView.findViewById(R.id.details_quality_rating_description);
			TextView descriptionText = (TextView) detailsView.findViewById(R.id.details_description);
			TextView cuisineText = (TextView) detailsView.findViewById(R.id.details_cuisine);
			TextView foodPriceText = (TextView) detailsView.findViewById(R.id.details_food_price);
			TextView addressText = (TextView) detailsView.findViewById(R.id.details_address);
			Button locationButton = (Button) detailsView.findViewById(R.id.details_location);
			Button emailButton = (Button) detailsView.findViewById(R.id.details_email);
			Button callPhoneButton = (Button) detailsView.findViewById(R.id.details_call);

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

		return detailsView;
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
