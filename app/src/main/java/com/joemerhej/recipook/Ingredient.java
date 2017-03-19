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
    dash,

/*  TODO:add support for dozen -> doz
    envelope -> env
    carton/container -> ctn
    package -> pkg
    gallon -> gal*/
}

public class Ingredient
{
    double quantity;
    Unit unit;
    String name;

    Ingredient()
    {
    }

    Ingredient(double Number, Unit Unit, String Name)
    {
        quantity = Number;
        unit = Unit;
        name = Name;
    }
}
