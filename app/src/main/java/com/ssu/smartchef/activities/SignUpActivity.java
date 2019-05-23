package com.ssu.smartchef.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssu.smartchef.R;
import com.ssu.smartchef.User;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mRootRef;
    private DatabaseReference userRef;

    private TextView nickNameText;
    private TextView emailText;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailText = findViewById(R.id.emailText);
        nickNameText = findViewById(R.id.nickNameText);

        Intent intent =  getIntent();
        email = intent.getStringExtra("email");
        emailText.setText(email);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        userRef = mRootRef.child("users");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        String nickName = nickNameText.getText().toString();

        if (i ==  R.id.emailAuthButton) {
            if (nickName.length() < 3) {
                Toast.makeText(getApplicationContext(), "닉네임은 최소 4문자 이상이어야 합니다.", Toast.LENGTH_LONG).show();
            }
            else {
                SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("nickName", nickName);
                editor.putString("email", email);
                editor.commit();

                sharedPreferences = getSharedPreferences("isSkipPressed", MODE_PRIVATE);
                editor = sharedPreferences.edit();

                editor.putBoolean("isSkipPressed", true);
                editor.commit();

                userRef.child(nickName).child("email").setValue(email);
                Intent mainActivityIntent = new Intent(SignUpActivity.this, MainActivity.class);
                mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainActivityIntent.putExtra("nickName", nickName);
                startActivity(mainActivityIntent);
            }
        }
    }
}
