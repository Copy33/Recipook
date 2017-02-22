package com.joemerhej.recipook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.NestedScrollView;
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
import android.widget.Toast;

import com.squareup.picasso.Picasso;


public class RecipeDetailActivity extends AppCompatActivity implements EditRecipeHeaderDialog.DetailEditRecipeHeaderDialogListener
{
    // intent extra
    public static final String EXTRA_RECIPE_ID = "recipe_id";

    // General Variables
    public Recipe mRecipe;                      // recipe in question
    public int mRecipeIndex;                    // index of recipe in question
    public Recipe mRecipeBeforeEdit;            // save a copy for undo edit changes
    public boolean mInEditMode;                 // if the activity is now in edit mode
    public boolean mAtLeastOneChange;           // so the app won't ask for discard/save changes if that didn't happen
    public int RESULT_LOAD_IMAGE;               // load image intent (from edit header dialog) will return this
    public Uri mNewImageUri;                    // need this to save the image uri returned from the pick image intent
    private InputMethodManager mInputManager;   // input manager to show/hide keyboard

    // views: toolbar area
    private ImageView mCollapsingToolbarImageView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private EditRecipeHeaderDialog mEditRecipeHeaderDialog;

    // views: scrollviews (ingredients, directions)
    private NestedScrollView mScrollView;
    private RecyclerView mIngredientsRecyclerView;
    private DetailIngredientListAdapter mIngredientListAdapter;
    private RecyclerView mDirectionsRecyclerView;
    private DetailDirectionListAdapter mDirectionListAdapter;

    // listeners : delete (ingredients, directions)
    private DetailIngredientListAdapter.OnIngredientButtonsClickListener mIngredientButtonsClickListener;
    private DetailDirectionListAdapter.OnDirectionButtonsClickListener mDirectionButtonsClickListener;

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
        mCollapsingToolbarImageView = (ImageView) findViewById(R.id.collapsing_toolbar_image_view);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mScrollView = (NestedScrollView) findViewById(R.id.detail_scroll_view);
        mIngredientsRecyclerView = (RecyclerView) findViewById(R.id.detail_ingredients_list);
        mDirectionsRecyclerView = (RecyclerView) findViewById(R.id.detail_directions_list);

        // set collapsing toolbar listener
        mCollapsingToolbarLayout.setOnClickListener(mCollapsingToolbarLayoutClickListener);

        // initialize the recycler view adapter listeners
        // Ingredient list adapter needs the ingredient buttons click listener to be implemented
        mIngredientButtonsClickListener = new DetailIngredientListAdapter.OnIngredientButtonsClickListener()
        {
            @Override
            public void onIngredientDeleteButtonClick(View view, int position)
            {
                mRecipe.ingredients.remove(position);
                mIngredientListAdapter.notifyItemRemoved(position);
                mEditAddIngredientText.requestFocus();
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
        mDirectionButtonsClickListener = new DetailDirectionListAdapter.OnDirectionButtonsClickListener()
        {
            @Override
            public void onDirectionDeleteButtonClick(View view, int position)
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
        mIngredientListAdapter.setIngredientButtonsClickListener(mIngredientButtonsClickListener);

        mDirectionListAdapter = new DetailDirectionListAdapter(this, mRecipe.directions);
        mDirectionsRecyclerView.setAdapter(mDirectionListAdapter);
        mDirectionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDirectionsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mDirectionListAdapter.setDirectionButtonsClickListener(mDirectionButtonsClickListener);

        // set up edit mode views (visibility GONE by default)
        mEditAddIngredientLinearLayout = (LinearLayout) findViewById(R.id.detail_ingredient_add_layout);
        mEditAddIngredientLinearLayout.setVisibility(View.GONE);
        mEditAddIngredientText = (TextInputEditText) findViewById(R.id.detail_ingredient_edit_text);
        //mEditAddIngredientText.addTextChangedListener(new MyAddIngredientEditTextWatcher()); // TODO (see other todo below)
        mEditAddIngredientButton = (Button) findViewById(R.id.detail_ingredient_add_button);

        mEditAddDirectionLinearLayout = (LinearLayout) findViewById(R.id.detail_direction_add_layout);
        mEditAddDirectionLinearLayout.setVisibility(View.GONE);
        mEditAddDirectionText = (TextInputEditText) findViewById(R.id.detail_direction_edit_text);
        mEditAddDirectionButton = (Button) findViewById(R.id.detail_direction_add_button);

        mDeleteRecipeButton = (Button) findViewById(R.id.detail_delete_recipe_button);
        mDeleteRecipeButton.setVisibility(View.GONE);

        // set up the main fab (top right of the screen)
        mMainFab = (FloatingActionButton) findViewById(R.id.recipe_detail_main_fab);
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

        // load recipe title and image
        mCollapsingToolbarLayout.setTitle(mRecipe.name);
        Picasso.with(this)
                .load(Uri.parse(mRecipe.imageUri))
                .into(mCollapsingToolbarImageView);

        // set up input manager
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
        mInputManager.hideSoftInputFromWindow(this.findViewById(android.R.id.content).getWindowToken(), 0);
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


    // ----------------------------------------------------------------------------------------------------------------------------------------------
    // ADD INGREDIENT TEXTWATCHER TODO: Implement text watcher for add ingredient textinput.
    // ----------------------------------------------------------------------------------------------------------------------------------------------

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


    // ----------------------------------------------------------------------------------------------------------------------------------------------
    // COLLAPSING TOOLBAR LISTENER AND CUSTOM DIALOG
    // ----------------------------------------------------------------------------------------------------------------------------------------------

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

                mInputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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
        mCollapsingToolbarImageView.setImageURI(mNewImageUri);
        mRecipe.imageUri = mNewImageUri.toString();

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
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        try
        {
            // check if it's the intent to change the toolbar image
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && intent != null)
            {
                Uri imageUri = intent.getData();
                Picasso.with(this)
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
