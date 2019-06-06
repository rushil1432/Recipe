package com.example.recipe.model;

import java.util.List;

public class RecipeModel {

    private int recipe_id;
    private int category_id;
    private String recipe_name;
    private String preparation_time;
    private String serve_people;
    private String cooking_time;
    private String recipe_image;
    private String recipe_video;
    private String description;
    private List<Ingredient> ingredients = null;


    public RecipeModel() {
    }

    public int getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(int recipe_id) {
        this.recipe_id = recipe_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getRecipe_name() {
        return recipe_name;
    }

    public void setRecipe_name(String recipe_name) {
        this.recipe_name = recipe_name;
    }

    public String getPreparation_time() {
        return preparation_time;
    }

    public void setPreparation_time(String preparation_time) {
        this.preparation_time = preparation_time;
    }

    public String getServe_people() {
        return serve_people;
    }

    public void setServe_people(String serve_people) {
        this.serve_people = serve_people;
    }

    public String getCooking_time() {
        return cooking_time;
    }

    public void setCooking_time(String cooking_time) {
        this.cooking_time = cooking_time;
    }

    public String getRecipe_image() {
        return recipe_image;
    }

    public void setRecipe_image(String recipe_image) {
        this.recipe_image = recipe_image;
    }

    public String getRecipe_video() {
        return recipe_video;
    }

    public void setRecipe_video(String recipe_video) {
        this.recipe_video = recipe_video;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
