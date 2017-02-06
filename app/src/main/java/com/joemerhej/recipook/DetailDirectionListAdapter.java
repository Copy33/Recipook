package com.joemerhej.recipook;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

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
    private OnItemFabClickListener mFabClickListener;

    // interface that activities that use this need to implement
    public interface OnItemFabClickListener
    {
        void onItemFabClick(View view, int position);
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

    // view holder class that this adapter needs
    public class DetailDirectionHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        // the views for every direction
        public TextView mDirectionNumber;
        public EditText mDirectionText;
        public FloatingActionButton mEditRemoveDirectionFab;

        // the listeners for every direction
        public MyDirectionTextEditTextListener mDirectionTextEditTextListener;

        public DetailDirectionHolder(View itemView, MyDirectionTextEditTextListener directionTextEditTextListener)
        {
            super(itemView);

            // bind the views
            mDirectionNumber = (TextView) itemView.findViewById(R.id.recycler_item_direction_number);
            mDirectionText = (EditText) itemView.findViewById(R.id.recycler_item_direction_text);
            mEditRemoveDirectionFab = (FloatingActionButton) itemView.findViewById(R.id.recipe_item_remove_direction_fab);

            // bind listeners : delete fab item
            mEditRemoveDirectionFab.setOnClickListener(this);

            // bind listeners : direction text text change
            mDirectionTextEditTextListener = directionTextEditTextListener;
            mDirectionText.addTextChangedListener(mDirectionTextEditTextListener);
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
    public void setOnItemFabClickListener(final OnItemFabClickListener itemFabClickListener)
    {
        mFabClickListener = itemFabClickListener;
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
            holder.mEditRemoveDirectionFab.setVisibility(View.VISIBLE);
            holder.mDirectionText.setEnabled(true);
        }
        else
        {
            holder.mEditRemoveDirectionFab.setVisibility(View.GONE);
            holder.mDirectionText.setEnabled(false);
        }
    }

    // every adapter needs this method
    @Override
    public int getItemCount()
    {
        return mDirectionsList.size();
    }
}
