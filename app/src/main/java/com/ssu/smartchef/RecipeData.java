package com.ssu.smartchef;

import java.util.ArrayList;

public class RecipeData {
    ArrayList<Ingredient> ingredientArrayList;
    String Explanation;

    public String getExplanation() {
        return Explanation;
    }

    public void setExplanation(String explanation) {
        Explanation = explanation;
    }

    public ArrayList<Ingredient> getIngredientArrayList() {
        return ingredientArrayList;
    }

    public void addIngredientArrayList(String ingredient, int weight) {
        this.ingredientArrayList.add(new Ingredient(ingredient,weight));
    }
}

class Ingredient{
    String ingredient;
    int weight;
    public Ingredient(String ingredient, int weight){
        this.ingredient = ingredient;
        this.weight = weight;
    }
    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

}

