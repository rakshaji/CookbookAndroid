package com.raksha.assignment.cookbookapp.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.raksha.assignment.cookbookapp.activity.collection.CollectionListFragment;
import com.raksha.assignment.cookbookapp.activity.recipe.RecipeListFragment;

public class MainActivityViewPagerAdapter extends FragmentStatePagerAdapter {
    private int NUM_ITEMS = 2;

    public MainActivityViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return RecipeListFragment.newInstance(null);

            case 1:
                return CollectionListFragment.newInstance();

            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "RECIPE";
                break;

            case 1:
                title = "COLLECTION";
                break;

            default:
                return null;
        }
        return title;
    }

}