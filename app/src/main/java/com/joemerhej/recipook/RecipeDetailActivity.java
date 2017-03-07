package com.joemerhej.recipook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


public class RecipeDetailActivity extends AppCompatActivity implements EditRecipeHeaderDialog.DetailEditRecipeHeaderDialogListener,
                                                                       EditRecipeDurationsDialog.DetailEditRecipeDurationsDialogListener
{
    // intent extra
    public static final String EXTRA_RECIPE_ID = "recipe_id";

    // General Variables
    public Recipe mRecipe;                          // recipe in question
    public int mRecipeIndex;                        // index of recipe in question
    public Recipe mRecipeBeforeEdit;                // save a copy for undo edit changes
    public boolean mInEditMode;                     // if the activity is now in edit mode
    public boolean mAtLeastOneChange;               // so the app won't ask for discard/save changes if that didn't happen
    public Uri mNewImageUri;                        // need this to save the image uri returned from the pick image intent
    private InputMethodManager mInputManager;       // input manager to show/hide keyboard

    // views : toolbar area
    private ImageView mCollapsingToolbarImageView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private EditRecipeHeaderDialog mEditRecipeHeaderDialog;

    // views : categories
    private LinearLayout mCategoryAppetizerLayout;
    private ImageView mCategoryAppetizerButton;
    private TextView mCategoryAppetizerText;
    private LinearLayout mCategoryMainCourseLayout;
    private ImageView mCategoryMainCourseButton;
    private TextView mCategoryMainCourseText;
    private LinearLayout mCategorySideDishLayout;
    private ImageView mCategorySideDishButton;
    private TextView mCategorySideDishText;
    private LinearLayout mCategoryDessertLayout;
    private ImageView mCategoryDessertButton;
    private TextView mCategoryDessertText;
    private LinearLayout mCategoryBeverageLayout;
    private ImageView mCategoryBeverageButton;
    private TextView mCategoryBeverageText;

    // views : preparation and cooking times
    private LinearLayout mPreparationTimeLayout;
    private TextView mPreparationTimeText;
    private LinearLayout mCookingTimeLayout;
    private TextView mCookingTimeText;
    private EditRecipeDurationsDialog mEditRecipeDurationsDialog;

    // views : ingredients, directions recycler views
    private RecyclerView mIngredientsRecyclerView;
    private DetailIngredientListAdapter mIngredientListAdapter;
    private RecyclerView mDirectionsRecyclerView;
    private DetailDirectionListAdapter mDirectionListAdapter;

    // views : edit mode views
    private RelativeLayout mEditAddIngredientLayout;
    private TextInputEditText mEditAddIngredientText;
    private RelativeLayout mEditAddDirectionLayout;
    private TextInputEditText mEditAddDirectionText;
    private Button mDeleteRecipeButton;

    // views : fam and fabs
    private com.github.clans.fab.FloatingActionButton mMainFab;
    private com.github.clans.fab.FloatingActionMenu mMainFAM;
    private com.github.clans.fab.FloatingActionButton mEditFab;
    private com.github.clans.fab.FloatingActionButton mShareFab;
    private com.github.clans.fab.FloatingActionButton mAddToShoppingListFab;

    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ACTIVITY CREATE FUNCTION
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // get the recipe from the index in the extra
        mRecipeIndex = getIntent().getIntExtra(EXTRA_RECIPE_ID, 0);
        mRecipe = RecipeData.Instance().getRecipeList().get(mRecipeIndex); // this is a shallow copy, mRecipe is now pointing at data

        // create temp recipe
        mRecipeBeforeEdit = new Recipe();

        // set up the page header views
        mCollapsingToolbarImageView = (ImageView) findViewById(R.id.collapsing_toolbar_image_view);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mCollapsingToolbarLayout.setOnClickListener(mCollapsingToolbarLayoutClickListener);

        // set up category views
        mCategoryAppetizerLayout = (LinearLayout) findViewById(R.id.detail_category_appetizer_layout);
        mCategoryAppetizerButton = (ImageView) findViewById(R.id.detail_category_appetizer_button);
        mCategoryAppetizerText = (TextView) findViewById(R.id.detail_category_appetizer_text);
        mCategoryMainCourseLayout = (LinearLayout) findViewById(R.id.detail_category_main_course_layout);
        mCategoryMainCourseButton = (ImageView) findViewById(R.id.detail_category_main_course_button);
        mCategoryMainCourseText = (TextView) findViewById(R.id.detail_category_main_course_text);
        mCategorySideDishLayout = (LinearLayout) findViewById(R.id.detail_category_side_dish_layout);
        mCategorySideDishButton = (ImageView) findViewById(R.id.detail_category_side_dish_button);
        mCategorySideDishText = (TextView) findViewById(R.id.detail_category_side_dish_text);
        mCategoryDessertLayout = (LinearLayout) findViewById(R.id.detail_category_dessert_layout);
        mCategoryDessertButton = (ImageView) findViewById(R.id.detail_category_dessert_button);
        mCategoryDessertText = (TextView) findViewById(R.id.detail_category_dessert_text);
        mCategoryBeverageLayout = (LinearLayout) findViewById(R.id.detail_category_beverage_layout);
        mCategoryBeverageButton = (ImageView) findViewById(R.id.detail_category_beverage_button);
        mCategoryBeverageText = (TextView) findViewById(R.id.detail_category_beverage_text);

        // set up preparation time and cooking time views
        mPreparationTimeLayout = (LinearLayout) findViewById(R.id.detail_preparation_time_layout);
        mPreparationTimeText = (TextView) findViewById(R.id.detail_preparation_time_text);
        mCookingTimeLayout = (LinearLayout) findViewById(R.id.detail_cooking_time_layout);
        mCookingTimeText = (TextView) findViewById(R.id.detail_cooking_time_text);

        // set up ingredients and directions views/adapters/listeners
        mIngredientsRecyclerView = (RecyclerView) findViewById(R.id.detail_ingredients_list);
        mDirectionsRecyclerView = (RecyclerView) findViewById(R.id.detail_directions_list);

        // create recycler views adapters, layout managers, and set item dividers
        mIngredientListAdapter = new DetailIngredientListAdapter(this, mRecipe.ingredients);
        mIngredientsRecyclerView.setAdapter(mIngredientListAdapter);
        mIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mIngredientsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mIngredientsRecyclerView.setItemAnimator(null);
        mIngredientListAdapter.setIngredientButtonsClickListener(mIngredientButtonsClickListener);

        mDirectionListAdapter = new DetailDirectionListAdapter(this, mRecipe.directions);
        mDirectionsRecyclerView.setAdapter(mDirectionListAdapter);
        mDirectionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDirectionsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mDirectionsRecyclerView.setItemAnimator(null);
        mDirectionListAdapter.setDirectionButtonsClickListener(mDirectionButtonsClickListener);

        // set up edit mode views (visibility GONE by default)
        mEditAddIngredientLayout = (RelativeLayout) findViewById(R.id.detail_ingredient_add_layout);
        mEditAddIngredientText = (TextInputEditText) findViewById(R.id.detail_ingredient_edit_text);
        //mEditAddIngredientText.addTextChangedListener(new MyAddIngredientEditTextWatcher()); // TODO (see other todo below)
        mEditAddDirectionLayout = (RelativeLayout) findViewById(R.id.detail_direction_add_layout);
        mEditAddDirectionText = (TextInputEditText) findViewById(R.id.detail_direction_edit_text);
        mDeleteRecipeButton = (Button) findViewById(R.id.detail_delete_recipe_button);

        // set up the main fab (top right of the screen)
        mMainFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.recipe_detail_main_fab);
        mMainFab.setOnClickListener(mClickDetailFabsListener);

        // set up the main fam and its children fabs
        mMainFAM = (com.github.clans.fab.FloatingActionMenu) findViewById(R.id.recipe_detail_main_fam);
        mMainFAM.setClosedOnTouchOutside(true);
        mMainFAM.setIconAnimated(false);
        mMainFAM.setOnMenuToggleListener(mFAMToggleListener);

        mEditFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.recipe_detail_edit_fab);
        mEditFab.setOnClickListener(mClickDetailFabsListener);

        mShareFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.recipe_detail_share_fab);
        mShareFab.setOnClickListener(mClickDetailFabsListener);
        mShareFab.setEnabled(false); //TODO: implement sharing and enable this button

        mAddToShoppingListFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.recipe_detail_add_to_shopping_list_fab);
        mAddToShoppingListFab.setOnClickListener(mClickDetailFabsListener);
        mAddToShoppingListFab.setEnabled(false); //TODO: implement adding to shopping list and enable this button

        // set up input manager
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // load recipe title and image
        mCollapsingToolbarLayout.setTitle(mRecipe.name);
        Glide.with(this)
                .load(Uri.parse(mRecipe.imageUri))
                .into(mCollapsingToolbarImageView);

        // activity starts in view mode
        engageViewMode();
    }


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // BACK BUTTON PRESSED LISTENER
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void ResetViewToLastSavedRecipe()
    {
        // reset mRecipe variable
        mRecipe.MakeCopyOf(mRecipeBeforeEdit);

        // reset title
        mCollapsingToolbarLayout.setTitle(mRecipe.name);

        // reset image
        Glide.with(this)
                .load(Uri.parse(mRecipe.imageUri))
                .into(mCollapsingToolbarImageView);

        // reset categories
        handleCategoryViews();

        // reset ingredients
        mIngredientListAdapter.UpdateDataWith(mRecipe.ingredients);
        mIngredientListAdapter.notifyDataSetChanged();

        // reset directions
        mDirectionListAdapter.UpdateDataWith(mRecipe.directions);
        mDirectionListAdapter.notifyDataSetChanged();

        // engage view mode
        engageViewMode();
    }

    // what happens when the back button is pressed
    @Override
    public void onBackPressed()
    {
        // if the user is in Edit mode show the dialog to save/discard/cancel if at least one change happened
        if (mInEditMode)
        {
            // check if there were changes in category (this is the best place to check so i don't have to check every time there's a change)
            if (mRecipe.HasSameCategoriesAs(mRecipeBeforeEdit) && !mAtLeastOneChange)
            {
                engageViewMode();
            }
            // if at least one change happened
            else
            {
                // set up the dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder.setMessage("Save Changes?");

                // positive button click listener
                alertDialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // user hits Save button
                        engageViewMode();
                    }
                });
                // negative button click listener
                alertDialogBuilder.setNegativeButton("Discard", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        ResetViewToLastSavedRecipe();
                    }
                });

                // show dialog
                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
            }
        }
        // else this is normal app behavior (will go back to main activity)
        else
        {
            Intent intent = new Intent();
            intent.putExtra("recipePosition", mRecipeIndex);
            setResult(RESULT_OK, intent);
            super.onBackPressed();
        }
    }


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // MAIN FAM AND CHILD FABS LISTENERS AND METHODS
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // click listener for the main fam icon
    private com.github.clans.fab.FloatingActionMenu.OnMenuToggleListener mFAMToggleListener = new com.github.clans.fab.FloatingActionMenu.OnMenuToggleListener()
    {
        @Override
        public void onMenuToggle(boolean opened)
        {
            mMainFAM.getMenuIconView().setImageResource(opened
                    ? R.drawable.ic_keyboard_arrow_down_white_24dp
                    : R.drawable.ic_restaurant_menu_white_24dp);
        }
    };

    // click listeners for the menu fabs
    private View.OnClickListener mClickDetailFabsListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.recipe_detail_main_fab:
                    handleMainFabClicked(); // the main fab is the one at the top right
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

        // TODO: fix focus to first ingredient when in edit mode
