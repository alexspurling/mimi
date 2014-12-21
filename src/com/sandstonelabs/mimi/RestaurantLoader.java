package com.sandstonelabs.mimi;

import android.content.Context;
import android.content.Loader;

/**
 * Created by alex on 20/12/14.
 */
public class RestaurantLoader extends Loader<Restaurant> {
    /**
     * Stores away the application context associated with context.
     * Since Loaders can be used across multiple activities it's dangerous to
     * store the context directly; always use {@link #getContext()} to retrieve
     * the Loader's Context, don't use the constructor argument directly.
     * The Context returned by {@link #getContext} is safe to use across
     * Activity instances.
     *
     * @param context used to retrieve the application context.
     */
    public RestaurantLoader(Context context) {
        super(context);
    }
}
