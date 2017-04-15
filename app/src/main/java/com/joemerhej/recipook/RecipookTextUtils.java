package com.joemerhej.recipook;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Joe Merhej on 1/29/17.
 */

public final class RecipookTextUtils
{
    private static RecipookTextUtils Instance = null;

    private static ArrayList<String> mSupportedUnitTexts = new ArrayList<>(
            Arrays.asList("oz", "ounce", "ounces", "lb","lbs", "pd", "pds", "pound", "pounds", "pint", "pints", "pt", "pts", "cup", "cups",
                            "cp", "cps", "tbsp", "tbsps", "tablespoon", "tablespoons", "tsp", "tsps", "teaspoon", "teaspoons", "kg", "kgs",
                            "kilogram", "kilograms", "k", "ks", "kilo", "kilos", "g", "gs", "gram", "grams", "l", "ls", "liter", "liters",
                            "ml", "mls", "milliliter", "milliliters", "milli", "millis", "unit", "units", "quart", "quarts", "q", "qs",
                            "qrt", "qrts", "dash", "dashes", "dashs", "d")
            );


    public static RecipookTextUtils Instance()
    {
        if(Instance == null)
            Instance = new RecipookTextUtils();

        return Instance;
    }


    // method that will return ingredient from full ingredient string
    public Ingredient GetIngredientFromIngredientString(String ingredientString)
    {
        if(ingredientString == null || ingredientString.isEmpty())
            return null;

        double quantity = GetQuantityFromQuantityString(ingredientString);
        Unit unit = GetUnitFromQuantityString(ingredientString);
        String name = "";

        ingredientString = ingredientString.trim();
        String [] strings = ingredientString.split("\\s+");

        if(strings.length == 0)
            return null;

        if(isValidQuantity(strings[0]))
        {
            if(strings.length > 1)
            {
                if(isValidUnit(strings[1]))
                {
                    if(strings.length > 2)
                    {
                        for(int i=2; i<strings.length; ++i)
                            name += strings[i] + " ";

                        return new Ingredient(quantity, unit, name, ShoppingStatus.NONE);
                    }
                    else return new Ingredient(quantity, unit, "", ShoppingStatus.NONE);
                }
                else
                {
                    for(int i=1; i<strings.length; ++i)
                        name += strings[i] + " ";

                    return new Ingredient(quantity, unit, name, ShoppingStatus.NONE);
                }
            }
            else return new Ingredient(quantity, unit, "", ShoppingStatus.NONE);
        }
        else if (isValidUnit(strings[0]))
        {
            if(strings.length > 1)
            {
                for(int i=1; i<strings.length; ++i)
                    name += strings[i] + " ";

                return new Ingredient(1, unit, name, ShoppingStatus.NONE);
            }
            else return new Ingredient(1, unit, "", ShoppingStatus.NONE);
        }
        else return new Ingredient(0, unit, ingredientString, ShoppingStatus.NONE);


    }

    // method that will format the unit and return it as a string.
    // this will take care of plurals and ommit the word "unit"
    public String GetUnitStringFromIngredient(Ingredient ingredient)
    {
        String ingredientUnit = ingredient.mUnit.toString();

        // check if "s" or "es" need to be added for plural
        if(ingredient.mQuantity > 1.0)
        {
            if(ingredient.mUnit == Unit.cup || ingredient.mUnit == Unit.lb || ingredient.mUnit == Unit.kg || ingredient.mUnit == Unit.gram
                    || ingredient.mUnit == Unit.liter || ingredient.mUnit == Unit.quart || ingredient.mUnit == Unit.pint
                    || ingredient.mUnit == Unit.tbsp || ingredient.mUnit == Unit.tsp)
            {
                ingredientUnit += "s";
            }
            else if(ingredient.mUnit == Unit.dash)
            {
                ingredientUnit += "es";
            }
        }

        // in case of "unit" return an empty text
        if(ingredient.mUnit == Unit.unit)
        {
            ingredientUnit = "";
        }

        return ingredientUnit;
    }

