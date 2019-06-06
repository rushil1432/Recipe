package com.example.recipe.model;

public class Category {

    private int category_id;
    private String category_image;
    private String category_name;

    public Category() {
    }

    public Category(int category_id, String category_image, String category_name) {
        this.category_id = category_id;
        this.category_image = category_image;
        this.category_name = category_name;
    }

    public int getCategory_id() {
        return category_id;
    }

    public String getCategory_image() {
        return category_image;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}