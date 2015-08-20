package com.raksha.assignment.cookbookapp.activity.recipe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.raksha.assignment.cookbookapp.R;
import com.raksha.assignment.cookbookapp.activity.adapter.RecipeListAdapter;
import com.raksha.assignment.cookbookapp.pojo.Collection;
import com.raksha.assignment.cookbookapp.pojo.Recipe;
import com.raksha.assignment.cookbookapp.utility.AppConstants;
import com.raksha.assignment.cookbookapp.utility.ApplicationHelper;
import com.raksha.assignment.cookbookapp.utility.DatabaseHelper;

import java.util.ArrayList;

/**
 * A list fragment representing a list of Recipes.
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class RecipeListFragment extends ListFragment {

    private ArrayList<Recipe> recipeArrayList = new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private Callbacks callbacks;
    private RecipeListAdapter listViewAdapter;
    private boolean isSortByTitle;
    private boolean isSortByRecentAdded;
    private long collectionId;
    private boolean isCollectionFlow;
    private ArrayList<Recipe> searchResults;

    public RecipeListFragment() {

    }

    // newInstance constructor for creating fragment with arguments
    public static RecipeListFragment newInstance(Bundle arguments) {
        RecipeListFragment recipeListFragment = new RecipeListFragment();
        if(arguments != null) {
            long id = arguments.getLong(AppConstants.COLLECTION_ID);
            if(id > 0){
                recipeListFragment.setCollectionId(id);
                recipeListFragment.setCollectionFlow(true);
            }

            ArrayList<Recipe> recipes = arguments.<Recipe>getParcelableArrayList
                    (AppConstants.ARG_RECIPE_LIST);
            recipeListFragment.setSearchResults(recipes);
        }
        return recipeListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openDatabase();
        loadRecipeList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listViewAdapter = new RecipeListAdapter(this.getActivity(),
                R.layout.activity_recipe_list_item, recipeArrayList);
        setListAdapter(listViewAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void loadRecipeList() {
        if (getSearchResults() != null){
            recipeArrayList.addAll(getSearchResults());
        } else {
            recipeArrayList.addAll(databaseHelper.getAllRecipes(isSortByTitle, isSortByRecentAdded,
                    collectionId));
        }
    }

    private void openDatabase() {
        if(databaseHelper == null){
            databaseHelper = DatabaseHelper.getInstance(this.getActivity());
        }
    }

    public void refresh(){
        reloadRecipes();
        if(isCollectionFlow()){
            callbacks.setParentRefreshNeeded(true);
        }
    }

    private void reloadRecipes(){
        recipeArrayList.clear();
        loadRecipeList();
        listViewAdapter.notifyDataSetChanged();
    }

    public void setSortOrder( boolean isSortByTitle, boolean isSortByRecentAdded){
        this.isSortByTitle = isSortByTitle;
        this.isSortByRecentAdded = isSortByRecentAdded;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListView listView = getListView();
        if(listView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            // Capture ListView item click
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                @Override
                public void onItemCheckedStateChanged(ActionMode mode,
                                                      int position, long id, boolean checked) {
                    // Capture total checked items
                    final int checkedCount = listView.getCheckedItemCount();
                    // Set the CAB title according to total checked items
                    mode.setTitle(String.format(getString(R.string.TEXT_SELECTED_COUNT),
                            checkedCount));
                    // Calls toggleSelection method from ListViewAdapter Class
                    listViewAdapter.toggleSelection(position);
                }

                @Override
                public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                    final SparseBooleanArray selected = listViewAdapter
                            .getSelectedIds();
                    switch (item.getItemId()) {
                        case R.id.action_delete:
                            if(isCollectionFlow()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle(getString(R.string.CONFIRM_REMOVE_MULTI_RECIPES));
                                builder.setMessage(getString(R.string
                                        .CONFIRM_REMOVE_MULTI_RECIPES_MSG, selected.size()))
                                        // Set the action buttons
                                        .setPositiveButton(getString(R.string.TEXT_OK), new DialogInterface
                                                .OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                // Captures all selected ids with a loop
                                                for (int i = (selected.size() - 1); i >= 0; i--) {
                                                    if (selected.valueAt(i)) {
                                                        Recipe selectedItem = listViewAdapter
                                                                .getItem(selected.keyAt(i));
                                                        // Remove selected items following the ids
                                                        listViewAdapter.remove(selectedItem);
                                                        // if not through collection flow
                                                        databaseHelper.deleteRecipeFromCollection(selectedItem
                                                                .getId(), collectionId);
                                                    }
                                                }
                                                refresh();
                                                // Close CAB
                                                mode.finish();
                                                callbacks.setParentRefreshNeeded(true);
                                            }
                                        })
                                        .setNegativeButton(getString(R.string.TEXT_CANCEL),
                                                new DialogInterface
                                                        .OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // Close CAB
                                                        dialog.dismiss();
                                                    }
                                                }).show();
                            } else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle(getString(R.string.CONFIRM_DELETE_SINGLE_RECIPE));
                                builder.setMessage(getString(R.string
                                        .CONFIRM_DELETE_MULTI_RECIPE_MSG, selected.size()))
                                        // Set the action buttons
                                        .setPositiveButton(getString(R.string.TEXT_OK), new DialogInterface
                                                .OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                // Captures all selected ids with a loop
                                                for (int i = (selected.size() - 1); i >= 0; i--) {
                                                    if (selected.valueAt(i)) {
                                                        Recipe selectedItem = listViewAdapter
                                                                .getItem(selected.keyAt(i));
                                                        databaseHelper.deleteRecipe(String.valueOf(selectedItem
                                                                .getId()));
                                                        ApplicationHelper.deleteThumbnail(getActivity(), selectedItem.getId());
                                                    }
                                                }
                                                refresh();
                                                // Close CAB
                                                mode.finish();
                                                callbacks.setParentRefreshNeeded(true);
                                            }
                                        })
                                        .setNegativeButton(getString(R.string.TEXT_CANCEL),
                                                new DialogInterface
                                                .OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                // Close CAB
                                                dialog.dismiss();
                                            }
                                        }).show();

                            }
                            return true;
                        case R.id.action_move:
                            final ArrayList<Collection> newSelectedItems = new ArrayList();
                            final ArrayList<Collection> allCollections = databaseHelper
                                    .getAllCollections(true, false);
                            final ArrayList<Collection> oldSelectedItems = new ArrayList<>();

                            // on single selection , show all collections saved for that recipe
                            if(selected.size() == 1){
                                Recipe selectedItem = listViewAdapter
                                        .getItem(selected.keyAt(0));
                                ArrayList<Collection> selections = databaseHelper.getCollectionByRecipeId
                                        (selectedItem.getId());
                                oldSelectedItems.addAll(selections);
                            } else {
                                oldSelectedItems.clear();
                                Collection collection = new Collection();
                                collection.setId((int)collectionId);
                                oldSelectedItems.add(collection);
                            }

                            CharSequence[] collectionNames = ApplicationHelper.getCharSequenceFromData
                                    (allCollections);
                            final int[] collectionIds = ApplicationHelper.getIdsFromData
                                    (allCollections);
                            newSelectedItems.clear();
                            newSelectedItems.addAll(oldSelectedItems);

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(getString(R.string.TEXT_MOVE_TO_COLLECTION));
                            builder.setMultiChoiceItems(collectionNames,
                                    ApplicationHelper.getCheckedItems(collectionIds,
                                            newSelectedItems),
                                    new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int indexSelected,
                                                            boolean isChecked) {
                                            if (isChecked) {
                                                // If the user checked the item, add it to the selected items
                                                Collection collection = new Collection();
                                                collection.setId(collectionIds[indexSelected]);
                                                collection.setCollectionName
                                                        (String.valueOf
                                                                (collectionIds[indexSelected]));
                                                newSelectedItems.add(collection);
                                            } else {
                                                for(int i = 0; i < newSelectedItems.size(); i++) {
                                                    Collection collection = newSelectedItems.get(i);
                                                    if (collection.getId() == collectionIds[indexSelected]) {
                                                        // Else, if the item is already in the array, remove it
                                                        newSelectedItems.remove(collection);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    })
                                    // Set the action buttons
                                    .setPositiveButton(getString(R.string.TEXT_OK), new DialogInterface
                                            .OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            oldSelectedItems.clear();
                                            oldSelectedItems.addAll(newSelectedItems);
                                            moveRecipeToCollections(newSelectedItems);
                                            mode.finish();
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.TEXT_CANCEL),
                                            new DialogInterface
                                            .OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            newSelectedItems.clear();
                                        }
                                    }).show();

                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    if(isCollectionFlow()){
                        mode.getMenuInflater().inflate(R.menu.move_delete_menu, menu);
                    } else {
                        mode.getMenuInflater().inflate(R.menu.delete_menu, menu);
                    }
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    listViewAdapter.removeSelection();
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }
            });
        }
    }

    public void moveRecipeToCollections(ArrayList<Collection> collectionArrayList){
        if(!isCollectionFlow()) {
            return;
        }

        SparseBooleanArray selected = listViewAdapter
                .getSelectedIds();
        // Captures all selected ids with a loop
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                Recipe selectedItem = listViewAdapter
                        .getItem(selected.keyAt(i));

                // Remove selected items following the ids
                listViewAdapter.remove(selectedItem);

                // if not through collection flow
                databaseHelper.moveRecipeToCollections(selectedItem.getId(),
                        collectionArrayList);
            }
        }
        refresh();
        callbacks.setParentRefreshNeeded(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        callbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        callbacks = null;
    }

    @Override
    public void onListItemClick(final ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        int recipeId = (recipeArrayList.get(position)).getId();
        callbacks.onRecipeSelected(recipeId);
    }

    public ArrayList<Recipe> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(ArrayList<Recipe> searchResults) {
        this.searchResults = searchResults;
    }

    public void setCollectionId(long collectionId) {
        this.collectionId = collectionId;
    }

    public boolean isCollectionFlow() {
        return isCollectionFlow;
    }

    public void setCollectionFlow(boolean isCollectionFlow) {
        this.isCollectionFlow = isCollectionFlow;
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onRecipeSelected(int id);
        public void setParentRefreshNeeded(boolean isNeeded);
    }

}
