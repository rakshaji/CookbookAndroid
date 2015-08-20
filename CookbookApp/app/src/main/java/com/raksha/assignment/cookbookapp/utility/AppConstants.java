package com.raksha.assignment.cookbookapp.utility;

/**
 * Created by Raksha on 4/24/2015.
 */
public interface AppConstants {
    String ARG_SELECTED_ITEM_ID = "selected_item_id";
    String ARG_NEW_RECIPE = "isNewRecipe";

    // RECIPE TABLE COLUMNS
    String TABLE_RECIPE = "Recipe";
    String RECIPE_ID = "RecipeId";
    String RECIPE_NAME = "RecipeTitle";
    String WEBSITE = "Website";
    String RATING = "Rating";
    String COOK_TIME = "CookTime";
    String PREP_TIME = "PrepTime";
    String TOTAL_TIME = "TotalTime";
    String CALORIES = "Calories";
    String YIELDS = "Yields";
    String NOTES = "Notes";
    String IMAGE_PATH = "ImagePath";

    //
    String TABLE_STEPS = "Steps";
    String STEP_ID = "StepId";
    String INSTRUCTION = "Instruction";

    //
    String TABLE_CUISINE = "Cuisine";
    String CUISINE_ID = "CuisineId";
    String CUISINE_NAME = "CuisineName";

    //
    String TABLE_COLLECTION = "Collection";
    String COLLECTION_ID = "CollectionId";
    String COLLECTION_NAME = "CollectionName";

    //
    String TABLE_INGREDIENT = "Ingredients";
    String INGREDIENT_ID = "IngredientId";
    String AMOUNT = "Amount";
    String UNIT = "Unit";
    String ITEM = "Item";

    String TABLE_LOOKUP_RECIPE_COLLECTION = "Lookup_Recipe_Collection";
    String TABLE_LOOKUP_RECIPE_INGREDIENTS = "Lookup_Recipe_Ingredients";
    String TABLE_LOOKUP_RECIPE_STEPS = "Lookup_Recipe_Steps ";

    String ACTION = "ActionEvent";
    String ACTION_DELETE = "Delete";
    String DATA_INGREDIENTS = "Ingredients_data";
    String DATA_STEPS = "Steps_data";

    int REQUEST_INGREDIENT_ACTIVITY = 111;
    int REQUEST_CAMERA = 112;
    int SELECT_FILE = 113;
    int REQUEST_STEPS_ACTIVITY = 114;
    int REFRESH_FRAGMENT = 115;
    String ARG_RECIPE_LIST = "RecipeList";
    int RECIPE_TAB = 0;
    int COLLECTION_TAB = 1;
    int NEW_RECIPE_ID = 0;
    String JPG_FILE_FORMAT = ".jpg";
}
