package com.sandstonelabs.mimi;

import com.sandstonelabs.mimi.Restaurant.RestaurantBuilder;

import android.os.Parcel;
import android.os.Parcelable;

public class RestaurantData implements Parcelable {

	private final Restaurant restaurant;

	public RestaurantData(Parcel in) {
		this.restaurant = loadFromParcel(in);
	}

	public RestaurantData(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(restaurant.id);
		dest.writeFloat(restaurant.latitude);
		dest.writeFloat(restaurant.longitude);
		dest.writeString(restaurant.name);
		dest.writeString(restaurant.description);
		dest.writeString(restaurant.cuisine);
		dest.writeString(restaurant.foodPrice);
		writeRating(dest, restaurant.comfortRating);
		writeRating(dest, restaurant.qualityRating);
		dest.writeString(restaurant.email);
		dest.writeString(restaurant.phoneNumber);
		dest.writeString(restaurant.oneLineAddress);
		dest.writeString(restaurant.address);
		dest.writeString(restaurant.city);
		dest.writeString(restaurant.zipCode);
		dest.writeString(restaurant.countryCode);
		dest.writeString(restaurant.country);
		dest.writeString(restaurant.website);
	}
	
	private Restaurant loadFromParcel(Parcel in) {
		RestaurantBuilder builder = new Restaurant.RestaurantBuilder();
		
		builder.id(in.readString()).
		latitude(in.readFloat()).
		longitude(in.readFloat()).
		name(in.readString()).
		description(in.readString()).
		cuisine(in.readString()).
		foodPrice(in.readString()).
		comfortRating(readRating(in)).
		qualityRating(readRating(in)).
		email(in.readString()).
		phoneNumber(in.readString()).
		oneLineAddress(in.readString()).
		address(in.readString()).
		city(in.readString()).
		zipCode(in.readString()).
		countryCode(in.readString()).
		country(in.readString()).
		website(in.readString());
		
		return builder.build();
	}
	
	private RestaurantRating readRating(Parcel in) {
		if (in.readByte() != 0) {
			return new RestaurantRating(in.readInt(), 
					RestaurantRating.RatingType.valueOf(in.readString()),
					in.readString()
					);
		}
		return null;
	}
	
	private void writeRating(Parcel dest, RestaurantRating rating) {
		dest.writeByte(rating != null ? (byte)1 : (byte)0);
		if (rating != null) {
			dest.writeInt(rating.ratingValue);
			dest.writeString(rating.ratingType.name());
			dest.writeString(rating.description);
		}
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<RestaurantData> CREATOR = new Parcelable.Creator<RestaurantData>() {
		@Override
		public RestaurantData createFromParcel(Parcel in) {
			return new RestaurantData(in);
		}

		@Override
		public RestaurantData[] newArray(int size) {
			return new RestaurantData[size];
		}
	};
}
