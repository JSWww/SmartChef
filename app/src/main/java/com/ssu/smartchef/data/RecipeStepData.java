package com.ssu.smartchef.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class RecipeStepData implements Parcelable {
    ArrayList<IngredientData> ingredientArrayList;

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

    public static final Creator<RecipeStepData> CREATOR = new Creator<RecipeStepData>() {
        @Override
        public RecipeStepData createFromParcel(Parcel in) {
            return new RecipeStepData(in);
        }

        @Override
        public RecipeStepData[] newArray(int size) {
            return new RecipeStepData[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.stepTitle);
        dest.writeString(this.stepExplain);
        dest.writeString(this.stepImageURL);
    }
}