package com.favesolution.jktotw.Models;

import android.graphics.drawable.Drawable;

/**
 * Created by Daniel on 11/9/2015 for JktOtw project.
 */
public class Category {
    private String categoryName;
    private String categoryFilter;
    private Drawable categoryIconMarker;
    private Drawable categoryIconMenu;

    public String getCategoryFilter() {
        return categoryFilter;
    }

    public void setCategoryFilter(String categoryFilter) {
        this.categoryFilter = categoryFilter;
    }

    public Drawable getCategoryIconMarker() {
        return categoryIconMarker;
    }

    public void setCategoryIconMarker(Drawable categoryIconMarker) {
        this.categoryIconMarker = categoryIconMarker;
    }

    public Drawable getCategoryIconMenu() {
        return categoryIconMenu;
    }

    public void setCategoryIconMenu(Drawable categoryIconMenu) {
        this.categoryIconMenu = categoryIconMenu;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
