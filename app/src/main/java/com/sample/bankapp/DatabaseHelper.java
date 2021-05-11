package com.sample.bankapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.SQLException;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "BankDBSK"; // the name of our database
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "Banking_Table";
    private static final int DB_VERSION2 = 2;

    //The key to access the balance (value) and user_id
    public static final String COL_ITEM1 = "user_id";
    public static final String COL_ITEM2 = "balance";
    public static final double MAX_INPUT = 4294967295.99;


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
    public double changeBalance (String transaction_amount, String transaction_type, String userID) {
        SQLiteDatabase db = this.getWritableDatabase();

        if(transaction_type == "w") { //Add to curr balance
            try{
                db.execSQL("UPDATE "+ DB_TABLE +" SET balance = balance - " + transaction_amount + " "+" WHERE  user_id = " + "'" + userID + "'");
            } catch (SQLException e){
                return -2;
            }

        } else if(transaction_type == "d") { //Subtract from curr balance
            try{
                db.execSQL("UPDATE "+ DB_TABLE +" SET balance = balance + " + transaction_amount + " "+" WHERE  user_id = " + "'" + userID + "'");
            } catch (SQLException e){
                return -2;
            }
        }
        double changed_balance = getBalance(userID);

        return changed_balance;
    }

    // Adds the initial user ID and deposit to the database (Only occurs when a user signs up)
    public boolean setupAccountInfo(String userId, String initial_deposit){
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.execSQL("INSERT INTO "+ DB_TABLE +"(" + "user_id" + "," + "balance" + ")"+" VALUES "+" ("+ "'" + userId + "'" + "," + "'" + initial_deposit + "'" + ")");
        }  catch (SQLException e){
            return false;
        }

        return true;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            //Code to run if the database version is 1
        }
    }
}

