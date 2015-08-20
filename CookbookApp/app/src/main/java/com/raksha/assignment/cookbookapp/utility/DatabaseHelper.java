package com.raksha.assignment.cookbookapp.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.raksha.assignment.cookbookapp.pojo.Collection;
import com.raksha.assignment.cookbookapp.pojo.Cuisine;
import com.raksha.assignment.cookbookapp.pojo.Ingredient;
import com.raksha.assignment.cookbookapp.pojo.Recipe;
import com.raksha.assignment.cookbookapp.pojo.SearchCriteria;
import com.raksha.assignment.cookbookapp.pojo.Steps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Raksha on 3/13/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.toString();
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.raksha.assignment.cookbookapp/databases/";
    private static String DB_NAME = "Cookbook.sqlite";
    private final Context context;
    private SQLiteDatabase sqLiteDatabase;
    private static DatabaseHelper mInstance;

    public static DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;

        try {
            createDataBase();
        } catch (IOException ioe) {
            Log.e(TAG, "Unable to create database", ioe);
        }

        try {
            openDataBase();
        } catch (SQLException sqle) {
            Log.e(TAG, "Problem in opening database", sqle);
        }
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if (dbExist) {
            //do nothing - database already exist
        } else {
            //By calling this method an empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(TAG, "Error copying database", e);
            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            File dbFile = new File(DB_PATH);

            if(!dbFile.exists()){
                dbFile.mkdirs();
            }
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.e(TAG, "Database does't exist yet.", e);
            return false;
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transferring bytestream.
     */
    private void copyDataBase() throws IOException {
        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        //Open the database
        String fullDatabasePath = DB_PATH + DB_NAME;
        sqLiteDatabase = SQLiteDatabase.openDatabase(fullDatabasePath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (sqLiteDatabase != null)
            sqLiteDatabase.close();

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
////////////////////////////////////////////////////////////////////////////////////////////////////
    public long insertRecipe(Recipe recipe) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = ApplicationHelper.getRecipeContentValues(recipe);

        long recipeId = database.replace(AppConstants.TABLE_RECIPE, null, values);

        deleteAllIngredientsForRecipe(String.valueOf(recipeId));
        ArrayList<Ingredient> ingredientArrayList = recipe.getIngredients();
        if(ingredientArrayList != null) {
            for (Ingredient ingredient : ingredientArrayList) {
                insertIngredient(ingredient, recipeId);
            }
        }

        deleteAllStepsForRecipe(String.valueOf(recipeId));
        ArrayList<Steps> stepsArrayList = recipe.getSteps();
        if(stepsArrayList != null) {
            for (Steps step : stepsArrayList) {
                insertSteps(step, recipeId);
            }
        }

        moveRecipeToCollections(recipeId, recipe.getCollections());

        return recipeId;
    }

    public void moveRecipeToCollections(long recipeId, ArrayList<Collection> collections){
        deleteAllCollectionForRecipe(String.valueOf(recipeId));
        ArrayList<Collection> collectionArrayList = collections;
        if(collectionArrayList != null) {
            for (Collection collection : collectionArrayList) {
                insertIntoLookupRecipeCollection(collection.getId(), recipeId);
            }
        }
    }

    public long insertIntoLookupRecipeCollection(long collectionId, long recipeId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppConstants.COLLECTION_ID, collectionId);
        values.put(AppConstants.RECIPE_ID, recipeId);
        long lookupId = database.replace(AppConstants.TABLE_LOOKUP_RECIPE_COLLECTION, null, values);
        return lookupId;
    }

    public long insertIngredient(Ingredient ingredient, long recipeId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = ApplicationHelper.getIngredientContentValues(ingredient);

        long ingredientId = database.replace(AppConstants.TABLE_INGREDIENT, null, values);
        insertIntoLookupRecipeIngredient(recipeId, ingredientId);

        return ingredientId;
    }

    public long insertIntoLookupRecipeIngredient(long recipeId, long ingredientId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppConstants.INGREDIENT_ID, ingredientId);
        values.put(AppConstants.RECIPE_ID, recipeId);
        long lookupId = database.replace(AppConstants.TABLE_LOOKUP_RECIPE_INGREDIENTS, null, values);
        return lookupId;
    }

    public long insertSteps(Steps steps, long recipeId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = ApplicationHelper.getStepsContentValues(steps);

        long ingredientId = database.replace(AppConstants.TABLE_STEPS, null, values);
        insertIntoLookupRecipeSteps(recipeId, ingredientId);

        return ingredientId;
    }

    public long insertIntoLookupRecipeSteps(long recipeId, long stepId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppConstants.STEP_ID, stepId);
        values.put(AppConstants.RECIPE_ID, recipeId);
        long lookupId = database.replace(AppConstants.TABLE_LOOKUP_RECIPE_STEPS, null, values);
        return lookupId;
    }

    public long insertRecipeInfoIfNotExist(Recipe recipe) {
        if(isRecipeExist(String.valueOf(recipe.getId()))){
            return -1;
        }
        return insertRecipe(recipe);
    }

    public long updateRecipe(Recipe recipe) {
        return insertRecipe(recipe);
    }

    public void deleteRecipe(String recipeId) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM "+AppConstants.TABLE_RECIPE
                +" WHERE "+AppConstants.RECIPE_ID+" = " + recipeId ;
        database.execSQL(deleteQuery);

        deleteAllCollectionForRecipe(recipeId);
        deleteAllIngredientsForRecipe(recipeId);
        deleteAllStepsForRecipe(recipeId);
    }

    public void deleteIngredients(String recipeId) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery =
                "DELETE FROM " + AppConstants.TABLE_INGREDIENT
                        + " WHERE "+AppConstants.INGREDIENT_ID+" IN (SELECT "+AppConstants
                        .INGREDIENT_ID
                        +" FROM "+ AppConstants.TABLE_RECIPE
                        + " as r inner join "+ AppConstants.TABLE_LOOKUP_RECIPE_INGREDIENTS
                        + " as i on i."+ AppConstants.RECIPE_ID + "= r." +AppConstants
                        .RECIPE_ID
                        + " where r."+AppConstants.RECIPE_ID+" = "+recipeId+")";

        database.execSQL(deleteQuery);
    }

    public void deleteSteps(String recipeId) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery =
                "DELETE FROM " + AppConstants.TABLE_STEPS
                        + " WHERE "+AppConstants.STEP_ID+" IN (SELECT "+AppConstants
                        .STEP_ID
                        +" FROM "+ AppConstants.TABLE_RECIPE
                        + " as r inner join "+ AppConstants.TABLE_LOOKUP_RECIPE_STEPS
                        + " as i on i."+ AppConstants.RECIPE_ID + "= r." +AppConstants
                        .RECIPE_ID
                        + " where r."+AppConstants.RECIPE_ID+" = "+recipeId+")";

        database.execSQL(deleteQuery);
    }

    public void deleteAllCollectionForRecipe(String recipeId) {
        deleteFromCollectionLookupForRecipe(recipeId);
    }

    public void deleteRecipeFromCollection(long recipeId, long collectionId) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + AppConstants.TABLE_LOOKUP_RECIPE_COLLECTION + " WHERE "
                + AppConstants.RECIPE_ID + " = " +  recipeId + " AND " + AppConstants
                .COLLECTION_ID + " = "+collectionId;
        database.execSQL(deleteQuery);
    }

    public void deleteCollection(long collectionId) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + AppConstants.TABLE_LOOKUP_RECIPE_COLLECTION + " WHERE "
                 + AppConstants.COLLECTION_ID + " = "+collectionId;
        database.execSQL(deleteQuery);

        deleteQuery = "DELETE FROM " + AppConstants.TABLE_COLLECTION + " WHERE "
                + AppConstants.COLLECTION_ID + " = "+collectionId;
        database.execSQL(deleteQuery);
    }

    public void deleteFromCollectionLookupForRecipe(String recipeId) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + AppConstants.TABLE_LOOKUP_RECIPE_COLLECTION + " WHERE "
                + AppConstants.RECIPE_ID + " =" + " " + recipeId;
        database.execSQL(deleteQuery);
    }

    public void deleteAllIngredientsForRecipe(String recipeId) {
        deleteFromIngredientLookupForRecipe(recipeId);
        deleteIngredients(recipeId);
    }

    public void deleteFromIngredientLookupForRecipe(String recipeId) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + AppConstants.TABLE_LOOKUP_RECIPE_INGREDIENTS + " WHERE "
                + AppConstants.RECIPE_ID + " = "+recipeId;
        database.execSQL(deleteQuery);
    }

    public void deleteAllStepsForRecipe(String recipeId) {
        deleteFromStepsLookupForRecipe(recipeId);
        deleteSteps(recipeId);
    }

    public void deleteFromStepsLookupForRecipe(String recipeId) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM "+AppConstants.TABLE_LOOKUP_RECIPE_STEPS+" WHERE "
                +AppConstants.RECIPE_ID+" = " + recipeId;
        database.execSQL(deleteQuery);
    }

    public ArrayList<Recipe> getAllRecipes(boolean sortByTitle, boolean sortByLastAdded, long collectionId) {
        ArrayList<Recipe> recipeList = new ArrayList<>();
        String selectQuery = "SELECT * FROM "+AppConstants.TABLE_RECIPE+" AS recipe";
        if(collectionId != 0){
            selectQuery += " INNER JOIN "+AppConstants.TABLE_LOOKUP_RECIPE_COLLECTION +" AS lookup"
                        +" ON lookup."+AppConstants.RECIPE_ID+" = recipe."+AppConstants .RECIPE_ID
                        +" WHERE lookup."+AppConstants.COLLECTION_ID+" = "+collectionId;
        }

        if(sortByTitle)
            selectQuery += " ORDER BY recipe."+AppConstants.RECIPE_NAME+" ASC";
        else if(sortByLastAdded)
            selectQuery += " ORDER BY recipe."+AppConstants.RECIPE_ID+" DESC";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = new Recipe();
                ApplicationHelper.setRecipeFromCursorData(cursor, recipe);

                // set cuisine
                String cuisineName = getCuisineByCuisineId(recipe.getCuisineId());
                recipe.setCuisineName(cuisineName);

                recipeList.add(recipe);
            }
            while (cursor.moveToNext());
        }
        return recipeList;
    }

    public Recipe getRecipeByRecipeId(String recipeId) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+AppConstants.TABLE_RECIPE
                            +" WHERE "+AppConstants.RECIPE_ID+" = " + recipeId ;
        Cursor cursor = database.rawQuery(selectQuery, null);
        Recipe recipe = new Recipe();
        if (cursor.moveToFirst()) {
            do {
                ApplicationHelper.setRecipeFromCursorData(cursor, recipe);
            } while (cursor.moveToNext());
        }

        // set ingredients
        ArrayList<Ingredient> ingredientArrayList = getAllIngredientsByRecipeId
                (recipe.getId());
        recipe.setIngredients(ingredientArrayList);

        // set steps
        ArrayList<Steps> stepsArrayList = getAllStepsByRecipeId
                (recipe.getId());
        recipe.setSteps(stepsArrayList);

        // set collection
        ArrayList<Collection> collectionArrayList = getCollectionByRecipeId(recipe.getId());
        recipe.setCollections(collectionArrayList);

        // set cuisine
        if(recipe.getCuisineId() != null) {
            String cuisineName = getCuisineByCuisineId(recipe.getCuisineId());
            recipe.setCuisineName(cuisineName);
        }

        return recipe;
    }

    public ArrayList<Recipe> getRecipeByRecipeIds(String recipeIds) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+AppConstants.TABLE_RECIPE
                +" WHERE "+AppConstants.RECIPE_ID+" IN (" + recipeIds +")";
        Cursor cursor = database.rawQuery(selectQuery, null);
        ArrayList<Recipe> recipes = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = new Recipe();
                ApplicationHelper.setRecipeFromCursorData(cursor, recipe);
                // set ingredients
                ArrayList<Ingredient> ingredientArrayList = getAllIngredientsByRecipeId
                        (recipe.getId());
                recipe.setIngredients(ingredientArrayList);

                // set steps
                ArrayList<Steps> stepsArrayList = getAllStepsByRecipeId
                        (recipe.getId());
                recipe.setSteps(stepsArrayList);

                // set collection
                ArrayList<Collection> collectionArrayList = getCollectionByRecipeId(recipe.getId());
                recipe.setCollections(collectionArrayList);

                // set cuisine
                if(recipe.getCuisineId() != null) {
                    String cuisineName = getCuisineByCuisineId(recipe.getCuisineId());
                    recipe.setCuisineName(cuisineName);
                }
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }

        return recipes;
    }

    public int getNextId(String tableName, String columnName) {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT IFNULL(MAX("+columnName+"), 0)+1 FROM '"+tableName+"'";
        Cursor cursor = database.rawQuery(query, null);

        int id = 0;
        if (cursor.moveToFirst())
        {
            do
            {
                id = cursor.getInt(0);
            } while(cursor.moveToNext());
        }
        return id;
    }

    public boolean isRecipeExist(String id) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+AppConstants.TABLE_RECIPE+" WHERE "+AppConstants.RECIPE_ID +
                " =" + id ;
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    public ArrayList<Collection> getAllCollections(boolean isSortByTitle, boolean isSortByRecentAdded) {
        ArrayList<Collection> collectionList = new ArrayList<>();
        String selectQuery = "SELECT c."
                                    + AppConstants.COLLECTION_ID+", c."
                                    + AppConstants.COLLECTION_NAME+", "
                                    + "count(lkp."+AppConstants.RECIPE_ID+") AS COUNT FROM " +
                                AppConstants.TABLE_COLLECTION+" AS c LEFT JOIN "+AppConstants
                .TABLE_LOOKUP_RECIPE_COLLECTION+" " +
                                "AS lkp ON lkp.collectionId = c.collectionId GROUP BY c.collectionId";

        if(isSortByTitle)
            selectQuery += " ORDER BY c."+AppConstants.COLLECTION_NAME+" ASC";
        else if(isSortByRecentAdded)
            selectQuery += " ORDER BY c."+AppConstants.COLLECTION_ID+" DESC";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Collection collection = new Collection();
                collection.setId(cursor.getInt(cursor.getColumnIndex(AppConstants.COLLECTION_ID)));
                collection.setCollectionName(cursor.getString(cursor.getColumnIndex(AppConstants
                        .COLLECTION_NAME)));
                collection.setTotalItems(cursor.getInt(cursor.getColumnIndex("COUNT")));
                collectionList.add(collection);
            }
            while (cursor.moveToNext());
        }
        return collectionList;
    }

    public ArrayList<Cuisine> getAllCuisines(boolean isSortByTitle, boolean isSortByRecentAdded) {
        ArrayList<Cuisine> cuisineArrayList = new ArrayList<>();
        String selectQuery = "SELECT * FROM "+AppConstants.TABLE_CUISINE;

        if(isSortByTitle)
            selectQuery += " ORDER BY "+AppConstants.CUISINE_NAME+" ASC";
        else if(isSortByRecentAdded)
            selectQuery += " ORDER BY "+AppConstants.CUISINE_ID+" DESC";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Cuisine cuisine = new Cuisine();
                cuisine.setId(cursor.getInt(cursor.getColumnIndex(AppConstants.CUISINE_ID)));
                cuisine.setCuisineName(cursor.getString(cursor.getColumnIndex(AppConstants
                        .CUISINE_NAME)));
                cuisineArrayList.add(cuisine);
            }
            while (cursor.moveToNext());
        }
        return cuisineArrayList;
    }

    public ArrayList<Collection> getCollectionByRecipeId(int recipeId) {
        ArrayList<Collection> collectionArrayList = new ArrayList<>();
        String selectQuery =
                "SELECT"
                        + " i."+AppConstants.COLLECTION_ID
                        + ", i."+AppConstants.COLLECTION_NAME
                        + " FROM " + AppConstants.TABLE_LOOKUP_RECIPE_COLLECTION
                        + " as lookup inner join "+ AppConstants.TABLE_RECIPE
                        + " as r on r."+ AppConstants.RECIPE_ID+" = lookup."+AppConstants.RECIPE_ID
                        + " inner join "+ AppConstants.TABLE_COLLECTION
                        + " as i on i."+ AppConstants.COLLECTION_ID + "= lookup." +AppConstants.COLLECTION_ID
                        + " where r."+AppConstants.RECIPE_ID+" = "+recipeId;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Collection collection = new Collection();
                collection.setId(cursor.getInt(cursor.getColumnIndex(AppConstants.COLLECTION_ID))
                        );
                collection.setCollectionName(cursor.getString(cursor.getColumnIndex(AppConstants
                        .COLLECTION_NAME)));
                collectionArrayList.add(collection);
            }
            while (cursor.moveToNext());
        }
        return collectionArrayList;
    }

    public ArrayList<Ingredient> getAllIngredientsByRecipeId(int recipeId) {
        ArrayList<Ingredient> ingredientArrayList = new ArrayList<>();
        String selectQuery = "SELECT"
                                    + " i."+AppConstants.INGREDIENT_ID
                                    + ", i."+AppConstants.AMOUNT
                                    + ", i."+ AppConstants.UNIT
                                    + ", i."+ AppConstants.ITEM
                            + " FROM " + AppConstants.TABLE_LOOKUP_RECIPE_INGREDIENTS
                                + " as lookup inner join "+ AppConstants.TABLE_RECIPE
                                + " as r on r."+ AppConstants.RECIPE_ID+" = lookup."+AppConstants.RECIPE_ID
                                + " inner join "+ AppConstants.TABLE_INGREDIENT
                                + " as i on i."+ AppConstants.INGREDIENT_ID + "= lookup." +AppConstants.INGREDIENT_ID
                            + " where r."+AppConstants.RECIPE_ID+" = "+recipeId;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(cursor.getInt(cursor.getColumnIndex(AppConstants.INGREDIENT_ID)));
                ingredient.setQuantity(cursor.getString(cursor.getColumnIndex(AppConstants.AMOUNT)));
                ingredient.setUnit(cursor.getString(cursor.getColumnIndex(AppConstants
                        .UNIT)));
                ingredient.setIngredient(cursor.getString(cursor.getColumnIndex(AppConstants
                        .ITEM)));
                ingredientArrayList.add(ingredient);
            }
            while (cursor.moveToNext());
        }
        return ingredientArrayList;
    }

    public ArrayList<Steps> getAllStepsByRecipeId(int recipeId) {
        ArrayList<Steps> stepsArrayList = new ArrayList<>();
        String selectQuery = "SELECT"
                                + " i."+AppConstants.STEP_ID
                                + ", i."+AppConstants.INSTRUCTION
                            + " FROM " + AppConstants.TABLE_LOOKUP_RECIPE_STEPS
                            + " as lookup inner join "+ AppConstants.TABLE_RECIPE
                            + " as r on r."+ AppConstants.RECIPE_ID+" = lookup."+AppConstants.RECIPE_ID
                            + " inner join "+ AppConstants.TABLE_STEPS
                            + " as i on i."+ AppConstants.STEP_ID + "= lookup." +AppConstants.STEP_ID
                            + " where r."+AppConstants.RECIPE_ID+" = "+recipeId;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Steps steps = new Steps();
                steps.setId(cursor.getInt(cursor.getColumnIndex(AppConstants.STEP_ID)));
                steps.setInstruction(cursor.getString(cursor.getColumnIndex(AppConstants.INSTRUCTION)));
                stepsArrayList.add(steps);
            }
            while (cursor.moveToNext());
        }
        return stepsArrayList;
    }

    public String getCuisineByCuisineId(int cuisineId) {
        String cuisine = null;
        String selectQuery = "SELECT "+AppConstants.CUISINE_NAME+" FROM "+AppConstants.TABLE_CUISINE
                                    +" WHERE "+AppConstants.CUISINE_ID+" = "+cuisineId;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                cuisine = cursor.getString(cursor.getColumnIndex(AppConstants
                        .CUISINE_NAME));
            }
            while (cursor.moveToNext());
        }
        return cuisine;
    }

    public long insertCollection(String newCollectionName) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppConstants.COLLECTION_NAME, newCollectionName);
        long collectionId = database.replace(AppConstants.TABLE_COLLECTION, null, values);
        return collectionId;
    }

    public void renameCollection(long collectionId, String newName) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppConstants.COLLECTION_ID, collectionId);
        values.put(AppConstants.COLLECTION_NAME, newName);
        database.replace(AppConstants.TABLE_COLLECTION, null, values);
    }

    public ArrayList<Recipe> searchRecipe(SearchCriteria searchCriteria) {
        ArrayList<Recipe> recipeList = new ArrayList<>();
        String whereClause = createCriteria(searchCriteria);
        String query =
        "select r.recipeId, r.recipeTitle, r.cuisineId, r.cookTime, r.prepTime, r.ImagePath " +
                "from recipe as r  " +
                "  left join Lookup_Recipe_Ingredients as lkp_i on lkp_i.recipeId = r.recipeId  " +
                "  left join Ingredients as i on i.ingredientId = lkp_i.ingredientId " +
                "  left join Lookup_Recipe_Collection as lkp_c on lkp_c.recipeId = r.recipeId  " +
                "  left join Collection as c on c.collectionId = lkp_c.collectionId " +
                "  left join Cuisine as cui on cui.cuisineId = r.cuisineId ";

        if(searchCriteria.getCollections().size() > 0){
            String collectionIdStr = searchCriteria
                    .getCollections().toString();
            collectionIdStr = collectionIdStr.substring(1, collectionIdStr.length()-1);
            whereClause += " lkp_c."+AppConstants.COLLECTION_ID+" IN ("+collectionIdStr
                    +") " +
                    "AND ";
        }
        if(!ApplicationHelper.isNullOrBlank(searchCriteria.getIngredients())){
            String ingredientsQuoted = searchCriteria.getIngredients();
            ingredientsQuoted = ingredientsQuoted.replace(",", "','");
            ingredientsQuoted = "'"+ingredientsQuoted+"'";
            whereClause +=  "i."+AppConstants.ITEM+" IN ("+ingredientsQuoted
                    +") AND ";
        }
        if(searchCriteria.getCuisineId() != -1){
            whereClause +=  " cui."+AppConstants.CUISINE_ID+" = "+searchCriteria.getCuisineId()+
                    " AND ";
        }

        SQLiteDatabase database = this.getReadableDatabase();
        if(!ApplicationHelper.isNullOrBlank(whereClause)){
            whereClause = " WHERE "+whereClause;
            whereClause = whereClause.substring(0, whereClause.lastIndexOf("AND"));
            query += whereClause +  "GROUP BY " +
                    "  r.recipeId";
        } else {
            return recipeList;
        }

        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = new Recipe();
                recipe.setId(cursor.getInt(cursor.getColumnIndex(AppConstants.RECIPE_ID)));
                recipe.setTitle(cursor.getString(cursor.getColumnIndex(AppConstants.RECIPE_NAME)));
                recipe.setCookTime(cursor.getString(cursor.getColumnIndex(AppConstants.COOK_TIME)));
                recipe.setPrepTime(cursor.getString(cursor.getColumnIndex(AppConstants.PREP_TIME)));
                recipe.setCuisineId(cursor.getInt(cursor.getColumnIndex(AppConstants.CUISINE_ID)));
                recipe.setPath(cursor.getString(cursor.getColumnIndex(AppConstants
                        .IMAGE_PATH)));
                // set cuisine
                String cuisineName = getCuisineByCuisineId(recipe.getCuisineId());
                recipe.setCuisineName(cuisineName);

                recipeList.add(recipe);
            }
            while (cursor.moveToNext());
        }
        return recipeList;
    }

    private String createCriteria(SearchCriteria searchCriteria) {
        String criteria = "";
        if(!ApplicationHelper.isNullOrBlank(searchCriteria.getTitle())){
            criteria += "r."+AppConstants.RECIPE_NAME+" like '%"+searchCriteria.getTitle()+"%'"
                    + " AND ";
        }
        if(searchCriteria.getYields() != null){
            criteria += "r."+AppConstants.YIELDS+searchCriteria.getYieldCondition()+searchCriteria.getYields()
                    + " AND ";
        }
        if(searchCriteria.getCalories() != null){
            criteria += "r."+AppConstants.CALORIES+searchCriteria.getCalorieCondition()+searchCriteria.getCalories()+ " AND ";
        }
        if(!ApplicationHelper.isNullOrBlank(searchCriteria.getPrepTime())){
            criteria += String.format("strftime('%%H:%%M', %s) %s  strftime('%%H:%%M', '%s')",
                    " r.prepTime"
                    , searchCriteria.getPrepTimeCondition()
                    , searchCriteria.getPrepTime());
            criteria += " AND ";
        }
        if(!ApplicationHelper.isNullOrBlank(searchCriteria.getCookTime())){
            criteria += String.format("strftime('%%H:%%M', %s) %s  strftime('%%H:%%M', '%s')",
                    " r.cookTime"
                    , searchCriteria.getCookTimeCondition()
                    , searchCriteria.getCookTime());
            criteria += " AND ";
        }
        if(!ApplicationHelper.isNullOrBlank(searchCriteria.getWebsite())){
            criteria += "r.website like '%"+searchCriteria.getWebsite()+"%'" +
                    " " +
                    "AND ";
        }
        return criteria;
    }
}