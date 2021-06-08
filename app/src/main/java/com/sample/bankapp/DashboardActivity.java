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

import java.util.Objects;
import java.util.regex.Pattern;

public class DashboardActivity extends AppCompatActivity {

    Button logoutBtn;
    FirebaseAuth mAuth;
    EditText amount;
    TextView ui_balance;

    private static String user_id;
    private static DatabaseHelper mydb;

    private final double MAX_BALANCE = 10000000000.00;

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
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        //Get User balance from sign up page
        String user_balance = getIntent().getStringExtra("USER_BALANCE");

        double starting_balance;
        //Make sure it's Sign up page that these values are valid
        if(user_balance != null) {
            // Add user id and initial balance
            double user_balance_double = Double.parseDouble(user_balance);
            starting_balance = mydb.setupAccountInfo(user_id, user_balance_double);
            // post condition
            if (starting_balance < 0 || starting_balance > MAX_BALANCE || starting_balance != user_balance_double){
                ui_balance.setText("Error");
                Toast.makeText(getApplicationContext(),"Account creation error. Please contact support.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            starting_balance = getCurrentBalance(user_id);
            // todo: what if there's no current balance? do we set to zero automatically?
            //  make sure to check DatabaseHelper new code to see if it throws an error if user not found
            //  then create a user in that case
            //  also check .equals vs == in dbhelper
            //  also change so dbhelper throws exceptions
        }
        ui_balance.setText(String.format("$%s", String.format("%.2f", starting_balance)));

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
    public void withdrawAmount(View view) {
        double transaction_result;
        String amountToWithdraw = amount.getText().toString();

        // todo: check if set errors work and delete comments
        if(!FormatChecker.isValidNumberFormat(amountToWithdraw)) {
            amount.setError("Invalid amount");
            amount.requestFocus();
//            Toast.makeText(getApplicationContext(), "Transaction Failed: Please enter a valid input!", Toast.LENGTH_SHORT).show();
        } else {
            try {
                transaction_result = bankTransaction(amountToWithdraw, "w", user_id);
                ui_balance.setText(String.format("$%s", String.format("%.2f", transaction_result)));
            }
            catch (InsufficientFundsException e) {
                Toast.makeText(getApplicationContext(), "Transaction Failed: Not enough money to withdraw!", Toast.LENGTH_SHORT).show();
            } catch (BalanceLimitExceededException e) {
                Toast.makeText(getApplicationContext(), "Transaction Failed: Would exceed maximum balance!", Toast.LENGTH_SHORT).show();
            } catch (TransactionStateException e) {
                // todo: have column in sql table to set inactive? and tell user to contact support to resolve the issue?
            }
        }
    }



    /** Called when the user touches the button */
    public void depositAmount(View view) throws TransactionStateException {
        double transaction_result;
        String amountToDeposit = amount.getText().toString();

        if (!FormatChecker.isValidNumberFormat(amountToDeposit)) {
            amount.setError("Invalid amount");
            amount.requestFocus();
//            Toast.makeText(getApplicationContext(), "Transaction Failed: Please enter a valid input", Toast.LENGTH_SHORT).show();
        } else {
            try {
                transaction_result = bankTransaction(amountToDeposit, "d", user_id);
                ui_balance.setText(String.format("$%s", String.format("%.2f", transaction_result)));
            } catch (InsufficientFundsException e){
                Toast.makeText(getApplicationContext(), "Transaction Failed: Not enough money to withdraw!", Toast.LENGTH_SHORT).show();
            } catch (BalanceLimitExceededException e){
                Toast.makeText(getApplicationContext(), "Transaction Failed: Would go over max balance!", Toast.LENGTH_SHORT).show();
            } catch (TransactionStateException e) {
                // todo: have column in sql table to set inactive? and tell user to contact support to resolve the issue?
            }
        }
    }

    /** Gets the current bank balance from database */
    private double getCurrentBalance(String userID) {
        return mydb.getBalance(userID);
    }

    /** Checks the transaction type and changes the balance on the database, returns -1 if would result in
        invalid balance and -2 if there is an unexpected transaction error */
    private double bankTransaction(String transaction_amount, String transaction_type, String userID) throws TransactionStateException, InsufficientFundsException, BalanceLimitExceededException {
        double curr_balance = getCurrentBalance(userID);
        double expected_balance = 0;
        double transaction_double = Double.parseDouble(transaction_amount);

        // precondition: checks if current balance is non-negative
        if (curr_balance < 0 || curr_balance > MAX_BALANCE) {
            throw new TransactionStateException("Current balance outside of allowed range.");
        }

        double result_balance = 0;

        if (transaction_type.equals("w")) {
            expected_balance = curr_balance - transaction_double;
        } else if (transaction_type.equals("d")) {
            expected_balance = curr_balance + transaction_double;
        }

        // precondition: checks if new balance would be non-negative
        if (expected_balance < 0){
            throw new InsufficientFundsException("Less than $" + transaction_amount + " in balance.");
        } // precondition: checks if new balance would be more than max
        else if (expected_balance > MAX_BALANCE){
            throw new BalanceLimitExceededException("Depositing $" + transaction_amount + " would exceed max balance allowed.");
        } else {
            result_balance = mydb.changeBalance(transaction_double, transaction_type, userID);
        }

        // post condition: checks if balance is within proper range and updated without error
        if (result_balance < 0 || result_balance > MAX_BALANCE) {
            throw new TransactionStateException("Balance after transaction outside of allowed range.");
        } else if (result_balance != expected_balance){
            throw new TransactionStateException("Transaction yielded unexpected balance.");
        }

        return result_balance;
    }

    // todo: also handle SQL exception from db
    class TransactionStateException extends Exception {
        public TransactionStateException(String message){
            super(message);
        }
    }

    class InsufficientFundsException extends Exception {
        public InsufficientFundsException(String message){
            super(message);
        }
    }

    class BalanceLimitExceededException extends Exception {
        public BalanceLimitExceededException(String message){
            super(message);
        }
    }
}