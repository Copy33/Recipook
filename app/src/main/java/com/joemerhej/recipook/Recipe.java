package com.joemerhej.recipook;

import android.content.Context;
import java.util.ArrayList;

/**
 * Created by Joe Merhej on 1/21/17.
 */

public class Recipe
{
    // recipe variables
    String name;
    String imageName;
    String imageUri;
    ArrayList<Ingredient> ingredients;
    ArrayList<String> directions;

    // default constructor
    Recipe()
    {
        ingredients = new ArrayList<>();
        directions = new ArrayList<>();
    }

    // constructor makes deep copy
    Recipe(String Name, String ImageName, ArrayList<Ingredient> Ingredients, ArrayList<String> Directions)
    {
        name = Name;
        imageName = ImageName;
        imageUri = "android.resource://com.joemerhej.recipook/drawable/" + ImageName;

        ingredients = new ArrayList<>();
        for(int i = 0; i < Ingredients.size(); ++i)
            ingredients.add(Ingredients.get(i));

        directions = new ArrayList<>();
        for(int d = 0; d < Directions.size(); ++d)
            directions.add(Directions.get(d));
    }

    // makes deep copy of recipe elements
    public void MakeCopyOf(Recipe CopyFrom)
    {
        name = CopyFrom.name;
        imageName = CopyFrom.imageName;
        imageUri = CopyFrom.imageUri;

        ingredients = new ArrayList<>();
        for(int i = 0; i < CopyFrom.ingredients.size(); ++i)
            ingredients.add(CopyFrom.ingredients.get(i));

        directions = new ArrayList<>();
        for(int d = 0; d < CopyFrom.directions.size(); ++d)
            directions.add(CopyFrom.directions.get(d));
    }

    // used to get the image resource id of every recipe
    public int getImageResourceId(Context context)
    {
        return context.getResources().getIdentifier(this.imageName, "drawable", context.getPackageName());
    }

}
