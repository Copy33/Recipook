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
                    || ingredient.unit == Unit.liter || ingredient.unit == Unit.quart || ingredient.unit == Unit.pint)
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

        if(ingredient.quantity == 0)
            return "";

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

    // method that will get quantity from quantity string
    public double GetQuantityFromQuantityString(String s)
    {
        if(s == null || s.isEmpty())
            return 0;

        String[] strings = s.split("\\s+");

        if(strings.length == 0)
            return 0;

        if(strings[0].equals("1/4"))
        {
            return 0.25;
        }
        else if(strings[0].equals("1/3"))
        {
            return 0.33;
        }
        else if(strings[0].equals("1/2"))
        {
            return 0.5;
        }
        else if(strings[0].equals("2/3"))
        {
            return 0.66;
        }
        else if(strings[0].equals("3/4"))
        {
            return 0.75;
        }
        else if(strings[0].matches("-?\\d+(\\.\\d+)?"))
        {
            return Double.valueOf(strings[0]);
        }
        else return 0;
    }

    // method that will get unit from quantity string
    public Unit GetUnitFromQuantityString(String s)
    {
        if(s == null || s.isEmpty())
            return Unit.unit;

        String[] strings = s.split("\\s+");

        if(strings.length < 2)
            return Unit.unit;

        if(strings[1].toLowerCase().equals("oz") || strings[1].toLowerCase().equals("ounce") || strings[1].toLowerCase().equals("ounces"))
        {
            return Unit.oz;
        }
        else if(strings[1].toLowerCase().equals("lb") || strings[1].toLowerCase().equals("lbs") || strings[1].toLowerCase().equals("pd")
                || strings[1].toLowerCase().equals("pds") || strings[1].toLowerCase().equals("pound") || strings[1].toLowerCase().equals("pounds"))
        {
            return Unit.lb;
        }
        else if(strings[1].toLowerCase().equals("pint") || strings[1].toLowerCase().equals("pints") || strings[1].toLowerCase().equals("pt") || strings[1].toLowerCase().equals("pts"))
        {
            return Unit.pint;
        }
        else if(strings[1].toLowerCase().equals("cup") || strings[1].toLowerCase().equals("cups") || strings[1].toLowerCase().equals("cp") || strings[1].toLowerCase().equals("cps"))
        {
            return Unit.cup;
        }
        else if(strings[1].toLowerCase().equals("tbsp") || strings[1].toLowerCase().equals("tbsps") || strings[1].toLowerCase().equals("tablespoon") || strings[1].toLowerCase().equals("tablespoons"))
        {
            return Unit.tbsp;
        }
        else if(strings[1].toLowerCase().equals("tsp") || strings[1].toLowerCase().equals("tsps") || strings[1].toLowerCase().equals("teaspoon") || strings[1].toLowerCase().equals("teaspoons"))
        {
            return Unit.tsp;
        }
        else if(strings[1].toLowerCase().equals("kg") || strings[1].toLowerCase().equals("kgs") || strings[1].toLowerCase().equals("kilogram") || strings[1].toLowerCase().equals("k")
                || strings[1].toLowerCase().equals("ks") || strings[1].toLowerCase().equals("kilograms") || strings[1].toLowerCase().equals("kilo") || strings[1].toLowerCase().equals("kilos"))
        {
            return Unit.kg;
        }
        else if(strings[1].toLowerCase().equals("g") || strings[1].toLowerCase().equals("gs") || strings[1].toLowerCase().equals("gram") || strings[1].toLowerCase().equals("grams"))
        {
            return Unit.gram;
        }
        else if(strings[1].toLowerCase().equals("l") || strings[1].toLowerCase().equals("liter") || strings[1].toLowerCase().equals("ls") || strings[1].toLowerCase().equals("liters"))
        {
            return Unit.liter;
        }
        else if(strings[1].toLowerCase().equals("ml") || strings[1].toLowerCase().equals("mls") || strings[1].toLowerCase().equals("milliliter") || strings[1].toLowerCase().equals("milliliters")
                || strings[1].toLowerCase().equals("milli") || strings[1].toLowerCase().equals("millis"))
        {
            return Unit.ml;
        }
        else if(strings[1].toLowerCase().equals("unit") || strings[1].toLowerCase().equals("units"))
        {
            return Unit.unit;
        }
        else if(strings[1].toLowerCase().equals("quart") || strings[1].toLowerCase().equals("quarts") || strings[1].toLowerCase().equals("q") || strings[1].toLowerCase().equals("qs")
                || strings[1].toLowerCase().equals("qrt") || strings[1].toLowerCase().equals("qrts"))
        {
            return Unit.quart;
        }
        else if(strings[1].toLowerCase().equals("dash") || strings[1].toLowerCase().equals("dashes") || strings[1].toLowerCase().equals("dashs"))
        {
            return Unit.dash;
        }

        else return Unit.unit;
    }
}






























