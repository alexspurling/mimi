package com.sandstonelabs.mimi;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {

	private final Context context;
	private final List<String> values;
	
	private int[] redStarImages = new int[] {
			R.drawable.redforkandspoon_small_1,
			R.drawable.redforkandspoon_small_2,
			R.drawable.redforkandspoon_small_3,
			R.drawable.redforkandspoon_small_4,
			R.drawable.redforkandspoon_small_5};
	
	private final Random random = new Random();

	public MySimpleArrayAdapter(Context context, List<String> values) {
		super(context, R.layout.result, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.result, parent, false);

		TextView textView = (TextView) rowView.findViewById(R.id.word);
		textView.setText(values.get(position));

		ImageView imageView = (ImageView) rowView.findViewById(R.id.stars1);
		int starRating = random.nextInt(5) + 1;

		imageView.setImageResource(redStarImages[starRating-1]);
		imageView.getLayoutParams().width = 13 * starRating;
		
		return rowView;
	}
}