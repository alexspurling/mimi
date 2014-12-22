package com.sandstonelabs.mimi;

import java.util.List;
import java.util.ListIterator;

import android.app.*;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import com.google.android.gms.maps.model.LatLng;

/**
 * This activity displays the results of a location based restaurant search.
 * Displays search results triggered by the search dialog and handles actions
 * from search suggestions.
 */
public class RestaurantListFragment extends Fragment implements OnScrollListener, RestaurantListener {

	private ListView mListView;
	private View footerView;
	private RestaurantSearchArrayAdapter listAdapter;


	private PopupWindow aboutPopup;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mainView = inflater.inflate(R.layout.listfragment, container, false);

		final MainActivity activity = getMainActivity();

		listAdapter = new RestaurantSearchArrayAdapter(activity, activity.restaurantList);
		listAdapter.setLocation(activity.location);

		mListView = (ListView) mainView.findViewById(R.id.list);
		mListView.setOnScrollListener(this);

		mListView.setAdapter(listAdapter);

		// Define the on-click listener for the list items
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Build the Intent used to open WordActivity with a specific
				// word Uri
				/*
				Intent restaurantIntent = new Intent(getActivity().getApplicationContext(), RestaurantDetailsFragment.class);

				Restaurant restaurant = listAdapter.getItem(position);

				restaurantIntent.putExtra("restaurant", new RestaurantData(restaurant));
				startActivity(restaurantIntent);
				*/

				activity.setSelectedRestaurant(listAdapter.getItem(position));

				RestaurantDetailsFragment restaurantDetailsFragment = new RestaurantDetailsFragment();

				FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();

				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				// Replace whatever is in the fragment_container view with this fragment,
				// and add the transaction to the back stack so the user can navigate back
				transaction.replace(R.id.fragment_container, restaurantDetailsFragment);
				transaction.addToBackStack(null);

				// Commit the transaction
				transaction.commit();
			}
		});

		footerView = inflater.inflate(R.layout.listfooter, container, false);

		addLoadingFooter();

		setupAboutPopup(inflater);

		return mainView;
	}

	private MainActivity getMainActivity() {
		return (MainActivity)getActivity();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private void setupAboutPopup(LayoutInflater inflater) {
//		getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		int popupWidth = display.getWidth() - 40;
		int popupHeight = 400;
		aboutPopup = new PopupWindow(inflater.inflate(R.layout.about, null, false), popupWidth, popupHeight, true);
		aboutPopup.setBackgroundDrawable(new BitmapDrawable());
		aboutPopup.setOutsideTouchable(true);
//		Button okButton = aboutPopup.
//		okButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				aboutPopup.dismiss();
//			}
//		});
	}
//
//	@Override
//	protected void onNewIntent(Intent intent) {
//		// Because this activity has set launchMode="singleTop", the system calls this method
//		// to deliver the intent if this activity is currently the foreground activity when
//		// invoked again (when the user executes a search from this activity, we don't create
//		// a new instance of this activity, so the system delivers the search intent here)
//		initialiseResults();
//	}

	@Override
	public void onRestaurantsLoaded(List<Restaurant> restaurants, LatLng location, int startIndex) {
		displayResults(restaurants, location, startIndex);
		Log.i(MimiLog.TAG, "Loaded " + getMainActivity().restaurantList.size() + " results");
		Toast toast = Toast.makeText(getActivity(), "Loaded " + getMainActivity().restaurantList.size() + " results", Toast.LENGTH_SHORT);
		toast.show();

		getMainActivity().loadingResults.set(false); //Unset mutex
	}

	private void displayResults(List<Restaurant> restaurants, LatLng location, int startIndex) {

		updateItemsInListAdapter(restaurants, location, startIndex);
		
		Log.i(MimiLog.TAG, "List has " + listAdapter.getCount() + " elements");

		
//		if (listAdapter.getCount() >= MAX_RESULTS) {
//			removeLoadingFooter();
//		}else{
//			addLoadingFooter();
//		}
	}

	private void addLoadingFooter() {
		if (mListView.getFooterViewsCount() == 0) {
			//mListView.addFooterView(footerView);
			mListView.setOnScrollListener(this);
		}
	}

	private void removeLoadingFooter() {
		mListView.removeFooterView(footerView);
		mListView.setOnScrollListener(null);
	}

	private void updateItemsInListAdapter(List<Restaurant> restaurants, LatLng location, int startIndex) {
		//Remove any existing items in the list from the insert index onwards

		Log.i(MimiLog.TAG, "Updating list with " + restaurants.size() + " restaurants starting at index " + startIndex);

		List<Restaurant> restaurantList = getMainActivity().restaurantList;
		if (restaurantList.size() > startIndex) {
			ListIterator<Restaurant> iter = restaurantList.listIterator(restaurantList.size());
			while(restaurantList.size() > startIndex && iter.hasPrevious()) {
				Restaurant restaurant = iter.previous();
				Log.i(MimiLog.TAG, "Removing restaurant: " + restaurant.name);
				iter.remove();
			}
		}
		
		restaurantList.addAll(restaurants);
		//Set the location so the distance to each restaurant can be calculated
		listAdapter.setLocation(location);
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount > 0) {
			//Load a page of new results onto the end of the existing list
			int startIndex = getMainActivity().restaurantList.size();
			getMainActivity().loadRestaurants(startIndex);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}
