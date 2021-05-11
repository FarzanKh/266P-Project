package com.sample.bankapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

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

        //Initialize DB
        mAuth = FirebaseAuth.getInstance();
        mydb = new DatabaseHelper(this);


        //*******************************************************************************

        //Get user UID from firebase
        String currentuserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Get User balance from sign up page
        String user_balance = getIntent().getStringExtra("USER_BALANCE");

        //Make sure it's Sign up page that these values are valid
        if(user_balance != null) {
            int int_balance = Integer.parseInt(user_balance);

            //Set user id in db
            mydb.setUserId(currentuserID);

            //Set db balance to user's initial amount
            initial_DB_Deposit(int_balance);
        }

        int starting_balance = getCurrentBalance();
        ui_balance.setText("$" + starting_balance);

        //*******************************************************************************


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
        int transaction_result = 0;

        String new_amount = amount.getText().toString();

        if(new_amount.equals("")) {
            Toast.makeText(getApplicationContext(), "Transaction Failed: Not enough money to withdraw!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            int int_amount = Integer.parseInt(new_amount);
            transaction_result = bankTransaction(int_amount, "w");
        }

        //checks if balance is 0
        if (transaction_result ==  -1) {
            Toast.makeText(getApplicationContext(), "Transaction Failed: Not enough money to withdraw!", Toast.LENGTH_SHORT).show();
        } else {
            ui_balance.setText("$" + String.valueOf(transaction_result));
        }
    }



    /** Called when the user touches the button */
    public void depositAmount(View view) {
        // Do something in response to button click
        int transaction_result = 0;
        String new_amount = amount.getText().toString();
        // todo: delete later:
        validateAmount(new_amount);

        if(new_amount.equals("")) {
            Toast.makeText(getApplicationContext(), "Transaction Failed: Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return;
        } else {
            int int_amount = Integer.parseInt(new_amount);
            transaction_result = bankTransaction(int_amount, "d");
        }

        ui_balance.setText("$" + String.valueOf(transaction_result));
    }

    public int validateAmount(String amount) {
        // check if valid number
        boolean matches = Pattern.matches("0|[1-9][0-9]*",amount); //.[0-9]{2}, check if greater than max
        Log.i("PATTERN_MATCH","Number: " + String.valueOf(matches));
        //todo: if (matches)
        int int_amount = Integer.parseInt(amount);
        return 0;
    }

    public static void initial_DB_Deposit(int deposit) {
        boolean dbResult = mydb.changeBalance(deposit);
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
            new_balance = curr_balance + transaction_amount;
            boolean dbResult = mydb.changeBalance(new_balance);
            if(dbResult){
                result_balance = new_balance;
            }
        }

        return result_balance;
    }




}