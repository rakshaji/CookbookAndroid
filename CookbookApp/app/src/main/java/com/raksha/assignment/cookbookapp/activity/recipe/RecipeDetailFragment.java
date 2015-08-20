package com.raksha.assignment.cookbookapp.activity.recipe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raksha.assignment.cookbookapp.R;
import com.raksha.assignment.cookbookapp.activity.common.IngredientsActivity;
import com.raksha.assignment.cookbookapp.activity.common.StepsActivity;
import com.raksha.assignment.cookbookapp.pojo.Collection;
import com.raksha.assignment.cookbookapp.pojo.Cuisine;
import com.raksha.assignment.cookbookapp.pojo.Ingredient;
import com.raksha.assignment.cookbookapp.pojo.Recipe;
import com.raksha.assignment.cookbookapp.pojo.Steps;
import com.raksha.assignment.cookbookapp.utility.AppConstants;
import com.raksha.assignment.cookbookapp.utility.ApplicationHelper;
import com.raksha.assignment.cookbookapp.utility.DatabaseHelper;

import java.util.ArrayList;

/**
 * A fragment representing a single Recipe detail screen.
 */
public class RecipeDetailFragment extends Fragment implements View.OnClickListener {

    private ImageView recipePhoto;
    private TextView prepTimeText;
    private TextView cookTimeText;
    private TextView collection;
    private TextView cuisine;
    private TextView ingredients;
    private EditText recipeTitle;
    private EditText calories;
    private EditText yields;
    private EditText website;
    private EditText notes;
    private TextView steps;
    private View rootView;
    private Recipe recipe;

    // array list to keep the selected items of collection & cuisine
    private final ArrayList<Collection> collectionSelectedItems = new ArrayList();
    private final ArrayList<Integer> cuisineSelectedItems = new ArrayList();

