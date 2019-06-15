package com.ssu.smartchef.activities;

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

import com.ssu.smartchef.R;
import com.ssu.smartchef.adapters.RegistAdapter;
import com.ssu.smartchef.adapters.SpiceAdapter;
import com.ssu.smartchef.data.RecipeStepData;
import com.ssu.smartchef.data.SpiceData;

public class RegistSpiceActivity extends AppCompatActivity {
    SpiceAdapter adapter;
    RecyclerView recyclerView;
    TextView save;
    ImageView add;

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
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.addItem(new SpiceData());
                adapter.notifyItemChanged(adapter.getItemCount());
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
}
