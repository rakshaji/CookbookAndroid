package com.raksha.assignment.cookbookapp.activity.common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.raksha.assignment.cookbookapp.R;
import com.raksha.assignment.cookbookapp.activity.recipe.RecipeDetailActivity;
import com.raksha.assignment.cookbookapp.pojo.Collection;
import com.raksha.assignment.cookbookapp.pojo.Ingredient;
import com.raksha.assignment.cookbookapp.pojo.Recipe;
import com.raksha.assignment.cookbookapp.pojo.Steps;
import com.raksha.assignment.cookbookapp.utility.AppConstants;
import com.raksha.assignment.cookbookapp.utility.ApplicationHelper;
import com.raksha.assignment.cookbookapp.utility.DatabaseHelper;

import java.util.ArrayList;

public class ViewRecipeActivity extends ActionBarActivity {

    private ImageView recipePhotoViewOnly;
    private TextView prepTimeTextViewOnly;
    private TextView cookTimeTextViewOnly;
    private TextView collectionViewOnly;
    private TextView cuisineViewOnly;
    private TextView ingredientsViewOnly;
    private TextView recipeTitleViewOnly;
    private TextView caloriesViewOnly;
    private TextView yieldsViewOnly;
    private TextView websiteViewOnly;
    private TextView notesViewOnly;
    private TextView stepsViewOnly;
    private DatabaseHelper databaseHelper;
    private Intent intent = null;
    private int selectedRecipeId;
    private ImageView noRecipePhotoViewOnly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_recipe_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        databaseHelper = DatabaseHelper.getInstance(this);
        if (savedInstanceState == null) {
            selectedRecipeId = getIntent().getIntExtra(AppConstants
                    .ARG_SELECTED_ITEM_ID, 0);
            showSavedData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_recipe_detail, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        intent = data;
        switch (requestCode){
            case AppConstants.REFRESH_FRAGMENT:
                if(intent != null){
                    String action = data.getStringExtra(AppConstants.ACTION);
                    long collectionId = data.getLongExtra(AppConstants.COLLECTION_ID, -1);
                    long recipeId = data.getLongExtra(AppConstants.RECIPE_ID, -1);
                    if(action != null && action.equals(AppConstants.ACTION_DELETE)){
                        goBack(recipeId);
                    }

                    if(recipeId > 0 || collectionId > 0) {
                        refresh();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void refresh() {
        showSavedData();
    }

    private void showSavedData(){
        Recipe recipe = databaseHelper.getRecipeByRecipeId(String.valueOf(selectedRecipeId));

        setTitle(recipe.getTitle());

        // get reference to ui elements
        recipeTitleViewOnly = (TextView)(findViewById(R.id.recipeName_viewOnly));
        caloriesViewOnly = (TextView)(findViewById(R.id.calories_text_viewOnly));
        yieldsViewOnly = (TextView)(findViewById(R.id.yields_text_viewOnly));
        recipePhotoViewOnly = (ImageView)(findViewById(R.id.recipeImage_viewOnly));
        noRecipePhotoViewOnly = (ImageView)(findViewById(R.id.noRecipeImage_viewOnly));
        prepTimeTextViewOnly = (TextView)(findViewById(R.id.prep_time_text_viewOnly));
        cookTimeTextViewOnly = (TextView)(findViewById(R.id.cook_time_text_viewOnly));
        collectionViewOnly = (TextView)(findViewById(R.id.collection_text_viewOnly));
        cuisineViewOnly = (TextView)(findViewById(R.id.cuisine_text_viewOnly));
        ingredientsViewOnly = (TextView)(findViewById(R.id.ingredients_text_viewOnly));
        stepsViewOnly = (TextView)(findViewById(R.id.steps_text_viewOnly));
        websiteViewOnly = (TextView)(findViewById(R.id.website_text_viewOnly));
        notesViewOnly = (TextView)(findViewById(R.id.notes_text_viewOnly));

        // set data to the ui elements
        if(recipe.getPath() != null) {
            Bitmap bitmap = ApplicationHelper.getThumbnailFromInternalStorage(recipe.getPath());
            recipePhotoViewOnly.setImageBitmap(bitmap);
            recipePhotoViewOnly.setVisibility(View.VISIBLE);
            noRecipePhotoViewOnly.setVisibility(View.GONE);
        } else {
            recipePhotoViewOnly.setVisibility(View.GONE);
            noRecipePhotoViewOnly.setVisibility(View.VISIBLE);
        }

        recipeTitleViewOnly.setText(recipe.getTitle());
        caloriesViewOnly.setText(String.valueOf(recipe.getCalories()));
        yieldsViewOnly.setText(String.valueOf(recipe.getYields()));
        cookTimeTextViewOnly.setText(recipe.getCookTime());
        prepTimeTextViewOnly.setText(recipe.getPrepTime());
        cuisineViewOnly.setText(recipe.getCuisineName() != null && recipe.getCuisineName().length() >
                0 ?
                recipe.getCuisineName() : getString(R.string.TEXT_NONE));
        websiteViewOnly.setText(recipe.getWebsite() != null && recipe.getWebsite().length() > 0 ?
                recipe.getWebsite() : getString(R.string.TEXT_NONE));
        notesViewOnly.setText(recipe.getNotes() != null && recipe.getNotes().length() > 0 ?
                recipe.getNotes() : getString(R.string.TEXT_NO_NOTES_FOUND));

        // ingredient
        ArrayList<Ingredient> ingredientArrayList = recipe.getIngredients();
        String ingredientsStr = "";
        for(Ingredient ingredient: ingredientArrayList){
            String quantity = ingredient.getQuantity();
            quantity = quantity == null ? "" : quantity;
            ingredientsStr += String.format("%s %s %s \n\n", quantity,
                    ingredient.getUnit(),
                    ingredient.getIngredient());
        }
        ingredientsViewOnly.setText(ingredientsStr.length() > 0 ? ingredientsStr.substring(0,
                ingredientsStr.length() - 2) : getString(R.string.TEXT_NO_INGREDIENTS_FOUND));

        // steps
        String stepsStr = "";
        ArrayList<Steps> stepsArrayList = recipe.getSteps();
        int stepCount = 0;
        for(Steps step: stepsArrayList){
            stepsStr += String.format("%d. %s \n\n",
                                        ++stepCount, step.getInstruction());
        }
        stepsViewOnly.setText(stepsStr.length() > 0 ? stepsStr.substring(0,
                stepsStr.length() - 2) : getString(R.string.TEXT_NO_STEPS_FOUND));

        // collection
        ArrayList<Collection> collectionSelectedItems = new ArrayList<>();
        collectionSelectedItems.addAll(recipe.getCollections());
        String collectionStr = "";
        for (Collection collection1: collectionSelectedItems){
            collectionStr += collection1.getCollectionName() + ", ";
        }
        collectionViewOnly.setText(collectionStr.length() > 0 ? collectionStr.substring(0,
                collectionStr.length() - 2) : getString(R.string.TEXT_NONE));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_edit:
                Intent detailIntent = new Intent(this, RecipeDetailActivity.class);
                detailIntent.putExtra(AppConstants.ARG_SELECTED_ITEM_ID, selectedRecipeId);
                startActivityForResult(detailIntent, AppConstants.REFRESH_FRAGMENT);
                return true;

            case R.id.action_delete:
                deleteRecipe();
                return true;

            case android.R.id.home:
                goBack((long) selectedRecipeId);
                return true;
        }
        return true;
    }

    public void deleteRecipe() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.CONFIRM_DELETE_SINGLE_RECIPE))
                    .setMessage(getString(R.string.CONFIRM_DELETE_RECIPE_PERMANENTLY))
                    // Set the action buttons
                    .setPositiveButton(getString(R.string.TEXT_OK), new DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            intent = new Intent();
                            databaseHelper.deleteRecipe(String.valueOf(selectedRecipeId));
                            dialog.dismiss();
                            goBack((long) selectedRecipeId);
                        }
                    })
                    .setNegativeButton(getString(R.string.TEXT_CANCEL), new DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();

    }

    @Override
    public void onBackPressed() {
        goBack((long) selectedRecipeId);
    }

    private void goBack(long id) {
        if(intent != null) {
            intent.putExtra(AppConstants.RECIPE_ID, id);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

}
