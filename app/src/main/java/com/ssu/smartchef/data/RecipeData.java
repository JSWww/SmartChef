package com.ssu.smartchef.data;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RecipeData {

    private String title="test";
    private String image="test";
    private String explain="test";
    private int category1=0;
    private int category2=0;
    private int category3=0;
    private int category4=0;
    private int numPerson=100;
    private int time=10;
    private String nickName="test";
    static long id;
    Context mcontext;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public void setCategory1(int category1) {
        this.category1 = category1;
    }

    public void setCategory2(int category2) {
        this.category2 = category2;
    }

    public void setCategory3(int category3) {
        this.category3 = category3;
    }

    public void setCategory4(int category4) {
        this.category4 = category4;
    }

    public void setNumPerson(int numPerson) {
        this.numPerson = numPerson;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setStepList(ArrayList<RecipeStepData> stepList) {
        this.stepList = stepList;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public ArrayList<RecipeStepData> stepList = new ArrayList<>();

    public RecipeData() {}
    public RecipeData(Context mcontext) {this.mcontext = mcontext;}
    public void SaveDB(){
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference recipeRef = mRootRef.child("recipelist");
        recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id = dataSnapshot.getChildrenCount();
                recipeRef.child(id+"").child("title").setValue(title);
                recipeRef.child(id+"").child("image").setValue(image);
                recipeRef.child(id+"").child("explain").setValue(explain);
                recipeRef.child(id+"").child("category1").setValue(category1);
                recipeRef.child(id+"").child("category2").setValue(category2);
                recipeRef.child(id+"").child("category3").setValue(category3);
                recipeRef.child(id+"").child("category4").setValue(category4);
                recipeRef.child(id+"").child("numPerson").setValue(numPerson);
                recipeRef.child(id+"").child("time").setValue(time);
                recipeRef.child(id+"").child("nickname").setValue(nickName);
                for(int i = 0 ; i < stepList.size();i++){
                    recipeRef.child(id+"").child("stepList").child(i+"").child("stepExplain").setValue(stepList.get(i).getExplain());
                    recipeRef.child(id+"").child("stepList").child(i+"").child("stepTitle").setValue(stepList.get(i).getTitle());
                    recipeRef.child(id+"").child("stepList").child(i+"").child("stepImage").setValue(stepList.get(i).getImage());
                    for(int j = 0 ; j < stepList.get(i).ingredientArrayList.size() ; j++){
                        recipeRef.child(id+"").child("stepList").child(i+"").child("ingredientList").child(j+"").child("weight").setValue(stepList.get(i).ingredientArrayList.get(j).getIngredientWeight());
                        recipeRef.child(id+"").child("stepList").child(i+"").child("ingredientList").child(j+"").child("ingredient").setValue(stepList.get(i).ingredientArrayList.get(j).getIngredientName());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}