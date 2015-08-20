package com.raksha.assignment.cookbookapp.activity.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.raksha.assignment.cookbookapp.R;
import com.raksha.assignment.cookbookapp.pojo.Ingredient;
import com.raksha.assignment.cookbookapp.utility.AppConstants;
import com.raksha.assignment.cookbookapp.utility.ApplicationHelper;

import java.util.ArrayList;
import java.util.Iterator;

public class IngredientsActivity extends ActionBarActivity implements View.OnClickListener  {

    private ArrayList<Ingredient> ingredientArrayList;
    private ListView listView;
    private IngredientsActivity ingredientsActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_list);
        ingredientsActivity = this;

        ingredientArrayList = getIntent().getParcelableArrayListExtra(AppConstants
                .DATA_INGREDIENTS);

        if(ingredientArrayList == null){
            ingredientArrayList = new ArrayList<>();
        }

        //set list adapter
        listView = (ListView) findViewById(R.id.ingredients_list);
        listView.setAdapter(new ListAdapter(this, R.layout.activity_ingredient_list_item, ingredientArrayList));
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void onClick(View button) {
        switch(button.getId()){
            case R.id.delete:
                deleteIngredient(button);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ingredients, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                return true;
            case R.id.action_delete:
                deleteAllIngredient();
                return true;
            case R.id.action_new:
                addNewIngredientRow();
                return true;
        }
        return true;
    }

    private void deleteAllIngredient() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.CONFIRM_DELETE_ALL_INGREDIENT))
                // Set the action buttons
                .setPositiveButton(getString(R.string.TEXT_OK), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ingredientArrayList.clear();
                        ((ArrayAdapter)listView.getAdapter()).notifyDataSetChanged();
                        dialog.dismiss();
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

    private void saveIngredients(){
        for (Iterator<Ingredient> iterator = ingredientArrayList.iterator(); iterator.hasNext();) {
            Ingredient ingredient = iterator.next();
            if(ApplicationHelper.isNullOrBlank(ingredient.getQuantity())
                    && ApplicationHelper.isNullOrBlank(ingredient.getIngredient())){
                iterator.remove();
            }
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(AppConstants.DATA_INGREDIENTS, ingredientArrayList);
        setResult(Activity.RESULT_OK, resultIntent);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        saveIngredients();
        hideKeyboard();
        finish();
    }

    private void hideKeyboard() {
        if(getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void deleteIngredient(final View button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.CONFIRM_DELETE_SINGLE_INGREDIENT))
                // Set the action buttons
                .setPositiveButton(getString(R.string.TEXT_OK), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int position = (int)button.getTag();
                        ingredientArrayList.remove(position);
                        ((ArrayAdapter)listView.getAdapter()).notifyDataSetChanged();
                        dialog.dismiss();
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

    private void addNewIngredientRow() {
        ListView listView = (ListView) findViewById(R.id.ingredients_list);
        ingredientArrayList.add(new Ingredient());
        ((ArrayAdapter)listView.getAdapter()).notifyDataSetChanged();
    }

    public class ListAdapter extends ArrayAdapter<String> {
        private LayoutInflater inflater;
        private ArrayList<Ingredient> ingredientArrayList;

        public ListAdapter(Context context, int resourceId,
                               ArrayList recipeList) {
            super(context, resourceId, recipeList);
            this.ingredientArrayList = recipeList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if(ingredientArrayList != null && ingredientArrayList.size() != 0){
                return ingredientArrayList.size();
            }
            return 0;
        }

        class ViewHolder {
            int id;
            EditText quantity;
            EditText ingredient;
            Spinner unit;
            ImageView delete;
        }

        public View getView(int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.activity_ingredient_list_item, null);

                holder.unit = (Spinner) view.findViewById(R.id.unit);
                holder.delete = (ImageView) view.findViewById(R.id.delete);
                holder.quantity = (EditText) view.findViewById(R.id.quantity);
                holder.ingredient = (EditText) view.findViewById(R.id.ingredient);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(view.getContext(),
                    R.array.units_array, android.R.layout.simple_spinner_item);

            // Specify the layout to use when the list of choices appears
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            holder.id = position;
            holder.delete.setOnClickListener(ingredientsActivity);
            holder.delete.setTag(position);
            holder.quantity.setText(ingredientArrayList.get(position).getQuantity());
            holder.quantity.setFocusable(true);
            holder.quantity.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    showSoftKeyboard(v);
                    return true;
                }
            });
            holder.quantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    ingredientArrayList.get(holder.id).setQuantity(arg0.toString());
                }
            });
            holder.ingredient.setText(ingredientArrayList.get(position).getIngredient());
            holder.ingredient.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    ingredientArrayList.get(holder.id).setIngredient(arg0.toString());
                }
            });
            holder.unit.setAdapter(arrayAdapter);
            holder.unit.setSelection(arrayAdapter.getPosition(ingredientArrayList.get
                    (position).getUnit()));
            holder.unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ingredientArrayList.get(holder.id).setUnit((String) parent.getItemAtPosition
                            (position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            view.requestFocus();
            return view;
        }
    }

}
