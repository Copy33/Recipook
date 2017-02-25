package com.joemerhej.recipook;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

/**
 * Created by Joe Merhej on 1/22/17.
 */

// adapter needed for the recipe list recycler view, it also needs a view holder inside it
public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeListViewHolder>
{
    // context
    private Context mContext;

    // list of all recipes
    private ArrayList<Recipe> mRecipesList;

    private OnRecipeItemSelected mRecipeItemClickListener;

    // interface for the recipe item click listener that the fragment should implement
    public interface OnRecipeItemSelected
    {
        void OnRecipeItemClicked(View view, int position);
    }


    // constructor of adapter
    public RecipeListAdapter(Context context)
    {
        this.mContext = context;
    }


    // this is the needed ViewHolder
    public class RecipeListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        // all the views of each row item (found in recycler_item_recipe layout)
        public LinearLayout mRecipeHolder;
        public TextView mRecipeName;
        public ImageView mRecipeImage;


        // constructor for view holder
        public RecipeListViewHolder(View itemView)
        {
            super(itemView);

            mRecipeHolder = (LinearLayout) itemView.findViewById(R.id.recycler_item_recipe_holder);
            mRecipeName = (TextView) itemView.findViewById(R.id.recycler_item_recipe_name);
            mRecipeImage = (ImageView) itemView.findViewById(R.id.recycler_item_recipe_image);

            // manually set click listener since recycler view doesn't have one for its items
            mRecipeHolder.setOnClickListener(this);
        }

        // implements onClick method since we're doing it manually
        @Override
        public void onClick(View v)
        {
            // when clicked, call the OnRecipeItemClicked callback method on this
            if (mRecipeItemClickListener != null)
            {
                mRecipeItemClickListener.OnRecipeItemClicked(itemView, getAdapterPosition());
            }
        }
    }

    // setter for the mItemClickListener
    public void setOnRecipeItemClickListener(final OnRecipeItemSelected itemClickListener, ArrayList<Recipe> recipes)
    {
        this.mRecipeItemClickListener = itemClickListener;
        mRecipesList = recipes;
    }

    // this will create the view of each row of the RecyclerView
    @Override
    public RecipeListViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_recipe, parent, false);
        return new RecipeListViewHolder(view);
    }

    // this will bind the data to each row of the RecyclerView
    @Override
    public void onBindViewHolder(RecipeListViewHolder holder, int position)
    {
        // get the position of the recipe we want form the data (global static instance)
        Recipe recipe = mRecipesList.get(position);

        // set the recipe data
        holder.mRecipeName.setText(recipe.name);

        // set the image of the recipe
        Glide.with(mContext)
                .load(Uri.parse(recipe.imageUri))
                .into(holder.mRecipeImage);
    }

    // every adapter needs getItemCount method
    @Override
    public int getItemCount()
    {
        return mRecipesList.size();
    }
}
