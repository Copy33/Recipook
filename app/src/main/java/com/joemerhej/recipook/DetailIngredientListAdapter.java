package com.joemerhej.recipook;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import java.util.ArrayList;


/**
 * Created by Joe Merhej on 1/25/17.
 */

public class DetailIngredientListAdapter extends RecyclerView.Adapter<DetailIngredientListAdapter.DetailIngredientHolder>
{
    // context (activity)
    private Context mContext;

    // list of ingredients
    public ArrayList<Ingredient> mIngredientList;

    // click listener for the ingredient buttons that activity will implement and assign
    private OnIngredientButtonsClickListener mIngredientButtonsClickListeners;

    // hold the original drawable of the EditTexts
    private ArrayList<Drawable> mOriginalIngredientTextEditTextBackground = new ArrayList<>();
    private ArrayList<Drawable> mOriginalIngredientQuantityEditTextBackground = new ArrayList<>();


    // interface for ingredient buttons listeners
    public interface OnIngredientButtonsClickListener
    {
        void onIngredientDeleteButtonClick(View view, int position);

        void onIngredientAddToShoppingListButtonClick(View view, int position);
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

        }

        @Override
        public void afterTextChanged(Editable s)
        {
            mIngredientList.get(mIngredientPosition).mName = s.toString();
        }
    }

    // listener for ingredient quantity text edit (textwatcher)
    public class MyIngredientQuantityEditTextListener implements TextWatcher
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

        }

        @Override
        public void afterTextChanged(Editable s)
        {
            mIngredientList.get(mIngredientPosition).mQuantity = RecipookTextUtils.Instance().GetQuantityFromQuantityString(s.toString());
            mIngredientList.get(mIngredientPosition).mUnit = RecipookTextUtils.Instance().GetUnitFromQuantityString(s.toString());
        }
    }

    // VIEW HOLDER ------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class DetailIngredientHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        // the views for every ingredient
        public EditText mIngredientQuantity;
        public EditText mIngredientText;
        public ImageButton mEditDeleteIngredientButton;
        public ImageButton mAddToShoppingListButton;

        // the listeners for every ingredient
        public MyIngredientNameEditTextListener mIngredientNameEditTextListener;
        public MyIngredientQuantityEditTextListener mIngredientQuantityEditTextListener;


        public DetailIngredientHolder(View itemView, MyIngredientNameEditTextListener ingredientNameEditTextListener, MyIngredientQuantityEditTextListener ingredientQuantityEditTextListener)
        {
            super(itemView);

            // bind the views
            mIngredientQuantity = (EditText) itemView.findViewById(R.id.recycler_item_detail_ingredient_quantity);
            mIngredientText = (EditText) itemView.findViewById(R.id.recycler_item_detail_ingredient_text);
            mEditDeleteIngredientButton = (ImageButton) itemView.findViewById(R.id.recycler_item_detail_ingredient_delete_button);
            mAddToShoppingListButton = (ImageButton) itemView.findViewById(R.id.recycler_item_detail_ingredient_add_to_shopping_list_button);

            // get the original background drawable for the text edits
            mOriginalIngredientTextEditTextBackground.add(mIngredientText.getBackground());
            mOriginalIngredientQuantityEditTextBackground.add(mIngredientQuantity.getBackground());

            Log.d("DetailIngredientHolder", "Increased position by 1 --- New size is: " + mOriginalIngredientQuantityEditTextBackground.size());

            // bind listeners : delete item fab
            mEditDeleteIngredientButton.setOnClickListener(this);
            mAddToShoppingListButton.setOnClickListener(this);

            // bind listeners : ingredient name text change (passed from parameter, created in onCreateView so we don't have to create listener in onBindViewHolder)
            mIngredientNameEditTextListener = ingredientNameEditTextListener;
            mIngredientText.addTextChangedListener(mIngredientNameEditTextListener);

            // bind listeners : ingredient quantity text change (passed from parameter, created in onCreateView so we don't have to create listener in onBindViewHolder)
            mIngredientQuantityEditTextListener = ingredientQuantityEditTextListener;
            mIngredientQuantity.addTextChangedListener(mIngredientQuantityEditTextListener);
        }

        // implements onClick method
        @Override
        public void onClick(View v)
        {
            if (mIngredientButtonsClickListeners != null)
            {
                if (v.getId() == mEditDeleteIngredientButton.getId())
                    mIngredientButtonsClickListeners.onIngredientDeleteButtonClick(v, getLayoutPosition());

                if (v.getId() == mAddToShoppingListButton.getId())
                    mIngredientButtonsClickListeners.onIngredientAddToShoppingListButtonClick(v, getLayoutPosition());
            }
        }
    }
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    // constructor
    public DetailIngredientListAdapter(Context context, ArrayList<Ingredient> ingredients)
    {
        mContext = context;
        mIngredientList = ingredients;
    }

    // method to update the data used when canceling/discarding changes (like constructor)
    public void updateDataWith(ArrayList<Ingredient> ingredients)
    {
        mIngredientList = ingredients;
    }

    // TODO: fix this memory leak (see onenote for details)
