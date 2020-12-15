package com.example.lab3.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.lab3.R;
import com.example.lab3.ViewModels.AuthenticateViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

public class SignInActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView status;
    private Button signInButton;
    private ProgressBar progressBar;
    private AuthenticateViewModel authenticate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        authenticate = ViewModelProviders.of(this).get(AuthenticateViewModel.class);

        signInButton = findViewById(R.id.signInButton);
        progressBar = findViewById(R.id.progressBar);


        status = findViewById(R.id.status);

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
        authenticate.googleSignIn(idToken, task -> {
            if (task.isSuccessful()) {
                authenticate.createUser();
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

        signInButton.setVisibility(View.VISIBLE);
    }
}