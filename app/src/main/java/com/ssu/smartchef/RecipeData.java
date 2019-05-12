package com.ssu.smartchef;

import java.util.ArrayList;

public class RecipeData {
    ArrayList<IngredientData> ingredientArrayList;
    String Explanation;

    String Title;


    public String getExplanation() {
        return Explanation;
    }

    public void setExplanation(String explanation) {
        Explanation = explanation;
    }

    public ArrayList<IngredientData> getIngredientArrayList() {
        return ingredientArrayList;
    }

    public void addIngredientArrayList(String ingredient, String weight) {
        this.ingredientArrayList.add(new IngredientData(ingredient,weight));
    }
    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}