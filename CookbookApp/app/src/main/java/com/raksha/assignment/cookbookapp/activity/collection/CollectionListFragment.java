package com.raksha.assignment.cookbookapp.activity.collection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

//import com.raksha.assignment.cookbookapp.dummy.DummyContent;

import com.raksha.assignment.cookbookapp.R;
import com.raksha.assignment.cookbookapp.activity.adapter.CollectionListAdapter;
import com.raksha.assignment.cookbookapp.pojo.Collection;
import com.raksha.assignment.cookbookapp.utility.AppConstants;
import com.raksha.assignment.cookbookapp.utility.DatabaseHelper;

import java.util.ArrayList;

/**
 * A list fragment representing a list of Collections.
 */
public class CollectionListFragment extends ListFragment {

    private ArrayList<Collection> collectionArrayList = new ArrayList<>();;
    private DatabaseHelper databaseHelper;
    private boolean isSortByTitle = true;
    private boolean isSortByRecentAdded;
    private CollectionListAdapter collectionListAdapter;

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks callbacks;

    public CollectionListFragment() {
    }

    public static CollectionListFragment newInstance() {
        CollectionListFragment collectionListFragment = new CollectionListFragment();
        return collectionListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openDatabase();
        // populate list
        loadCollection();
    }

    private void loadCollection(){
        collectionArrayList.addAll(databaseHelper.getAllCollections(isSortByTitle, isSortByRecentAdded));
    }

    private void openDatabase() {
        if(databaseHelper == null){
            databaseHelper = DatabaseHelper.getInstance(this.getActivity());
        }
    }

    public void refresh(){
        collectionArrayList.clear();
        loadCollection();
        collectionListAdapter.notifyDataSetChanged();
    }

    public void setSortOrder( boolean isSortByTitle, boolean isSortByRecentAdded){
        this.isSortByTitle = isSortByTitle;
        this.isSortByRecentAdded = isSortByRecentAdded;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        collectionListAdapter = new CollectionListAdapter(this.getActivity(),
                R.layout.activity_collection_list_item, collectionArrayList);
        setListAdapter(collectionListAdapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView listView = getListView();
        final CollectionListAdapter listViewAdapter = (CollectionListAdapter)listView.getAdapter();
        if(listView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            // Capture ListView item click
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                @Override
                public void onItemCheckedStateChanged(ActionMode mode,
                                                      int position, long id, boolean checked) {
                    // Capture total checked items
                    final int checkedCount = listView.getCheckedItemCount();
                    mode.setTitle(String.format(getString(R.string.TEXT_SELECTED_COUNT),
                            checkedCount));
                    listViewAdapter.toggleSelection(position);
                }

                @Override
                public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_delete:
                            // Calls getSelectedIds method from ListViewAdapter Class
                            final SparseBooleanArray selected = listViewAdapter
                                    .getSelectedIds();
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(getString(R.string.CONFIRM_DELETE_COLLECTION));
                            builder.setMessage(getString(R.string
                                    .CONFIRM_DELETE_MULTI_COLLECTION_MSG, selected.size()))
                                    // Set the action buttons
                                    .setPositiveButton(getString(R.string.TEXT_OK), new DialogInterface
                                            .OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Captures all selected ids with a loop
                                            for (int i = (selected.size() - 1); i >= 0; i--) {
                                                if (selected.valueAt(i)) {
                                                    Collection selectedItem = listViewAdapter
                                                            .getItem(selected.keyAt(i));
                                                    // Remove selected items following the ids
                                                    listViewAdapter.remove(selectedItem);
                                                    databaseHelper.deleteCollection(selectedItem.getId());
                                                }
                                            }
                                            refresh();
                                            mode.finish();
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.TEXT_CANCEL), new DialogInterface
                                            .OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Close CAB
                                            dialog.dismiss();
                                        }
                                    }).show();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.delete_menu, menu);
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

        // Reset the active callbacks interface to the dummy implementation.
        callbacks = null;
    }

    @Override
    public void onListItemClick(final ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);



        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        callbacks.onCollectionSelected((collectionArrayList.get(position)));
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
        public void onCollectionSelected(Collection collection);
    }

}
