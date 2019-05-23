package com.ssu.smartchef.data;

import java.util.ArrayList;

public class RecipeData {

    private String title;
    private String image;
    private String explain;
    private int category1;
    private int category2;
    private int category3;
    private int category4;
    private int numPerson;
    private int time;

    ArrayList<RecipeStepData> stepList = new ArrayList<>();

    private String nickName;
    private int id;

    RecipeData() {}
}
