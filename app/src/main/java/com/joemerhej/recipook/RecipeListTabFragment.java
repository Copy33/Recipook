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
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Joe Merhej on 1/21/17.
 */

public class RecipeListTabFragment extends Fragment
{
    // the layout manager used to change quantity of columns...
    public static StaggeredGridLayoutManager mStaggeredLayoutManager;

    // adapter of the recipe list
    public RecipeListAdapter mRecipeListAdapter;

    // will return from RecipeDetailActivity
    private int RECIPE_DETAIL_RESULT_CODE;


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
        mRecipeListAdapter = new RecipeListAdapter(activity);
        recyclerView.setAdapter(mRecipeListAdapter);
        mRecipeListAdapter.setOnRecipeItemClickListener(onRecipeItemClickListener, RecipeData.Instance().getRecipeList());

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
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, position);
            startActivityForResult(intent, RECIPE_DETAIL_RESULT_CODE);
        }
    };

    // this method will be called when intents triggered with "startActivityForResult" come back to this activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        try
        {
            // check if it's the intent to change the toolbar image
            if (requestCode == RECIPE_DETAIL_RESULT_CODE && resultCode == RESULT_OK && data != null)
            {
                int recipePosition = data.getIntExtra("recipePosition", 0);
                mRecipeListAdapter.notifyItemChanged(recipePosition);
            }

            // check if it's the intent to change the toolbar image
            if (requestCode == RECIPE_DETAIL_RESULT_CODE && resultCode == RecipookIntentResult.RESULT_RECIPE_DELETED && data != null)
            {
                int recipePosition = data.getIntExtra("recipePosition", 0);
                mRecipeListAdapter.notifyItemRemoved(recipePosition);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this.getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }
}
