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

import static android.R.attr.fragment;
import static android.app.Activity.RESULT_OK;


/**
 * Created by Joe Merhej on 1/21/17.
 */

public class RecipeListTabFragment extends Fragment
{
    // parent activity of this fragment
    public Activity mParentActivity;

    // the layout manager used to change quantity of columns...
    public StaggeredGridLayoutManager mStaggeredLayoutManager;

    // the recipes list
    public RecyclerView mRecipesList;

    // adapter of the recipe list
    public RecipeListAdapter mRecipeListAdapter;

    // main activity's main fab click listener for this fragment
    public MainActivity.OnMainFabClickListener mMainFabClickListener = new MainActivity.OnMainFabClickListener()
    {
        @Override
        public void onMainFabClick()
        {
            Toast.makeText(mParentActivity,"from recipe list",Toast.LENGTH_SHORT).show();
        }
    };

    public MainActivity.OnMainFabClickListener getMainFabClickListener()
    {
        return mMainFabClickListener;
    }


    // every fragment requires a default constructor and a Instance method
    public RecipeListTabFragment()
    {
    }

    // returns a RecipeListTabFragment instance
    public static RecipeListTabFragment Instance(int position)
    {
        RecipeListTabFragment fragment = new RecipeListTabFragment();

        Bundle args = new Bundle();
        args.putInt("TabPosition", position);
        fragment.setArguments(args);

        return fragment;
    }

    // creates the view of the fragment
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // inflate fragment view
        final View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        // get activity (parent) to use when setting the recipe list adapter
        mParentActivity = getActivity();

        // set up recipes list recycler view
        mRecipesList = (RecyclerView) view.findViewById(R.id.recipe_recycler_view);

        // set the layout manager
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecipesList.setLayoutManager(mStaggeredLayoutManager);

        // set the adapter, and add the click listener
        mRecipeListAdapter = new RecipeListAdapter(mParentActivity, RecipeData.Instance().getRecipeList());
        mRecipesList.setAdapter(mRecipeListAdapter);
        mRecipeListAdapter.setOnRecipeItemClickListener(onRecipeItemClickListener);

        return view;
    }

    // Implement the click listener for every recipe list item
    RecipeListAdapter.OnRecipeItemClickListener onRecipeItemClickListener = new RecipeListAdapter.OnRecipeItemClickListener()
    {
        @Override
        public void OnRecipeItemClicked(View view, int position)
        {
            // clicking a recipe item will launch the recipe detail activity
            Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, position);
            startActivityForResult(intent, RecipookIntentResult.RESULT_RECIPE_MODIFIED);
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
            if (requestCode == RecipookIntentResult.RESULT_RECIPE_MODIFIED && resultCode == RESULT_OK && data != null)
            {
                int recipePosition = data.getIntExtra("recipePosition", 0);
                mRecipeListAdapter.notifyItemChanged(recipePosition);
            }

            // check if it's the intent to change the toolbar image
            if (requestCode == RecipookIntentResult.RESULT_RECIPE_MODIFIED && resultCode == RecipookIntentResult.RESULT_RECIPE_DELETED && data != null)
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
