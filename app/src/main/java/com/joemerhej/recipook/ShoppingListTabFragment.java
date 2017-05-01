package com.joemerhej.recipook;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


/**
 * Created by Joe Merhej on 3/18/17.
 */

public class ShoppingListTabFragment extends Fragment
{
    // parent activity of this fragment
    public Activity mParentActivity;

    // shopping list of activity, this is a shallow copy from RecipeData
    public ArrayList<Ingredient> mShoppingIngredientList;

    // views : header area - header image, add ingredient views
    private ImageView mHeaderImageView;
    private ImageButton mAddIngredientButton;
    private EditText mAddIngredientEditText;

    // views : the shopping ingredients recycler view
    private RecyclerView mShoppingIngredientsRecyclerView;
    private ShoppingIngredientListAdapter mShoppingIngredientListAdapter;


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // CONSTRUCTOR AND INSTANCE METHODS - Every fragment requires both
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public ShoppingListTabFragment()
    {
    }

    public static ShoppingListTabFragment Instance(int position)
    {
        ShoppingListTabFragment fragment = new ShoppingListTabFragment();

        Bundle args = new Bundle();
        args.putInt("TabPosition", position);
        fragment.setArguments(args);

        return fragment;
    }


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // FRAGMENT CREATE FUNCTION : Creates the view
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

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

        // set up the header area views
        mHeaderImageView = (ImageView) view.findViewById(R.id.shopping_header_image_view);
        Glide.with(this)
                .load(R.drawable.shoppinglistheaderbackground)
                .into(mHeaderImageView);
        mAddIngredientButton = (ImageButton) view.findViewById(R.id.shopping_list_add_ingredient_button);
        mAddIngredientButton.setOnClickListener(mAddIngredientButtonClickListener);
        mAddIngredientEditText = (EditText) view.findViewById(R.id.shopping_list_add_ingredient_edit_text);
        mAddIngredientEditText.addTextChangedListener(new MyAddIngredientEditTextWatcher());

        // set up shopping ingredients list recycler view
        mShoppingIngredientsRecyclerView = (RecyclerView) view.findViewById(R.id.shopping_list_ingredients_list);
        mShoppingIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(mParentActivity));

        return view;
    }


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // FRAGMENT RESUME FUNCTION : set up the shopping list adapter since other activities/fragments will modify the shopping list
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onResume()
    {
        super.onResume();

        // set up shopping ingredients list recycler view
        mShoppingIngredientListAdapter = new ShoppingIngredientListAdapter(mParentActivity, mShoppingIngredientList);
        mShoppingIngredientsRecyclerView.setAdapter(mShoppingIngredientListAdapter);
        mShoppingIngredientListAdapter.setOnShoppingIngredientClickListener(mShoppingIngredientClickListener);
    }


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // CLICK LISTENERS : Ingredient click listener
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

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


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // CLICK LISTENERS : Add Ingredient Button
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private ImageView.OnClickListener mAddIngredientButtonClickListener = new ImageView.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String newIngredientText = mAddIngredientEditText.getText().toString();
            newIngredientText = newIngredientText.trim();

            if (newIngredientText.isEmpty())
                return;

            mShoppingIngredientsRecyclerView.scrollToPosition(0);

            Ingredient newIngredient = RecipookTextUtils.Instance().GetIngredientFromIngredientString(newIngredientText);
            if (newIngredient != null)
            {
                newIngredient.mShoppingStatus = ShoppingStatus.ADDED;
                mShoppingIngredientList.add(0, newIngredient);
                mShoppingIngredientListAdapter.notifyItemInserted(0);
                mAddIngredientEditText.getText().clear();
            }
        }
    };


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // CLICK LISTENERS : Main Fab - main activity will set this
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public MainActivity.OnMainFabClickListener mMainFabClickListener = new MainActivity.OnMainFabClickListener()
    {
        @Override
        public void onMainFabClick()
        {
            int originalSize = mShoppingIngredientList.size();

            // remove the checked ingredients from the shopping list and notify the adapter
            for (int i = mShoppingIngredientList.size() - 1; i >= 0; --i)
            {
                if (mShoppingIngredientList.get(i).mShoppingStatus == ShoppingStatus.CHECKED)
                {
                    mShoppingIngredientList.get(i).mShoppingStatus = ShoppingStatus.NONE;
                    mShoppingIngredientList.remove(i);
                    mShoppingIngredientListAdapter.notifyItemRemoved(i);
                }
            }

            int postSize = mShoppingIngredientList.size();

            if (postSize == originalSize)
                Toast.makeText(mParentActivity, getResources().getString(R.string.shopping_list_clear_notification_text_empty), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(mParentActivity, getResources().getString(R.string.shopping_list_clear_notification_text_non_empty), Toast.LENGTH_SHORT).show();
        }
    };


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ADD INGREDIENT TEXTWATCHER
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // text watcher to be attached to the add ingredient editText
    private class MyAddIngredientEditTextWatcher implements TextWatcher
    {
        boolean editing = false;

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
            if (editing)
                return;

            int[] qunIndices = RecipookTextUtils.Instance().GetQUNIndicesFromIngredientString(s.toString());
            int length = s.length();
            int colorStartIndex = 0;
            int colorEndIndex = 0;

            // if there are no quantity in the beginning...
            if (qunIndices[0] == -1)
            {
                // ...and no unit, this will keep indices at 0 and color everything black
                if (qunIndices[1] == -1)
                {
                }
                // ...and valid unit
                else
                {
                    // ...and no name, this will color the whole text.
                    if (qunIndices[2] == -1)
                        colorEndIndex = length;
                        // ...and a name
                    else
                        // this will color only the unit
                        colorEndIndex = qunIndices[2];
                }
            }
            // else if there is a valid quantity but no unit...
            else if (qunIndices[1] == -1)
            {
                // ...and no name, this will color the whole text.
                if (qunIndices[2] == -1)
                    colorEndIndex = length;
                    // ...and a valid name, this will color quantity.
                else
                    colorEndIndex = qunIndices[2];
            }
            // else if there's a valid quantity and a valid unit...
            else
            {
                // and no name, this will color the whole text.
                if (qunIndices[2] == -1)
                    colorEndIndex = length;
                    // and a valid name, this will color quantity and unit.
                else
                    colorEndIndex = qunIndices[2];
            }

            // set the primary color and text color spannables and apply them
            int green = ContextCompat.getColor(mParentActivity, R.color.colorPrimary);
            int black = ContextCompat.getColor(mParentActivity, R.color.textColorPrimary);
            Spannable sectionToSpan = new SpannableString(s.toString());
            sectionToSpan.setSpan(new ForegroundColorSpan(green), colorStartIndex, colorEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sectionToSpan.setSpan(new ForegroundColorSpan(black), colorEndIndex, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            editing = true;
            int startSelection = mAddIngredientEditText.getSelectionStart();
            mAddIngredientEditText.setText(sectionToSpan);
            mAddIngredientEditText.setSelection(startSelection);
            editing = false;
        }
    }


}

























