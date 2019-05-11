package com.ssu.smartchef;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class googleSignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_up);

        TextView emailText = findViewById(R.id.emailText);
        final TextView nickNameText = findViewById(R.id.nickNameText);
        Button okButton = findViewById(R.id.emailAuthButton);

        Intent intent =  getIntent();
        String email = intent.getStringExtra("email");
        emailText.setText(email);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nickNameText.getText().toString().length() < 3) {
                    Toast.makeText(getApplicationContext(), "닉네임은 최소 4문자 이상이어야 합니다.", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent mainActivityIntent = new Intent(googleSignUpActivity.this, MainActivity.class);
                    startActivity(mainActivityIntent);
                    LoginActivity loginActivity = (LoginActivity)LoginActivity.loginActivity;
                    loginActivity.finish();
                    finish();
                }
            }
        });
    }


}
