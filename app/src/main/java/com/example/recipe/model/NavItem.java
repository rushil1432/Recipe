package com.example.recipe.model;

public class NavItem {

    private String mTitle;
    private int mIcon;

    public NavItem(String title, int icon) {
        mTitle = title;
        mIcon = icon;
    }

    public String getmTitle() {
        return mTitle;
    }

    public int getmIcon() {
        return mIcon;
    }
}
