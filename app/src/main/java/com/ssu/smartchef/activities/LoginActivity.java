package com.ssu.smartchef.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.ssu.smartchef.R;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    /* add code */
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int SIGN_UP = 9002;
    private static boolean isSkipPressed = false;

    private TextView skip;
    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* add code */
        skip = findViewById(R.id.skip);

        SharedPreferences sharedPreferences = getSharedPreferences("isSkipPressed", MODE_PRIVATE);
        isSkipPressed = sharedPreferences.getBoolean("isSkipPressed", false);

        Intent intent = getIntent();
        boolean get_boolean = intent.getBooleanExtra("isSkipPressed", false);

        if (get_boolean) {
            isSkipPressed = false;
            skip.setVisibility(View.GONE);
        }


        // [START config_signin]
        // Configure Google Sign In

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

        else if (requestCode == SIGN_UP) {
            signOut();
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        // [START_EXCLUDE silent]
        showProgressDialog();
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

                            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                            intent.putExtra("email", email);
                            startActivityForResult(intent, SIGN_UP);
                        }

                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(android.R.id.content), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
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

    private void updateUI(final FirebaseUser user) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        if (user != null) { // 로그인 된 상태

            SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
            String nickName = sharedPreferences.getString("nickName", null);

            intent.putExtra("nickName", nickName);

            /* database 에서 값 읽는 코드 */
//            DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
//            DatabaseReference userRef = mRootRef.child("users");
//
//            userRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot nickNameSnapshot : dataSnapshot.getChildren()) {
//                        String email = nickNameSnapshot.child("email").getValue(String.class);
//                        if (email.equals(user.getEmail())) {
//                            String nickName = nickNameSnapshot.getKey();
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            intent.putExtra("nickName", "test");
//                            Log.d(TAG, nickName);
//
//                            break;
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });

            startActivity(intent);
            finish();
        }
        else { // 로그인 안 된 상태
            // 이전에 skip 을 누른적이 있다면 메인으로 바로 가게 해야 함
            if (isSkipPressed == true) {
                intent.putExtra("nickName", (String)null);
                startActivity(intent);
                finish();
            }
        }
    }


    @Override
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
            intent.putExtra("nickName", (String)null);
            startActivity(intent);

        }
    }
}
