package com.ssu.smartchef.data;

public class IngredientData {
    public IngredientData() {}
    public IngredientData(String Name, int Weight, boolean isEditable)
    {
        this.ingredientName = Name;
        this.ingredientWeight = Weight;
        this.isEditable = isEditable;
    }

    private String ingredientName;
    private int ingredientWeight;
    private boolean isEditable = true;

    public String getIngredientName() {
        return ingredientName;
    }

    public int getIngredientWeight() {
        return ingredientWeight;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public void setIngredientWeight(int ingredientWeight) {
        this.ingredientWeight = ingredientWeight;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }
}

