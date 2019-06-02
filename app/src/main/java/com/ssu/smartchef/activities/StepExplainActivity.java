package com.ssu.smartchef.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ssu.smartchef.R;
import com.ssu.smartchef.adapters.IngredientAdapter;
import com.ssu.smartchef.adapters.mainAdapter;
import com.ssu.smartchef.data.IngredientData;
import com.ssu.smartchef.data.RecipeStepData;

import java.util.ArrayList;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class StepExplainActivity extends AppCompatActivity {
    TextView stepNumber,stepTitle,stepExplain;
    RecyclerView stepIngredient;
    ImageView stepImage;
    CircularProgressIndicator stepScale;
    ImageButton pre,next;
    public ArrayList<RecipeStepData> stepList = new ArrayList<>();
    IngredientAdapter adapter = new IngredientAdapter();
    int index = 0;
    int ingredientIndex = 0;
    boolean isScale=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_explain);
        Intent intent = getIntent();
        stepList = (ArrayList<RecipeStepData>) intent.getSerializableExtra("list");
        stepNumber  = findViewById(R.id.runStepNumber);
        stepTitle = findViewById(R.id.runStepTitle);
        stepExplain = findViewById(R.id.runStepExplain);
        stepIngredient = findViewById(R.id.stepIngredient);
        stepImage = findViewById(R.id.runStepImage);
        stepScale = findViewById(R.id.runStepScale);
        pre = findViewById(R.id.runStepBackButton);
        next = findViewById(R.id.runStepNextButton);
        init();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isScale == true){
                    ingredientIndex++;
                }
                else if(isScale == false){
                    isScale = true;
                }
                if(ingredientIndex == stepList.get(index).getIngredientArrayList().size()){
                    index++;
                    ingredientIndex = 0;
                    isScale = false;
                }
                dataChange(index,isScale,ingredientIndex);
            }
        });
        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isScale == true){
                    ingredientIndex--;
                    if(ingredientIndex == -1){
                        ingredientIndex = 0 ;
                        isScale = false;
                    }
                }
                else if(isScale == false){
                    index--;
                    ingredientIndex = stepList.get(index).getIngredientArrayList().size() - 1;
                    isScale = true;

                }
                dataChange(index,isScale,ingredientIndex);
            }
        });
        dataChange(0,isScale,0);
    }
    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        stepIngredient.setLayoutManager(linearLayoutManager);
        adapter = new IngredientAdapter();
        stepIngredient.setAdapter(adapter);
    }
    private void dataChange(int index,boolean isScale,int ingredientNumber){
        stepNumber.setText("STEP " + (index+1) +"/"+stepList.size());
        if(isScale == true){
            stepImage.setVisibility(View.INVISIBLE);
            stepScale.setVisibility(View.VISIBLE);
        }
        else{
            stepImage.setVisibility(View.VISIBLE);
            stepScale.setVisibility(View.INVISIBLE);
            Glide.with(getApplicationContext())
                    .load(stepList.get(index).getStepImageURL())
                    .into(stepImage);
        }
        stepTitle.setText(stepList.get(index).getStepTitle());
        stepExplain.setText(stepList.get(index).getStepExplain());
        adapter.listData = stepList.get(index).getIngredientArrayList();
        if(index == 0 && ingredientIndex == 0 && isScale == true){
            pre.setVisibility(View.INVISIBLE);
        }
        else{
            pre.setVisibility(View.VISIBLE);
        }
        if(index == stepList.size() - 1 && ingredientIndex == stepList.get(index).getIngredientArrayList().size() - 1){
            next.setVisibility(View.INVISIBLE);
        }
        else{
            next.setVisibility(View.VISIBLE);
        }
        if(isScale == true){
            adapter.setPos(ingredientNumber);
        }
        else{
            adapter.setPos(-1);
        }
        adapter.notifyDataSetChanged();
    }
}
