package com.favesolution.jktotw.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Daniel on 11/9/2015 for JktOtw project.
 */
public class Category implements Parcelable {
    private String categoryName;
    private String categoryFilter;
    private int categoryIconMarker;
    private int categoryIconMenu;

    public String getCategoryFilter() {
        return categoryFilter;
    }

    public void setCategoryFilter(String categoryFilter) {
        this.categoryFilter = categoryFilter;
    }

    public int getCategoryIconMarker() {
        return categoryIconMarker;
    }

    public void setCategoryIconMarker(int categoryIconMarker) {
        this.categoryIconMarker = categoryIconMarker;
    }

    public int getCategoryIconMenu() {
        return categoryIconMenu;
    }

    public void setCategoryIconMenu(int categoryIconMenu) {
        this.categoryIconMenu = categoryIconMenu;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.categoryName);
        dest.writeString(this.categoryFilter);
        dest.writeInt(this.categoryIconMarker);
        dest.writeInt(this.categoryIconMenu);
    }

    public Category() {
    }

    protected Category(Parcel in) {
        this.categoryName = in.readString();
        this.categoryFilter = in.readString();
        this.categoryIconMarker = in.readInt();
        this.categoryIconMenu = in.readInt();
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
