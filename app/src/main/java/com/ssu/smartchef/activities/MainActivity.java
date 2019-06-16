package com.ssu.smartchef.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    //음성인식
    Intent i;
    SpeechRecognizer mRecognizer;
    //
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
        /*음성인식*/
        RecognitionListener listener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                System.out.println("onReadyForSpeech.........................");
            }
            @Override
            public void onBeginningOfSpeech() {
               // Toast.makeText(getApplicationContext(), "지금부터 말을 해주세요!", Toast.LENGTH_SHORT).show();
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
                String message;

                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "오디오 에러";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "클라이언트 에러";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "퍼미션 없음";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "네트워크 에러";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "네트웍 타임아웃";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "찾을 수 없음";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "RECOGNIZER가 바쁨";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "서버가 이상함";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "말하는 시간초과";
                        break;
                    default:
                        message = "알 수 없는 오류임";
                        break;
                }

                Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message,Toast.LENGTH_SHORT).show();
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
                searchView.setQuery(rs[0],false);
            }
        };
        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplicationContext().getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        mRecognizer.setRecognitionListener(listener);
        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("-------------------------------------- 음성인식 시작!");
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 100);
                    //권한을 허용하지 않는 경우
                } else {
                    //권한을 허용한 경우
                    try {
                        mRecognizer.startListening(i);
                    } catch(SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        ImageView micButton = (ImageView) findViewById(R.id.micButton);
        micButton.setOnClickListener(onClickListener);
        /*음성인식*/

        Menu menuNav = navigationView.getMenu();
        final MenuItem registItem = menuNav.findItem(R.id.regist);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null)
            registItem.setVisible(false);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {

                    mAuth.signOut();
                    nickNameTextView.setText("로그인 하세요");
                    loginButton.setText("login");
                    registItem.setVisible(false);

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
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
