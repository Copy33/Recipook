package com.joemerhej.recipook;

import android.util.ArraySet;

import java.util.ArrayList;
import java.util.Set;


/**
 * Created by Joe Merhej on 1/21/17.
 */

public class RecipeData
{
    // Instance
    private static RecipeData Instance = null;

    // list of all recipes
    private static ArrayList<Recipe> mRecipelist = null;

    // list of all ingredients
    private static ArrayList<ArrayList<Ingredient>> mAllIngredients;

    // list of recipes per category
    private static ArrayList<Integer> mAppetizerList = null;
    private static ArrayList<Integer> mMainCourseList = null;
    private static ArrayList<Integer> mSideDishList = null;
    private static ArrayList<Integer> mDessertList = null;
    private static ArrayList<Integer> mBeverageList = null;



    // mock data
    private static String[] recipeListNames =
            {
                    "Buffalo Drumsticks",
                    "Chicken Pasta",
                    "Green Vegetable Soup",
                    "Herbed Olive Spirals",
                    "Pesto Provolone Terrine",
                    "Raspberry Pie",
                    "Toasted Almonds with Rosemary"
            };

    private static String[][] recipeListDirections =
            {
                    // Buffalo Drumsticks
                    {
                            "Place drumsticks in a 4- or 5-quart slow cooker. In a medium bowl, combine hot sauce, tomato paste, vinegar, and Worcestershire sauce. Pour over chicken in cooker.",
                            "Cover and cook on low-heat setting for 6 to 8 hours or on high-heat setting for 3 to 4 hours.",
                            "Meanwhile, in a small bowl, combine sour cream, mayonnaise, blue cheese, and cayenne pepper. Reserve half of the blue cheese dip (3/4 cup); store as directed below. Cover and chill the remaining dip until ready to serve.",
                            "Using a slotted spoon, remove drumsticks from cooker. Skim fat from cooking juices. Reserve eight of the drumsticks and 1 cup of the cooking juices; store as directed below. Serve remaining drumsticks with some of the remaining cooking juices, the remaining blue cheese dip, and celery sticks. Makes 4 servings and reserves"
                    },

                    // Chicken Pasta
                    {
                            "Drain artichoke hearts, reserving marinade, and chop them. In a large skillet, heat oil over medium-high heat; add chicken and garlic. Cook and stir until chicken is brown. Add the reserved artichoke marinade, broth, wine and dried oregano.",
                            "Bring to a boil; reduce heat. Simmer, covered, 10 minutes. Stir in chopped artichokes, roasted peppers and olives.",
                            "To serve, spoon chicken mixture over pasta. If desired, sprinkle with feta cheese."
                    },

                    // Green Vegetable Soup
                    {
                            "In 4-quart Dutch oven, heat oil over medium heat. Add onion; cook about 5 minutes or until onion is tender, stirring occasionally. Add garlic and ginger; cook and stir for 30 seconds. Add the water, broth, soybeans, peas, soy sauce, and cayenne pepper. Bring to boiling; reduce heat. Cover and simmer for 5 minutes.",
                            "Gradually add spinach to the soup until all is added. Remove from heat. Using an immersion blender, blend soup until it has a slightly chunky pureed consistency. To serve, ladle soup into shallow bowls. If desired, sprinkle with cracked pepper. Top each serving with some of the red pepper rings."
                    },

                    // Herbed Olive Spirals
                    {
                            "Prepare the hot roll mix according to package directions. After kneading, divide the dough into two portions; cover and let rest for 5 minutes. Grease a large baking sheet or line with parchment paper; set aside.",
                            "For filling, drain tomatoes, reserving oil. Chop the tomatoes. Combine tomatoes, cream cheese, olives, green onions, egg yolk, pepper, and, if desired, oregano or thyme in a mixing bowl. Stir in about 1 tablespoon reserved tomato oil, if necessary, to make a filling that is easy to spread.",
                            "Turn dough portions out onto a lightly floured surface. Roll each portion to a 14x11-inch rectangle. Spread half of the filling atop each rectangle to within 1/2 inch of edges (filling amount will seem generous). Roll up dough tightly from long sides. Seal seams. Place loaves on the prepared baking sheet with seams down. Cover and let dough rise till nearly double (about 30 to 40 minutes).",
                            "Make three or four diagonal cuts about 1/4 inch deep in loaf tops, using a sharp knife. Combine the egg and water; brush onto loaves. Bake in a 375 degrees oven about 25 minutes or till golden. Carefully remove loaves from baking sheet and cool on wire racks. Serve warm or at room temperature. Slice with a serrated knife."
                    },

                    // Pesto Provolone Terrine
                    {
                            "Line a 7-1/2 x 3-1/2x2-inch loaf pan with plastic wrap, extending wrap beyond edges of pan; set aside.",
                            "In a medium mixing bowl beat cream cheese and pesto with an electric mixer on medium speed until smooth. Lay 2 slices of the provolone cheese in bottom and slightly up sides of pan. Spread half of the pesto mixture on cheese layer. Repeat layers; top with remaining 2 slices of provolone cheese. Cover surface with plastic wrap. Weight it down with a can of soup or vegetables. Chill overnight.",
                            "To serve, remove plastic wrap from top of terrine. Invert onto a serving plate and remove plastic wrap. Cut terrine lengthwise in half. Cut crosswise into 1/2-inch-thick slices. Serve with baguette slices or crackers. If desired, garnish with basil leaves. Makes 24 to 28 servings."
                    },

                    // Raspberry Pie
                    {
                            "In a mixing bowl combine the 2 cups flour and salt. Using a pastry blender, cut in shortening until pieces are pea-size. Sprinkle 1 tablespoon of the water over part of mixture; gently toss with a fork. Push moistened dough to side of bowl. Repeat, using 1 tablespoon water at a time, until all the dough is moistened. Divide in half. Form each half into a ball.",
                            "On a lightly floured surface flatten one dough ball. Roll from center to edges into a 12-inch circle.",
                            "To transfer pastry, wrap it around the rolling pin; unroll into a 9-inch pie plate. Ease pastry into pie plate, being careful not to stretch pastry.",
                            "In a large mixing bowl combine the sugar and 1/3 cup flour. Stir in berries and lemon peel. Gently toss the berries until well coated. Transfer berry mixture to the pastry-lined pie plate.",
                            "On lightly floured surface roll remaining dough into a 12-inch circle. For a lattice crust, trim bottom pastry to 1/2 inch beyond edge of pie plate. Cut rolled pastry into 1/2-inch strips and weave strips over filling. Fold bottom crust over strip ends; trimming strips as necessary. For a 2-crust pie, trim bottom pastry to edge of pie plate. Cut slits in top crust for escape of steam; place on filling and fold edge under bottom pastry. Flute edge as desired.",
                            "If desired, brush pastry top with a little milk and sprinkle with additional sugar.",
                            "To prevent overbrowning, cover edge of pie with foil. Bake for 25 minutes. Remove foil. Bake in a 375 degree F oven for 25 to 30 minutes more or until top is golden. Cool on wire rack. Makes 8 servings."
                    },

                    // Toasted Almonds with Rosemary
                    {
                            "Spread almonds in a single layer on a baking sheet. Bake in a 350 degree F oven about 10 minutes or until nuts are lightly toasted and fragrant.",
                            "Meanwhile, in a medium saucepan melt margarine or butter over medium heat until sizzling. Remove from heat. Stir in rosemary, sugar, salt, and red pepper. Add almonds to butter mixture and toss to coat. Cool slightly before serving. Makes 16 servings."
                    },
            };