//        RelativeLayout a  = (RelativeLayout)mIngredientsRecyclerView.getLayoutManager().findViewByPosition(0);
//        a.findViewById(R.id.recycler_item_ingredient_text).requestFocus();

        mCookingTimeLayout.setFocusable(true);
        mCookingTimeLayout.setFocusableInTouchMode(true);
        mCookingTimeLayout.requestFocus();

    }


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // EDIT MODE AND VIEW MODE METHODS
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // what happens when the activity is in edit mode
    public void engageEditMode()
    {
        mInEditMode = true;

        // hide the main fam
        mMainFAM.hideMenu(false);

        // set the main fab icon to save and show it
        mMainFab.setImageResource(R.drawable.ic_save_white_24dp);
        mMainFab.show(false);

        // show all categories
        mCategoryAppetizerLayout.setVisibility(View.VISIBLE);
        mCategoryMainCourseLayout.setVisibility(View.VISIBLE);
        mCategorySideDishLayout.setVisibility(View.VISIBLE);
        mCategoryDessertLayout.setVisibility(View.VISIBLE);
        mCategoryBeverageLayout.setVisibility(View.VISIBLE);

        // make the edit views visible
        mEditAddIngredientLayout.setVisibility(View.VISIBLE);
        mEditAddDirectionLayout.setVisibility(View.VISIBLE);
        mDeleteRecipeButton.setVisibility(View.VISIBLE);

        // notify the recycler view adapters so they make the right changes to theirs views
        mIngredientListAdapter.notifyDataSetChanged();
        mDirectionListAdapter.notifyDataSetChanged();
    }

    // what happens when the activity is view mode
    public void engageViewMode()
    {
        // hide the main fab, show the main fam
        mMainFab.hide(false);
        mMainFAM.showMenu(false);

        // save a copy of the new edited recipe on user validation
        mRecipeBeforeEdit.MakeCopyOf(mRecipe);

        // reset edit variables
        mInEditMode = false;
        mAtLeastOneChange = false;

        // handle category views
        handleCategoryViews();

        // handle durations views
        handleDurationsViews();

        // make the edit views invisible
        mEditAddIngredientLayout.setVisibility(View.GONE);
        mEditAddDirectionLayout.setVisibility(View.GONE);
        mEditAddDirectionText.getText().clear();
        mEditAddIngredientText.getText().clear();
        mDeleteRecipeButton.setVisibility(View.GONE);

        // notify the recycler view adapters so they make the right changes to theirs views
        mIngredientListAdapter.notifyDataSetChanged();
        mDirectionListAdapter.notifyDataSetChanged();

        // hide the keyboard
        mInputManager.hideSoftInputFromWindow(this.findViewById(android.R.id.content).getWindowToken(), 0);
    }

    // method that will set the right category views in view mode
    public void handleCategoryViews()
    {
        int green = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        int grey = ContextCompat.getColor(this, R.color.categoryColorDisabled);


        // only show category if it's contained in the recipe
        if (mRecipe.categories.contains(Category.Appetizer))
        {
            mCategoryAppetizerLayout.setVisibility(View.VISIBLE);
            mCategoryAppetizerButton.setImageResource(R.drawable.ic_category_appetizer_green_32dp);
            mCategoryAppetizerText.setTextColor(green);
        }
        else
        {
            mCategoryAppetizerLayout.setVisibility(View.GONE);
            mCategoryAppetizerButton.setImageResource(R.drawable.ic_category_appetizer_grey_32dp);
            mCategoryAppetizerText.setTextColor(grey);
        }

        if (mRecipe.categories.contains(Category.MainCourse))
        {
            mCategoryMainCourseLayout.setVisibility(View.VISIBLE);
            mCategoryMainCourseButton.setImageResource(R.drawable.ic_category_main_course_green_32dp);
            mCategoryMainCourseText.setTextColor(green);
        }
        else
        {
            mCategoryMainCourseLayout.setVisibility(View.GONE);
            mCategoryMainCourseButton.setImageResource(R.drawable.ic_category_main_course_grey_32dp);
            mCategoryMainCourseText.setTextColor(grey);
        }

        if (mRecipe.categories.contains(Category.SideDish))
        {
            mCategorySideDishLayout.setVisibility(View.VISIBLE);
            mCategorySideDishButton.setImageResource(R.drawable.ic_category_side_dish_green_32dp);
            mCategorySideDishText.setTextColor(green);
        }
        else
        {
            mCategorySideDishLayout.setVisibility(View.GONE);
            mCategorySideDishButton.setImageResource(R.drawable.ic_category_side_dish_grey_32dp);
            mCategorySideDishText.setTextColor(grey);
        }

        if (mRecipe.categories.contains(Category.Dessert))
        {
            mCategoryDessertLayout.setVisibility(View.VISIBLE);
            mCategoryDessertButton.setImageResource(R.drawable.ic_category_dessert_green_32dp);
            mCategoryDessertText.setTextColor(green);
        }
        else
        {
            mCategoryDessertLayout.setVisibility(View.GONE);
            mCategoryDessertButton.setImageResource(R.drawable.ic_category_dessert_grey_32dp);
            mCategoryDessertText.setTextColor(grey);
        }

        if (mRecipe.categories.contains(Category.Beverage))
        {
            mCategoryBeverageLayout.setVisibility(View.VISIBLE);
            mCategoryBeverageButton.setImageResource(R.drawable.ic_category_beverage_green_32dp);
            mCategoryBeverageText.setTextColor(green);
        }
        else
        {
            mCategoryBeverageLayout.setVisibility(View.GONE);
            mCategoryBeverageButton.setImageResource(R.drawable.ic_category_beverage_grey_32dp);
            mCategoryBeverageText.setTextColor(grey);
        }
    }

    // method that will set the right durations in the view in view mode
    public void handleDurationsViews()
    {
        mPreparationTimeText.setText(String.valueOf(mRecipe.preparationTimeMinutes / 60) + "h " + String.valueOf(mRecipe.preparationTimeMinutes % 60) + "m");
        mCookingTimeText.setText(String.valueOf(mRecipe.cookingTimeMinutes / 60) + "h " + String.valueOf(mRecipe.cookingTimeMinutes % 60) + "m");
    }


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ADD INGREDIENT TEXTWATCHER TODO: Implement text watcher for add ingredient textinput.
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