    // method that will format the quantity and return it as a string.
    // this will take care of ".0" after ints and will make fractions.
    public String GetQuantityStringFromIngredient(Ingredient ingredient)
    {
        String ingredientQuantity = String.valueOf(ingredient.mQuantity);

        // truncate to 2 decimal places
        ingredient.mQuantity = Math.floor(ingredient.mQuantity * 100) / 100;

        if(ingredient.mQuantity == 0)
            return "";

        if(ingredient.mQuantity % 1 == 0)
        {
            int ingredientQuantityInt = (int) ingredient.mQuantity;
            ingredientQuantity = String.valueOf(ingredientQuantityInt);
            return ingredientQuantity;
        }

        if(Double.compare(ingredient.mQuantity, 0.25) == 0)
        {
            ingredientQuantity = "1/4";
        }
        else if(Double.compare(ingredient.mQuantity, 0.33) == 0)
        {
            ingredientQuantity = "1/3";
        }
        else if(Double.compare(ingredient.mQuantity, 0.5) == 0)
        {
            ingredientQuantity = "1/2";
        }
        else if(Double.compare(ingredient.mQuantity, 0.66) == 0)
        {
            ingredientQuantity = "2/3";
        }
        else if(Double.compare(ingredient.mQuantity, 0.75) == 0)
        {
            ingredientQuantity = "3/4";
        }

        return ingredientQuantity;
    }

    // returns if the string is a valid quantity
    public boolean isValidQuantity(String s)
    {
        s = s.trim();

        String[] fraction = s.split("/");
        if(fraction.length == 2)
        {
            if(fraction[0].matches("-?\\d+(\\.\\d+)?") && fraction[1].matches("-?\\d+(\\.\\d+)?"))
                return true;
        }

        if(s.matches("-?\\d+(\\.\\d+)?"))
        {
            return true;
        }

        return false;
    }

    // returns is the string is a valid unit
    public boolean isValidUnit(String s)
    {
        s = s.trim();

        s = s.toLowerCase();
        return mSupportedUnitTexts.contains(s);
    }

    // method that will get quantity from quantity string
    public double GetQuantityFromQuantityString(String s)
    {
        if(s == null || s.isEmpty())
            return 0;

        s = s.trim();

        String[] strings = s.split("\\s+");

        if(strings.length == 0)
            return 0;

        // check for fraction
        String[] fraction = strings[0].split("/");
        if(fraction.length > 1)
        {
            if(fraction[0].matches("\\d+(\\.\\d+)?") && fraction[1].matches("\\d+(\\.\\d+)?"))
                return Math.floor(Double.valueOf(fraction[0])/Double.valueOf(fraction[1])*100)/100;
            else return 0;
        }

        else if(strings[0].matches("-?\\d+(\\.\\d+)?"))
        {
            double result = Math.floor(Double.valueOf(strings[0])*100)/100;
            if(result > 0)
                return result;
            else
                return -result;
        }
        else return 0;
    }

