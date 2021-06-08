package com.sample.bankapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

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
            String postUserName = preUserName + "@secure-bank.com";
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
        if (!FormatChecker.isValidUsernameFormat(username)) {
            usernameField.setError("Invalid username");
            usernameField.requestFocus();
        } else if (!FormatChecker.isValidPasswordFormat(password)) {
            passwordField.setError("Invalid password");
            passwordField.requestFocus();
        } else if (!FormatChecker.isValidNumberFormat(initial_balance)) {
            initialAmount.setError("Invalid initial balance");
            initialAmount.requestFocus();
        } else {
            return true;
        }
        return false;
    }
}

