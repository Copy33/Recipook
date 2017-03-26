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
    // context of the Recipe List
    private Context mContext;

    // list of all recipes
    private ArrayList<Recipe> mRecipeList;

    // click listener instance
    private OnRecipeItemClickListener mRecipeItemClickListener;

    // interface for the recipe item click listener that the fragment should implement
    public interface OnRecipeItemClickListener
    {
        void OnRecipeItemClicked(View view, int position);
    }


    // VIEW HOLDER ------------------------------------------------------------------------------------------------------------------------------------------------------------
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

            // bind the views
            mRecipeHolder = (LinearLayout) itemView.findViewById(R.id.recycler_item_recipe_holder);
            mRecipeName = (TextView) itemView.findViewById(R.id.recycler_item_recipe_name);
            mRecipeImage = (ImageView) itemView.findViewById(R.id.recycler_item_recipe_image);

            // manually set click listener since recycler view doesn't have one for its items
            mRecipeHolder.setOnClickListener(this);
        }

        // implements onClick method
        @Override
        public void onClick(View v)
        {
            // when clicked, call the OnRecipeItemClicked callback method on the right viewholder position
            if (mRecipeItemClickListener != null)
            {
                mRecipeItemClickListener.OnRecipeItemClicked(v, getLayoutPosition());
            }
        }
    }
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    // constructor of adapter
    public RecipeListAdapter(Context context, ArrayList<Recipe> recipes)
    {
        mContext = context;
        mRecipeList = recipes;
    }

    // setter for the mItemClickListener
    public void setOnRecipeItemClickListener(final OnRecipeItemClickListener itemClickListener)
    {
        mRecipeItemClickListener = itemClickListener;
    }

    // creates the view holder
    @Override
    public RecipeListViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_item_recipe, parent, false);
        return new RecipeListViewHolder(view);
    }

    // this will bind the data to each row of the RecyclerView
    @Override
    public void onBindViewHolder(RecipeListViewHolder holder, int position)
    {
        // get the position of the recipe we want form the data
        Recipe recipe = mRecipeList.get(position);

        // set the recipe data
        holder.mRecipeName.setText(recipe.mName);

        // set the image of the recipe
        Glide.with(mContext)
                .load(Uri.parse(recipe.mImageUri))
                .into(holder.mRecipeImage);
    }

    // every adapter needs getItemCount method
    @Override
    public int getItemCount()
    {
        return mRecipeList.size();
    }
}
