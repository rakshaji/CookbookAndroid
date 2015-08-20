package com.raksha.assignment.cookbookapp.activity.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.raksha.assignment.cookbookapp.R;
import com.raksha.assignment.cookbookapp.pojo.Collection;

import java.util.ArrayList;

/**
 * Created by Raksha on 4/23/2015.
 */
public class CollectionListAdapter extends ArrayAdapter<Collection> {
    public ArrayList<Collection> collections;
    private LayoutInflater inflater;
    private SparseBooleanArray selectedItemsIds;

    public CollectionListAdapter(Context context, int resourceId,
                             ArrayList<Collection> collectionArrayList) {
        super(context, resourceId, collectionArrayList);
        selectedItemsIds = new SparseBooleanArray();
        this.collections = collectionArrayList;
        inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return collections.size();
    }

    public Collection getItem(int position) {
        return collections.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_collection_list_item, null);

            holder = new ViewHolder();
            holder.collectionName = (TextView) convertView.findViewById(R.id.collectionName);
            holder.recipeCount = (TextView) convertView.findViewById(R.id.recipe_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.collectionName.setText(collections.get(position).getCollectionName());
        holder.recipeCount.setText(String.valueOf(collections.get(position).getTotalItems()));

        return convertView;
    }

    static class ViewHolder {
        TextView collectionName;
        TextView recipeCount;
    }

    @Override
    public void remove(Collection object) {
        collections.remove(object);
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !selectedItemsIds.get(position));
    }

    public void removeSelection() {
        selectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            selectedItemsIds.put(position, value);
        else
            selectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return selectedItemsIds;
    }
}