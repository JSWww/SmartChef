package com.ssu.smartchef.data;

public class IngredientData {
    public IngredientData() {}
    public IngredientData(String Name, String Weight, boolean isEditable)
    {
        this.ingredientName = Name;
        this.ingredientWeight = Weight;
        this.isEditable = isEditable;
    }
    private String ingredientName;
    private String ingredientWeight;
    private boolean isEditable = true;

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

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }
}

