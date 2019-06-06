package com.example.recipe.Utils;

import com.example.recipe.model.Ingredient;

import java.util.List;

public class RecipeIngredientBuilder {

    public static String[] IngredientQuantityBuilder(List<Ingredient> ingredientModel){
        int numberOfIngredients = ingredientModel.size();
        String completedIngredientList;
        String[] IngredientQuantityArray = new String[numberOfIngredients];
        Ingredient loopedThroughIngredient;

        for (int i = 0; i < numberOfIngredients; i++){
            loopedThroughIngredient = ingredientModel.get(i);
                 completedIngredientList=loopedThroughIngredient.getQuantity();
                completedIngredientList += " " + loopedThroughIngredient.getIngredient_name();

            IngredientQuantityArray[i] = completedIngredientList;
        }

        return IngredientQuantityArray;
    }
}
