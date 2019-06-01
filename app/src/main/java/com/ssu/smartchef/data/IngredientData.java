package com.ssu.smartchef.data;

public class IngredientData {

    private String ingredientName;
    private double ingredientWeight;
    private boolean isEditable = true;

    public IngredientData() {}

    public String getIngredientName() {
        return ingredientName;
    }

    public double getIngredientWeight() {
        return ingredientWeight;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public void setIngredientWeight(double ingredientWeight) {
        this.ingredientWeight = ingredientWeight;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }
}

