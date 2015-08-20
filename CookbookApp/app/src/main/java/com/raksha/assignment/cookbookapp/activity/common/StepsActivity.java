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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.raksha.assignment.cookbookapp.R;
import com.raksha.assignment.cookbookapp.pojo.Ingredient;
import com.raksha.assignment.cookbookapp.pojo.Steps;
import com.raksha.assignment.cookbookapp.utility.AppConstants;
import com.raksha.assignment.cookbookapp.utility.ApplicationHelper;

import java.util.ArrayList;
import java.util.Iterator;

public class StepsActivity extends ActionBarActivity implements View.OnClickListener  {

    private ArrayList<Steps> stepsArrayList;
    private ListView listView;
    private StepsActivity stepsActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_list);
        stepsActivity = this;

        stepsArrayList = getIntent().getParcelableArrayListExtra(AppConstants
                .DATA_STEPS);

        if(stepsArrayList == null){
            stepsArrayList = new ArrayList<>();
        }

        //set list adapter
        listView = (ListView) findViewById(R.id.steps_list);
        listView.setAdapter(new ListAdapter(this, R.layout.activity_steps_list_item, stepsArrayList));
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
                deleteStep(button);
                break;

            default:
                break;
        }
    }

    private void deleteAllSteps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.TEXT_DELETE_ALL_STEPS))
                // Set the action buttons
                .setPositiveButton(getString(R.string.TEXT_OK), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        stepsArrayList.clear();
                        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
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
                deleteAllSteps();
                return true;

            case R.id.action_new:
                addNewStepsRow();
                return true;
        }
        return true;
    }

    private void saveSteps(){
        for (Iterator<Steps> iterator = stepsArrayList.iterator(); iterator.hasNext();) {
            Steps steps = iterator.next();
            if(ApplicationHelper.isNullOrBlank(steps.getInstruction())){
                iterator.remove();
            }
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(AppConstants.DATA_STEPS, stepsArrayList);
        setResult(Activity.RESULT_OK, resultIntent);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        saveSteps();
        hideKeyboard();
        finish();
    }

    private void deleteStep(final View button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.TEXT_DELETE_SINGLE_STEP))
                // Set the action buttons
                .setPositiveButton(getString(R.string.TEXT_OK), new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int position = (int)button.getTag();
                        stepsArrayList.remove(position);
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

    private void addNewStepsRow() {
        ListView listView = (ListView) findViewById(R.id.steps_list);
        stepsArrayList.add(new Steps());
        ((ArrayAdapter)listView.getAdapter()).notifyDataSetChanged();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)this.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    public class ListAdapter extends ArrayAdapter<String> {
        private LayoutInflater inflater;
        private ArrayList<Steps> stepsList;

        public ListAdapter(Context context, int resourceId,
                               ArrayList stepsList) {
            super(context, resourceId, stepsList);
            this.stepsList = stepsList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if(stepsList != null && stepsList.size() != 0){
                return stepsList.size();
            }
            return 0;
        }

        class ViewHolder {
            int id;
            EditText instruction;
            TextView count;
            ImageView delete;
        }

        public View getView(int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.activity_steps_list_item, null);

                holder.delete = (ImageView) view.findViewById(R.id.delete);
                holder.instruction = (EditText) view.findViewById(R.id.steps_text);
                holder.count = (TextView) view.findViewById(R.id.steps_count);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.id = position;
            holder.delete.setOnClickListener(stepsActivity);
            holder.delete.setTag(position);
            holder.count.setText("Step "+String.valueOf(position+1));
            holder.instruction.setText(stepsList.get(position).getInstruction());
            holder.instruction.setFocusable(true);
            holder.instruction.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    showSoftKeyboard(v);
                    return true;
                }
            });
            holder.instruction.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    stepsList.get(holder.id).setInstruction(arg0.toString());
                }
            });

            view.requestFocus();
            return view;
        }
    }

}
