package com.joemerhej.recipook;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;


/**
 * Created by Joe Merhej on 3/18/17.
 */

public class ShoppingListTabFragment extends Fragment
{
    // parent activity of this fragment
    public Activity mParentActivity;

    // shopping list of activity, this is a shallow copy of RecipeData
    public ArrayList<Ingredient> mShoppingIngredientList;

    // views : toolbar area
    private ImageView mHeaderImageView;

    // views : autocomplete view
    private MultiAutoCompleteTextView mAutoCompleteTextView;

    // views : the shopping ingredients recycler view
    private RecyclerView mShoppingIngredientsRecyclerView;
    private ShoppingIngredientListAdapter mShoppingIngredientListAdapter;
    private ShoppingIngredientListAdapter.OnShoppingIngredientClickListener mShoppingIngredientClickListener = new ShoppingIngredientListAdapter.OnShoppingIngredientClickListener()
    {
        @Override
        public void onIngredientClick(View view, int position)
        {
            // mark ingredient checked if it isn't
            Ingredient clickedIngredient = mShoppingIngredientList.get(position);
            if (clickedIngredient.mShoppingStatus != ShoppingStatus.CHECKED)
            {
                clickedIngredient.mShoppingStatus = ShoppingStatus.CHECKED;
                mShoppingIngredientListAdapter.notifyItemChanged(position);
            }
            // if it is checked, uncheck it
            else
            {
                clickedIngredient.mShoppingStatus = ShoppingStatus.ADDED;
                mShoppingIngredientListAdapter.notifyItemChanged(position);
            }
        }
    };


    // every fragment requires a default constructor and a Instance method
    public ShoppingListTabFragment()
    {
    }

    // returns a ShoppingListTabFragment instance
    public static ShoppingListTabFragment Instance()
    {
        return new ShoppingListTabFragment();
    }

    // creates the view of the fragment
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // inflate fragment view
        final View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        // get activity (parent) to use when setting the recipe list adapter
        mParentActivity = getActivity();

        // set up the main shopping list from the RecipeData (shallow copy)
        mShoppingIngredientList = RecipeData.Instance().getShoppingIngredientList();

        // set up the toolbar views
        mHeaderImageView = (ImageView) view.findViewById(R.id.shopping_header_image_view);
        Glide.with(this)
                .load(R.drawable.shoppinglistheaderbackground)
                .into(mHeaderImageView);

        // set up shopping ingredients list recycler view
        mShoppingIngredientsRecyclerView = (RecyclerView) view.findViewById(R.id.shopping_ingredients_list);
        mShoppingIngredientListAdapter = new ShoppingIngredientListAdapter(mParentActivity, mShoppingIngredientList);
        mShoppingIngredientsRecyclerView.setAdapter(mShoppingIngredientListAdapter);
        mShoppingIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(mParentActivity));
        mShoppingIngredientListAdapter.setOnShoppingIngredientClickListener(mShoppingIngredientClickListener);

        return view;
    }
}