//    // text watcher to be attached to the add ingredient editText
//    public class MyAddIngredientEditTextWatcher implements TextWatcher
//    {
//        //boolean editing = false;
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after)
//        {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count)
//        {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable s)
//        {
//
//        }
//    }


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // INGREDIENT/DIRECTION RECYCLERVIEW ADAPTERS INTERFACE IMPLEMENTATION
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // Ingredient list adapter needs the ingredient buttons click listener to be implemented
    private DetailIngredientListAdapter.OnIngredientButtonsClickListener mIngredientButtonsClickListener = new DetailIngredientListAdapter.OnIngredientButtonsClickListener()
    {
        @Override
        public void onIngredientDeleteButtonClick(View view, int position)
        {
            mRecipe.ingredients.remove(position);
            mIngredientListAdapter.notifyItemRemoved(position);
            mAtLeastOneChange = true;
        }

        @Override
        public void onIngredientAddToShoppingListButtonClick(View view, int position)
        {
            //TODO: logic to add ingredient to shopping list
            Snackbar.make(view, "Added " + mRecipe.ingredients.get(position).name + " to the Shopping List.", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }
    };

    //Direction list adapter needs the direction buttons click listener to be implemented
    private DetailDirectionListAdapter.OnDirectionButtonsClickListener mDirectionButtonsClickListener = new DetailDirectionListAdapter.OnDirectionButtonsClickListener()
    {
        @Override
        public void onDirectionDeleteButtonClick(View view, int position)
        {
            mRecipe.directions.remove(position);
            mDirectionListAdapter.notifyItemRemoved(position);
            mDirectionListAdapter.notifyItemRangeChanged(position, mDirectionListAdapter.getItemCount());   // this updates the numbers
            mAtLeastOneChange = true;
        }
    };


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // CLICK LISTENERS FROM XML : Add Ingredient Button, Add Direction Button, Delete Recipe Button
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // method to be called when add ingredient button pressed
    public void onClickDetailAddIngredient(View view)
    {
        String newIngredientText = mEditAddIngredientText.getText().toString();
        newIngredientText = newIngredientText.trim();

        if (newIngredientText.isEmpty())
            return;

        Ingredient newIngredient = RecipookTextUtils.Instance().GetIngredientFromIngredientString(newIngredientText);
        if (newIngredient != null)
        {
            mRecipe.ingredients.add(newIngredient);
            mIngredientListAdapter.notifyItemInserted(mRecipe.ingredients.size() - 1);

            mEditAddIngredientText.getText().clear();

            mAtLeastOneChange = true;
        }
    }

    // method to be called when add direction button pressed
    public void onClickDetailAddDirection(View view)
    {
        String newDirectionText = mEditAddDirectionText.getText().toString();

        if (newDirectionText.isEmpty())
            return;

        mRecipe.directions.add(newDirectionText);
        mDirectionListAdapter.notifyItemInserted(mRecipe.directions.size() - 1);

        mEditAddDirectionText.getText().clear();

        mAtLeastOneChange = true;
    }

    // method to be called when delete recipe button pressed
    public void onClickDetailDeleteRecipe(View view)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("Delete Recipe?");

        // positive button click listener
        alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // remove the recipe and go back to the main activity
                RecipeData.Instance().removeRecipe(mRecipeIndex);

                Intent intent = new Intent();
                intent.putExtra("recipePosition", mRecipeIndex);
                setResult(RecipookIntentResult.RESULT_RECIPE_DELETED, intent);
                RecipeDetailActivity.super.finish();
            }
        });
        // negative button click listener
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // nothing happens if user cancels but we need this method
            }
        });

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();

    }


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // CLICK LISTENERS FROM XML : Categories
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // method to be called when the appetizer category layout is clicked
    public void onClickDetailCategoryAppetizer(View view)
    {
        if (mInEditMode)
        {
            if (mRecipe.categories.contains(Category.Appetizer))
            {
                mRecipe.categories.remove(Category.Appetizer);
                mCategoryAppetizerButton.setImageResource(R.drawable.ic_category_appetizer_grey_32dp);
                mCategoryAppetizerText.setTextColor(ContextCompat.getColor(this, R.color.categoryColorDisabled));
            }
            else
            {
                mRecipe.categories.add(Category.Appetizer);
                mCategoryAppetizerButton.setImageResource(R.drawable.ic_category_appetizer_green_32dp);
                mCategoryAppetizerText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            }
        }
    }

    // method to be called when the main course category layout is clicked
    public void onClickDetailCategoryMainCourse(View view)
    {
        if (mInEditMode)
        {
            if (mRecipe.categories.contains(Category.MainCourse))
            {
                mRecipe.categories.remove(Category.MainCourse);
                mCategoryMainCourseButton.setImageResource(R.drawable.ic_category_main_course_grey_32dp);
                mCategoryMainCourseText.setTextColor(ContextCompat.getColor(this, R.color.categoryColorDisabled));
            }
            else
            {
                mRecipe.categories.add(Category.MainCourse);
                mCategoryMainCourseButton.setImageResource(R.drawable.ic_category_main_course_green_32dp);
                mCategoryMainCourseText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            }
        }
    }

    // method to be called when the side dish category layout is clicked
    public void onClickDetailCategorySideDish(View view)
    {
        if (mInEditMode)
        {
            if (mRecipe.categories.contains(Category.SideDish))
            {
                mRecipe.categories.remove(Category.SideDish);
                mCategorySideDishButton.setImageResource(R.drawable.ic_category_side_dish_grey_32dp);
                mCategorySideDishText.setTextColor(ContextCompat.getColor(this, R.color.categoryColorDisabled));
            }
            else
            {
                mRecipe.categories.add(Category.SideDish);
                mCategorySideDishButton.setImageResource(R.drawable.ic_category_side_dish_green_32dp);
                mCategorySideDishText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            }
        }
    }

    // method to be called when the dessert category layout is clicked
    public void onClickDetailCategoryDessert(View view)
    {
        if (mInEditMode)
        {
            if (mRecipe.categories.contains(Category.Dessert))
            {
                mRecipe.categories.remove(Category.Dessert);
                mCategoryDessertButton.setImageResource(R.drawable.ic_category_dessert_grey_32dp);
                mCategoryDessertText.setTextColor(ContextCompat.getColor(this, R.color.categoryColorDisabled));
            }
            else
            {
                mRecipe.categories.add(Category.Dessert);
                mCategoryDessertButton.setImageResource(R.drawable.ic_category_dessert_green_32dp);
                mCategoryDessertText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            }
        }
    }

    // method to be called when the beverage category layout is clicked
    public void onClickDetailCategoryBeverage(View view)
    {
        if (mInEditMode)
        {
            if (mRecipe.categories.contains(Category.Beverage))
            {
                mRecipe.categories.remove(Category.Beverage);
                mCategoryBeverageButton.setImageResource(R.drawable.ic_category_beverage_grey_32dp);
                mCategoryBeverageText.setTextColor(ContextCompat.getColor(this, R.color.categoryColorDisabled));
            }
            else
            {
                mRecipe.categories.add(Category.Beverage);
                mCategoryBeverageButton.setImageResource(R.drawable.ic_category_beverage_green_32dp);
                mCategoryBeverageText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            }
        }
    }

    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // CLICK LISTENERS FROM XML : Preparation time, Cooking time
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // what happens when the user clicks the cooking time layout in edit mode
    public void onClickCookingTimeLayout(View view)
    {
        if (mInEditMode)
        {
            int cookingTimeHours = mRecipe.cookingTimeMinutes / 60;
            int cookingTimeMinutes = mRecipe.cookingTimeMinutes % 60;
            mEditRecipeDurationsDialog = EditRecipeDurationsDialog.Instance(EditRecipeDurationsDialog.RecipeDurationDialogType.COOKING_TIME_DIALOG,
                    getResources().getString(R.string.detail_cooking_time_title),
                    cookingTimeHours,
                    cookingTimeMinutes);
            mEditRecipeDurationsDialog.show(getFragmentManager(), EditRecipeDurationsDialog.class.getName());
        }
    }

    // what happens when the user clicks the preparatino time layout in edit mode
    public void onClickPreparationTimeLayout(View view)
    {
        if (mInEditMode)
        {
            int preparationTimeHours = mRecipe.preparationTimeMinutes / 60;
            int preparationTimeMinutes = mRecipe.preparationTimeMinutes % 60;
            mEditRecipeDurationsDialog = EditRecipeDurationsDialog.Instance(EditRecipeDurationsDialog.RecipeDurationDialogType.PREPARATION_TIME_DIALOG,
                    getResources().getString(R.string.detail_preparation_time_title),
                    preparationTimeHours,
                    preparationTimeMinutes);
            mEditRecipeDurationsDialog.show(getFragmentManager(), EditRecipeDurationsDialog.class.getName());
        }
    }

    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // EDIT DURATIONS DIALOG INTERFACE IMPLEMENTATION
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // what happens when the user clicks the positive button of the edit duration dialog
    @Override
    public void onEditDurationsDialogPositiveClick(EditRecipeDurationsDialog dialog, EditRecipeDurationsDialog.RecipeDurationDialogType dialogType)
    {
        // get the new times from dialog
        int newHours = Integer.valueOf(dialog.mHoursText.getText().toString());
        int newMinutes = Integer.valueOf(dialog.mMinutesText.getText().toString());
        int newTotalInMinutes = (newHours * 60) + newMinutes;

        // depending on dialog type, save the mRecipe values, also handle mAtLeastOneChange
        switch (dialogType)
        {
            case PREPARATION_TIME_DIALOG:
            {
                if (newTotalInMinutes != mRecipe.preparationTimeMinutes)
                {
                    mAtLeastOneChange = true;
                    mRecipe.preparationTimeMinutes = newTotalInMinutes;
                }
            }
            break;

            case COOKING_TIME_DIALOG:
            {
                if (newTotalInMinutes != mRecipe.cookingTimeMinutes)
                {
                    mAtLeastOneChange = true;
                    mRecipe.cookingTimeMinutes = newTotalInMinutes;
                }
            }
            break;
        }

        // reset the views
        handleDurationsViews();
    }

    // what happens when the user clicks the negative button of the edit duration dialog
    @Override
    public void onEditDurationsDialogNegativeClick(EditRecipeDurationsDialog dialog, EditRecipeDurationsDialog.RecipeDurationDialogType dialogType)
    {
        // nothing happens here but we need this implementation
    }

    // what happens when the user clicks the backspace button of the edit duration dialog
    @Override
    public void onEditDurationsDialogBackspaceClick(EditRecipeDurationsDialog dialog)
    {
        int hours = Integer.valueOf(dialog.mHoursText.getText().toString());
        int minutes = Integer.valueOf(dialog.mMinutesText.getText().toString());

        int hoursFirstDigit = hours / 10;
        int hoursSecondDigit = hours % 10;

        int minutesFirstDigit = minutes / 10;
        int minutesSecondDigit;

        minutesSecondDigit = minutesFirstDigit;
        minutesFirstDigit = hoursSecondDigit;
        hoursSecondDigit = hoursFirstDigit;
        hoursFirstDigit = 0;

        int newMinutes = (minutesFirstDigit * 10) + minutesSecondDigit;
        int newHours = (hoursFirstDigit * 10) + hoursSecondDigit;

        dialog.setTimeInView(newHours, newMinutes);
    }

    // what happens when the user clicks a number button of the edit duration dialog
    @Override
    public void onEditHeaderDialogNumberClick(EditRecipeDurationsDialog dialog, int digit)
    {
        int hours = Integer.valueOf(dialog.mHoursText.getText().toString());
        int minutes = Integer.valueOf(dialog.mMinutesText.getText().toString());

        int hoursFirstDigit = hours / 10;
        int hoursSecondDigit = hours % 10;

        int minutesFirstDigit = minutes / 10;
        int minutesSecondDigit = minutes % 10;

        // return if the view is full
        if (hoursFirstDigit > 0)
            return;

        hoursFirstDigit = hoursSecondDigit;
        hoursSecondDigit = minutesFirstDigit;
        minutesFirstDigit = minutesSecondDigit;
        minutesSecondDigit = digit;

        int newMinutes = (minutesFirstDigit * 10) + minutesSecondDigit;
        int newHours = (hoursFirstDigit * 10) + hoursSecondDigit;

        dialog.setTimeInView(newHours, newMinutes);
    }

    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // COLLAPSING TOOLBAR LISTENER AND EDIT HEADER DIALOG INTERFACE IMPLEMENTATION
    // ------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // method to be called when the collapsing toolbar layout is clicked
    private View.OnClickListener mCollapsingToolbarLayoutClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (mInEditMode)
            {
                // show the dialog
                mEditRecipeHeaderDialog = EditRecipeHeaderDialog.Instance(mRecipe.name, mRecipe.imageUri);
                mEditRecipeHeaderDialog.show(getFragmentManager(), EditRecipeHeaderDialog.class.getName());
                mNewImageUri = Uri.parse(mRecipe.imageUri);
            }
        }
    };

    // user clicks "Save" in the edit header dialog
    @Override
    public void onEditHeaderDialogPositiveClick(EditRecipeHeaderDialog dialog)
    {
        // get the new title
        String newTitle = dialog.mRecipeNameEditText.getText().toString();
        mCollapsingToolbarLayout.setTitle(newTitle);
        mRecipe.name = newTitle;

        // get the new image
        mRecipe.imageUri = mNewImageUri.toString();
        Glide.with(this)
                .load(Uri.parse(mRecipe.imageUri))
                .into(mCollapsingToolbarImageView);

        // check if smth actually changed in the recipe from this dialog
        if (!mRecipe.name.equalsIgnoreCase(mRecipeBeforeEdit.name) || !mRecipe.imageUri.equalsIgnoreCase(mRecipeBeforeEdit.imageUri))
            mAtLeastOneChange = true;

        // hide the keyboard
        mInputManager.hideSoftInputFromWindow(dialog.mRecipeNameEditText.getWindowToken(), 0);
    }

    // user clicks "Discard" in the edit header dialog
    @Override
    public void onEditHeaderDialogNegativeClick(EditRecipeHeaderDialog dialog)
    {
        // hide the keyboard
        mInputManager.hideSoftInputFromWindow(dialog.mRecipeNameEditText.getWindowToken(), 0);
    }

    // user clicks on remove image button in the edit header dialog
    @Override
    public void onEditHeaderDialogRemoveImageClick(EditRecipeHeaderDialog dialog)
    {
        mNewImageUri = Uri.EMPTY;

        Glide.with(this)
                .load(mNewImageUri)
                .into(mEditRecipeHeaderDialog.mRecipeImageView);
    }

    // user clicks on choose image button in the edit header dialog
    @Override
    public void onEditHeaderDialogChooseImageClick(EditRecipeHeaderDialog dialog)
    {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");

        startActivityForResult(chooserIntent, RecipookIntentResult.RESULT_IMAGE_RECEIVED);
    }

    // this method will be called when intents triggered with "startActivityForResult" come back to this activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        try
        {
            // check if it's the intent to change the toolbar image
            if (requestCode == RecipookIntentResult.RESULT_IMAGE_RECEIVED && resultCode == RESULT_OK && intent != null)
            {
                Uri imageUri = intent.getData();
                Glide.with(this)
                        .load(imageUri)
                        .into(mEditRecipeHeaderDialog.mRecipeImageView);

                mNewImageUri = imageUri;
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }

    }
}
