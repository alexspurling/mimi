/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sandstonelabs.mimi;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HeadlinesFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We need to use a different list item layout for devices older than Honeycomb
        int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;

        // Create an array adapter for the list view, using the Ipsum headlines array
        setListAdapter(new ArrayAdapter<String>(getActivity(), layout, Ipsum.Headlines));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Notify the parent activity of selected item
        onArticleSelected(position);
        
        // Set the item as checked to be highlighted when in two-pane layout
        getListView().setItemChecked(position, true);
    }

    public void onArticleSelected(int position) {
        // The user selected the headline of an article from the HeadlinesFragment

        // Create fragment and give it an argument for the selected article
        ArticleFragment newFragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putInt(ArticleFragment.ARG_POSITION, position);
        newFragment.setArguments(args);
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }
}