package com.ssu.smartchef.data;

import com.ssu.smartchef.adapters.RegistIngredientAdapter;
import com.ssu.smartchef.data.IngredientData;

import java.util.ArrayList;

public class RecipeData {
    ArrayList<IngredientData> ingredientArrayList;
    String Explanation;
    String Title;
    public RegistIngredientAdapter adapter;

    public RegistIngredientAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(RegistIngredientAdapter adapter) {
        this.adapter = adapter;
    }



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