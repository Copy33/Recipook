package com.joemerhej.recipook;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Joe Merhej on 3/18/17.
 */

public class ShoppingIngredientListAdapter extends RecyclerView.Adapter<ShoppingIngredientListAdapter.ShoppingIngredientListViewHolder>
{
    // context of the shopping ingredient list
    private Context mContext;

    // list of all the shopping ingredients
    private ArrayList<Ingredient> mShoppingIngredientsList;


    // VIEW HOLDER ------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class ShoppingIngredientListViewHolder extends RecyclerView.ViewHolder
    {
        // all the views of each row item
        public CheckedTextView mShoppingIngredientText;
        public ImageView mShoppingIngredientImage;


        // constructor for the view holder
        public ShoppingIngredientListViewHolder(View itemView)
        {
            super(itemView);

            // bind the views
            mShoppingIngredientText = (CheckedTextView) itemView.findViewById(R.id.recycler_item_shopping_ingredient_text);
            mShoppingIngredientImage = (ImageView) itemView.findViewById(R.id.recycler_item_shopping_ingredient_picture);
        }
    }
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    // constructor of adapter
    public ShoppingIngredientListAdapter(Context context, ArrayList<Ingredient> shoppingIngredientsList)
    {
        mContext = context;
        mShoppingIngredientsList = shoppingIngredientsList;
    }

    // creates the view holder
    @Override
    public ShoppingIngredientListAdapter.ShoppingIngredientListViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_item_shopping_ingredient, parent, false);
        return new ShoppingIngredientListViewHolder(view);
    }

    // this will bind the data every time a new row is created
    @Override
    public void onBindViewHolder(ShoppingIngredientListAdapter.ShoppingIngredientListViewHolder holder, int position)
    {
        // get the ingredient from data
        Ingredient ingredient = mShoppingIngredientsList.get(position);

        // set the ingredient name
        holder.mShoppingIngredientText.setText(ingredient.name);

        // TODO: set the ingredient image with Glide
    }

    // every adapter needs this method
    @Override
    public int getItemCount()
    {
        return mShoppingIngredientsList.size();
    }
}
