package com.sample.bankapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.firestore.auth.User;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailEt, passwordEt1;
    private Button SignUpButton;
    private Button signInButton;
    //    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
//    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
//        mDatabase = FirebaseDatabase.getInstance("https://hungryapp-d791e-default-rtdb.firebaseio.com/").getReference();

        emailEt = findViewById(R.id.email);
        passwordEt1 = findViewById(R.id.password1);
        SignUpButton = findViewById(R.id.register);
        signInButton = findViewById(R.id.loginBtn);
//        progressDialog=new ProgressDialog(this);
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void Register() {
        String email = emailEt.getText().toString();
        String password1 = passwordEt1.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEt.setError("Enter your email");
            emailEt.requestFocus();
        } else if (TextUtils.isEmpty(password1)) {
            passwordEt1.setError("Enter your password");
            passwordEt1.requestFocus();
        } else if (password1.length() < 4) {
            passwordEt1.setError("Length should be > 4");
            return;
        } else if (!isValidEmail(email)) {
            emailEt.setError("invalid email");
            emailEt.requestFocus();
        } else {
            //        progressDialog.setMessage("Please wait...");
//        progressDialog.show();
//        progressDialog.setCanceledOnTouchOutside(false);
            mAuth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // save to database
//                    writeNewUser(task.getResult().getUser());
                        // notify success
                        Toast.makeText(SignUpActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SignUpActivity.this, DashboardActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Sign up fail!", Toast.LENGTH_LONG).show();
                    }
//                progressDialog.dismiss();
                }
            });
        }

    }

//    private void writeNewUser(FirebaseUser user){
//        User dbUser = new User(user.getEmail());
//        mDatabase.child("users").child(user.getUid()).setValue(dbUser);
//    }

    private Boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}