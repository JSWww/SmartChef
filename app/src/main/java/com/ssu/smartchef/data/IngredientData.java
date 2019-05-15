package com.ssu.smartchef.data;

public class IngredientData {
    public IngredientData() {}
    public IngredientData(String Name,String Weight)
    {
        this.ingredientName = Name;
        this.ingredientWeight = Weight;
    }
    private String ingredientName;
    private String ingredientWeight;

    public String getIngredientName() {
        return ingredientName;
    }

    public String getIngredientWeight() {
        return ingredientWeight;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public void setIngredientWeight(String ingredientWeight) {
        this.ingredientWeight = ingredientWeight;
    }
}

