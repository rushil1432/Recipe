package com.example.recipe.model;

public class Ingredient {

    private String ingredient_name;
    private String quantity;

    public Ingredient(String ingredient_name, String quantity) {
        this.ingredient_name = ingredient_name;
        this.quantity = quantity;
    }

    public Ingredient() {
    }

    public String getIngredient_name() {
        return ingredient_name;
    }

    public void setIngredient_name(String ingredient_name) {
        this.ingredient_name = ingredient_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
