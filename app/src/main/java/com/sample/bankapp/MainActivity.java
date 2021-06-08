package com.sample.bankapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private EditText usernameEt, passwordEt;
    private Button SignInButton;
    private Button SignUpBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
        // remove bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        usernameEt = findViewById(R.id.usernameSignIn);
        passwordEt = findViewById(R.id.passwordSignIn);
        SignInButton = findViewById(R.id.signInBtn);
        SignUpBtn = findViewById(R.id.signUpBtn);
        Button callButton = findViewById(R.id.callSupport);

        SignInButton.setOnClickListener(v -> Login());

        SignUpBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            finish();
        });


        callButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                /* Bad Code
                Intent intent = new Intent();
                intent.putExtra("Phonenumber", "8582038129");
                intent.setAction("CallService");
                intent.addCategory("android.intent.category.DEFAULT");
                startActivity(intent);
                */
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + "8582038129"));
                startActivity(callIntent);
            } else {
                showToast();
            }
        });
    }

    private void showToast() {
        Toast toast = Toast.makeText(this, "You must give the phone call permission", Toast.LENGTH_SHORT);
        toast.show();
    }


    private void Login() {
        String preUserName = usernameEt.getText().toString();
        String password = passwordEt.getText().toString();

        if (signInValidation(preUserName, password)) {
            String postUserName = preUserName + "@secure-bank.com";
            mAuth.signInWithEmailAndPassword(postUserName, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Successfully logged in", Toast.LENGTH_LONG).show();
                    /* Bad Code
                    Intent intent = new Intent();
                    intent.setAction("DashBoard");
                    intent.addCategory("android.intent.category.DEFAULT");
                    */
                    Intent intent = new Intent(this, DashboardActivity.class); // Fix using explicit intent
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Sign in fail!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean signInValidation(String username, String password) {
        if (!FormatChecker.isValidUsernameFormat(username)) {
            usernameEt.setError("Invalid username");
            usernameEt.requestFocus();
        } else if (!FormatChecker.isValidPasswordFormat(password)) {
            passwordEt.setError("Invalid password");
            passwordEt.requestFocus();
        } else {
            return true;
        }
        return false;
    }
}