package com.joemerhej.recipook;

import java.util.ArrayList;

/**
 * Created by Joe Merhej on 1/21/17.
 */

enum Category
{
    Appetizer,
    MainCourse,
    SideDish,
    Dessert,
    Beverage,
    All
}

public class Recipe
{
    // recipe variables
    String mName;
    ArrayList<Category> mCategories;
    int mPreparationTimeMinutes;
    int mCookingTimeMinutes;
    ArrayList<Ingredient> mIngredients;
    ArrayList<String> mDirections;
    String mImageName;
    String mImageUri;


    // default constructor
    Recipe()
    {
        mCategories = new ArrayList<>();
        mCategories.add(Category.All); // all recipes will have 'All' category
        mIngredients = new ArrayList<>();
        mDirections = new ArrayList<>();
    }

    // makes deep copy of recipe elements
    void MakeCopyOf(Recipe CopyFrom)
    {
        mName = CopyFrom.mName;
        mImageName = CopyFrom.mImageName;
        mImageUri = CopyFrom.mImageUri;
        mPreparationTimeMinutes = CopyFrom.mPreparationTimeMinutes;
        mCookingTimeMinutes = CopyFrom.mCookingTimeMinutes;

        mCategories = new ArrayList<>();
        for(Category c : CopyFrom.mCategories)
            mCategories.add(c);

        mIngredients = new ArrayList<>();
        for(Ingredient i : CopyFrom.mIngredients)
            mIngredients.add(i);

        mDirections = new ArrayList<>();
        for(String d : CopyFrom.mDirections)
            mDirections.add(d);
    }

    // check if recipe has same categories as another recipe
    boolean HasSameCategoriesAs(Recipe recipe)
    {
        int result = 0;
        for(Category c : recipe.mCategories)
            result ^= c.ordinal();
        for(Category c : mCategories)
            result ^= c.ordinal();

        return result==0;
    }
}
