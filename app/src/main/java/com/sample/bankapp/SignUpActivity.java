package com.sample.bankapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private EditText usernameField, passwordField, initialAmount;
    private Button SignUpButton;
    private Button signInButton;
    private FirebaseAuth mAuth;
    private static final String TAG = "SignUp - Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_sign_up);

        usernameField = findViewById(R.id.usernameSignUp);
        passwordField = findViewById(R.id.passwordSignUp);
        initialAmount = findViewById(R.id.initialAmount);
        SignUpButton = findViewById(R.id.registerBtn);
        signInButton = findViewById(R.id.signInBtn);

        mAuth = FirebaseAuth.getInstance();

        SignUpButton.setOnClickListener(v -> Register());

        signInButton.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
        });
    }

    private void Register() {
        String preUserName = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String initial_balance = initialAmount.getText().toString();

        if (signUpValidation(preUserName, password, initial_balance)) {
            String postUserName = preUserName + "@unsecure-bank.com";
            mAuth.createUserWithEmailAndPassword(postUserName, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "It was successful");

                    Toast.makeText(SignUpActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(SignUpActivity.this,DashboardActivity.class);
                    intent.putExtra("USER_BALANCE", initial_balance);
                    startActivity(intent);
                    finish();
                } else {
                    Log.w(TAG, "createUserWithUserName:failure", task.getException());
                    Toast.makeText(SignUpActivity.this,"Sign up fail!",Toast.LENGTH_LONG).show();
                }

            });
        }
    }

    private boolean signUpValidation(String username, String password, String initial_balance) {
        if (username.length() < 1 || username.length() > 127 || !Pattern.matches("[_\\-\\.0-9a-z]*",username)) {
            usernameField.setError("Invalid username");
            usernameField.requestFocus();
        } else if (password.length() < 6 || password.length() > 127 || !Pattern.matches("[_\\-\\.0-9a-z]*",password)) {
            passwordField.setError("Invalid password");
            passwordField.requestFocus();
        } else if (initial_balance.length() < 4 || initial_balance.length() > 13 || !Pattern.matches("[0-9.]+",initial_balance) || !Pattern.matches("0|[1-9][0-9]*.[0-9]{2}",initial_balance)
                || Double.parseDouble(initial_balance) > DatabaseHelper.MAX_INPUT) {
            initialAmount.setError("Invalid initial balance");
            initialAmount.requestFocus();
        } else {
            return true;
        }
        return false;
    }
}

