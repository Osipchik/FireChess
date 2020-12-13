package com.example.lab3.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab3.Enums.Fields;
import com.example.lab3.Models.ModelAuthenticate;
import com.example.lab3.Models.ModelDatabase;
import com.example.lab3.Models.UserModel;
import com.example.lab3.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;

    private ModelAuthenticate authenticate;
    private ModelDatabase database;

    private GoogleSignInClient mGoogleSignInClient;

    private TextView status, detail;
//    private com.google.android.gms.common.SignInButton signInButton;

    private Button signInButton;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        authenticate = new ModelAuthenticate();
        database = new ModelDatabase();

        signInButton = findViewById(R.id.signInButton);
        progressBar = findViewById(R.id.progressBar);


        status = findViewById(R.id.status);
        detail = findViewById(R.id.detail);

        signInButton.setOnClickListener(this::signIn);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (authenticate.isAuthenticated()){
            startMainActivity();
        }
    }

    private void startMainActivity(){
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                updateUI();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        authenticate.signIn(credential, task -> {
            if (task.isSuccessful()) {
                database.getReference(Fields.Users + authenticate.getUserId())
                        .child(Fields.profileImage)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()){
                                    UserModel user = authenticate.getUserModel();
                                    database.setValue(Fields.Users + authenticate.getUserId(), user);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                startMainActivity();
            }
            else {
                Snackbar.make(findViewById(R.id.main_layout), getString(R.string.auth_failed), Snackbar.LENGTH_SHORT).show();
                updateUI();
            }

            progressBar.setVisibility(ProgressBar.INVISIBLE);
        });
    }

    private void signIn(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void updateUI() {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        status.setText(R.string.signed_out);
        detail.setText(null);

        signInButton.setVisibility(View.VISIBLE);
    }
}