package com.joemerhej.recipook;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class RecipeDetailActivity extends AppCompatActivity
{
    // intent extra
    public static final String EXTRA_PARAM_ID = "recipe_id";

    // General Variables
    public Recipe mRecipe;                      // recipe in question
    public int mRecipeIndex;                    // index of recipe in question
    public boolean mInEditMode;                 // if the activity is now in edit mode
    public Recipe mRecipeBeforeEdit;            // save a copy for undo edit changes

    // views: toolbar area
    private ImageView mImageView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    // views: scrollviews (ingredients, directions)
    private NestedScrollView mScrollView;
    private RecyclerView mIngredientsRecyclerView;
    private DetailIngredientListAdapter mIngredientListAdapter;
    private RecyclerView mDirectionsRecyclerView;
    private DetailDirectionListAdapter mDirectionListAdapter;

    // listeners
    private DetailIngredientListAdapter.OnItemFabClickListener mIngredientFabClickListener;
    private DetailDirectionListAdapter.OnItemFabClickListener mDirectionFabClickListener;

    // views: edit mode views
    private LinearLayout mEditAddIngredientLinearLayout;
    private EditText mEditAddIngredientText;
    private Button mEditAddIngredientButton;
    private LinearLayout mEditAddDirectionLinearLayout;
    private EditText mEditAddDirectionText;
    private Button mEditAddDirectionButton;

    // main fab
    private FloatingActionButton mMainFab;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // get the recipe from the index in the extra
        mRecipeIndex = getIntent().getIntExtra(EXTRA_PARAM_ID, 0);
        mRecipe = RecipeData.Instance().getRecipeList().get(mRecipeIndex); // this is a shallow copy

        // make a deep copy of the recipe to hold for canceling changes
        mRecipeBeforeEdit = new Recipe(mRecipe.name, mRecipe.imageName, mRecipe.ingredients, mRecipe.directions);

        // set general variables
        mInEditMode = false;

        // fill in the views
        mImageView = (ImageView) findViewById(R.id.recycler_item_recipe_image);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mScrollView = (NestedScrollView) findViewById(R.id.detail_scroll_view);
        mIngredientsRecyclerView = (RecyclerView) findViewById(R.id.detail_ingredients_list);
        mDirectionsRecyclerView = (RecyclerView) findViewById(R.id.detail_directions_list);

        // initialize the recycler view adapter listeners
        // DetailIngredientListAdapter fab click listener implemented to handle clicking on fabs of items
        mIngredientFabClickListener = new DetailIngredientListAdapter.OnItemFabClickListener()
        {
            @Override
            public void onItemFabClick(View view, int position)
            {
                RecipeData.Instance().removeIngredient(mRecipeIndex, position);
                mIngredientListAdapter.notifyItemRemoved(position);
                mEditAddIngredientText.requestFocus();
            }
        };

        //DetailDirectionListAdapter fab click listener implemented to handle clicking on fabs of items
        mDirectionFabClickListener = new DetailDirectionListAdapter.OnItemFabClickListener()
        {
            @Override
            public void onItemFabClick(View view, int position)
            {
                RecipeData.Instance().removeDirection(mRecipeIndex, position);
                mDirectionListAdapter.notifyItemRemoved(position);
                mDirectionListAdapter.notifyItemRangeChanged(position, mDirectionListAdapter.getItemCount());   // this updates the numbers
                mEditAddDirectionText.requestFocus();
            }
        };

        // create recycler views adapters, layout managers, and set item dividers
        mIngredientListAdapter = new DetailIngredientListAdapter(this, mRecipe.ingredients);
        mIngredientsRecyclerView.setAdapter(mIngredientListAdapter);
        mIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mIngredientsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mIngredientListAdapter.setOnItemFabClickListener(mIngredientFabClickListener);

        mDirectionListAdapter = new DetailDirectionListAdapter(this, mRecipe.directions);
        mDirectionsRecyclerView.setAdapter(mDirectionListAdapter);
        mDirectionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDirectionsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mDirectionListAdapter.setOnItemFabClickListener(mDirectionFabClickListener);

        // set up edit mode views (visibility GONE by default)
        mEditAddIngredientLinearLayout = (LinearLayout) findViewById(R.id.detail_ingredient_add_layout);
        mEditAddIngredientLinearLayout.setVisibility(View.GONE);
        mEditAddIngredientText = (EditText) findViewById(R.id.detail_ingredient_edit_text);
        mEditAddIngredientButton = (Button) findViewById(R.id.detail_ingredient_add_button);

        mEditAddDirectionLinearLayout = (LinearLayout) findViewById(R.id.detail_direction_add_layout);
        mEditAddDirectionLinearLayout.setVisibility(View.GONE);
        mEditAddDirectionText = (EditText) findViewById(R.id.detail_direction_edit_text);
        mEditAddDirectionButton = (Button) findViewById(R.id.detail_direction_add_button);

        // set up the main fab
        mMainFab = (FloatingActionButton) findViewById(R.id.recipeDetailFab);
        mMainFab.setOnClickListener(new View.OnClickListener()
        {
            // click listener for main fab
            @Override
            public void onClick(View view)
            {
                // toggle edit mode
                mInEditMode = !mInEditMode;

                // if in edit mode
                if (mInEditMode)
                {
                    engageEditMode();
                }
                // if in view mode (or when done editing)
                else
                {
                    engageViewMode();
                }
            }
        });

        // additional set ups
        loadRecipe();
        getPhoto();
    }

    // what happens when the back button is pressed
    @Override
    public void onBackPressed()
    {
        // if the user is in Edit mode show the dialog to save/discard/cancel
        if (mInEditMode)
        {
            // set up the dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setMessage("Save Changes?");

            alertDialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    // user hits Save button
                    mInEditMode = false;

                    engageViewMode();
                }
            });

            alertDialogBuilder.setNegativeButton("Discard", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    // user hits Discard button
                    mInEditMode = false;

                    mRecipe.MakeCopyOf(mRecipeBeforeEdit);
                    mIngredientListAdapter.UpdateDataWith(mRecipe.ingredients);
                    mDirectionListAdapter.UpdateDataWith(mRecipe.directions);

                    mDirectionListAdapter.notifyDataSetChanged();
                    mIngredientListAdapter.notifyDataSetChanged();

                    engageViewMode();
                }
            });

            AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();

        }
        // else this is normal app behavior (will go back to main activity)
        else
            super.onBackPressed();
    }

    // loads the recipe (title and image url for now)
    private void loadRecipe()
    {
        mCollapsingToolbarLayout.setTitle(mRecipe.name);

        mImageView.setImageResource(mRecipe.getImageResourceId(this));
    }

    // get the photo of the recipe from the loaded url
    private void getPhoto()
    {
        int defaultColor = ContextCompat.getColor(this, R.color.colorPrimary);
        Bitmap photo = BitmapFactory.decodeResource(getResources(), mRecipe.getImageResourceId(this));

        Palette mPalette = Palette.from(photo).generate();

        mCollapsingToolbarLayout.setContentScrim(new ColorDrawable(mPalette.getDarkVibrantColor(defaultColor)));
    }

    // what happens when the activity is in edit mode
    public void engageEditMode()
    {
        // change the main fab icon
        mMainFab.setImageResource(android.R.drawable.ic_dialog_email);

        // make the edit views visible
        mEditAddIngredientLinearLayout.setVisibility(View.VISIBLE);
        mEditAddDirectionLinearLayout.setVisibility(View.VISIBLE);

        // notify the recycler view adapters so they make the right changes to theirs views
        mIngredientListAdapter.notifyDataSetChanged();
        mDirectionListAdapter.notifyDataSetChanged();
    }

    // what happens when the activity is not in edit mode
    public void engageViewMode()
    {
        // save a copy of the new edited recipe on user edit validation
        mRecipeBeforeEdit.MakeCopyOf(mRecipe);

        // change the main fab icon
        mMainFab.setImageResource(android.R.drawable.ic_menu_edit);

        // make the edit views invisible
        mEditAddIngredientLinearLayout.setVisibility(View.GONE);
        mEditAddDirectionLinearLayout.setVisibility(View.GONE);

        // notify the recycler view adapters so they make the right changes to theirs views
        mIngredientListAdapter.notifyDataSetChanged();
        mDirectionListAdapter.notifyDataSetChanged();

        // hide the keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.findViewById(android.R.id.content).getWindowToken(), 0);
    }

    // method to be called when add ingredient button pressed
    public void onClickDetailAddIngredient(View view)
    {
        String newIngredientText = mEditAddIngredientText.getText().toString();

        if (newIngredientText.isEmpty())
            return;

        Ingredient newIngredient = new Ingredient(1, Unit.unit, newIngredientText);
        RecipeData.Instance().addIngredient(mRecipeIndex, newIngredient);
        mIngredientListAdapter.notifyItemInserted(mRecipe.ingredients.size() - 1);

        mEditAddIngredientText.getText().clear();
    }

    // method to be called when add direction button pressed
    public void onClickDetailAddDirection(View view)
    {
        String newDirectionText = mEditAddDirectionText.getText().toString();

        if (newDirectionText.isEmpty())
            return;

        RecipeData.Instance().addDirection(mRecipeIndex, newDirectionText);
        mDirectionListAdapter.notifyItemInserted(mRecipe.directions.size() - 1);

        mEditAddDirectionText.getText().clear();
    }


}
