package com.ssu.smartchef.data;

import com.ssu.smartchef.adapters.RegistIngredientAdapter;
import com.ssu.smartchef.data.IngredientData;

import java.util.ArrayList;

public class RecipeStepData {
    ArrayList<IngredientData> ingredientArrayList;

    String Title;
    String Explain;
    String image;

    public void setExplain(String explain) {
        Explain = explain;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public RecipeStepData() {
    }

    public String getExplain() {
        return Explain;
    }

    public void setExplanation(String explain) {
        Explain = explain;
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