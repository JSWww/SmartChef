package com.ssu.smartchef;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class RegistRecipe extends AppCompatActivity {
    Spinner spinner1,spinner2,spinner3,spinner4;
    ArrayList<String> list1,list2,list3,list4;
    RegistAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_recipe);
        spinner1 = (Spinner)findViewById(R.id.spinner1);
        spinner2 = (Spinner)findViewById(R.id.spinner2);
        spinner3 = (Spinner)findViewById(R.id.spinner3);
        spinner4 = (Spinner)findViewById(R.id.spinner4);
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();
        list4 = new ArrayList<>();
        list1.add("::종류별");
        list2.add("::상황별");
        list3.add("::재료별");
        list4.add("::방법별");
        ArrayAdapter spinner1Adapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,list1);
        ArrayAdapter spinner2Adapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,list2);
        ArrayAdapter spinner3Adapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,list3);
        ArrayAdapter spinner4Adapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,list4);
        spinner1.setAdapter(spinner1Adapter);
        spinner2.setAdapter(spinner2Adapter);
        spinner3.setAdapter(spinner3Adapter);
        spinner4.setAdapter(spinner4Adapter);
        init();
        RecipeData data = new RecipeData();
        adapter.addItem(data);
        adapter.notifyDataSetChanged();
    }
    private void init() {
        RecyclerView recyclerView = findViewById(R.id.regist_recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RegistAdapter();
        recyclerView.setAdapter(adapter);
    }
}
