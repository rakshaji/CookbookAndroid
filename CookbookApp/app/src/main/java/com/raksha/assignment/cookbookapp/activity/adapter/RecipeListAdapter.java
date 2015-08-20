package com.raksha.assignment.cookbookapp.activity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.raksha.assignment.cookbookapp.R;
import com.raksha.assignment.cookbookapp.pojo.Recipe;
import com.raksha.assignment.cookbookapp.utility.ApplicationHelper;

import java.util.ArrayList;

public class RecipeListAdapter extends ArrayAdapter<Recipe> {

	private LayoutInflater inflater;
	private ArrayList<Recipe> recipeList;
	private SparseBooleanArray selectedItemsIds;

	public RecipeListAdapter(Context context, int resourceId,
                             ArrayList<Recipe> recipeList) {
		super(context, resourceId, recipeList);
		selectedItemsIds = new SparseBooleanArray();
		this.recipeList = recipeList;
		inflater = LayoutInflater.from(context);
	}

    class ViewHolder {
        ImageView recipeImage;
        TextView recipeName;
        TextView cuisineName;
        TextView cookTime;
        TextView prepTime;
    }

	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.activity_recipe_list_item, null);
			// Locate the TextViews in list
            holder.recipeImage = (ImageView) view.findViewById(R.id.recipeImage);
            holder.recipeName = (TextView) view.findViewById(R.id.recipeName);
            holder.cookTime = (TextView) view.findViewById(R.id.cook_time_text);
            holder.prepTime = (TextView) view.findViewById(R.id.prep_time_text);
            holder.cuisineName = (TextView) view.findViewById(R.id.cuisine_text);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		// Capture position and set to the TextViews
        String imagePath = recipeList.get(position).getPath();
        if(imagePath != null) {
            Bitmap bitmap = ApplicationHelper.getThumbnailFromInternalStorage(imagePath);
            holder.recipeImage.setImageBitmap(bitmap);
            holder.recipeImage.setTag(imagePath);
        } else {
            holder.recipeImage.setImageResource(R.drawable.ic_action_picture_dark);
            holder.recipeImage.setTag(imagePath);
        }

        holder.recipeName.setText(recipeList.get(position).getTitle());
        holder.cuisineName.setText(recipeList.get(position).getCuisineName());
        holder.prepTime.setText(recipeList.get(position).getPrepTime());
        holder.cookTime.setText(recipeList.get(position).getCookTime());
		return view;
	}

	@Override
	public void remove(Recipe object) {
		recipeList.remove(object);
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