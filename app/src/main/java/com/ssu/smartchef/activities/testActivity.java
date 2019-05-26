package com.ssu.smartchef.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ssu.smartchef.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class testActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private Button button;
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference testRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        imageView = findViewById(R.id.imageView4);
        button = findViewById(R.id.button5);

        button.setOnClickListener(this);

        storage = FirebaseStorage.getInstance();
        storageReference =  storage.getReference();
        testRef = storageReference.child("num1.PNG");


    }

    @Override
    public void onClick(View v) {


        Toast.makeText(getApplicationContext(), testRef.getDownloadUrl().toString(), Toast.LENGTH_SHORT).show();

        testRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Toast.makeText(getApplicationContext(), uri.toString(), Toast.LENGTH_SHORT).show();

                Glide.with(getApplicationContext())
                        .load(uri.toString())
                        .into(imageView);


            }
        });


    }


}
