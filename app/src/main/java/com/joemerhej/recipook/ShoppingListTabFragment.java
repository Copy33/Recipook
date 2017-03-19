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


/**
 * Created by Joe Merhej on 3/18/17.
 */

public class ShoppingListTabFragment extends Fragment
{
    // parent activity of this fragment
    public Activity mParentActivity;

    // views : the shopping ingredients recycler view
    public RecyclerView mShoppingIngredientsRecyclerView;
    public ShoppingIngredientListAdapter mShoppingIngredientListAdapter;


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

        // set up shopping ingredients list recycler view
        mShoppingIngredientsRecyclerView = (RecyclerView) view.findViewById(R.id.shopping_ingredients_list);
        mShoppingIngredientListAdapter = new ShoppingIngredientListAdapter(mParentActivity, RecipeData.Instance().getShoppingIngredientList());
        mShoppingIngredientsRecyclerView.setAdapter(mShoppingIngredientListAdapter);
        mShoppingIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(mParentActivity));

        return view;
    }
}
