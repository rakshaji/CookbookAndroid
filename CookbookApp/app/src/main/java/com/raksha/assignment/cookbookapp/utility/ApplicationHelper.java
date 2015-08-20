package com.raksha.assignment.cookbookapp.utility;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.TextView;

import com.raksha.assignment.cookbookapp.R;
import com.raksha.assignment.cookbookapp.activity.MainActivity;
import com.raksha.assignment.cookbookapp.activity.common.RecipeListActivity;
import com.raksha.assignment.cookbookapp.activity.common.CustomTimePickerDialog;
import com.raksha.assignment.cookbookapp.activity.filter.FilterRecipeActivity;
import com.raksha.assignment.cookbookapp.pojo.Collection;
import com.raksha.assignment.cookbookapp.pojo.Cuisine;
import com.raksha.assignment.cookbookapp.pojo.Ingredient;
import com.raksha.assignment.cookbookapp.pojo.Recipe;
import com.raksha.assignment.cookbookapp.pojo.Steps;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Raksha on 4/24/2015.
 */
public class ApplicationHelper {
    /**
     * Shows application icon
     */
    public static void showAppIcon(ActionBar actionBar) {
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
    }

    public static boolean isNullOrBlank(String str){
        if(str == null)
            return true;

        if(str.equals(""))
            return true;

        return false;
    }

    public static void setRecipeFromCursorData(Cursor cursor, Recipe recipe) {
        recipe.setId(cursor.getInt(cursor.getColumnIndex(AppConstants.RECIPE_ID)));
        recipe.setTitle(cursor.getString(cursor.getColumnIndex(AppConstants
                .RECIPE_NAME)));
        recipe.setCuisineId(cursor.getInt(cursor.getColumnIndex(AppConstants
                .CUISINE_ID)));
        recipe.setWebsite(cursor.getString(cursor.getColumnIndex(AppConstants
                .WEBSITE)));
        recipe.setRating(cursor.getInt(cursor.getColumnIndex(AppConstants
                .RATING)));
        recipe.setCookTime(cursor.getString(cursor.getColumnIndex(AppConstants
                .COOK_TIME)));
        recipe.setPrepTime(cursor.getString(cursor.getColumnIndex(AppConstants
                .PREP_TIME)));
        recipe.setTotalTime(cursor.getString(cursor.getColumnIndex(AppConstants
                .TOTAL_TIME)));
        recipe.setCalories(cursor.getInt(cursor.getColumnIndex(AppConstants
                .CALORIES)));
        recipe.setYields(cursor.getInt(cursor.getColumnIndex(AppConstants
                .YIELDS)));
        recipe.setPath(cursor.getString(cursor.getColumnIndex(AppConstants
                .IMAGE_PATH)));
        recipe.setNotes(cursor.getString(cursor.getColumnIndex(AppConstants
                .NOTES)));
    }

    public static int[] getIdsFromData(ArrayList collectionList) {
        int[] ids = new int[collectionList.size()];
        int i = 0;
        for(Object obj : collectionList){
            if(obj instanceof Collection){
                ids[i++] = ((Collection)obj).getId();
            } else {
                ids[i++] = ((Cuisine)obj).getId();
            }
        }
        return ids;
    }

    public static CharSequence[] getCharSequenceFromData(ArrayList list){
        CharSequence[] charSequencesList = new CharSequence[list.size()];
        int i = 0;
        for(Object obj : list){
            if(obj instanceof Collection){
                charSequencesList[i++] = ((Collection)obj).getCollectionName();
            } else {
                charSequencesList[i++] = ((Cuisine)obj).getCuisineName();
            }
        }
        return  charSequencesList;
    }

    public static ContentValues getIngredientContentValues(Ingredient ingredient) {
        ContentValues values = new ContentValues();
        values.put(AppConstants.ITEM, ingredient.getIngredient());
        values.put(AppConstants.AMOUNT, ingredient.getQuantity());
        values.put(AppConstants.UNIT, ingredient.getUnit());
        return values;
    }

    public static ContentValues getStepsContentValues(Steps steps) {
        ContentValues values = new ContentValues();
        values.put(AppConstants.INSTRUCTION, steps.getInstruction());
        return values;
    }

    public static ContentValues getRecipeContentValues(Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(AppConstants.RECIPE_ID, recipe.getId());
        values.put(AppConstants.RECIPE_NAME, recipe.getTitle());
        values.put(AppConstants.CUISINE_ID, recipe.getCuisineId());
        values.put(AppConstants.WEBSITE, recipe.getWebsite());
        values.put(AppConstants.RATING, recipe.getRating());
        values.put(AppConstants.COOK_TIME, recipe.getCookTime());
        values.put(AppConstants.PREP_TIME, recipe.getPrepTime());
        values.put(AppConstants.TOTAL_TIME, recipe.getTotalTime());
        values.put(AppConstants.CALORIES, recipe.getCalories());
        values.put(AppConstants.YIELDS, recipe.getYields());
        values.put(AppConstants.IMAGE_PATH, recipe.getPath());
        values.put(AppConstants.NOTES, recipe.getNotes());
        return values;
    }