    private boolean isNewRecipe = false;
    private int selectedRecipeId;
    private int currentRecipeId;
    private DatabaseHelper databaseHelper;
    private ImageView addRecipePhoto;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = DatabaseHelper.getInstance(this.getActivity());
        if (getArguments().containsKey(AppConstants.ARG_SELECTED_ITEM_ID)) {
            selectedRecipeId = getArguments().getInt(AppConstants.ARG_SELECTED_ITEM_ID);

            if(selectedRecipeId == AppConstants.NEW_RECIPE_ID){
                isNewRecipe = true;
                // fetch the next RecipeId from database for the new recipe
                currentRecipeId = databaseHelper.getNextId(AppConstants.TABLE_RECIPE
                        , AppConstants.RECIPE_ID);
            } else {
                currentRecipeId = selectedRecipeId;
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        addRecipePhoto = (ImageView)(rootView.findViewById(R.id.noRecipeImage));
        recipePhoto = (ImageView)(rootView.findViewById(R.id.recipeImage));
        recipeTitle = (EditText)(rootView.findViewById(R.id.title_text));
        calories = (EditText)(rootView.findViewById(R.id.calorie_text));
        yields = (EditText)(rootView.findViewById(R.id.yields_text));
        prepTimeText = (TextView)(rootView.findViewById(R.id.prep_time_text));
        cookTimeText = (TextView)(rootView.findViewById(R.id.cook_time_text));
        collection = (TextView)(rootView.findViewById(R.id.collection_text));
        cuisine = (TextView)(rootView.findViewById(R.id.cuisine_text));
        ingredients = (TextView)(rootView.findViewById(R.id.ingredients_text));
        steps = (TextView)(rootView.findViewById(R.id.steps_text));
        website = (EditText)(rootView.findViewById(R.id.website_text));
        notes = (EditText)(rootView.findViewById(R.id.notes_text));

        LinearLayout prepTimeRow = (LinearLayout)(rootView.findViewById(R.id.prep_time_row));
        LinearLayout cookTimeRow = (LinearLayout)(rootView.findViewById(R.id.cook_time_row));
        LinearLayout ingredientRow = (LinearLayout)(rootView.findViewById(R.id.ingredient_row));
        LinearLayout stepsRow = (LinearLayout)(rootView.findViewById(R.id.steps_row));
        LinearLayout cuisineRow = (LinearLayout)(rootView.findViewById(R.id.cuisine_row));
        LinearLayout collectionRow = (LinearLayout)(rootView.findViewById(R.id.collection_row));

        recipePhoto.setOnClickListener(this);
        addRecipePhoto.setOnClickListener(this);
        prepTimeRow.setOnClickListener(this);
        cookTimeRow.setOnClickListener(this);
        ingredientRow.setOnClickListener(this);
        stepsRow.setOnClickListener(this);
        cuisineRow.setOnClickListener(this);
        collectionRow.setOnClickListener(this);

        if (isNewRecipe) {
            recipe = new Recipe();
            getActivity().setTitle(getString(R.string.TITLE_NEW_RECIPE));
            addRecipePhoto.setVisibility(View.VISIBLE);
            recipePhoto.setVisibility(View.GONE);
        } else {
            showSavedData();
        }

        return rootView;
    }

    @Override
    public void onClick(View view) {
        final ArrayList newSelectedItems = new ArrayList();
        switch(view.getId()){
            case R.id.recipeImage:
                showAddPhotoOptions();
                break;

            case R.id.noRecipeImage:
                showAddPhotoOptions();
                break;

            case R.id.steps_row:
                Intent stepsIntent = new Intent(getActivity(), StepsActivity.class);
                ArrayList<Steps> stepsArrayList = recipe.getSteps();
                if(stepsArrayList == null && recipe.getId() > 0) {
                    stepsArrayList = databaseHelper.getAllStepsByRecipeId(recipe.getId());
                }
                stepsIntent.putExtra(AppConstants.ARG_NEW_RECIPE, isNewRecipe);
                stepsIntent.putParcelableArrayListExtra(AppConstants.DATA_STEPS
                        , stepsArrayList);
                startActivityForResult(stepsIntent, AppConstants.REQUEST_STEPS_ACTIVITY);
                break;

            case R.id.ingredient_row:
                Intent ingredientsIntent = new Intent(getActivity(), IngredientsActivity.class);
                ArrayList<Ingredient> ingredientArrayList = recipe.getIngredients();
                if(recipe.getId() > 0 && ingredientArrayList == null) {
                    ingredientArrayList = databaseHelper.getAllIngredientsByRecipeId
                            (recipe.getId());
                }
                ingredientsIntent.putExtra(AppConstants.ARG_NEW_RECIPE, isNewRecipe);
                ingredientsIntent.putParcelableArrayListExtra(AppConstants.DATA_INGREDIENTS
                        , ingredientArrayList);
                startActivityForResult(ingredientsIntent, AppConstants.REQUEST_INGREDIENT_ACTIVITY);
                break;

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
                        getString(R.string.TEXT_SELECT_COLLECTION), collection, rootView,
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
                        getString(R.string.TEXT_SELECT_CUISINE), cuisine, rootView,
                        cuisineIds).show();
                break;

            case R.id.cook_time_row:
                ApplicationHelper.setTimePickerToTextView(getString(R.string.TEXT_SET_COOK_TIME),
                        cookTimeText,
                        rootView.getContext());
                break;

            case R.id.prep_time_row:
                ApplicationHelper.setTimePickerToTextView(getString(R.string.TEXT_SET_PREP_TIME),
                        prepTimeText,
                        rootView.getContext());
                break;
        }
    }

