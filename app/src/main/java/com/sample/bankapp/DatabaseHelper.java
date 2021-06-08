package com.sample.bankapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.SQLException;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "BankApp"; // the name of our database
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "Banking_Table";
    private static final int DB_VERSION2 = 2;

    //The key to access the balance (value) and user_id
    public static final String COL_ITEM1 = "user_id";
    public static final String COL_ITEM2 = "balance";


    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Banking_Table(user_id varchar, balance real);");
    }

    //Method gets the balance from the database
    public double getBalance(String user_id) {

        double result_balance = 0;
        double item = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "SELECT balance FROM Banking_Table WHERE user_id = ?", new String[] {user_id} );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            item = res.getDouble(res.getColumnIndex(COL_ITEM2));
            res.moveToNext();
        }

        result_balance = item;
        return result_balance;
    }

    //changes the balance in the database
    public double changeBalance (double transaction_amount, String transaction_type, String userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        double wBalance;

        double currBalance = getBalance(userID);

        if(transaction_type.equals("w")) { //Subtract from curr balance

            if(transaction_amount > currBalance) {
                return -1;
            } else {
                wBalance = currBalance - transaction_amount;
            }

            ContentValues values = new ContentValues();
            values.put("balance", wBalance);

            db.update(DB_TABLE, values, "user_id=?", new String[]{userID});

        } else if(transaction_type.equals("d")) { //Add to curr balance

            ContentValues values = new ContentValues();
            values.put("balance", Double.sum(currBalance, transaction_amount));
            db.update(DB_TABLE, values, "user_id=?", new String[]{userID});

        }

        return getBalance(userID);
    }

    // Adds the initial user ID and deposit to the database (Only occurs when a user signs up)
    public double setupAccountInfo(String userId, double initial_deposit){
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("balance", initial_deposit);

        db.insert(DB_TABLE, null, values);

        return getBalance(userId);
    }


    // manages the db version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            //Code to run if the database version is 1
        }
    }
}

