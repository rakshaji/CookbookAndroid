package com.raksha.assignment.cookbookapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Raksha on 4/25/2015.
 */
public class Collection implements Parcelable {
    int id;
    String collectionName;
    int totalItems;

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    @Override
    public String toString() {
        String id = String.valueOf(getId());
        return id;
    }

    public Collection() {}

    protected Collection(Parcel in) {
        id = in.readInt();
        collectionName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(collectionName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Collection> CREATOR = new Parcelable.Creator<Collection>() {
        @Override
        public Collection createFromParcel(Parcel in) {
            return new Collection(in);
        }

        @Override
        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };
}
