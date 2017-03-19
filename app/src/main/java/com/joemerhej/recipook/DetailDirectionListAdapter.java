package com.joemerhej.recipook;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Joe Merhej on 1/25/17.
 */

public class DetailDirectionListAdapter extends RecyclerView.Adapter<DetailDirectionListAdapter.DetailDirectionHolder>
{
    // context (activity)
    Context mContext;

    // list of directions
    private ArrayList<String> mDirectionsList;

    // click listener for the fabs that activity will deal with
    private OnDirectionButtonsClickListener mDirectionButtonsClickListener;

    // hold the original drawable of the EditTexts
    private ArrayList<Drawable> mOriginalDirectionEditTextBackground = new ArrayList<>();


    // interface that activities that use this need to implement
    public interface OnDirectionButtonsClickListener
    {
        void onDirectionDeleteButtonClick(View view, int position);
    }

    // listener for direction text edit text (textwatcher)
    public class MyDirectionTextEditTextListener implements TextWatcher
    {
        // position of the view in the list
        private int mDirectionPosition;

        public void updateDirectionPosition(int position)
        {
            mDirectionPosition = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            mDirectionsList.set(mDirectionPosition, s.toString());
        }

        @Override
        public void afterTextChanged(Editable s)
        {

        }
    }

    // VIEW HOLDER ------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class DetailDirectionHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        // the views for every direction
        public TextView mDirectionNumber;
        public EditText mDirectionText;
        public ImageButton mEditDeleteDirectionButton;

        // the listeners for every direction
        public MyDirectionTextEditTextListener mDirectionTextEditTextListener;

        public DetailDirectionHolder(View itemView, MyDirectionTextEditTextListener directionTextEditTextListener)
        {
            super(itemView);

            // bind the views
            mDirectionNumber = (TextView) itemView.findViewById(R.id.recycler_item_detail_direction_edit_number);
            mDirectionText = (EditText) itemView.findViewById(R.id.recycler_item_detail_direction_text);
            mEditDeleteDirectionButton = (ImageButton) itemView.findViewById(R.id.recycler_item_detail_direction_delete_button);

            // get the original background drawable for the text edits
            mOriginalDirectionEditTextBackground.add(mDirectionText.getBackground());

            // bind listeners : delete direction button
            mEditDeleteDirectionButton.setOnClickListener(this);

            // bind listeners : direction text text change
            mDirectionTextEditTextListener = directionTextEditTextListener;
            mDirectionText.addTextChangedListener(mDirectionTextEditTextListener);
        }

        @Override
        public void onClick(View v)
        {
            if(mDirectionButtonsClickListener != null)
            {
                mDirectionButtonsClickListener.onDirectionDeleteButtonClick(v, getLayoutPosition());
            }
        }
    }
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    // constructor
    public DetailDirectionListAdapter(Context context, ArrayList<String> directions)
    {
        mContext = context;
        mDirectionsList = directions;
    }

    // method to update the data used when canceling/discarding changes (like constructor)
    public void UpdateDataWith(ArrayList<String> directions)
    {
        mDirectionsList = directions;
    }

    // setter for fab click listener
    public void setDirectionButtonsClickListener(final OnDirectionButtonsClickListener itemFabClickListener)
    {
        mDirectionButtonsClickListener = itemFabClickListener;
    }

    //creates the view holder from the inflated view
    @Override
    public DetailDirectionListAdapter.DetailDirectionHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create the view holder from the inflated view
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_detail_direction, parent, false);

        return new DetailDirectionHolder(inflatedView, new MyDirectionTextEditTextListener());
    }

    // called once for every row
    @Override
    public void onBindViewHolder(DetailDirectionListAdapter.DetailDirectionHolder holder, int position)
    {
        // fill the views with data
        String direction = mDirectionsList.get(position);

        holder.mDirectionTextEditTextListener.updateDirectionPosition(position);
        holder.mDirectionText.setText(direction);

        holder.mDirectionNumber.setText(String.valueOf(position+1) + ".");

        // show/hide the right views depending on edit mode
        if(((RecipeDetailActivity)mContext).mInEditMode)
        {
            holder.mEditDeleteDirectionButton.setVisibility(View.VISIBLE);
            holder.mDirectionText.setEnabled(true);
            holder.mDirectionText.setBackground(mOriginalDirectionEditTextBackground.get(position));
        }
        else
        {
            holder.mEditDeleteDirectionButton.setVisibility(View.GONE);
            holder.mDirectionText.setEnabled(false);
            holder.mDirectionText.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    // every adapter needs this method
    @Override
    public int getItemCount()
    {
        return mDirectionsList.size();
    }
}
