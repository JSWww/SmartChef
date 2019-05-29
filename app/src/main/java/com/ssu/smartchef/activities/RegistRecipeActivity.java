package com.ssu.smartchef.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ssu.smartchef.R;
import com.ssu.smartchef.data.RecipeData;
import com.ssu.smartchef.data.RecipeStepData;
import com.ssu.smartchef.adapters.RegistAdapter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RegistRecipeActivity extends BaseActivity {
    Spinner spinner1,spinner2,spinner3,spinner4;
    ArrayList<String> list1,list2,list3,list4;
    RegistAdapter adapter;
    RecyclerView recyclerView;
    TextView save;
    EditText title,explain,numPerson,time;
    ImageView image;
    String image_url;
    private Uri filePath;
    RecipeData data;
    String filename;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        final String nickName = sharedPreferences.getString("nickName", "anonymous");
        setContentView(R.layout.activity_regist_recipe);
        Toolbar regist_toolbar;
        data = new RecipeData(getApplicationContext());
        regist_toolbar = findViewById(R.id.regist_toolbar);
        setSupportActionBar(regist_toolbar);
        save = findViewById(R.id.regist_save);
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
        title = findViewById(R.id.regist_title);
        explain = findViewById(R.id.regist_explain);
        numPerson = findViewById(R.id.regist_person);
        time = findViewById(R.id.regist_time);
        image = findViewById(R.id.regist_image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"이미지를 선택하세요"),0);
            }
        });
        ImageView add_step = findViewById(R.id.add_step_btn);
        add_step.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
                layoutParams.height = layoutParams.height*2;
                adapter.addItem(new RecipeStepData());
                recyclerView.setLayoutParams(layoutParams);
                adapter.notifyDataSetChanged();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.setTitle(title.getText().toString());
                data.setExplain(explain.getText().toString());
                data.setNumPerson(Integer.parseInt(numPerson.getText().toString()));
                data.setTime(Integer.parseInt(time.getText().toString()));
                data.setCategory1(spinner1.getSelectedItemPosition());
                data.setCategory2(spinner2.getSelectedItemPosition());
                data.setCategory3(spinner3.getSelectedItemPosition());
                data.setCategory4(spinner4.getSelectedItemPosition());
                data.setNickName(nickName);
                data.setStepList(adapter.listData);
                uploadFile();
            }
        });
    }
    private void init() {
        recyclerView = findViewById(R.id.regist_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RegistAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                image.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    private void uploadFile() {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();
            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //Unique한 파일명을 만들자.
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            filename = formatter.format(now) + ".png";
            //storage 주소와 폴더 파일명을 지정해 준다.
            storageRef = storage.getReferenceFromUrl("gs://smartchef-dc7ae.appspot.com").child(filename);
            //올라가거라...

            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자
                            Toast.makeText(getApplicationContext(), "업로드 성공!", Toast.LENGTH_SHORT).show();
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    data.setImage(uri.toString());
                                    data.SaveDB();
                                }
                            });
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //진행중
        }
        else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