//    // method to remove original background of element from the arrays
//    public void removeBackgroundsOfIngredient(int position)
//    {
//        mOriginalIngredientQuantityEditTextBackground.remove(position);
//        mOriginalIngredientTextEditTextBackground.remove(position);
//
//        Log.d("Removed" ,"Removed at position: " + position + " --- New size is : " + mOriginalIngredientQuantityEditTextBackground.size());
//    }

    // setter for the fab click listener
    public void setIngredientButtonsClickListener(final OnIngredientButtonsClickListener ingredientButtonsClickListener)
    {
        mIngredientButtonsClickListeners = ingredientButtonsClickListener;
    }

    // creates the view holder
    @Override
    public DetailIngredientListAdapter.DetailIngredientHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_detail_ingredient, parent, false);

        // we pass the listeners to the view here so we don't have to created them in onBindViewHolder which is expensive
        return new DetailIngredientHolder(inflatedView, new MyIngredientNameEditTextListener(), new MyIngredientQuantityEditTextListener());
    }

    // called once every time the viewholder wants to fill each row
    @Override
    public void onBindViewHolder(DetailIngredientListAdapter.DetailIngredientHolder holder, int position)
    {
        Log.d("onBindViewHolder", "Binding position: " + position + " --- Size is: " + mOriginalIngredientQuantityEditTextBackground.size());

        // fill the views with data
        Ingredient ingredient = mIngredientList.get(position);

        // parse ingredient unit and quantity with the correct text handling
        String ingredientUnit = RecipookTextUtils.Instance().GetUnitStringFromIngredient(ingredient);
        String ingredientQuantity = RecipookTextUtils.Instance().GetQuantityStringFromIngredient(ingredient);

        // update the listener position so it knows which EditText to listen to for each view, and set that text
        holder.mIngredientNameEditTextListener.updateIngredientPosition(position);

        holder.mIngredientText.setText(ingredient.mName);
        holder.mIngredientText.setSelection(holder.mIngredientText.getText().length());

        holder.mIngredientQuantityEditTextListener.updateIngredientPosition(position);

        holder.mIngredientQuantity.setText(ingredientQuantity + " " + ingredientUnit);
        holder.mIngredientQuantity.setSelection(holder.mIngredientQuantity.getText().length());

        // show the right views depending on edit mode
        if (((RecipeDetailActivity) mContext).mInEditMode)
        {
            holder.mIngredientQuantity.setEnabled(true);
            holder.mIngredientText.setEnabled(true);
            holder.mIngredientText.setBackground(mOriginalIngredientTextEditTextBackground.get(position));
            holder.mIngredientQuantity.setBackground(mOriginalIngredientQuantityEditTextBackground.get(position));
            holder.mEditDeleteIngredientButton.setVisibility(View.VISIBLE);
            holder.mAddToShoppingListButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.mIngredientQuantity.setEnabled(false);
            holder.mIngredientText.setEnabled(false);
            holder.mIngredientText.setBackgroundColor(Color.TRANSPARENT);
            holder.mIngredientQuantity.setBackgroundColor(Color.TRANSPARENT);
            holder.mEditDeleteIngredientButton.setVisibility(View.INVISIBLE);
            holder.mAddToShoppingListButton.setVisibility(View.VISIBLE);
        }
    }

    // every adapter needs this method
    @Override
    public int getItemCount()
    {
        return mIngredientList.size();
    }
}




