    private static Category[][] recipeListCategories =
            {
                    // Buffalo Drumsticks
                    {
                            Category.MainCourse, Category.SideDish
                    },

                    // Chicken Pasta
                    {
                            Category.MainCourse
                    },

                    // Green Vegetable Soup
                    {
                            Category.MainCourse, Category.Appetizer
                    },

                    // Herbed Olive Spirals
                    {
                            Category.Appetizer, Category.SideDish
                    },

                    // Pesto Provolone Terrine
                    {
                            Category.Appetizer
                    },

                    // Raspberry Pie
                    {
                            Category.Dessert
                    },

                    // Toasted Almonds with Rosemary
                    {
                            Category.Appetizer, Category.SideDish
                    }
            };

    private static int[] preparationTimes = {90, 120, 100, 60, 75, 30, 25};
    private static int[] cookingTimes = {20, 45, 140, 242, 12, 10, 5};

    private static void setupIngredientsArrayLists()
    {
        mAllIngredients = new ArrayList<ArrayList<Ingredient>>();
        ArrayList<Ingredient> BuffaloDrumsticksIngredients = new ArrayList<>();
        ArrayList<Ingredient> ChickenPastaIngredients = new ArrayList<>();
        ArrayList<Ingredient> GreenVegetableSoupIngredients = new ArrayList<>();
        ArrayList<Ingredient> HerbedOliveSpiralsIngredients = new ArrayList<>();
        ArrayList<Ingredient> PestoProvoloneTerrineIngredients = new ArrayList<>();
        ArrayList<Ingredient> RaspberryPieIngredients = new ArrayList<>();
        ArrayList<Ingredient> ToastedAlmondsWithRosemaryIngredients = new ArrayList<>();

        BuffaloDrumsticksIngredients.add(new Ingredient(16, Unit.unit, "chicken drumsticks"));
        BuffaloDrumsticksIngredients.add(new Ingredient(1, Unit.unit, "16 ounce bottle buffalo wing hot sauce"));
        BuffaloDrumsticksIngredients.add(new Ingredient(0.25, Unit.cup, "tomato paste"));
        BuffaloDrumsticksIngredients.add(new Ingredient(2, Unit.tbsp, "cider vinegar"));
        BuffaloDrumsticksIngredients.add(new Ingredient(2, Unit.tbsp, "worcestershire sauce"));
        BuffaloDrumsticksIngredients.add(new Ingredient(1, Unit.unit, "8 ounce carton dairy sour cream"));
        BuffaloDrumsticksIngredients.add(new Ingredient(0.5, Unit.cup, "mayonnaise"));
        BuffaloDrumsticksIngredients.add(new Ingredient(0.5, Unit.cup, "crumbled blue cheese"));
        BuffaloDrumsticksIngredients.add(new Ingredient(0.5, Unit.tsp, "cayenne pepper"));
        BuffaloDrumsticksIngredients.add(new Ingredient(2, Unit.unit, "celery sticks"));

        ChickenPastaIngredients.add(new Ingredient(1, Unit.unit, "6 ounce jar marinated artichoke hearts"));
        ChickenPastaIngredients.add(new Ingredient(1, Unit.tbsp, "olive oil"));
        ChickenPastaIngredients.add(new Ingredient(12, Unit.oz, "skinless, boneless chicken breasts"));
        ChickenPastaIngredients.add(new Ingredient(3, Unit.unit, "garlic cloves"));
        ChickenPastaIngredients.add(new Ingredient(0.25, Unit.cup, "chicken broth"));
        ChickenPastaIngredients.add(new Ingredient(0.25, Unit.cup, "dry white wine"));
        ChickenPastaIngredients.add(new Ingredient(1, Unit.tsp, "dried oregano, crushed"));
        ChickenPastaIngredients.add(new Ingredient(1, Unit.unit, "7 ounce jar roasted peppers"));
        ChickenPastaIngredients.add(new Ingredient(0.25, Unit.cup, "pitted kalamata olives"));
        ChickenPastaIngredients.add(new Ingredient(3, Unit.cup, "hot cooked campanelle"));
        ChickenPastaIngredients.add(new Ingredient(0.25, Unit.cup, "crumbled feta cheese"));

        GreenVegetableSoupIngredients.add(new Ingredient(2, Unit.tsp, "canola oil"));
        GreenVegetableSoupIngredients.add(new Ingredient(1, Unit.unit, "medium onion, chopped"));
        GreenVegetableSoupIngredients.add(new Ingredient(3, Unit.unit, "cloves garlic"));
        GreenVegetableSoupIngredients.add(new Ingredient(1.5, Unit.tsp, "grated fresh ginger"));
        GreenVegetableSoupIngredients.add(new Ingredient(3.5, Unit.cup, "water"));
        GreenVegetableSoupIngredients.add(new Ingredient(1.25, Unit.cup, "less sodium vegetable broth"));
        GreenVegetableSoupIngredients.add(new Ingredient(1, Unit.unit, "12 ounce package frozen shelled soybeans"));
        GreenVegetableSoupIngredients.add(new Ingredient(1, Unit.unit, "9 ounce package frozen peas"));
        GreenVegetableSoupIngredients.add(new Ingredient(2, Unit.tbsp, "reduced sodium soy sauce"));
        GreenVegetableSoupIngredients.add(new Ingredient(0.25, Unit.tsp, "cayenne pepper"));
        GreenVegetableSoupIngredients.add(new Ingredient(1, Unit.unit, "9 ounce package fresh spinach"));
        GreenVegetableSoupIngredients.add(new Ingredient(1, Unit.unit, "medium red sweet pepper"));

        HerbedOliveSpiralsIngredients.add(new Ingredient(1, Unit.unit, "16 ounce hot roll mix"));
        HerbedOliveSpiralsIngredients.add(new Ingredient(0.33, Unit.cup, "oil pack dried tomatoes"));
        HerbedOliveSpiralsIngredients.add(new Ingredient(2, Unit.unit, "3 ounce cream cheese, softened"));
        HerbedOliveSpiralsIngredients.add(new Ingredient(0.33, Unit.cup, "finely chopped pitted ripe olives and/or green olives"));
        HerbedOliveSpiralsIngredients.add(new Ingredient(0.33, Unit.cup, "chopped green onions"));
        HerbedOliveSpiralsIngredients.add(new Ingredient(1, Unit.unit, "egg yolk"));
        HerbedOliveSpiralsIngredients.add(new Ingredient(1, Unit.tsp, "cracked black pepper"));
        HerbedOliveSpiralsIngredients.add(new Ingredient(0.5, Unit.tsp, "dried oregano or thyme, crushed"));
        HerbedOliveSpiralsIngredients.add(new Ingredient(1, Unit.unit, "slightly beaten egg"));
        HerbedOliveSpiralsIngredients.add(new Ingredient(1, Unit.tbsp, "water"));

        PestoProvoloneTerrineIngredients.add(new Ingredient(1, Unit.unit, "8 ounce package cream cheese, softened"));
        PestoProvoloneTerrineIngredients.add(new Ingredient(0.33, Unit.cup, "purchased basil pesto or dried tomato pesto"));
        PestoProvoloneTerrineIngredients.add(new Ingredient(6, Unit.unit, "thinly sliced provolone cheese"));
        PestoProvoloneTerrineIngredients.add(new Ingredient(0, Unit.unit, "thin baguette slices"));

        RaspberryPieIngredients.add(new Ingredient(2, Unit.cup, "all-purpose flour"));
        RaspberryPieIngredients.add(new Ingredient(0.5, Unit.tsp, "salt"));
        RaspberryPieIngredients.add(new Ingredient(0.66, Unit.cup, "shortening"));
        RaspberryPieIngredients.add(new Ingredient(6, Unit.tbsp, "water"));
        RaspberryPieIngredients.add(new Ingredient(1, Unit.cup, "sugar"));
        RaspberryPieIngredients.add(new Ingredient(0.33, Unit.cup, "all-purpose white flour"));
        RaspberryPieIngredients.add(new Ingredient(5, Unit.cup, "raspberries"));
        RaspberryPieIngredients.add(new Ingredient(2, Unit.tsp, "finely shredded lemon peel"));
        RaspberryPieIngredients.add(new Ingredient(0, Unit.unit, "milk"));
        RaspberryPieIngredients.add(new Ingredient(0, Unit.unit, "sugar"));

        ToastedAlmondsWithRosemaryIngredients.add(new Ingredient(8, Unit.oz, "unblanched almonds oro pecan halves"));
        ToastedAlmondsWithRosemaryIngredients.add(new Ingredient(1.5, Unit.tsp, "margarine or butter"));
        ToastedAlmondsWithRosemaryIngredients.add(new Ingredient(1, Unit.tbsp, "finely snipped fresh rosemary"));
        ToastedAlmondsWithRosemaryIngredients.add(new Ingredient(1.5, Unit.tsp, "brown sugar"));
        ToastedAlmondsWithRosemaryIngredients.add(new Ingredient(0.5, Unit.tsp, "salt"));
        ToastedAlmondsWithRosemaryIngredients.add(new Ingredient(0.25, Unit.tsp, "ground red pepper"));


        mAllIngredients.add(BuffaloDrumsticksIngredients);
        mAllIngredients.add(ChickenPastaIngredients);
        mAllIngredients.add(GreenVegetableSoupIngredients);
        mAllIngredients.add(HerbedOliveSpiralsIngredients);
        mAllIngredients.add(PestoProvoloneTerrineIngredients);
        mAllIngredients.add(RaspberryPieIngredients);
        mAllIngredients.add(ToastedAlmondsWithRosemaryIngredients);
    }

