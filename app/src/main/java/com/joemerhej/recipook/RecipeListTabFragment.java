package com.joemerhej.recipook;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Joe Merhej on 1/21/17.
 */

public class RecipeListTabFragment extends Fragment
{
    // the layout manager used to change quantity of columns...
    public static StaggeredGridLayoutManager mStaggeredLayoutManager;

    // adapter of the recipe list
    public RecipeListAdapter mAdapter;


    // every fragment requires a default constructor and a newInstance method
    public RecipeListTabFragment()
    {
    }

    // returns a RecipeListTabFragment instance
    public static RecipeListTabFragment newInstance()
    {
        Bundle args = new Bundle();

        RecipeListTabFragment fragment = new RecipeListTabFragment();
        fragment.setArguments(args);

        return fragment;
    }

    // creates the view of the fragment
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        // get activity (parent) to use as context when setting layout manager of the recycler view
        //                                     and when setting the recipe list adapter
        final Activity activity = getActivity();
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recipe_recycler_view);

        // set the layout manager
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mStaggeredLayoutManager);

        // set the adapter, and add the click listener
        mAdapter = new RecipeListAdapter(activity);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnRecipeItemClickListener(onRecipeItemClickListener);

        return view;
    }

    // Implement the click listener for every recipe list item
    RecipeListAdapter.OnRecipeItemSelected onRecipeItemClickListener = new RecipeListAdapter.OnRecipeItemSelected()
    {
        @Override
        public void OnRecipeItemClicked(View view, int position)
        {
            // clicking a recipe item will launch the recipe detail activity
            Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_PARAM_ID, position);
            startActivity(intent);
        }
    };
}
