package com.joemerhej.recipook;

/**
 * Created by Joe Merhej on 1/29/17.
 */

public final class RecipookParser
{
    private static RecipookParser Instance = null;


    public static RecipookParser Instance()
    {
        if(Instance == null)
            Instance = new RecipookParser();

        return Instance;
    }

    // method that will take a user string and format it to an ingredient.
    public Ingredient GetIngredientFromString(String text)
    {
        return null;
    }

    // method that will format the unit and return it as a string.
    // this will take care of plurals and ommit the word "unit"
    public String GetUnitStringFromIngredient(Ingredient ingredient)
    {
        String ingredientUnit = ingredient.unit.toString();

        // check if "s" or "es" need to be added for plural
        if(ingredient.quantity > 1.0)
        {
            if(ingredient.unit == Unit.cup || ingredient.unit == Unit.lb || ingredient.unit == Unit.kg || ingredient.unit == Unit.gram
                    || ingredient.unit == Unit.liter || ingredient.unit == Unit.quart || ingredient.unit == Unit.pint || ingredient.unit == Unit.unit)
            {
                ingredientUnit += "s";
            }
            else if(ingredient.unit == Unit.dash)
            {
                ingredientUnit += "es";
            }
        }

        // in case of "unit" return an empty text
        if(ingredient.unit == Unit.unit)
        {
            ingredientUnit = "";
        }

        return ingredientUnit;
    }

    // method that will format the quantity and return it as a string.
    // this will take care of ".0" after ints and will make fractions.
    public String GetQuantityStringFromIngredient(Ingredient ingredient)
    {
        String ingredientQuantity = String.valueOf(ingredient.quantity);

        if(ingredient.quantity % 1 == 0)
        {
            int ingredientQuantityInt = (int) ingredient.quantity;
            ingredientQuantity = String.valueOf(ingredientQuantityInt);
            return ingredientQuantity;
        }

        if(Double.compare(ingredient.quantity, 0.25) == 0)
        {
            ingredientQuantity = "1/4";
        }
        else if(Double.compare(ingredient.quantity, 0.33) == 0)
        {
            ingredientQuantity = "1/3";
        }
        else if(Double.compare(ingredient.quantity, 0.5) == 0)
        {
            ingredientQuantity = "1/2";
        }
        else if(Double.compare(ingredient.quantity, 0.66) == 0)
        {
            ingredientQuantity = "2/3";
        }
        else if(Double.compare(ingredient.quantity, 0.75) == 0)
        {
            ingredientQuantity = "3/4";
        }

        return ingredientQuantity;
    }
}
