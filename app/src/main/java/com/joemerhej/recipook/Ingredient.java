package com.joemerhej.recipook;


/**
 * Created by Joe Merhej on 1/22/17.
 */

enum Unit
{
    oz,
    lb,
    pint,
    cup,
    tbsp,
    tsp,
    kg,
    gram,
    liter,
    ml,
    unit,
    quart,
    dash
}

public class Ingredient
{
    double quantity;
    Unit unit;
    String name;

    Ingredient()
    {
    }

    Ingredient(double number_, Unit unit_, String name_)
    {
        quantity = number_;
        unit = unit_;
        name = name_;
    }
}
