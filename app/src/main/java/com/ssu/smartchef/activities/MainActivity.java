package com.ssu.smartchef.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssu.smartchef.data.MainViewData;
import com.ssu.smartchef.R;
import com.ssu.smartchef.adapters.mainAdapter;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private mainAdapter adapter;
    private String nickName;
    private TextView nickNameTextView;
    private Button loginButton;
    private FirebaseAuth mAuth;

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
        Button.OnClickListener onClickListener= new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.micButton :
                        Toast.makeText(getApplicationContext(),"mic",Toast.LENGTH_SHORT).show();
                        break ;
                    case R.id.regist_save:
                        Toast.makeText(getApplicationContext(),"camera",Toast.LENGTH_SHORT).show();
                        break ;
                }
            }
        };

        ImageView micButton = (ImageView) findViewById(R.id.micButton);
        ImageView cameraButton = (ImageView)findViewById(R.id.regist_save);
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
                }
                else {

//                    SharedPreferences sharedPreferences = getSharedPreferences("isSkipPressed", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//
//                    editor.putBoolean("isSkipPressed", false);
//                    editor.commit();

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

        adapter = new mainAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getData() {
        // 임의의 데이터입니다.
        MainViewData data;
        List<String> listTitle = Arrays.asList("불고기", "김밥", "탕수육");
        List<String> listwriter = Arrays.asList("아이유", "다현", "김태희");
        List<String> listtag = Arrays.asList("배고파", "맛있음", "미미");
        List<Integer> listResId = Arrays.asList(
                R.drawable.basic_icon,
                R.drawable.basic_icon,
                R.drawable.basic_icon
        );
        for (int i = 0; i < listTitle.size(); i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            data = new MainViewData();
            data.setTitle(listTitle.get(i));
            data.setWriter(listwriter.get(i));
            data.setTag(listtag.get(i));
            data.setResId(listResId.get(i));

            // 각 값이 들어간 data를 adapter에 추가합니다.
            adapter.addItem(data);
        }
        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

        } else if (id == R.id.cookhelp) {

        } else if (id == R.id.setting) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
