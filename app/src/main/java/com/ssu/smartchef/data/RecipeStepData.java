package com.ssu.smartchef.data;

import com.ssu.smartchef.adapters.RegistIngredientAdapter;
import com.ssu.smartchef.data.IngredientData;

import java.util.ArrayList;

public class RecipeStepData {
    ArrayList<IngredientData> ingredientArrayList;

    String Title;
    String Explanation;
    String image;




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
        this.ingredientArrayList.add(new IngredientData(ingredient,weight,true));
    }
    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}