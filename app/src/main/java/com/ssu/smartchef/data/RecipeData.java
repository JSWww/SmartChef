package com.ssu.smartchef.data;

import android.content.Context;
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

    private String title;
    private String image;
    private String explain;
    private int category1;
    private int category2;
    private int category3;
    private int category4;
    private int numPerson;
    private int time;
    private String nickName;
    static long id;
    Context mcontext;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image) {
        this.image = image;
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

    ArrayList<RecipeStepData> stepList = new ArrayList<>();

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

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}