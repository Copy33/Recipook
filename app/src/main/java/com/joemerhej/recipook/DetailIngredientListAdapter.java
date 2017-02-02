package com.joemerhej.recipook;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import java.util.ArrayList;

/**
 * Created by Joe Merhej on 1/25/17.
 */

public class DetailIngredientListAdapter extends RecyclerView.Adapter<DetailIngredientListAdapter.DetailIngredientHolder>
{
    // context (activity)
    private Context mContext;

    // list of ingredients
    public ArrayList<Ingredient> mIngredientsList;

    private OnItemFabClickListener mFabClickListener;

    // interface that activities that use this need to implement
    public interface OnItemFabClickListener
    {
        void onItemFabClick(View view, int position);
    }

    // listener for ingredient name text edit (textwatcher)
    public class MyIngredientNameEditTextListener implements TextWatcher
    {
        // position of the view in the list
        private int mIngredientPosition;


        public void updateIngredientPosition(int position)
        {
            mIngredientPosition = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            mIngredientsList.get(mIngredientPosition).name = s.toString();
        }

        @Override
        public void afterTextChanged(Editable s)
        {

        }
    }

    // view holder class that each adapter needs
    public class DetailIngredientHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        // the views for every ingredient
        public EditText mIngredientQuantity;
        public EditText mIngredientText;
        public FloatingActionButton mEditRemoveIngredientFab;

        // the listeners for every ingredient
        public MyIngredientNameEditTextListener mIngredientNameEditTextListener;

        public DetailIngredientHolder(View itemView, MyIngredientNameEditTextListener ingredientNameEditTextListener)
        {
            super(itemView);

            // bind the views
            mIngredientQuantity = (EditText) itemView.findViewById(R.id.recycler_item_ingredient_quantity);
            mIngredientText = (EditText) itemView.findViewById(R.id.recycler_item_ingredient_text);
            mEditRemoveIngredientFab = (FloatingActionButton) itemView.findViewById(R.id.recycler_item_remove_ingredient_fab);

            // bind listeners : delete item fab
            mEditRemoveIngredientFab.setOnClickListener(this);

            // bind listeners : ingredient name text change
            mIngredientNameEditTextListener = ingredientNameEditTextListener;
            mIngredientText.addTextChangedListener(mIngredientNameEditTextListener);
        }

        @Override
        public void onClick(View v)
        {
            if(mFabClickListener != null)
            {
                mFabClickListener.onItemFabClick(v, getLayoutPosition());
            }
        }
    }


    // constructor
    public DetailIngredientListAdapter(Context context, ArrayList<Ingredient> ingredients)
    {
        mContext = context;
        mIngredientsList = ingredients;
    }

    // method to update the data used when canceling/discarding changes (like constructor)
    public void UpdateDataWith(ArrayList<Ingredient> ingredients)
    {
        mIngredientsList = ingredients;
    }

    // setter for the fab click listener
    public void setOnItemFabClickListener(final OnItemFabClickListener itemFabClickListener)
    {
        mFabClickListener = itemFabClickListener;
    }

    // creates the view holder
    @Override
    public DetailIngredientListAdapter.DetailIngredientHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_detail_ingredient, parent, false);

        // we pass the listeners to the view here so we don't have to created them in onBindViewHolder which is expensive
        return new DetailIngredientHolder(inflatedView, new MyIngredientNameEditTextListener());
    }

    // called once every time the viewholder wants to fill each row
    @Override
    public void onBindViewHolder(DetailIngredientListAdapter.DetailIngredientHolder holder, int position)
    {
        // fill the views with data
        Ingredient ingredient = mIngredientsList.get(position);

        // parse ingredient unit and quantity with the correct handling
        String ingredientUnit = RecipookParser.Instance().GetUnitStringFromIngredient(ingredient);
        String ingredientQuantity = RecipookParser.Instance().GetQuantityStringFromIngredient(ingredient);

        holder.mIngredientQuantity.setText(ingredientQuantity + " " + ingredientUnit);

        // update the listener position so it knows which EditText to listen to for each view, and set that text
        holder.mIngredientNameEditTextListener.updateIngredientPosition(position);
        holder.mIngredientText.setText(ingredient.name);

        // show the right views depending on edit mode
        if(((RecipeDetailActivity)mContext).mInEditMode)
        {
            holder.mEditRemoveIngredientFab.setVisibility(View.VISIBLE);
            holder.mIngredientQuantity.setEnabled(true);
            holder.mIngredientText.setEnabled(true);
        }
        else
        {
            holder.mEditRemoveIngredientFab.setVisibility(View.GONE);
            holder.mIngredientQuantity.setEnabled(false);
            holder.mIngredientText.setEnabled(false);
        }
    }

    // every adapter needs this method
    @Override
    public int getItemCount()
    {
        return mIngredientsList.size();
    }
}




















