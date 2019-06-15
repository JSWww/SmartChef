package com.ssu.smartchef.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssu.smartchef.R;
import com.ssu.smartchef.adapters.mainAdapter;
import com.ssu.smartchef.data.MainViewData;

import java.util.ArrayList;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private mainAdapter adapter;
    private String nickName;
    private TextView nickNameTextView;
    private Button loginButton;
    private FirebaseAuth mAuth;
    SearchView searchView;
    public ArrayList<MainViewData> fulllist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.micButton:
                        Toast.makeText(getApplicationContext(),"mic",Toast.LENGTH_SHORT).show();
                        break ;
                    case R.id.cameraButton:
                        Toast.makeText(getApplicationContext(),"camera",Toast.LENGTH_SHORT).show();
                        break ;
                }
            }
        };

        ImageView micButton = (ImageView) findViewById(R.id.micButton);
        ImageView cameraButton = (ImageView)findViewById(R.id.cameraButton);
        micButton.setOnClickListener(onClickListener);
        cameraButton.setOnClickListener(onClickListener);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Intent intent = getIntent();
        nickName = intent.getStringExtra("nickName");

        View nav_header_view = navigationView.getHeaderView(0);
        nickNameTextView = nav_header_view.findViewById(R.id.nickNameTextView);
        loginButton = nav_header_view.findViewById(R.id.loginButton);

        if (nickName != null) {
            nickNameTextView.setText(nickName);
            loginButton.setText("logout");
        }

        Menu menuNav = navigationView.getMenu();
        MenuItem registItem = menuNav.findItem(R.id.regist);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null)
            registItem.setVisible(false);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {

                    mAuth.signOut();
                    nickNameTextView.setText("로그인하세요");
                    loginButton.setText("login");

                    SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("nickName", null);
                    editor.putString("email", null);
                    editor.commit();
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("isSkipPressed", true);
                    startActivity(intent);
                }

            }
        });
        init();
        getData();
    }

    private void init() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new mainAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    private void getData() {

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference recipelistRef = mRootRef.child("recipelist");

        recipelistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    MainViewData data = new MainViewData();

                    data.setTitle(recipeSnapshot.child("title").getValue(String.class));
                    data.setWriter(recipeSnapshot.child("nickname").getValue(String.class));
                    data.setIamgeURL(recipeSnapshot.child("image").getValue(String.class));
                    adapter.addItem(data);
                    fulllist.add(data);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchView = (SearchView) findViewById(R.id.searchfood);
        searchView.clearFocus();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.searchfood);
        searchView.onActionViewExpanded(); //바로 검색 할 수 있도록
        searchView.clearFocus();
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
            searchView.setQueryHint("요리명 검색");
            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.setFilter(filter(fulllist, newText));
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

            };
            searchView.setOnQueryTextListener(queryTextListener);
        }

        return true;
    }

    private ArrayList<MainViewData> filter(ArrayList<MainViewData> noticeList, String query){
        if(query.length() == 0){
            return fulllist;
        }
        query = query.toLowerCase();
        final ArrayList<MainViewData>  filteredNoticeList = new ArrayList<>();
        if (query != null && !query.equals("")) {
            for (MainViewData model : noticeList) {
                final String title = model.getTitle().toLowerCase();
                if (title.contains(query)) {
                    filteredNoticeList.add(model);
                }
            }
        }
        return filteredNoticeList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent;
        int id = item.getItemId();

        if (id == R.id.regist) {
            intent = new Intent(this, RegistRecipeActivity.class);
            startActivity(intent);
            //finish();
        } else if (id == R.id.cookscale) {
            intent = new Intent(this, ManualScaleActivity.class);
            startActivity(intent);

        } else if (id == R.id.registSpice) {
            intent = new Intent(this,  RegistSpiceActivity.class);
            startActivity(intent);
        } else if (id == R.id.setting) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
