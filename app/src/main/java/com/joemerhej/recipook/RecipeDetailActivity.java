package com.joemerhej.recipook;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class RecipeDetailActivity extends AppCompatActivity implements EditRecipeHeaderDialog.DetailEditRecipeHeaderDialogListener
{
    // intent extra
    public static final String EXTRA_RECIPE_ID = "recipe_id";

    // General Variables
    public Recipe mRecipe;                      // recipe in question
    public int mRecipeIndex;                    // index of recipe in question
    public boolean mInEditMode;                 // if the activity is now in edit mode
    public Recipe mRecipeBeforeEdit;            // save a copy for undo edit changes
    public boolean mAtLeastOneChange;           // so the app won't ask for discard/save changes if that didn't happen
    public int RESULT_LOAD_IMAGE;               // load image intent will return this

    // views: toolbar area
    private ImageView mToolbarImageView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private EditRecipeHeaderDialog mEditRecipeHeaderDialog;

    // views: scrollviews (ingredients, directions)
    private NestedScrollView mScrollView;
    private RecyclerView mIngredientsRecyclerView;
    private DetailIngredientListAdapter mIngredientListAdapter;
    private RecyclerView mDirectionsRecyclerView;
    private DetailDirectionListAdapter mDirectionListAdapter;

    // listeners : delete fabs (ingredients, directions)
    private DetailIngredientListAdapter.OnItemFabClickListener mIngredientFabClickListener;
    private DetailDirectionListAdapter.OnItemFabClickListener mDirectionFabClickListener;

    // views: edit mode views
    private LinearLayout mEditAddIngredientLinearLayout;
    private TextInputEditText mEditAddIngredientText;
    private Button mEditAddIngredientButton;
    private LinearLayout mEditAddDirectionLinearLayout;
    private TextInputEditText mEditAddDirectionText;
    private Button mEditAddDirectionButton;
    private Button mDeleteRecipeButton;

    // views: fam and fabs
    private FloatingActionButton mMainFab;
    private com.github.clans.fab.FloatingActionMenu mMainFAM;
    private com.github.clans.fab.FloatingActionButton mEditFab;
    private com.github.clans.fab.FloatingActionButton mShareFab;
    private com.github.clans.fab.FloatingActionButton mAddToShoppingListFab;


    // ----------------------------------------------------------------------------------------------------------------------------------------------
    // ACTIVITY CREATE FUNCTION
    // ----------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // get the recipe from the index in the extra
        mRecipeIndex = getIntent().getIntExtra(EXTRA_RECIPE_ID, 0);
        mRecipe = RecipeData.Instance().getRecipeList().get(mRecipeIndex); // this is a shallow copy, mRecipe is now pointing at data

        // make a deep copy of the recipe to hold for canceling changes
        mRecipeBeforeEdit = new Recipe(mRecipe.name, mRecipe.imageName, mRecipe.ingredients, mRecipe.directions);

        // set general variables
        mInEditMode = false;
        mAtLeastOneChange = false;

        // fill in the views
        mToolbarImageView = (ImageView) findViewById(R.id.recycler_item_recipe_image);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mScrollView = (NestedScrollView) findViewById(R.id.detail_scroll_view);
        mIngredientsRecyclerView = (RecyclerView) findViewById(R.id.detail_ingredients_list);
        mDirectionsRecyclerView = (RecyclerView) findViewById(R.id.detail_directions_list);

        mCollapsingToolbarLayout.setOnClickListener(new CollapsingToolbarLayoutClickListener());

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
        mEditAddIngredientText = (TextInputEditText) findViewById(R.id.detail_ingredient_edit_text);
        mEditAddIngredientText.addTextChangedListener(new MyAddIngredientEditTextWatcher());
        mEditAddIngredientButton = (Button) findViewById(R.id.detail_ingredient_add_button);

        mEditAddDirectionLinearLayout = (LinearLayout) findViewById(R.id.detail_direction_add_layout);
        mEditAddDirectionLinearLayout.setVisibility(View.GONE);
        mEditAddDirectionText = (TextInputEditText) findViewById(R.id.detail_direction_edit_text);
        mEditAddDirectionButton = (Button) findViewById(R.id.detail_direction_add_button);

        mDeleteRecipeButton = (Button) findViewById(R.id.detail_delete_recipe_button);
        mDeleteRecipeButton.setVisibility(View.GONE);

        // set up the main fab (top right of the screen)
        mMainFab = (FloatingActionButton) findViewById(R.id.recipe_detail_main_fab);
        mMainFab.setOnClickListener(onClickDetailFabsListener);

        // set up the main fam and its children fabs
        mMainFAM = (com.github.clans.fab.FloatingActionMenu) findViewById(R.id.recipe_detail_main_fam);
        mMainFAM.setClosedOnTouchOutside(true);
        createMainFAMAnimation();

        mEditFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.recipe_detail_edit_fab);
        mEditFab.setOnClickListener(onClickDetailFabsListener);

        mShareFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.recipe_detail_share_fab);
        mShareFab.setOnClickListener(onClickDetailFabsListener);
        mShareFab.setEnabled(false);

        mAddToShoppingListFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.recipe_detail_add_to_shopping_list_fab);
        mAddToShoppingListFab.setOnClickListener(onClickDetailFabsListener);
        mAddToShoppingListFab.setEnabled(false);

        // load recipe
        mCollapsingToolbarLayout.setTitle(mRecipe.name);
        mToolbarImageView.setImageResource(mRecipe.getImageResourceId(this));

        // get photo palette
        //Bitmap photo = BitmapFactory.decodeResource(getResources(), mRecipe.getImageResourceId(this));
        //int defaultColor = ContextCompat.getColor(this, R.color.colorPrimary);
        //Palette mPalette = Palette.from(photo).generate();
        //mCollapsingToolbarLayout.setContentScrim(new ColorDrawable(mPalette.getDarkVibrantColor(defaultColor)));
    }


    // ----------------------------------------------------------------------------------------------------------------------------------------------
    // BACK BUTTON PRESSED LISTENER
    // ----------------------------------------------------------------------------------------------------------------------------------------------

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
        {
            Intent intent = new Intent();
            intent.putExtra("recipePosition", mRecipeIndex);
            setResult(RESULT_OK, intent);
            super.onBackPressed();
        }
    }


    // ----------------------------------------------------------------------------------------------------------------------------------------------
    // MAIN FAM AND CHILD FABS LISTENERS AND METHODS
    // ----------------------------------------------------------------------------------------------------------------------------------------------

    // click listeners for the menu fabs
    private View.OnClickListener onClickDetailFabsListener = new View.OnClickListener()
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
    }

    private void createMainFAMAnimation()
    {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(mMainFAM.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(mMainFAM.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(mMainFAM.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(mMainFAM.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mMainFAM.getMenuIconView().setImageResource(mMainFAM.isOpened()
                        ? R.drawable.ic_keyboard_arrow_down_white_24dp
                        : R.drawable.ic_restaurant_menu_white_24dp);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);

        set.setInterpolator(new OvershootInterpolator(2));

        mMainFAM.setIconToggleAnimatorSet(set);
    }


    // ----------------------------------------------------------------------------------------------------------------------------------------------
    // EDIT MODE AND VIEW MODE METHODS
    // ----------------------------------------------------------------------------------------------------------------------------------------------

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
        mDeleteRecipeButton.setVisibility(View.VISIBLE);

        // notify the recycler view adapters so they make the right changes to theirs views
        mIngredientListAdapter.notifyDataSetChanged();
        mDirectionListAdapter.notifyDataSetChanged();
    }

    // what happens when the activity is view mode
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
        mDeleteRecipeButton.setVisibility(View.GONE);

        // notify the recycler view adapters so they make the right changes to theirs views
        mIngredientListAdapter.notifyDataSetChanged();
        mDirectionListAdapter.notifyDataSetChanged();

        // hide the keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.findViewById(android.R.id.content).getWindowToken(), 0);
    }


    // ----------------------------------------------------------------------------------------------------------------------------------------------
    // CALLBACKS ADDED FROM THE LAYOUT XML (Add Ingredient Button, Add Direction Button, Delete Recipe Button)
    // ----------------------------------------------------------------------------------------------------------------------------------------------

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
                setResult(RecipookIntentResult.RESULT_DELETED, intent);
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


    // ----------------------------------------------------------------------------------------------------------------------------------------------
    // ADD INGREDIENT TEXTWATCHER TODO: Implement text watcher.
    // ----------------------------------------------------------------------------------------------------------------------------------------------

    // text watcher to be attached to the add ingredient editText
    public class MyAddIngredientEditTextWatcher implements TextWatcher
    {
        //boolean editing = false;

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

        }
    }


    // ----------------------------------------------------------------------------------------------------------------------------------------------
    // COLLAPSING TOOLBAR LISTENER AND CUSTOM DIALOG
    // ----------------------------------------------------------------------------------------------------------------------------------------------

    // method to be called when the collapsing toolbar layout is clicked
    public class CollapsingToolbarLayoutClickListener implements CollapsingToolbarLayout.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            if (mInEditMode)
            {
                // show the dialog
                mEditRecipeHeaderDialog = EditRecipeHeaderDialog.Instance(mRecipe.name);
                mEditRecipeHeaderDialog.show(getFragmentManager(), EditRecipeHeaderDialog.class.getName());
            }
        }
    }

    // user clicks "Save" in the edit header dialog
    @Override
    public void onEditHeaderDialogPositiveClick(EditRecipeHeaderDialog dialog)
    {
        String newTitle = dialog.mNewRecipeTitleEditText.getText().toString();
        mCollapsingToolbarLayout.setTitle(newTitle);
        mRecipe.name = newTitle;
    }

    // user clicks "Discard" in the edit header dialog
    @Override
    public void onEditHeaderDialogNegativeClick(EditRecipeHeaderDialog dialog)
    {
        // nothing happens when the user discards, but we need this empty implementation
    }

    // user clicks on choose image button in the edit header dialog
    @Override
    public void onEditHeaderDialogChooseImageClick(EditRecipeHeaderDialog dialog)
    {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, RESULT_LOAD_IMAGE);
    }

    // this method will be called when intents triggered with "startActivityForResult" come back to this activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        try
        {
            // check if it's the intent to change the toolbar image
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null)
            {
                Uri imageUri = data.getData();
                mToolbarImageView.setImageURI(imageUri);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }

    }
}
