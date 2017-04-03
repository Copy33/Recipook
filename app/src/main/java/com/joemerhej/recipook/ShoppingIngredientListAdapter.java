package com.joemerhej.recipook;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Joe Merhej on 3/18/17.
 */

public class ShoppingIngredientListAdapter extends RecyclerView.Adapter<ShoppingIngredientListAdapter.ShoppingIngredientListViewHolder>
{
    // context of the shopping ingredient list
    private Context mContext;

    // list of all the shopping ingredients
    private ArrayList<Ingredient> mShoppingIngredientList;

    // click listener instance for the shopping ingredient that parent will implement and assign
    private OnShoppingIngredientClickListener mShoppingIngredientClickListener;


    // interface for shopping ingredient listener
    public interface OnShoppingIngredientClickListener
    {
        void onIngredientClick(View view, int position);
    }


    // VIEW HOLDER ------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class ShoppingIngredientListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        // all the views of each row item
        public RelativeLayout mShoppingIngredientLayout;
        public TextView mShoppingIngredientQuantity;
        public TextView mShoppingIngredientText;
        public ImageView mShoppingIngredientImage;


        // constructor for the view holder
        public ShoppingIngredientListViewHolder(View itemView)
        {
            super(itemView);

            // bind the views
            mShoppingIngredientLayout = (RelativeLayout) itemView.findViewById(R.id.recycler_item_shopping_ingredient_holder);
            mShoppingIngredientQuantity = (TextView) itemView.findViewById(R.id.recycler_item_shopping_ingredient_quantity);
            mShoppingIngredientText = (TextView) itemView.findViewById(R.id.recycler_item_shopping_ingredient_text);
            mShoppingIngredientImage = (ImageView) itemView.findViewById(R.id.recycler_item_shopping_ingredient_picture);

            // need to set the click listener
            mShoppingIngredientLayout.setOnClickListener(this);
        }

        // implements onClick method
        @Override
        public void onClick(View v)
        {
            // when clicked, call the OnShoppingIngredientListener callback method on the right viewholder position
            if (mShoppingIngredientClickListener != null)
            {
                mShoppingIngredientClickListener.onIngredientClick(v, getLayoutPosition());
            }
        }
    }
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    // constructor of adapter
    public ShoppingIngredientListAdapter(Context context, ArrayList<Ingredient> shoppingIngredientsList)
    {
        mContext = context;
        mShoppingIngredientList = shoppingIngredientsList;
    }

    // setter for the mShoppingListClickListener
    public void setOnShoppingIngredientClickListener(final OnShoppingIngredientClickListener itemClickListener)
    {
        mShoppingIngredientClickListener = itemClickListener;
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
        Ingredient ingredient = mShoppingIngredientList.get(position);

        // parse ingredient unit and quantity with the correct text handling
        String ingredientUnit = RecipookTextUtils.Instance().GetUnitStringFromIngredient(ingredient);
        String ingredientQuantity = RecipookTextUtils.Instance().GetQuantityStringFromIngredient(ingredient);

        holder.mShoppingIngredientQuantity.setText(ingredientQuantity + " " + ingredientUnit);

        // set the ingredient name
        holder.mShoppingIngredientText.setText(ingredient.mName);

        // set the right styling depending on shopping status
        int grey = ContextCompat.getColor(mContext, R.color.categoryColorDisabled);
        int textColorPrimary = ContextCompat.getColor(mContext, R.color.textColorPrimary);
        int colorPrimary = ContextCompat.getColor(mContext, R.color.colorPrimary);

        if(mShoppingIngredientList.get(position).mShoppingStatus == ShoppingStatus.CHECKED)
        {
            holder.mShoppingIngredientQuantity.setTextColor(grey);
            holder.mShoppingIngredientQuantity.getPaint().setStrikeThruText(true);
            holder.mShoppingIngredientText.setTextColor(grey);
            holder.mShoppingIngredientText.getPaint().setStrikeThruText(true);
        }
        else
        {
            holder.mShoppingIngredientQuantity.setTextColor(colorPrimary);
            holder.mShoppingIngredientQuantity.getPaint().setStrikeThruText(false);
            holder.mShoppingIngredientText.setTextColor(textColorPrimary);
            holder.mShoppingIngredientText.getPaint().setStrikeThruText(false);
        }

        // TODO: set the ingredient image with Glide

    }

    // every adapter needs this method
    @Override
    public int getItemCount()
    {
        return mShoppingIngredientList.size();
    }
}
