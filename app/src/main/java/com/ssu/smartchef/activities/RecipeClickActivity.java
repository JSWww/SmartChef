package com.ssu.smartchef.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssu.smartchef.adapters.IngredientAdapter;
import com.ssu.smartchef.data.IngredientData;
import com.ssu.smartchef.R;
import com.ssu.smartchef.adapters.RecipeOrderAdapter;
import com.ssu.smartchef.data.MainViewData;
import com.ssu.smartchef.data.RecipeStepData;

public class RecipeClickActivity extends AppCompatActivity {

    private IngredientAdapter ingredientAdapter;
    private RecipeOrderAdapter recipeOrderAdapter;
    private DatabaseReference mRootRef;
    private DatabaseReference recipeRef;

    private RecyclerView ingredientRecycler;
    private RecyclerView recipeOrderRecycler;

    private TextView recipeName;
    private TextView nickName;
    private TextView recipeExplain;
    private TextView numPerson;
    private ImageView recipeFood;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_click);

        recipeName = findViewById(R.id.recipeName);
        nickName = findViewById(R.id.nickName);
        recipeExplain = findViewById(R.id.recipeExplain);
        numPerson = findViewById(R.id.numPerson);
        recipeFood = findViewById(R.id.recipeFood);

        ingredientRecycler = findViewById(R.id.ingredientRecycler);
        ingredientRecycler.setNestedScrollingEnabled(false);
        ingredientRecycler.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        ingredientRecycler.setLayoutManager(linearLayoutManager1);
        ingredientAdapter = new IngredientAdapter();
        ingredientRecycler.setAdapter(ingredientAdapter);

        recipeOrderRecycler = findViewById(R.id.recipeOrderRecycler);
        recipeOrderRecycler.setNestedScrollingEnabled(false);
        recipeOrderRecycler.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        recipeOrderRecycler.setLayoutManager(linearLayoutManager2);
        recipeOrderAdapter = new RecipeOrderAdapter();
        recipeOrderRecycler.setAdapter(recipeOrderAdapter);

        Intent intent = getIntent();
        String recipeID = intent.getStringExtra("recipeID");

        mRootRef = FirebaseDatabase.getInstance().getReference();
        recipeRef = mRootRef.child("recipelist").child(recipeID);

        recipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeName.setText(dataSnapshot.child("title").getValue(String.class));
                nickName.setText(dataSnapshot.child("nickname").getValue(String.class));
                recipeExplain.setText(dataSnapshot.child("explain").getValue(String.class));
                numPerson.setText(dataSnapshot.child("numPerson").getValue(Integer.class) + "인분");

                Glide.with(getApplicationContext())
                        .load(dataSnapshot.child("image").getValue(String.class))
                        .into(recipeFood);

                for (DataSnapshot stepList : dataSnapshot.child("stepList").getChildren()) {
                    RecipeStepData recipeStepData = new RecipeStepData();
                    recipeStepData.setStepTitle(stepList.child("stepTitle").getValue(String.class));
                    recipeStepData.setStepExplain(stepList.child("stepExplain").getValue(String.class));
                    recipeStepData.setStepImageURL(stepList.child("stepImage").getValue(String.class));

                    for(DataSnapshot step : stepList.child("ingredientList").getChildren()) {
                        IngredientData data = new IngredientData();
                        data.setEditable(false);
                        data.setIngredientName(step.child("ingredient").getValue(String.class));
                        data.setIngredientWeight(step.child("weight").getValue(Integer.class) + "g");
                        ingredientAdapter.addItem(data);
                        recipeStepData.addIngredientArrayList(data);
                    }

                    recipeOrderAdapter.addItem(recipeStepData);
                }

                ViewGroup.LayoutParams layoutParams = ingredientRecycler.getLayoutParams();
                layoutParams.height = layoutParams.height * ingredientAdapter.getItemCount();
                ingredientRecycler.setLayoutParams(layoutParams);
                ingredientAdapter.notifyDataSetChanged();
                recipeOrderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
