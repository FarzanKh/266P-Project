package com.sample.bankapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    Button logoutBtn;
    FirebaseAuth mAuth;
    EditText amount;
    TextView ui_balance;

    private static DatabaseHelper mydb ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        logoutBtn = findViewById(R.id.logoutBtn);
        amount = findViewById(R.id.editTextNumberDecimal);
        ui_balance = findViewById(R.id.balance);

        int starting_balance = getCurrentBalance();
        ui_balance.setText("$" + starting_balance);

        mAuth = FirebaseAuth.getInstance();
        mydb = new DatabaseHelper(this);

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

        String new_amount = amount.getText().toString();
        int int_amount = Integer.parseInt(new_amount);
        int transaction_result = bankTransaction(int_amount, "w");

        //checks if balance is 0
        if (transaction_result ==  -1) {
            //transaction below $0
        } else {
            ui_balance.setText(String.valueOf(transaction_result));
        }
    }



    /** Called when the user touches the button */
    public void depositAmount(View view) {
        // Do something in response to button click

        String new_amount = amount.getText().toString();
        System.out.println("WHAT IS THE AMOUNT IN user DEPOSIT" + new_amount);

        int int_amount = Integer.parseInt(new_amount);
        System.out.println("CHECK 1");

        int transaction_result = bankTransaction(int_amount, "d");

        System.out.println("HELLO WHAT IS THIS IN DEPOSIT" + transaction_result);
        ui_balance.setText(String.valueOf(transaction_result));

    }



    //gets the current bank balance from database
    public static int getCurrentBalance() {
        int result = mydb.getBalance();
        return result;
    }

    //checks the transaction type and changes the balance on the database
    public static int bankTransaction(int transaction_amount, String transaction_type) {
        int result_balance = 0;
        int curr_balance = getCurrentBalance();

        int new_balance = 0;

        if(transaction_type == "w") {
            new_balance = curr_balance - transaction_amount;
            System.out.println("W");
            //checks if new balance is more than $0
            if (new_balance < 0 ){
                return -1;
            } else {
                boolean dbResult = mydb.changeBalance(new_balance);
                if(dbResult){
                    result_balance = new_balance;
                }

            }

        } else if (transaction_type == "d") {
            System.out.println("D");

            new_balance = curr_balance + transaction_amount;
            boolean dbResult = mydb.changeBalance(new_balance);
            if(dbResult){
                result_balance = new_balance;
            }
        }

        return result_balance;
    }


//    public static ArrayList<String> withdrawMoney(String item) {
//        ArrayList<String> todo_list = new ArrayList<String>();
//
//        boolean result = mydb.insertItem(item);
//
//        if(result) {
//            todo_list = mydb.getAllItems();
//
//        }
//        return todo_list;
//    }
//
//
//
//    public static ArrayList<String> searchWorkout(String task) {
//        ArrayList<String> todo_list = new ArrayList<String>();
//
//        todo_list = mydb.searchItems(task);
//        return todo_list;
//    }



}