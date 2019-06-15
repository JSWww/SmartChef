package com.ssu.smartchef.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ssu.smartchef.R;
import com.ssu.smartchef.adapters.RegistAdapter;
import com.ssu.smartchef.adapters.SpiceAdapter;
import com.ssu.smartchef.data.RecipeStepData;
import com.ssu.smartchef.data.SpiceData;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class RegistSpiceActivity extends AppCompatActivity {
    SpiceAdapter adapter;
    RecyclerView recyclerView;
    TextView save;
    ImageView add;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_spice);
        Toolbar spice_toolbar;
        spice_toolbar = findViewById(R.id.spiceToolbar);
        setSupportActionBar(spice_toolbar);
        save = findViewById(R.id.spice_save);
        add = findViewById(R.id.add_spice_btn);
        init();
        onSearchData();
        adapter.notifyDataSetChanged();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.addItem(new SpiceData());
                adapter.notifyItemChanged(adapter.getItemCount());
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveData();
                Intent intent = new Intent(RegistSpiceActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void init() {
        recyclerView = findViewById(R.id.spiceRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SpiceAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
    }
    protected void onSaveData(){
        gson = new GsonBuilder().create();
        Type listType = new TypeToken<ArrayList<SpiceData>>() {}.getType();
        String json = gson.toJson(adapter.listData,listType);

        SharedPreferences sp = getSharedPreferences("spice",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("spicedata",json);
        editor.commit();
    }
    protected void onSearchData() {
        gson = new GsonBuilder().create();
        SharedPreferences sp = getSharedPreferences("spice", MODE_PRIVATE);
        String strSpice = sp.getString("spicedata", null);
        if (strSpice != null) {
            Type listType = new TypeToken<ArrayList<SpiceData>>() {}.getType();
            ArrayList<SpiceData> saveData = gson.fromJson(strSpice, listType);
            adapter.listData = saveData;
        }
    }
}
