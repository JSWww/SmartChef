package com.ssu.smartchef;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class SignUpDialog extends Dialog {

    private Button emailButton;
    private Button googleButton;

    public SignUpDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.signup_dialog);

        emailButton = findViewById(R.id.email);
        googleButton = findViewById(R.id.google);
    }
}
