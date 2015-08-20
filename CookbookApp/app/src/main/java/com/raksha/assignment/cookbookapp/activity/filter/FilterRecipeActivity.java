package com.raksha.assignment.cookbookapp.activity.filter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.raksha.assignment.cookbookapp.R;
import com.raksha.assignment.cookbookapp.activity.common.RecipeListActivity;
import com.raksha.assignment.cookbookapp.pojo.Collection;
import com.raksha.assignment.cookbookapp.pojo.Cuisine;
import com.raksha.assignment.cookbookapp.pojo.Recipe;
import com.raksha.assignment.cookbookapp.pojo.SearchCriteria;
import com.raksha.assignment.cookbookapp.utility.AppConstants;
import com.raksha.assignment.cookbookapp.utility.ApplicationHelper;
import com.raksha.assignment.cookbookapp.utility.DatabaseHelper;

import java.util.ArrayList;

public class FilterRecipeActivity extends ActionBarActivity implements View.OnClickListener{
    private DatabaseHelper databaseHelper;
    private TextView prepTimeText;
    private TextView cookTimeText;
    private TextView collection;
    private TextView cuisine;
    private EditText ingredients;
    private EditText recipeTitle;
    private EditText calories;
    private EditText yields;
    private EditText website;
    private SearchCriteria searchCriteria;

