package com.raksha.assignment.cookbookapp.activity.recipe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import android.view.Menu;
import android.view.MenuItem;

import com.raksha.assignment.cookbookapp.R;
import com.raksha.assignment.cookbookapp.utility.AppConstants;


/**
 * An activity representing a single Recipe detail screen.
 */
public class RecipeDetailActivity extends ActionBarActivity {
    private RecipeDetailFragment fragment;
    private boolean isNewRecipe;
    private int selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (savedInstanceState == null) {
            selectedItem = getIntent().getIntExtra(AppConstants
                    .ARG_SELECTED_ITEM_ID, AppConstants.NEW_RECIPE_ID);

            if(selectedItem == AppConstants.NEW_RECIPE_ID){
                isNewRecipe = true;
            }

            Bundle arguments = new Bundle();
            arguments.putInt(AppConstants.ARG_SELECTED_ITEM_ID, selectedItem);
            fragment = new RecipeDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isNewRecipe){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_new_recipe_detail, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_edit_recipe_detail, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_delete:
                deleteRecipe();
                return true;

            case android.R.id.home:
                goBack((long)selectedItem);
                return true;

            case R.id.action_save:
                saveRecipe();
                return true;

            default:
                break;
        }
        return true;
    }

    public void saveRecipe(){
        long newId = fragment.saveRecipe();
        goBack(newId);
    }

    private void deleteRecipe(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.CONFIRM_DELETE_SINGLE_RECIPE))
                .setMessage(getString(R.string.CONFIRM_DELETE_RECIPE_PERMANENTLY))
                .setPositiveButton(getString(R.string.TEXT_OK), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        fragment.deleteRecipe();

                        Intent intent = new Intent();
                        intent.putExtra(AppConstants.ACTION, AppConstants.ACTION_DELETE);
                        intent.putExtra(AppConstants.RECIPE_ID, (long) selectedItem);
                        setResult(RESULT_OK, intent);
                        finish();
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
        goBack((long) selectedItem);
    }

    private void goBack(long id) {
        Intent intent = new Intent();
        intent.putExtra(AppConstants.RECIPE_ID, id);
        setResult(RESULT_OK, intent);
        finish();
    }

}
