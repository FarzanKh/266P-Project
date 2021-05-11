package com.sample.bankapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    Button logoutBtn;
    FirebaseAuth mAuth;
    EditText amount;
    TextView ui_balance;

    private static String user_id;

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

        //******************************************************************************

        //Get user UID from firebase
        String currentuserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user_id = currentuserID;

        //Get User balance from sign up page
        String user_balance = getIntent().getStringExtra("USER_BALANCE");

        double starting_balance = 0;
        //Make sure it's Sign up page that these values are valid
        if(user_balance != null) {
            //Add user id and initial balance
            starting_balance = mydb.setupAccountInfo(currentuserID, user_balance);
            if (starting_balance < 0 || starting_balance != Double.parseDouble(user_balance)){
                ui_balance.setText("Error");
                return;
            }
        }
        else{
            starting_balance = getCurrentBalance(user_id);
        }
        ui_balance.setText("$" +  String.format("%.2f",starting_balance));

        //*******************************************************************************

        logoutBtn.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) startActivity(new Intent(DashboardActivity.this, MainActivity.class));

    }


    /** Called when the user touches the button */
    public void withdrawAmount(View view) throws TransactionStateException  {
        double transaction_result ;
        String amountToWithdraw  = amount.getText().toString();

        if(amountToWithdraw .equals("")) {
            Toast.makeText(getApplicationContext(), "Transaction Failed: Please enter a valid input!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            transaction_result = bankTransaction(amountToWithdraw, "w", user_id);
        }

        //checks if value would be negative
        if (transaction_result ==  -1) {
            Toast.makeText(getApplicationContext(), "Transaction Failed: Not enough money to withdraw!", Toast.LENGTH_SHORT).show();
        } else if (transaction_result == -2){
            Toast.makeText(getApplicationContext(), "Transaction Failed: Withdraw Error", Toast.LENGTH_SHORT).show();
            throw new TransactionStateException("Withdraw error.");
        } else {
            ui_balance.setText("$" + String.format("%.2f",transaction_result));
        }
    }



    /** Called when the user touches the button */
    public void depositAmount(View view) throws TransactionStateException {
        double transaction_result;
        String amountToDeposit = amount.getText().toString();

        if(amountToDeposit.equals("")) {
            Toast.makeText(getApplicationContext(), "Transaction Failed: Please enter a valid input", Toast.LENGTH_SHORT).show();
            return;
        } else {
            transaction_result = bankTransaction(amountToDeposit, "d", user_id);
        }

        if (transaction_result == -2){
            Toast.makeText(getApplicationContext(), "Transaction Failed: Withdraw Error", Toast.LENGTH_SHORT).show();
            throw new TransactionStateException("Deposit error.");
        }
        else {
            ui_balance.setText("$" + String.format("%.2f",transaction_result));
        }

    }



    //gets the current bank balance from database
    public double getCurrentBalance(String userID) {
        return mydb.getBalance(userID);
    }

    //checks the transaction type and changes the balance on the database
    public double bankTransaction(String transaction_amount, String transaction_type, String userID) {
        double result_balance = 0;
        double curr_balance = getCurrentBalance(userID);

        // precondition: checks if current balance is non-negative
        if (curr_balance < 0) {
            return -2;
        }

        if(transaction_type == "w") {
            // precondition: checks if new balance is non-negative
            double expected_balance = curr_balance - Double.parseDouble(transaction_amount);
            if (expected_balance < 0 ){
                return -1;
            } else {
                result_balance = mydb.changeBalance(transaction_amount, "w", userID);
                if (result_balance != expected_balance){
                    return -2;
                }
            }
        } else if (transaction_type == "d") {
            result_balance = mydb.changeBalance(transaction_amount, "d", userID);;
        }

        // post condition: checks if balance is non-negative and updated without error
        if (result_balance < 0) {
            return -2;
        }

        return result_balance;
    }


    class TransactionStateException extends Exception {
        public TransactionStateException(String message){
            super(message);
        }
    }


}