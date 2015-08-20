package com.raksha.assignment.cookbookapp.pojo;

import java.util.ArrayList;

/**
 * Created by Raksha on 5/1/2015.
 */
public class SearchCriteria {

    String title;
    Integer cuisineId;
    String website;
    String cookTime;
    String prepTime;
    Integer calories;
    Integer yields;
    String ingredients;
    String yieldCondition;
    String calorieCondition;
    String cookTimeCondition;
    String prepTimeCondition;
    ArrayList<Collection> collections;

    public String getYieldCondition() {
        return yieldCondition;
    }

    public void setYieldCondition(String yieldCondition) {
        this.yieldCondition = yieldCondition;
    }

    public String getCalorieCondition() {
        return calorieCondition;
    }

    public void setCalorieCondition(String calorieCondition) {
        this.calorieCondition = calorieCondition;
    }

    public String getCookTimeCondition() {
        return cookTimeCondition;
    }

    public void setCookTimeCondition(String cookTimeCondition) {
        this.cookTimeCondition = cookTimeCondition;
    }

    public String getPrepTimeCondition() {
        return prepTimeCondition;
    }

    public void setPrepTimeCondition(String prepTimeCondition) {
        this.prepTimeCondition = prepTimeCondition;
    }

    public SearchCriteria(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCuisineId() {
        return cuisineId;
    }

    public void setCuisineId(Integer cuisineId) {
        this.cuisineId = cuisineId;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCookTime() {
        return cookTime;
    }

    public void setCookTime(String cookTime) {
        this.cookTime = cookTime;
    }

    public String getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(String prepTime) {
        this.prepTime = prepTime;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Integer getYields() {
        return yields;
    }

    public void setYields(Integer yields) {
        this.yields = yields;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<Collection> getCollections() {
        return collections;
    }

    public void setCollections(ArrayList<Collection> collections) {
        this.collections = collections;
    }
}
