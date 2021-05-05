package com.sample.bankapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    Button logoutBtn;
    FirebaseAuth mAuth;
    EditText amount;

    private static DatabaseHelper mydb ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        logoutBtn = findViewById(R.id.logoutBtn);
        amount = findViewById(R.id.editTextNumberDecimal);

        mAuth = FirebaseAuth.getInstance();

        logoutBtn.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
//            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) startActivity(new Intent(DashboardActivity.this, MainActivity.class));

    }


    /** Called when the user touches the button */
    public void withdrawAmount(View view) {
        // Do something in response to button click
        System.out.println("WHAT IS THE AMOUNT IN ET WITHDRAW" + amount);


    }



    /** Called when the user touches the button */
    public void depositAmount(View view) {
        // Do something in response to button click
        System.out.println("WHAT IS THE AMOUNT IN ET DEPOSIT" + amount);

    }



    //Methods to make changes to the database
    public static ArrayList<String> getAll() {
        ArrayList<String> result = new ArrayList<String>();
        result = mydb.getAllItems();
        return result;
    }


    public static ArrayList<String> depositMoney(String item) {
        ArrayList<String> todo_list = new ArrayList<String>();

        boolean result = mydb.insertItem(item);

        if(result) {
            todo_list = mydb.getAllItems();

        }
        return todo_list;
    }


    public static ArrayList<String> withdrawMoney(String item) {
        ArrayList<String> todo_list = new ArrayList<String>();

        boolean result = mydb.insertItem(item);

        if(result) {
            todo_list = mydb.getAllItems();

        }
        return todo_list;
    }



    public static ArrayList<String> searchWorkout(String task) {
        ArrayList<String> todo_list = new ArrayList<String>();

        todo_list = mydb.searchItems(task);
        return todo_list;
    }



}