    private void showAddPhotoOptions() {
        final String takePhoto = getString(R.string.OPTION_TAKE_PHOTO);
        final String fromGallery = getString(R.string.OPTION_CHOOSE_FROM_GALLERY);
        final String cancel = getString(R.string.OPTION_CANCEL);
        final CharSequence[] items = { takePhoto, fromGallery, cancel};

        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
        builder.setTitle(getString(R.string.TEXT_ADD_PHOTO));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(takePhoto)) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, AppConstants.REQUEST_CAMERA);
                } else if (items[item].equals(fromGallery)) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, getString(R.string.TEXT_SELECT_FILE)),
                            AppConstants.SELECT_FILE);
                } else if (items[item].equals(cancel)) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case AppConstants.REQUEST_CAMERA:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    String selectedImagePath = ApplicationHelper.saveFileToInternalStorage(rootView
                            .getContext(), bitmap, currentRecipeId);

                    if(selectedImagePath == null){
                        return;
                    }
                    addRecipePhoto.setVisibility(View.GONE);
                    recipePhoto.setVisibility(View.VISIBLE);
                    recipePhoto.setImageBitmap(ApplicationHelper.getThumbnailFromInternalStorage(selectedImagePath));
                    recipePhoto.setTag(selectedImagePath);
                    break;

                case AppConstants.SELECT_FILE:
                    bitmap = ApplicationHelper.getGalleryImagePath(data,
                            rootView.getContext());

                    String filePath = ApplicationHelper.saveFileToInternalStorage(rootView
                            .getContext(), bitmap, currentRecipeId);
                    if(filePath == null){
                        return;
                    }
                    addRecipePhoto.setVisibility(View.GONE);
                    recipePhoto.setVisibility(View.VISIBLE);
                    recipePhoto.setImageBitmap(ApplicationHelper.getThumbnailFromInternalStorage(filePath));
                    recipePhoto.setTag(filePath);
                    break;

                case AppConstants.REQUEST_INGREDIENT_ACTIVITY:
                    ArrayList<Ingredient> ingredientArrayList = data.getParcelableArrayListExtra(
                            (AppConstants.DATA_INGREDIENTS));
                    recipe.setIngredients(ingredientArrayList);
                    ingredients.setText(String.format(getString(R.string
                            .TEXT_INGREDIENTS), ingredientArrayList.size()));
                    break;

                case AppConstants.REQUEST_STEPS_ACTIVITY:
                    ArrayList<Steps> stepsArrayList = data.getParcelableArrayListExtra(
                            (AppConstants.DATA_STEPS));
                    recipe.setSteps(stepsArrayList);
                    steps.setText(getString(R.string
                            .TEXT_STEPS, stepsArrayList.size()));
                    break;
            }
        }
    }

    public long saveRecipe() {
        fillRecipe();
        hideKeyboard();
        return databaseHelper.insertRecipe(recipe);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(website.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(recipeTitle.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(yields.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(calories.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(notes.getWindowToken(), 0);
    }

    private void fillRecipe(){
        recipe.setId(currentRecipeId);
        recipe.setPath(recipePhoto.getTag() == null ? null : (String)recipePhoto.getTag());
        recipe.setTitle(recipeTitle.getText() == null ? null : recipeTitle.getText().toString());
        recipe.setCalories(calories.getText() == null || (calories.getText().toString()).equals
                ("") ? null : Integer.parseInt(calories.getText().toString()));
        recipe.setYields(yields.getText() == null || (yields.getText().toString()).equals("") ?
                null : Integer.parseInt(yields.getText().toString()));
        recipe.setCookTime(cookTimeText.getText() == null ? getString(R.string.TEXT_DEFAULT_TIME)
                : cookTimeText.getText().toString());
        recipe.setPrepTime(prepTimeText.getText() == null ? getString(R.string.TEXT_DEFAULT_TIME)
                : prepTimeText.getText().toString());
        recipe.setCuisineId(cuisineSelectedItems.size() > 0 ? cuisineSelectedItems.get(0) : -1);
        recipe.setCollections(collectionSelectedItems);
        recipe.setWebsite(website.getText() == null ? null : website.getText().toString());
        recipe.setNotes(notes.getText() == null ? null : notes.getText().toString());
    }

    private void showSavedData(){
        recipe = databaseHelper.getRecipeByRecipeId(String.valueOf(currentRecipeId));
        if(recipe == null)
            return;

        getActivity().setTitle(recipe.getTitle());
        if(recipe.getPath() != null) {
            Bitmap bitmap = ApplicationHelper.getThumbnailFromInternalStorage(recipe.getPath());
            recipePhoto.setImageBitmap(bitmap);
            recipePhoto.setTag(recipe.getPath());
            recipePhoto.setVisibility(View.VISIBLE);
            addRecipePhoto.setVisibility(View.GONE);
        } else {
            recipePhoto.setVisibility(View.GONE);
            addRecipePhoto.setVisibility(View.VISIBLE);
        }

        recipeTitle.setText(recipe.getTitle());
        calories.setText(String.valueOf(recipe.getCalories()));
        yields.setText(String.valueOf(recipe.getYields()));
        cookTimeText.setText(recipe.getCookTime());
        prepTimeText.setText(recipe.getPrepTime());
        cuisineSelectedItems.add(recipe.getCuisineId());
        cuisine.setText(recipe.getCuisineName());
        website.setText(recipe.getWebsite());
        notes.setText(recipe.getNotes());
        ingredients.setText(String.format(getString(R.string
                .TEXT_INGREDIENTS), recipe.getIngredients().size()));
        steps.setText(getString(R.string.TEXT_STEPS, recipe.getSteps().size()));

        collectionSelectedItems.addAll(recipe.getCollections());
        String collectionStr = "";
        for(Collection collectionObj: collectionSelectedItems){
            collectionStr += collectionObj.getCollectionName() + ", ";
        }
        collection.setText(collectionStr.length() > 0 ? collectionStr.substring(0,
                collectionStr.length() - 2) : getString(R.string.zero_selected));
    }

    public void deleteRecipe() {
        databaseHelper.deleteRecipe(String.valueOf(currentRecipeId));
    }

}
