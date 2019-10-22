package com.ssu.smartchef.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.ssu.smartchef.R;

/**
 * Demonstrate Firebase Authentication without a password, using a link sent to an
 * email address.
 */
public class EmailLoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PasswordlessSignIn";
    private static final String KEY_PENDING_EMAIL = "key_pending_email";
    private static final int SIGN_UP = 9002;

    private FirebaseAuth mAuth;

    private EditText emailText;
    private Button emailAuthButton;
    private Button emailSignInButton;

    private String mPendingEmail;
    private String mEmailLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.emailField);

        emailAuthButton = findViewById(R.id.emailAuthButton);
        emailSignInButton = findViewById(R.id.emailSignInButton);

        emailAuthButton.setOnClickListener(this);
        emailSignInButton.setOnClickListener(this);

        // Restore the "pending" email address
        if (savedInstanceState != null) {
            mPendingEmail = savedInstanceState.getString(KEY_PENDING_EMAIL, null);
            emailText.setText(mPendingEmail);
        }

        // Check if the Intent that started the Activity contains an email sign-in link.
        checkIntent(getIntent());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_UP) {
            mAuth.signOut();
            emailAuthButton.setEnabled(true);
            emailSignInButton.setEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI(mAuth.getCurrentUser());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_PENDING_EMAIL, mPendingEmail);
    }

    /**
     * Check to see if the Intent has an email link, and if so set up the UI accordingly.
     * This can be called from either onCreate or onNewIntent, depending on how the Activity
     * was launched.
     */
    private void checkIntent(@Nullable Intent intent) {
        if (intentHasEmailLink(intent)) {
            mEmailLink = intent.getData().toString();

            emailAuthButton.setEnabled(false);
            emailSignInButton.setEnabled(true);
            emailSignInButton.setTextColor(Color.parseColor("#607d8b"));
        } else {
            emailAuthButton.setEnabled(true);
            emailSignInButton.setEnabled(false);
        }
    }

    /**
     * Determine if the given Intent contains an email sign-in link.
     */
    private boolean intentHasEmailLink(@Nullable Intent intent) {
        if (intent != null && intent.getData() != null) {
            String intentData = intent.getData().toString();
            if (mAuth.isSignInWithEmailLink(intentData)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Send an email sign-in link to the specified email.
     */
    private void sendSignInLink(final String email) {
        ActionCodeSettings settings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName(
                        getPackageName(),
                        true, /* install if not available? */
                        null   /* minimum app version */)
                .setHandleCodeInApp(true)
                .setUrl("https://smartchef.page.link/smartchef")
                .build();

        hideKeyboard(emailText);
        showProgressDialog();

        mAuth.sendSignInLinkToEmail(email, settings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            Log.d(TAG, "Link sent");
                            showSnackbar("인증 링크를 보냈습니다.");

                            mPendingEmail = email;
                        } else {
                            Exception e = task.getException();
                            Log.w(TAG, "Could not send link", e);
                            showSnackbar("인증 링크를 보내는데 실패했습니다.");

                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                emailText.setError("Invalid email address.");
                            }
                        }
                    }
                });
    }

    /**
     * Sign in using an email address and a link, the link is passed to the Activity
     * from the dynamic link contained in the email.
     */
    private void signInWithEmailLink(final String email, String link) {
        Log.d(TAG, "signInWithLink:" + link);

        hideKeyboard(emailText);
        showProgressDialog();

        mAuth.signInWithEmailLink(email, link)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        mPendingEmail = null;

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmailLink:success");

                            Intent intent = new Intent(EmailLoginActivity.this, SignUpActivity.class);
                            intent.putExtra("email", email);
                            startActivityForResult(intent, SIGN_UP);

                        } else {
                            Log.w(TAG, "signInWithEmailLink:failure", task.getException());
                            updateUI(null);

                            if (task.getException() instanceof FirebaseAuthActionCodeException) {
                                showSnackbar("잘못되거나 만료된 인증 링크입니다.");
                            }
                        }
                    }
                });
    }

    private void onSendLinkClicked() {
        String email = emailText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailText.setError("Email must not be empty.");
            return;
        }

        sendSignInLink(email);
    }

    private void onSignInClicked() {
        String email = emailText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailText.setError("Email must not be empty.");
            return;
        }

        signInWithEmailLink(email, mEmailLink);
    }

    private void onSignOutClicked() {
        mAuth.signOut();
        updateUI(null);
    }

    private void updateUI(@Nullable FirebaseUser user) {
        if (user != null) {

        } else {

        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emailAuthButton:
                onSendLinkClicked();
                break;

            case R.id.emailSignInButton:
                onSignInClicked();
                break;
        }
    }
}
