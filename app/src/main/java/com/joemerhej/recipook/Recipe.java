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
    String name;
    ArrayList<Category> categories;
    ArrayList<Ingredient> ingredients;
    ArrayList<String> directions;
    String imageName;
    String imageUri;


    // default constructor
    Recipe()
    {
        categories = new ArrayList<>();
        categories.add(Category.All); // all recipes will have 'All' category
        ingredients = new ArrayList<>();
        directions = new ArrayList<>();
    }

    // makes deep copy of recipe elements
    void MakeCopyOf(Recipe CopyFrom)
    {
        name = CopyFrom.name;
        imageName = CopyFrom.imageName;
        imageUri = CopyFrom.imageUri;

        categories = new ArrayList<>();
        for(Category c : CopyFrom.categories)
            categories.add(c);

        ingredients = new ArrayList<>();
        for(Ingredient i : CopyFrom.ingredients)
            ingredients.add(i);

        directions = new ArrayList<>();
        for(String d : CopyFrom.directions)
            directions.add(d);
    }

    // check if recipe has same categories as another recipe
    boolean HasSameCategoriesAs(Recipe recipe)
    {
        int result = 0;
        for(Category c : recipe.categories)
            result ^= c.ordinal();
        for(Category c : categories)
            result ^= c.ordinal();

        return result==0;
    }
}
