package com.ssu.smartchef.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ssu.smartchef.adapters.IngredientAdapter;
import com.ssu.smartchef.data.IngredientData;
import com.ssu.smartchef.R;
import com.ssu.smartchef.adapters.RecipeOrderAdapter;

public class RecipeClickActivity extends AppCompatActivity {

    private IngredientAdapter ingredientAdapter;
    private RecipeOrderAdapter recipeOrderAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_click);

        RecyclerView recyclerView = findViewById(R.id.ingredientRecycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setLayoutManager(linearLayoutManager);

        ingredientAdapter = new IngredientAdapter();
        recyclerView.setAdapter(ingredientAdapter);

        IngredientData data1 = new IngredientData();
        data1.setIngredientName("오징어 젓");
        data1.setIngredientWeight("75g");
        ingredientAdapter.addItem(data1);

        IngredientData data2 = new IngredientData();
        data2.setIngredientName("깻잎");
        data2.setIngredientWeight("10g");
        ingredientAdapter.addItem(data2);

        IngredientData data3 = new IngredientData();
        data3.setIngredientName("김");
        data3.setIngredientWeight("10g");
        ingredientAdapter.addItem(data3);

        IngredientData data4 = new IngredientData();
        data4.setIngredientName("쌀밥");
        data4.setIngredientWeight("200g");
        ingredientAdapter.addItem(data4);

        IngredientData data5 = new IngredientData();
        data5.setIngredientName("간장");
        data5.setIngredientWeight("5g");
        ingredientAdapter.addItem(data5);

        ingredientAdapter.notifyDataSetChanged();

        RecyclerView recyclerView1 = findViewById(R.id.recipeOrderRecycler);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        recyclerView1.setLayoutManager(linearLayoutManager1);

        recipeOrderAdapter = new RecipeOrderAdapter();
        recyclerView1.setAdapter(recipeOrderAdapter);

        IngredientData data6 = new IngredientData();
        recipeOrderAdapter.addItem(data6);

        ingredientAdapter.notifyDataSetChanged();

    }
}