    // method that will get unit from quantity string
    public Unit GetUnitFromQuantityString(String s)
    {
        if(s == null || s.isEmpty())
            return Unit.unit;

        s = s.trim();

        String[] strings = s.split("\\s+");
        String evaluate;

        if(strings.length == 0)
            return Unit.unit;

        if(GetQuantityFromQuantityString(strings[0]) == 0)
        {
            if(!isValidUnit(strings[0]))
                return Unit.unit;
            else
                evaluate = strings[0];
        }
        else
        {
            if(strings.length > 1)
                evaluate = strings[1];
            else
                return Unit.unit;
        }

        if(evaluate.toLowerCase().equals("oz") || evaluate.toLowerCase().equals("ounce") || evaluate.toLowerCase().equals("ounces"))
        {
            return Unit.oz;
        }
        else if(evaluate.toLowerCase().equals("lb") || evaluate.toLowerCase().equals("lbs") || evaluate.toLowerCase().equals("pd")
                || evaluate.toLowerCase().equals("pds") || evaluate.toLowerCase().equals("pound") || evaluate.toLowerCase().equals("pounds"))
        {
            return Unit.lb;
        }
        else if(evaluate.toLowerCase().equals("pint") || evaluate.toLowerCase().equals("pints") || evaluate.toLowerCase().equals("pt") || evaluate.toLowerCase().equals("pts"))
        {
            return Unit.pint;
        }
        else if(evaluate.toLowerCase().equals("cup") || evaluate.toLowerCase().equals("cups") || evaluate.toLowerCase().equals("cp") || evaluate.toLowerCase().equals("cps"))
        {
            return Unit.cup;
        }
        else if(evaluate.toLowerCase().equals("tbsp") || evaluate.toLowerCase().equals("tbsps") || evaluate.toLowerCase().equals("tablespoon") || evaluate.toLowerCase().equals("tablespoons"))
        {
            return Unit.tbsp;
        }
        else if(evaluate.toLowerCase().equals("tsp") || evaluate.toLowerCase().equals("tsps") || evaluate.toLowerCase().equals("teaspoon") || evaluate.toLowerCase().equals("teaspoons"))
        {
            return Unit.tsp;
        }
        else if(evaluate.toLowerCase().equals("kg") || evaluate.toLowerCase().equals("kgs") || evaluate.toLowerCase().equals("kilogram") || evaluate.toLowerCase().equals("k")
                || evaluate.toLowerCase().equals("ks") || evaluate.toLowerCase().equals("kilograms") || evaluate.toLowerCase().equals("kilo") || evaluate.toLowerCase().equals("kilos"))
        {
            return Unit.kg;
        }
        else if(evaluate.toLowerCase().equals("g") || evaluate.toLowerCase().equals("gs") || evaluate.toLowerCase().equals("gram") || evaluate.toLowerCase().equals("grams"))
        {
            return Unit.gram;
        }
        else if(evaluate.toLowerCase().equals("l") || evaluate.toLowerCase().equals("liter") || evaluate.toLowerCase().equals("ls") || evaluate.toLowerCase().equals("liters"))
        {
            return Unit.liter;
        }
        else if(evaluate.toLowerCase().equals("ml") || evaluate.toLowerCase().equals("mls") || evaluate.toLowerCase().equals("milliliter") || evaluate.toLowerCase().equals("milliliters")
                || evaluate.toLowerCase().equals("milli") || evaluate.toLowerCase().equals("millis"))
        {
            return Unit.ml;
        }
        else if(evaluate.toLowerCase().equals("unit") || evaluate.toLowerCase().equals("units"))
        {
            return Unit.unit;
        }
        else if(evaluate.toLowerCase().equals("quart") || evaluate.toLowerCase().equals("quarts") || evaluate.toLowerCase().equals("q") || evaluate.toLowerCase().equals("qs")
                || evaluate.toLowerCase().equals("qrt") || evaluate.toLowerCase().equals("qrts"))
        {
            return Unit.quart;
        }
        else if(evaluate.toLowerCase().equals("dash") || evaluate.toLowerCase().equals("dashes") || evaluate.toLowerCase().equals("dashs") || evaluate.toLowerCase().equals("d"))
        {
            return Unit.dash;
        }
        else return Unit.unit;
    }

    // method that will return indices of quantity, unit, and name of ingredient string
    public int [] GetQUNIndicesFromIngredientString(String s)
    {
        int [] result = {-1, -1, -1};

        if(s == null || s.isEmpty())
            return result;

        String trimmed = s.trim();

        String[] strings = trimmed.split("\\s+");

        if(isValidQuantity(strings[0]))
        {
            result[0] = s.indexOf(strings[0]);

            if(strings.length > 1 && isValidUnit(strings[1]))
            {
                result[1] = s.indexOf(strings[1]);

                if(strings.length > 2)
                {
                    result[2] = s.indexOf(strings[2], result[1]+strings[1].length());
                }
            }
            else if(strings.length > 1)
            {
                result[2] = s.indexOf(strings[1]);
            }
        }
        else if(isValidUnit(strings[0]))
        {
            result[1] = s.indexOf(strings[0]);

            if(strings.length > 1)
            {
                result[2] = s.indexOf(strings[1], result[1]+strings[0].length());
            }
        }
        else
        {
            result[2] = s.indexOf(strings[0]);
        }

        return result;
    }
}








































