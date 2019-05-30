package com.ssu.smartchef.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ssu.smartchef.data.MainViewData;
import com.ssu.smartchef.R;
import com.ssu.smartchef.adapters.mainAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private mainAdapter adapter;
    private String nickName;
    private TextView nickNameTextView;
    private Button loginButton;
    private FirebaseAuth mAuth;
    Intent i;
    SpeechRecognizer mRecognizer;


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
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                            //권한을 허용하지 않는 경우
                        } else {
                            //권한을 허용한 경우
                            try {
                                mRecognizer.startListening(i);
                            } catch(SecurityException e) {
                                e.printStackTrace();
                            }
                        }
                        break ;
                    case R.id.cameraButton:
                        Toast.makeText(getApplicationContext(),"camera",Toast.LENGTH_SHORT).show();
                        break ;
                }
            }
        };
        //음성인식 코드
        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplicationContext().getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        mRecognizer.setRecognitionListener(listener);


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
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("isSkipPressed", true);
                    startActivity(intent);
                }

            }
        });
        init();
        getData();

    }
    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            System.out.println("onReadyForSpeech.........................");
        }
        @Override
        public void onBeginningOfSpeech() {
            Toast.makeText(getApplicationContext(), "지금부터 말을 해주세요!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            System.out.println("onRmsChanged.........................");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            System.out.println("onBufferReceived.........................");
        }

        @Override
        public void onEndOfSpeech() {
            System.out.println("onEndOfSpeech.........................");
        }

        @Override
        public void onError(int error) {
            Toast.makeText(getApplicationContext(), "천천히 다시 말해주세요.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            System.out.println("onPartialResults.........................");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            System.out.println("onEvent.........................");
        }

        @Override
        public void onResults(Bundle results) {
            String key= "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            Toast.makeText(getApplicationContext(), rs[0], Toast.LENGTH_SHORT).show();
            mRecognizer.startListening(i); //음성인식이 계속 되는 구문이니 필요에 맞게 쓰시길 바람
        }
    };

    private void init() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new mainAdapter();
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
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
