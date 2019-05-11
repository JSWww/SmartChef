package com.ssu.smartchef;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    /* add code */
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int GOOGLE_SIGN_UP = 9002;
    private static boolean isSkipPressed = false;
    public static Activity loginActivity;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* add code */
        SharedPreferences sharedPreferences = getSharedPreferences("isSkipPressed", MODE_PRIVATE);
        isSkipPressed = sharedPreferences.getBoolean("isSkipPressed", false);

        // [START config_signin]
        // Configure Google Sign In
        loginActivity = LoginActivity.this;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    /* add code */
    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]


    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
//                updateUI(null);
                // [END_EXCLUDE]
            }
        }

        else if (requestCode == GOOGLE_SIGN_UP) {
            signOut();
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        // [START_EXCLUDE silent]
//        showProgressDialog();
        // [END_EXCLUDE]


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String email = user.getEmail();

                            Intent intent = new Intent(LoginActivity.this, googleSignUpActivity.class);
                            intent.putExtra("email", email);
                            startActivityForResult(intent, GOOGLE_SIGN_UP);
                        }

                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

//    private void revokeAccess() {
//        // Firebase sign out
//        mAuth.signOut();
//
//        // Google revoke access
//        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        updateUI(null);
//                    }
//                });
//    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        if (user != null) { // 로그인 된 상태
            startActivity(intent);
            finish();
        }
        else { // 로그인 안 된 상태
            // 이전에 skip 을 누른적이 있다면 메인으로 바로 가게 해야 함
            if (isSkipPressed == true) {
                startActivity(intent);
                finish();
            }
        }
    }


//    @Override
    public void onClick(View v) {
        Intent intent;
        int i = v.getId();

        if (i == R.id.googleLoginButton) {
            signIn();
        }
        else if (i == R.id.skip) {
            SharedPreferences sharedPreferences = getSharedPreferences("isSkipPressed", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean("isSkipPressed", true);
            editor.commit();

            intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        else if (i == R.id.emailLoginButton) {
            intent = new Intent(LoginActivity.this, EmailLoginActivity.class);
            startActivity(intent);

        }
//            SignUpDialog signUpDialog = new SignUpDialog(LoginActivity.this);
//            signUpDialog.setContentView(R.layout.signup_dialog);
//
//            // 디바이스 가로 세로 길이 가져오기
//            DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
//            int width = dm.widthPixels;
//            int height = dm.heightPixels;
//
//            // 다이얼로그 크기 조절하기
//            WindowManager.LayoutParams layoutParams = signUpDialog.getWindow().getAttributes();
//            layoutParams.copyFrom(signUpDialog.getWindow().getAttributes());
//            layoutParams.width = width - width / 3;
//            layoutParams.height = height / 3;
//            signUpDialog.getWindow().setAttributes(layoutParams);
//
//            signUpDialog.show();
//        }

//        else if (i == R.id.signOutButton) {
//            signOut();
//        }
//
//        else if (i == R.id.disconnectButton) {
//            revokeAccess();
//        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
