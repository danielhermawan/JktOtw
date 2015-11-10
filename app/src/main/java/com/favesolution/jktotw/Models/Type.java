package com.favesolution.jktotw.Models;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;

import com.favesolution.jktotw.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 11/9/2015 for JktOtw project.
 */
public class Type implements Parcelable {
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
    public static List<Type> getCategory(Context context) {
        TypedArray categories = context.getResources().obtainTypedArray(R.array.categories);
        TypedArray categoryFilters = context.getResources().obtainTypedArray(R.array.category_filter);
        TypedArray categoryMarkers = context.getResources().obtainTypedArray(R.array.category_icon_marker);
        TypedArray categoryMenus = context.getResources().obtainTypedArray(R.array.category_icon_menu);
        List<Type> types = new ArrayList<>();
        for (int i = 0; i < categories.length(); i++) {
            Type type = new Type();
            type.setCategoryName(categories.getString(i));
            type.setCategoryFilter(categoryFilters.getString(i));
            type.setCategoryIconMarker(categoryMarkers.getResourceId(i,0));
            type.setCategoryIconMenu(categoryMenus.getResourceId(i,0));
            types.add(type);
        }
        return types;
    }
    public Type() {
    }

    protected Type(Parcel in) {
        this.categoryName = in.readString();
        this.categoryFilter = in.readString();
        this.categoryIconMarker = in.readInt();
        this.categoryIconMenu = in.readInt();
    }

    public static final Parcelable.Creator<Type> CREATOR = new Parcelable.Creator<Type>() {
        public Type createFromParcel(Parcel source) {
            return new Type(source);
        }

        public Type[] newArray(int size) {
            return new Type[size];
        }
    };
}
