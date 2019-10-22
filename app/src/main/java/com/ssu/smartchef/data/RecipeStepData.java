package com.ssu.smartchef.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class RecipeStepData implements Serializable {
    ArrayList<IngredientData> ingredientArrayList = new ArrayList<>();

    private String stepTitle;
    private String stepExplain;
    private String stepImageURL;

    public RecipeStepData() {
        ingredientArrayList = new ArrayList<>();
    }
    public RecipeStepData(Parcel in){
        this.stepTitle = in.readString();
        this.stepExplain = in.readString();
        this.stepImageURL = in.readString();
    }

    public String getStepTitle() {
        return stepTitle;
    }

    public void setStepTitle(String stepTitle) {
        this.stepTitle = stepTitle;
    }

    public String getStepExplain() {
        return stepExplain;
    }

    public void setStepExplain(String stepExplain) {
        this.stepExplain = stepExplain;
    }

    public void setIngredientArrayList(ArrayList<IngredientData> ingredientArrayList) {
        this.ingredientArrayList = ingredientArrayList;
    }

    public String getStepImageURL() {
        return stepImageURL;
    }

    public void setStepImageURL(String stepImageURL) {
        this.stepImageURL = stepImageURL;
    }

    public ArrayList<IngredientData> getIngredientArrayList() {
        return ingredientArrayList;
    }

    public void addIngredientArrayList(IngredientData data) {
        this.ingredientArrayList.add(data);
    }

}