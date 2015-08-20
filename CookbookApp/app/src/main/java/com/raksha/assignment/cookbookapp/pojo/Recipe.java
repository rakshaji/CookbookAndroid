package com.raksha.assignment.cookbookapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Raksha on 4/25/2015.
 */
public class Recipe implements Parcelable{
    int id;
    String title;
    Integer cuisineId;
    String website;
    int rating;
    String cookTime;
    String prepTime;
    String totalTime;
    Integer calories;
    Integer yields;
    String notes;
    String path;
    ArrayList<Ingredient> ingredients;
    ArrayList<Steps> steps;
    ArrayList<Collection> collections;
    String cuisineName;

    public Recipe(){}

    protected Recipe(Parcel in) {
        id = in.readInt();
        title = in.readString();
        cuisineId = in.readByte() == 0x00 ? null : in.readInt();
        website = in.readString();
        rating = in.readInt();
        cookTime = in.readString();
        prepTime = in.readString();
        totalTime = in.readString();
        calories = in.readByte() == 0x00 ? null : in.readInt();
        yields = in.readByte() == 0x00 ? null : in.readInt();
        notes = in.readString();
        path = in.readString();

        if (in.readByte() == 0x01) {
            ingredients = new ArrayList<Ingredient>();
            in.readList(ingredients, Ingredient.class.getClassLoader());
        } else {
            ingredients = null;
        }

        if (in.readByte() == 0x01) {
            steps = new ArrayList<Steps>();
            in.readList(steps, Steps.class.getClassLoader());
        } else {
            steps = null;
        }

        if (in.readByte() == 0x01) {
            collections = new ArrayList<Collection>();
            in.readList(collections, Collection.class.getClassLoader());
        } else {
            collections = null;
        }

        cuisineName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);

        if (cuisineId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(cuisineId);
        }

        dest.writeString(website);
        dest.writeInt(rating);
        dest.writeString(cookTime);
        dest.writeString(prepTime);
        dest.writeString(totalTime);

        if (calories == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(calories);
        }

        if (yields == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(yields);
        }

        dest.writeString(notes);
        dest.writeString(path);

        if (ingredients == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(ingredients);
        }

        if (steps == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(steps);
        }

        if (collections == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(collections);
        }

        dest.writeString(cuisineName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getCuisineName() {
        return cuisineName;
    }

    public void setCuisineName(String cuisineName) {
        this.cuisineName = cuisineName;
    }

    public ArrayList<Collection> getCollections() {
        return collections;
    }

    public void setCollections(ArrayList<Collection> collections) {
        this.collections = collections;
    }

    public ArrayList<Steps> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Steps> steps) {
        this.steps = steps;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
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

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
