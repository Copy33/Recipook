package com.joemerhej.recipook;

import android.content.Context;
import java.util.ArrayList;

/**
 * Created by Joe Merhej on 1/21/17.
 */

public class Recipe
{
    String name;
    String imageName;
    ArrayList<Ingredient> ingredients;
    ArrayList<String> directions;

    Recipe()
    {
        ingredients = new ArrayList<>();
        directions = new ArrayList<>();
    }

    Recipe(String name_, String imageName_, ArrayList<Ingredient> ingredients_, ArrayList<String> directions_)
    {
        name = name_;
        imageName = imageName_;
        ingredients = new ArrayList<>();
        ingredients = ingredients_;
        directions = new ArrayList<>();
        directions = directions_;
    }

    // used to get the image resource id of every recipe
    int getImageResourceId(Context context)
    {
        return context.getResources().getIdentifier(this.imageName, "drawable", context.getPackageName());
    }

}
