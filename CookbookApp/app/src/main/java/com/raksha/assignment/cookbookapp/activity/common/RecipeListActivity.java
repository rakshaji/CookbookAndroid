package com.raksha.assignment.cookbookapp.activity.common;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.raksha.assignment.cookbookapp.R;
import com.raksha.assignment.cookbookapp.activity.recipe.RecipeListFragment;
import com.raksha.assignment.cookbookapp.activity.common.ViewRecipeActivity;
import com.raksha.assignment.cookbookapp.pojo.Collection;
import com.raksha.assignment.cookbookapp.pojo.Recipe;
import com.raksha.assignment.cookbookapp.utility.AppConstants;
import com.raksha.assignment.cookbookapp.utility.ApplicationHelper;
import com.raksha.assignment.cookbookapp.utility.DatabaseHelper;

import java.util.ArrayList;

public class RecipeListActivity extends ActionBarActivity implements RecipeListFragment
.Callbacks {
    private RecipeListFragment fragment;
    private boolean isParentRefreshNeeded = false;
    private int selectedCollectionId;
    private Collection selectedCollection;
    private DatabaseHelper databaseHelper;
    private boolean isCollectionFlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_recipe_list);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        databaseHelper = DatabaseHelper.getInstance(this);
        if (savedInstanceState == null) {
            ArrayList<Recipe> searchResult = getIntent().getParcelableArrayListExtra(AppConstants
                    .ARG_RECIPE_LIST);
            selectedCollection = getIntent().getParcelableExtra(AppConstants.ARG_SELECTED_ITEM_ID);

            Bundle arguments = new Bundle();
            if(selectedCollection != null) {
                // collection flow
                selectedCollectionId = selectedCollection.getId();
                arguments.putLong(AppConstants.COLLECTION_ID, selectedCollectionId);
                setTitle(selectedCollection.getCollectionName().toString());
                setCollectionFlow(true);
            } else {
                // search flow
                arguments.putParcelableArrayList(AppConstants.ARG_RECIPE_LIST, searchResult);
                setTitle(getString(R.string.TEXT_SEARCH_RESULT));
            }

            fragment = RecipeListFragment.newInstance(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.recipe_list_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isCollectionFlow()) {
            getMenuInflater().inflate(R.menu.menu_edit_collection_detail, menu);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case AppConstants.REFRESH_FRAGMENT:
                if(data != null){
                    if(isCollectionFlow()) {
                        long collectionId = data.getLongExtra(AppConstants.COLLECTION_ID, -1);
                        long recipeId = data.getLongExtra(AppConstants.RECIPE_ID, -1);
                        if (recipeId > 0 || collectionId > 0) {
                            RecipeListFragment recipeListFragment = (RecipeListFragment)
                                    (getSupportFragmentManager()
                                            .getFragments()).get(AppConstants.RECIPE_TAB);

                            recipeListFragment.setSortOrder(true, false);
                            recipeListFragment.refresh();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_edit:
                editCollectionName();
                return true;
            case R.id.action_delete:
                deleteSingleCollection();
                return true;
            case android.R.id.home:
                goBack((long) selectedCollectionId);
                return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        goBack((long) selectedCollectionId);
    }

    @Override
    public void onRecipeSelected(int recipeId) {
        Intent detailIntent = new Intent(this, ViewRecipeActivity.class);
        detailIntent.putExtra(AppConstants.ARG_SELECTED_ITEM_ID, recipeId);
        startActivityForResult(detailIntent, AppConstants.REFRESH_FRAGMENT);
    }

    @Override
    public void setParentRefreshNeeded(boolean isParentRefreshNeeded){
        this.isParentRefreshNeeded = isParentRefreshNeeded;
    }

    private void goBack(long id) {
        if(isParentRefreshNeeded()) {
            Intent intent = new Intent();
            intent.putExtra(AppConstants.COLLECTION_ID, id);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    private void deleteSingleCollection(){
        ApplicationHelper.deleteCollectionDialog(this).show();
    }

    public void deleteCollection(){
        setParentRefreshNeeded(true);
        databaseHelper.deleteCollection(selectedCollectionId);
        goBack((long)selectedCollectionId);
    }

    private void editCollectionName(){
        AlertDialog.Builder builder = ApplicationHelper.changeCollectionTitleDialog(this, selectedCollectionId);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public void renameCollection(long collectionId, String newName) {
        setTitle(newName);
        databaseHelper.renameCollection(collectionId, newName);
        setParentRefreshNeeded(true);
    }

    private boolean isParentRefreshNeeded(){
        return isParentRefreshNeeded;
    }

    private boolean isCollectionFlow() {
        return isCollectionFlow;
    }

    private void setCollectionFlow(boolean isCollectionFlow) {
        this.isCollectionFlow = isCollectionFlow;
    }

}