    private static void setupCategoriesArrayLists()
    {
        mAppetizerList = new ArrayList<>();
        mMainCourseList = new ArrayList<>();
        mSideDishList = new ArrayList<>();
        mDessertList = new ArrayList<>();
        mBeverageList = new ArrayList<>();

        mAppetizerList.add(2);
        mAppetizerList.add(3);
        mAppetizerList.add(4);
        mAppetizerList.add(6);
        mMainCourseList.add(0);
        mMainCourseList.add(1);
        mMainCourseList.add(2);
        mSideDishList.add(0);
        mSideDishList.add(3);
        mSideDishList.add(6);
        mDessertList.add(5);
    }


    // instance method will create the instance or return it if it exists
    public static RecipeData Instance()
    {
        if(Instance == null)
        {
            Instance = new RecipeData();
            mRecipelist = new ArrayList<>();

            setupIngredientsArrayLists();
            setupCategoriesArrayLists();

            for (int i = 0; i < recipeListNames.length; ++i)
            {
                Recipe recipe = new Recipe();
                recipe.name = recipeListNames[i];
                recipe.imageName = recipeListNames[i].replaceAll("\\s+", "").toLowerCase();
                recipe.imageUri = "android.resource://com.joemerhej.recipook/drawable/" + recipe.imageName;
                recipe.preparationTimeMinutes = preparationTimes[i];
                recipe.cookingTimeMinutes = cookingTimes[i];

                for(int j = 0; j < recipeListCategories[i].length; ++j)
                {
                    recipe.categories.add(recipeListCategories[i][j]);
                }

                for(int j = 0; j < recipeListDirections[i].length; ++j)
                {
                    recipe.directions.add(recipeListDirections[i][j]);
                }

                for(int j = 0; j < mAllIngredients.get(i).size(); ++j)
                {
                    recipe.ingredients.add(mAllIngredients.get(i).get(j));
                }

                mRecipelist.add(recipe);
            }

            return Instance;
        }
        else
            return Instance;
    }

    // method to get a point to the recipe list
    public ArrayList<Recipe> getRecipeList()
    {
        return mRecipelist;
    }

    // method to remove a recipe at a given index
    public void removeRecipe(int recipeIndex)
    {
        mRecipelist.remove(recipeIndex);
    }
}































