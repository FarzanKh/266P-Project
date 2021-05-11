package com.sample.bankapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "BankDB"; // the name of our database
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "Bank_Table";
    private static final int DB_VERSION2 = 2;

    //The key to access the balance (value) and user_id
    public static final String COL_ITEM1 = "balance";
    public static final String COL_ITEM2 = "user_id";
    public static final double MAX_INPUT = 4294967295.99;


    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Bank_Table(user_id varchar, balance real);");
    }

    //Method gets the balance from the database
    public double getBalance() {

        double result_balance = 0;
        double item = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        //db.execSQL("CREATE TABLE IF NOT EXISTS Bank_Table(balance int);");
        Cursor res =  db.rawQuery( "select balance from Bank_Table", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            item = res.getInt(res.getColumnIndex(COL_ITEM1));
            res.moveToNext();
        }

        result_balance = item;
        return result_balance;
    }

    //changes the balance in the database
    public boolean changeBalance (String new_balance) {
        double converted_balance = Double.parseDouble(new_balance);

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.execSQL("INSERT INTO "+ DB_TABLE +"("+"balance"+")"+" VALUES "+"  ("+ converted_balance +")");
        }
        catch (SQLException e){
            return false;
        }

        //db.execSQL( "insert into Bank_Table (COL_ITEM1) values (converted_balance)" );

          //This is correct and secure code *commented out for now*
//                SQLiteDatabase db = this.getWritableDatabase();
//                ContentValues contentValues = new ContentValues();
//                contentValues.put(COL_ITEM1, converted_balance);
//                db.insert(DB_TABLE, null, contentValues);

        return true;
    }

    // Sets the user ID retrieved from Firebase
    public boolean setUserId(String userId){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ITEM2, userId);

        db.insert(DB_TABLE, null, contentValues);
        return true;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            //Code to run if the database version is 1
        }
    }
}

