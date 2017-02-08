package com.joemerhej.recipook;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    public boolean mAtLeastOneChange;           // so the app won't ask for discard/save changes if that didn't happen

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

    // views: fabs
    private FloatingActionButton mMainFab;
    private com.github.clans.fab.FloatingActionMenu mMainFAM;
    private com.github.clans.fab.FloatingActionButton mEditFab;
    private com.github.clans.fab.FloatingActionButton mShareFab;
    private com.github.clans.fab.FloatingActionButton mAddToShoppingListFab;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // get the recipe from the index in the extra
        mRecipeIndex = getIntent().getIntExtra(EXTRA_PARAM_ID, 0);
        mRecipe = RecipeData.Instance().getRecipeList().get(mRecipeIndex); // this is a shallow copy, mRecipe is now pointing at data

        // make a deep copy of the recipe to hold for canceling changes
        mRecipeBeforeEdit = new Recipe(mRecipe.name, mRecipe.imageName, mRecipe.ingredients, mRecipe.directions);

        // set general variables
        mInEditMode = false;
        mAtLeastOneChange = false;

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
                mRecipe.ingredients.remove(position);
                mIngredientListAdapter.notifyItemRemoved(position);
                mEditAddIngredientText.requestFocus();
                mAtLeastOneChange = true;
            }
        };

        //DetailDirectionListAdapter fab click listener implemented to handle clicking on fabs of items
        mDirectionFabClickListener = new DetailDirectionListAdapter.OnItemFabClickListener()
        {
            @Override
            public void onItemFabClick(View view, int position)
            {
                mRecipe.directions.remove(position);
                mDirectionListAdapter.notifyItemRemoved(position);
                mDirectionListAdapter.notifyItemRangeChanged(position, mDirectionListAdapter.getItemCount());   // this updates the numbers
                mEditAddDirectionText.requestFocus();
                mAtLeastOneChange = true;
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

        // set up the main fab (top right of the screen)
        mMainFab = (FloatingActionButton) findViewById(R.id.recipe_detail_main_fab);
        mMainFab.setOnClickListener(onClickDetailFabsListener);

        // set up the main fam and its children fabs
        mMainFAM = (com.github.clans.fab.FloatingActionMenu) findViewById(R.id.recipe_detail_main_fam);
        mMainFAM.setClosedOnTouchOutside(true);

        mEditFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.recipe_detail_edit_fab);
        mEditFab.setOnClickListener(onClickDetailFabsListener);

        mShareFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.recipe_detail_share_fab);
        mShareFab.setOnClickListener(onClickDetailFabsListener);
        mShareFab.setEnabled(false);

        mAddToShoppingListFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.recipe_detail_add_to_shopping_list_fab);
        mAddToShoppingListFab.setOnClickListener(onClickDetailFabsListener);
        mAddToShoppingListFab.setEnabled(false);

        // additional set ups
        loadRecipe();
        getPhoto();
    }

    // what happens when the back button is pressed
    @Override
    public void onBackPressed()
    {
        // if the user is in Edit mode show the dialog to save/discard/cancel if at least one change happened
        if (mInEditMode)
        {
            if (!mAtLeastOneChange)
            {
                engageViewMode();
            }
            // if at least one change happened
            else
            {
                // set up the dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder.setMessage("Save Changes?");

                alertDialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // user hits Save button
                        engageViewMode();
                    }
                });

                alertDialogBuilder.setNegativeButton("Discard", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // user hits Discard button, reset recipe to last saved recipe
                        mRecipe.MakeCopyOf(mRecipeBeforeEdit);
                        mIngredientListAdapter.UpdateDataWith(mRecipe.ingredients);
                        mDirectionListAdapter.UpdateDataWith(mRecipe.directions);

                        mDirectionListAdapter.notifyDataSetChanged();
                        mIngredientListAdapter.notifyDataSetChanged();

                        engageViewMode();
                    }
                });

                // show dialog
                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
            }

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

        //mCollapsingToolbarLayout.setContentScrim(new ColorDrawable(mPalette.getDarkVibrantColor(defaultColor)));
    }

    // click listeners for the menu fabs
    private View.OnClickListener onClickDetailFabsListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.recipe_detail_main_fab:
                    handleMainFabClicked();
                    break;

                case R.id.recipe_detail_edit_fab:
                    handleEditButtonClicked();
                    break;

                case R.id.recipe_detail_share_fab:
                    break;

                case R.id.recipe_detail_add_to_shopping_list_fab:
                    break;
            }
        }
    };

    // what happens when the main fab button is clicked
    private void handleMainFabClicked()
    {
        // if in edit mode
        if (mInEditMode)
        {
            engageViewMode();
        }
    }

    // what happens when the edit button is clicked
    private void handleEditButtonClicked()
    {
        engageEditMode();
    }

    // what happens when the activity is in edit mode
    public void engageEditMode()
    {
        mInEditMode = true;

        // hide the main fam
        mMainFAM.hideMenu(false);

        // set the main fab icon to save and show it
        mMainFab.setImageResource(R.drawable.ic_save_white_24dp);
        mMainFab.show();

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
        // hide the main fab, show the main fam
        mMainFab.hide();
        mMainFAM.showMenu(false);

        // save a copy of the new edited recipe on user edit validation
        mRecipeBeforeEdit.MakeCopyOf(mRecipe);

        // reset edit variables
        mInEditMode = false;
        mAtLeastOneChange = false;

        // make the edit views invisible
        mEditAddIngredientLinearLayout.setVisibility(View.GONE);
        mEditAddDirectionLinearLayout.setVisibility(View.GONE);
        mEditAddDirectionText.getText().clear();
        mEditAddIngredientText.getText().clear();

        // notify the recycler view adapters so they make the right changes to theirs views
        mIngredientListAdapter.notifyDataSetChanged();
        mDirectionListAdapter.notifyDataSetChanged();

        // hide the keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.findViewById(android.R.id.content).getWindowToken(), 0);
    }


    // ------------------------------------
    // CALLBACKS ADDED FROM THE LAYOUT XML
    // ------------------------------------

    // method to be called when add ingredient button pressed
    public void onClickDetailAddIngredient(View view)
    {
        String newIngredientText = mEditAddIngredientText.getText().toString();
        newIngredientText = newIngredientText.trim();

        if (newIngredientText.isEmpty())
            return;

        mAtLeastOneChange = true;

        Ingredient newIngredient = RecipookParser.Instance().GetIngredientFromIngredientString(newIngredientText);
        if (newIngredient != null)
        {
            mRecipe.ingredients.add(newIngredient);
            mIngredientListAdapter.notifyItemInserted(mRecipe.ingredients.size() - 1);

            mEditAddIngredientText.getText().clear();
        }
    }

    // method to be called when add direction button pressed
    public void onClickDetailAddDirection(View view)
    {
        String newDirectionText = mEditAddDirectionText.getText().toString();

        if (newDirectionText.isEmpty())
            return;

        mAtLeastOneChange = true;

        mRecipe.directions.add(newDirectionText);
        mDirectionListAdapter.notifyItemInserted(mRecipe.directions.size() - 1);

        mEditAddDirectionText.getText().clear();
    }

    // method to be called when the collapsing toolbar layout is clicked
    public void onClickDetailCollapsingToolbarLayout(View view)
    {
        // only allow changes in edit mode
        if (mInEditMode)
        {
            // set up the dialog
            final EditText newTitleEditText = new EditText(this);
            newTitleEditText.setText(mRecipe.name);
            newTitleEditText.setSelection(newTitleEditText.length());

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setMessage("Recipe Name:");

            alertDialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    String newTitle = newTitleEditText.getText().toString();
                    mCollapsingToolbarLayout.setTitle(newTitle);
                    mRecipe.name = newTitle;
                    mRecipeBeforeEdit.name = newTitle;
                }
            });

            alertDialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    // nothing happens if the user presses discard but we need this empty listener
                }
            });

            alertDialogBuilder.setView(newTitleEditText);
            alertDialogBuilder.show();
        }
    }

}
