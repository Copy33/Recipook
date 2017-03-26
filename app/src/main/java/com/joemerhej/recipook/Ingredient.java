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

enum ShoppingStatus
{
    NONE,
    ADDED,
    CHECKED
}

public class Ingredient
{
    // ingredient details
    double mQuantity;
    Unit mUnit;
    String mName;
    ShoppingStatus mShoppingStatus;

    
    Ingredient(double Number, Unit Unit, String Name, ShoppingStatus ShoppingStatus)
    {
        mQuantity = Number;
        mUnit = Unit;
        mName = Name;
        mShoppingStatus = ShoppingStatus;
    }
}
