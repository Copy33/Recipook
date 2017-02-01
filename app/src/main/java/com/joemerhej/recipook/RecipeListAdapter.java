package com.joemerhej.recipook;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

/**
 * Created by Joe Merhej on 1/22/17.
 */

// adapter needed for the recipe list recycler view, it also needs a view holder inside it
public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeListViewHolder>
{
    private Context mContext;

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
        public LinearLayout recipeHolder;
        public LinearLayout recipeNameHolder;
        public TextView recipeName;
        public ImageView recipeImage;


        // constructor for view holder
        public RecipeListViewHolder(View itemView)
        {
            super(itemView);

            recipeHolder = (LinearLayout) itemView.findViewById(R.id.recycler_item_recipe_holder);
            recipeName = (TextView) itemView.findViewById(R.id.recycler_item_recipe_name);
            recipeNameHolder = (LinearLayout) itemView.findViewById(R.id.recycler_item_recipe_name_holder);
            recipeImage = (ImageView) itemView.findViewById(R.id.recycler_item_recipe_image);

            // manually set click listener since recycler view doesn't have one for its items
            recipeHolder.setOnClickListener(this);
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
    public void setOnRecipeItemClickListener(final OnRecipeItemSelected mItemClickListener)
    {
        this.mRecipeItemClickListener = mItemClickListener;
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
        final Recipe recipe = RecipeData.Instance().getRecipeList().get(position);

        // set the recipe data
        holder.recipeName.setText(recipe.name);

        // set the image of the recipe
        Picasso.with(mContext)
                .load(recipe.getImageResourceId(mContext))
                .resize(300, 300)
                .centerCrop()
                .into(holder.recipeImage);
    }

    // every adapter needs getItemCount method
    @Override
    public int getItemCount()
    {
        return RecipeData.Instance().getRecipeList().size();
    }
}