    public static boolean[] getCheckedItems(int[] itemIds, ArrayList<Collection> selectedItems) {
        boolean[] checkItemsState = new boolean[itemIds.length];
        boolean isFound;
        if(selectedItems == null || selectedItems.size() == 0){
            return null;
        }
        for(int index = 0; index < itemIds.length; index++) {
            isFound = false;
            for (Collection collection : selectedItems) {
                if(itemIds[index] == collection.getId()){
                    isFound = true;
                    break;
                }
            }

            checkItemsState[index] = isFound;
        }
        return checkItemsState;
    }

    public static AlertDialog.Builder createNewCollectionDialog(final Context context){

        AlertDialog.Builder createCollectionDialog = new AlertDialog.Builder(context);

        final EditText collectionNameEditText = new EditText(context);
        collectionNameEditText.setHint(context.getString(R.string.TEXT_NEW_COLLECTION_HINT));
        collectionNameEditText.setMaxLines(1);
        collectionNameEditText.setLines(1);
        collectionNameEditText.requestFocus();
        createCollectionDialog.setView(collectionNameEditText)
                .setTitle(context.getString(R.string.TEXT_NEW_COLLECTION))
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.TEXT_OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String newCollection = collectionNameEditText.getText().toString();
                        ((MainActivity)context).saveCollection(newCollection);
                        hideCollectionKeyboard(context, collectionNameEditText);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(context.getString(R.string.TEXT_CANCEL), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        hideCollectionKeyboard(context, collectionNameEditText);
                        dialog.cancel();
                    }
                });

        return createCollectionDialog;
    }

    public static void hideCollectionKeyboard(Context context, EditText editText){
        InputMethodManager imm = (InputMethodManager)context.getSystemService
                (Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static AlertDialog.Builder changeCollectionTitleDialog(final Context context,
                                                                  final long collectionId){

        AlertDialog.Builder createCollectionDialog = new AlertDialog.Builder(context);

        final EditText collectionNameEditText = new EditText(context);
        collectionNameEditText.setMaxLines(1);
        collectionNameEditText.setLines(1);

        createCollectionDialog.setView(collectionNameEditText)
                .setTitle(context.getString(R.string.TEXT_RENAME_COLLECTION))
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.TEXT_OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String newName = collectionNameEditText.getText().toString();
                        ((RecipeListActivity)context).renameCollection(collectionId, newName);
                        hideCollectionKeyboard(context, collectionNameEditText);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(context.getString(R.string.TEXT_CANCEL), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        hideCollectionKeyboard(context, collectionNameEditText);
                        dialog.cancel();
                    }
                });

        return createCollectionDialog;
    }

    public static AlertDialog.Builder deleteCollectionDialog(final Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.CONFIRM_DELETE_COLLECTION));
        builder.setMessage(context.getString(R.string.CONFIRM_DELETE_COLLECTION_MSG))
                // Set the action buttons
                .setPositiveButton(context.getString(R.string.TEXT_OK), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ((RecipeListActivity)context).deleteCollection();

                        // save collection and refresh ui
                        dialog.cancel();
                    }
                })
                .setNegativeButton(context.getString(R.string.TEXT_CANCEL), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Close CAB
                        dialog.dismiss();
                    }
                });
        return builder;
    }

    public static AlertDialog.Builder getCheckboxDialog(final CharSequence[] items,
                                                         final ArrayList<Collection> oldSelectedItems,
                                                         final ArrayList<Collection> newSelectedItems,
                                                         final String title,
                                                         final TextView textView,
                                                         final View rootView,
                                                         final int[] itemIds){

        Context context = rootView.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
        builder.setTitle(title);
        builder.setMultiChoiceItems(items, getCheckedItems(itemIds, newSelectedItems),
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            Collection collection = new Collection();
                            collection.setId(itemIds[indexSelected]);
                            collection.setCollectionName(items[indexSelected].toString());
                            newSelectedItems.add(collection);
                        } else {
                            for(int i = 0; i < newSelectedItems.size(); i++) {
                                Collection collection = newSelectedItems.get(i);
                                if (collection.getId() == itemIds[indexSelected]) {
                                    // Else, if the item is already in the array, remove it
                                    newSelectedItems.remove(collection);
                                    break;
                                }
                            }
                        }
                    }
                })
                // Set the action buttons
                .setPositiveButton(context.getString(R.string.TEXT_OK), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        oldSelectedItems.clear();
                        oldSelectedItems.addAll(newSelectedItems);
                        String collectionStr = "";
                        for(Collection collection : oldSelectedItems){
                            collectionStr += collection.getCollectionName() + ", ";
                        }
                        textView.setText(collectionStr.length() > 0 ? collectionStr.substring(0,
                                collectionStr.length() - 2) : "0 selected");
                    }
                })
                .setNegativeButton(context.getString(R.string.TEXT_CANCEL), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        newSelectedItems.clear();
                    }
                });
        return builder;
    }

    public static AlertDialog.Builder getRadioDialog(final CharSequence[] items,
                                                      final ArrayList<Integer> oldSelectedItems,
                                                      final ArrayList<Integer> newSelectedItems,
                                                      final String title,
                                                      final TextView textView,
                                                      final View rootView,
                                                      final int[] itemIds){
        final Context context = rootView.getContext();
        // find selected items position
        int selectionItemPosition = -1;
        if(newSelectedItems.size() > 0){
            int selectedCuisineId = newSelectedItems.get(0);
            for(int index = 0; index < itemIds.length; index++) {
                if (selectedCuisineId == itemIds[index]) {
                    selectionItemPosition = index;
                    break;
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
        builder.setTitle(title);
        builder.setSingleChoiceItems(items, selectionItemPosition,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        newSelectedItems.clear();
                        newSelectedItems.add(itemIds[which]);
                    }
                })
                // Set the action buttons
                .setPositiveButton(context.getString(R.string.TEXT_OK), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        oldSelectedItems.clear();
                        oldSelectedItems.addAll(newSelectedItems);
                        String collectionStr = "";
                        for (int index = 0; index < itemIds.length; index++) {
                            for (Integer itemId : oldSelectedItems) {
                                if (itemIds[index] == itemId) {
                                    collectionStr += items[index];
                                    break;
                                }
                            }
                        }
                        textView.setText(collectionStr.toString());
                    }
                })
                .setNegativeButton(context.getString(R.string.TEXT_CANCEL), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        newSelectedItems.clear();
                    }
                })
                .setNeutralButton(context.getString(R.string.reset_btn_text), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        newSelectedItems.clear();
                        //((AlertDialog) dialog).getListView().clearChoices();
                        oldSelectedItems.clear();
                        textView.setText(context.getString(R.string.zero_selected));
                    }
                });

        return builder;
    }

    public static File getAlbumStorageDir(Context context, String albumName) {
        File picDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // Get the directory for the app's private pictures directory.
        File file = new File(picDir, albumName);
        try {
            file.createNewFile();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return file;
    }



    public static String saveFileToInternalStorage(Context context, Bitmap image, int nextRecipeId){
        if(image == null){
            return null;
        }

        File file = getAlbumStorageDir(context, nextRecipeId+AppConstants.JPG_FILE_FORMAT);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = file;
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getPath();
    }

    public static Bitmap getGalleryImagePath(Intent data, Context context) {
        // find path
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImageUri,
                projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String selectedImagePath = cursor.getString(column_index);

        // form a image
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(selectedImagePath, options);

        return bitmap;
    }


    public static Bitmap getThumbnailFromInternalStorage(String path) {
        Bitmap thumbnail = null;

        try {
            File filePath = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            thumbnail = BitmapFactory.decodeFile(filePath.getPath(), options);
        } catch (Exception ex) {
            Log.e(ApplicationHelper.class.toString(), ex.getMessage());
        }

        return thumbnail;
    }

    public static Bitmap getThumbnailFromInternalStorage(Context context, int recipeId) {
        Bitmap thumbnail = null;

        // If no file on external storage, look in internal storage
        if (thumbnail == null) {
            try {
                File filePath = new File(context.getExternalFilesDir(
                        Environment.DIRECTORY_PICTURES), recipeId + AppConstants.JPG_FILE_FORMAT);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                thumbnail = BitmapFactory.decodeFile(filePath.getPath(), options);
            } catch (Exception ex) {
                Log.e(ApplicationHelper.class.toString(), ex.getMessage());
            }
        }
        return thumbnail;
    }

    public static boolean deleteThumbnail(Context context, int recipeId) {
        boolean isDeleted = false;

        // If no file on external storage, look in internal storage
        try {
            File filePath = new File(context.getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES), recipeId + AppConstants.JPG_FILE_FORMAT);
            filePath.delete();
        } catch (Exception ex) {
            Log.e(ApplicationHelper.class.toString(), ex.getMessage());
        }
        return isDeleted;
    }

    public static void setTimePickerToTextView(final String dialogTitle,
                                               final TextView timeTextView,
                                               Context context){
        int hour = 0;
        int minute = 0;

        String selectedTime = timeTextView.getText().toString();
        if(!selectedTime.equals("")) {
            String[] splitTime = selectedTime.split(":");
            hour = Integer.parseInt(splitTime[0]);
            minute = Integer.parseInt(splitTime[1]);
        }

        CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(context,
                CustomTimePickerDialog.timeSetListener, hour, minute, true);
        timePickerDialog.setPermanentTitle(dialogTitle);
        timePickerDialog.setTimeTextView(timeTextView);
        timePickerDialog.show();
    }


    public static void openSearchRecipePage(Context context) {
        // open search recipe page
        Intent detailIntent = new Intent(context, FilterRecipeActivity.class);
        context.startActivity(detailIntent);
    }
}