    // array list to keep the selected items
    final ArrayList<Collection> collectionSelectedItems = new ArrayList();
    final ArrayList<Integer> cuisineSelectedItems = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_filter_recipe);
        setTitle(getString(R.string.TEXT_FIND_RECIPE));
        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recipeTitle = (EditText)(findViewById(R.id.title_text));
        calories = (EditText)(findViewById(R.id.calorie_text));
        yields = (EditText)(findViewById(R.id.yields_text));
        prepTimeText = (TextView)(findViewById(R.id.prep_time_text));
        cookTimeText = (TextView)(findViewById(R.id.cook_time_text));
        collection = (TextView)(findViewById(R.id.collection_text));
        cuisine = (TextView)(findViewById(R.id.cuisine_text));
        ingredients = (EditText)(findViewById(R.id.ingredients_text));
        website = (EditText)(findViewById(R.id.website_text));

        LinearLayout prepTimeRow = (LinearLayout)(findViewById(R.id.prep_time_row));
        LinearLayout cookTimeRow = (LinearLayout)(findViewById(R.id.cook_time_row));
        LinearLayout cuisineRow = (LinearLayout)(findViewById(R.id.cuisine_row));
        LinearLayout collectionRow = (LinearLayout)(findViewById(R.id.collection_row));

        prepTimeRow.setOnClickListener(this);
        cookTimeRow.setOnClickListener(this);
        cuisineRow.setOnClickListener(this);
        collectionRow.setOnClickListener(this);
        searchCriteria = new SearchCriteria();

        databaseHelper = DatabaseHelper.getInstance(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_reset:
                resetFilter();
                return true;

            case R.id.action_search:
                searchRecipe();
                return true;

            case android.R.id.home:
                goBack();
                return true;
        }
        return true;
    }

    private void searchRecipe() {
        hideKeyboard(this);
        fillRecipe();
        ArrayList<Recipe> searchResult = databaseHelper.searchRecipe(searchCriteria);
        // open recipe list with search result
        Intent detailIntent = new Intent(this, RecipeListActivity.class);
        detailIntent.putParcelableArrayListExtra(AppConstants.ARG_RECIPE_LIST, searchResult);
        startActivityForResult(detailIntent, AppConstants.REFRESH_FRAGMENT);
    }

    private void resetFilter() {
        reset();
    }

    private void goBack() {
        finish();
    }

    @Override
    public void onClick(View view) {
        final ArrayList newSelectedItems = new ArrayList();
        switch(view.getId()){
            case R.id.collection_row:
                ArrayList<Collection> collectionList = databaseHelper
                        .getAllCollections(true, false);
                CharSequence[] collectionNames = ApplicationHelper.getCharSequenceFromData
                        (collectionList);
                int[] collectionIds = ApplicationHelper.getIdsFromData(collectionList);
                newSelectedItems.clear();
                newSelectedItems.addAll(collectionSelectedItems);
                ApplicationHelper.getCheckboxDialog(collectionNames, collectionSelectedItems,
                        newSelectedItems,
                        getString(R.string.TEXT_SELECT_COLLECTION), collection, view,
                        collectionIds).show();
                break;

            case R.id.cuisine_row:
                ArrayList<Cuisine> cuisineArrayList = databaseHelper
                        .getAllCuisines(true, false);
                CharSequence[] cuisineNames = ApplicationHelper.getCharSequenceFromData(cuisineArrayList);
                int[] cuisineIds = ApplicationHelper.getIdsFromData(cuisineArrayList);
                newSelectedItems.clear();
                newSelectedItems.addAll(cuisineSelectedItems);
                ApplicationHelper.getRadioDialog(cuisineNames, cuisineSelectedItems, newSelectedItems,
                        getString(R.string.TEXT_SELECT_CUISINE), cuisine, view, cuisineIds).show();
                break;

            case R.id.cook_time_row:
                ApplicationHelper.setTimePickerToTextView(getString(R.string.TEXT_SET_COOK_TIME),
                        cookTimeText,
                        this);
                break;

            case R.id.prep_time_row:
                ApplicationHelper.setTimePickerToTextView(getString(R.string.TEXT_SET_PREP_TIME),
                        prepTimeText,
                        this);
                break;
        }
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(website.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(recipeTitle.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(ingredients.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(yields.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(calories.getWindowToken(), 0);
    }

    private void fillRecipe(){
        searchCriteria.setTitle(recipeTitle.getText() == null ? null : recipeTitle.getText().toString());
        searchCriteria.setIngredients(ingredients.getText() == null
                ? null : ingredients.getText().toString());
        searchCriteria.setCalories(calories.getText() == null
                                        || (calories.getText().toString()).equals("")
                ? null : Integer.parseInt(calories.getText().toString()));
        searchCriteria.setYields(yields.getText() == null || (yields.getText().toString()).equals("")
                ? null : Integer.parseInt(yields.getText().toString()));
        searchCriteria.setCookTime((cookTimeText.getText().toString()).equals(getString(R.string
                .TEXT_DEFAULT_TIME)) ? "" : cookTimeText.getText().toString());
        searchCriteria.setPrepTime((prepTimeText.getText().toString()).equals(getString(R.string
                .TEXT_DEFAULT_TIME)) ? "" : prepTimeText.getText().toString());
        searchCriteria.setCuisineId(cuisineSelectedItems.size() > 0 ? cuisineSelectedItems.get(0) : -1);
        searchCriteria.setCollections(collectionSelectedItems);
        searchCriteria.setWebsite(website.getText() == null ? null : website.getText().toString());

        RadioGroup radioGroupYields = (RadioGroup) findViewById(R.id.yields_condition);
        int radioButtonID = radioGroupYields.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton)radioGroupYields.findViewById(radioButtonID);
        searchCriteria.setYieldCondition(radioButton.getText().toString());

        radioGroupYields = (RadioGroup) findViewById(R.id.calories_condition);
        radioButtonID = radioGroupYields.getCheckedRadioButtonId();
        radioButton = (RadioButton)radioGroupYields.findViewById(radioButtonID);
        searchCriteria.setCalorieCondition(radioButton.getText().toString());

        radioGroupYields = (RadioGroup) findViewById(R.id.cook_condition);
        radioButtonID = radioGroupYields.getCheckedRadioButtonId();
        radioButton = (RadioButton)radioGroupYields.findViewById(radioButtonID);
        searchCriteria.setCookTimeCondition(radioButton.getText().toString());

        radioGroupYields = (RadioGroup) findViewById(R.id.prep_condition);
        radioButtonID = radioGroupYields.getCheckedRadioButtonId();
        radioButton = (RadioButton)radioGroupYields.findViewById(radioButtonID);
        searchCriteria.setPrepTimeCondition(radioButton.getText().toString());
    }

    private void reset(){
        recipeTitle.setText("");
        calories.setText("");
        yields.setText("");
        prepTimeText.setText(getString(R.string.TEXT_DEFAULT_TIME));
        cookTimeText.setText(getString(R.string.TEXT_DEFAULT_TIME));
        collection.setText("");
        cuisine.setText("");
        ingredients.setText("");
        website.setText("");
        collectionSelectedItems.clear();
        cuisineSelectedItems.clear();

        // default check second check box i.e. <=
        RadioGroup radioGroupYields = (RadioGroup) findViewById(R.id.yields_condition);
        RadioButton radioButton = (RadioButton) radioGroupYields.getChildAt(1);
        radioButton.setChecked(true);

        radioGroupYields = (RadioGroup) findViewById(R.id.calories_condition);
        radioButton = (RadioButton) radioGroupYields.getChildAt(1);
        radioButton.setChecked(true);

        radioGroupYields = (RadioGroup) findViewById(R.id.cook_condition);
        radioButton = (RadioButton) radioGroupYields.getChildAt(1);
        radioButton.setChecked(true);

        radioGroupYields = (RadioGroup) findViewById(R.id.prep_condition);
        radioButton = (RadioButton) radioGroupYields.getChildAt(1);
        radioButton.setChecked(true);
    }

}
