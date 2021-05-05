package com.sample.bankapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "BankDB"; // the name of our database
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "Bank_Table";
    private static final int DB_VERSION2 = 2;

    //The key to access the balance (value)
    public static final String COL_ITEM = "balance";

    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Bank_Table(balance int);");
    }

    //Method gets the balance from the database
    public int getBalance() {

        int result_balance = 0;
        int item = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        //db.execSQL("CREATE TABLE IF NOT EXISTS Bank_Table(balance int);");
        Cursor res =  db.rawQuery( "select balance from Bank_Table", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            item = res.getInt(res.getColumnIndex(COL_ITEM));
            res.moveToNext();
        }
        System.out.println(" get BALANCE "+ item);

        result_balance = item;
        return result_balance;
    }

    //changes the balance in the database
    public boolean changeBalance (int new_balance) {
        System.out.println("What is the new balance"+ new_balance);


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ITEM, new_balance);

        db.insert(DB_TABLE, null, contentValues);
        return true;
    }


//    public ArrayList<String> searchItems(String searchQuery) {
//        ArrayList<String> result = new ArrayList<String>();
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        Cursor c = db.query(DB_TABLE,
//                new String[] {"balance"},
//                COL_ITEM + " LIKE '%" + searchQuery + "%' ", null, null, null,
//                null);
//
//        int iRow = c.getColumnIndex(COL_ITEM); //Cursor looking for column setting equal to these ints.
//
//        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
//            result.add(c.getString(iRow));
//        }
//
//        return result;
//    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            //Code to run if the database version is 1
        }
    }
}

