package com.raksha.assignment.cookbookapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.raksha.assignment.cookbookapp.R;
import com.raksha.assignment.cookbookapp.activity.collection.CollectionListFragment;
import com.raksha.assignment.cookbookapp.activity.common.RecipeListActivity;
import com.raksha.assignment.cookbookapp.activity.recipe.RecipeDetailActivity;
import com.raksha.assignment.cookbookapp.activity.recipe.RecipeListFragment;
import com.raksha.assignment.cookbookapp.activity.common.ViewRecipeActivity;
import com.raksha.assignment.cookbookapp.pojo.Collection;
import com.raksha.assignment.cookbookapp.utility.AppConstants;
import com.raksha.assignment.cookbookapp.utility.ApplicationHelper;
import com.raksha.assignment.cookbookapp.utility.DatabaseHelper;

public class MainActivity extends ActionBarActivity
        implements RecipeListFragment.Callbacks, CollectionListFragment.Callbacks {

    private MainActivityViewPagerAdapter adapterViewPager;
    private ViewPager tabsViewPager;
    private boolean isSortByTitle;
    private boolean isSortByRecentAdded;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initActivity();
        setDefaultSettings();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case AppConstants.REFRESH_FRAGMENT:
                if(data != null){
                    long collectionId = data.getLongExtra(AppConstants.COLLECTION_ID, -1);
                    long recipeId = data.getLongExtra(AppConstants.RECIPE_ID, -1);
                    if(recipeId > 0 || collectionId > 0) {
                        refreshTabs(true);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                searchRecipe();
                return true;

            case R.id.action_compose:
                compose();
                return true;

            case R.id.action_sort_title:
                setSortOrder(true, false);
                refreshTabs(true);
                return true;

            case R.id.action_sort_by_recent_added:
                setSortOrder(false, true);
                refreshTabs(true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Loads the activity view and initializes all the instance variables
     */
    private void initActivity(){
        setContentView(R.layout.main_activity);
        ApplicationHelper.showAppIcon(getSupportActionBar());
        initDatabase();
        loadTabs();
    }

    private void initDatabase(){
        databaseHelper = DatabaseHelper.getInstance(this);
    }

    private void setDefaultSettings() {
        isSortByTitle = true;
        tabsViewPager.setCurrentItem(0);
    }

    private void loadTabs(){
        tabsViewPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MainActivityViewPagerAdapter(getSupportFragmentManager());
        tabsViewPager.setAdapter(adapterViewPager);
    }

    /**
     * Perform action on Add action bar button press -
     * opens New Collection dialog if Collection tab is active
     * opens New recipe page if Recipe tab is active
     */
    private void compose(){
        int currentActiveTab = tabsViewPager.getCurrentItem();

        switch (currentActiveTab){
            case AppConstants.RECIPE_TAB:
                createRecipe();
                break;

            case AppConstants.COLLECTION_TAB:
                AlertDialog.Builder builder = ApplicationHelper.createNewCollectionDialog(this);
                AlertDialog dialog = builder.create();
                // show keyboard when dialog opens
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                        .SOFT_INPUT_STATE_VISIBLE);
                dialog.show();
                break;

            default:
                break;
        }
    }

    /**
     * For all the tabs, fetches fresh data from database and reloads the list
     * @param isSortOrderAction, specify sort order if list need to be sorted
     */
    public void refreshTabs(boolean isSortOrderAction) {
        // Refresh RECIPE tab
        RecipeListFragment recipeListFragment = (RecipeListFragment)
                (getSupportFragmentManager()
                        .getFragments()).get(AppConstants.RECIPE_TAB);
        if(isSortOrderAction)
            recipeListFragment.setSortOrder(isSortByTitle, isSortByRecentAdded);
        recipeListFragment.refresh();

        // Refresh COLLECTION tab
        CollectionListFragment collectionListFragment = (CollectionListFragment)
                (getSupportFragmentManager()
                        .getFragments()).get(AppConstants.COLLECTION_TAB);
        if(isSortOrderAction)
            collectionListFragment.setSortOrder(isSortByTitle, isSortByRecentAdded);
        collectionListFragment.refresh();
    }

    public void setSortOrder( boolean isSortByTitle, boolean isSortByRecentAdded){
        this.isSortByTitle = isSortByTitle;
        this.isSortByRecentAdded = isSortByRecentAdded;
    }

    /**
     * Opens Search Recipe Page
     */
    private void searchRecipe(){
        ApplicationHelper.openSearchRecipePage(this);
    }

    /**
     * Opens New Recipe page
     */
    private void createRecipe(){
        Intent detailIntent = new Intent(this, RecipeDetailActivity.class);
        // for new recipe, set selected item id any number lass than 1
        detailIntent.putExtra(AppConstants.ARG_SELECTED_ITEM_ID, AppConstants.NEW_RECIPE_ID);
        startActivityForResult(detailIntent, AppConstants.REFRESH_FRAGMENT);
    }

    /**
     * Callback method from {@link RecipeListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
      public void onRecipeSelected(int selectedRecipeId) {
        // open the selected recipe for view
        Intent detailIntent = new Intent(this, ViewRecipeActivity.class);
        detailIntent.putExtra(AppConstants.ARG_SELECTED_ITEM_ID, selectedRecipeId);
        startActivityForResult(detailIntent, AppConstants.REFRESH_FRAGMENT);
    }

    /**
     * Callback method from {@link RecipeListFragment.Callbacks}
     * refresh the tabs if needed like on back press esp. only when any update has
     * happened and you want to reflect those changes in the tabs
     */
    @Override
    public void setParentRefreshNeeded(boolean isParentRefreshNeeded){
        refreshTabs(isParentRefreshNeeded);
    }

    /**
     * Callback method from {@link CollectionListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onCollectionSelected(Collection selectedCollection) {
        // show recipes for the selected collection
        Intent detailIntent = new Intent(this, RecipeListActivity.class);
        detailIntent.putExtra(AppConstants.ARG_SELECTED_ITEM_ID, selectedCollection);
        startActivityForResult(detailIntent, AppConstants.REFRESH_FRAGMENT);
    }

    /**
     * Adds a new collection and refreshes the tabs
     * @param newCollectionName
     */
    public void saveCollection(String newCollectionName) {
        databaseHelper.insertCollection(newCollectionName);
        refreshTabs(true);
    }

}
