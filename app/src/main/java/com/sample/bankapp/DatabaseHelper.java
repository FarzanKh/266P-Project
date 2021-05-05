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

    public static final String COL_ITEM = "item";

    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Bank_Table(item VARCHAR);");
    }

    //Method gets all of the data from the database
    public ArrayList<String> getAllItems() {

        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL("CREATE TABLE IF NOT EXISTS Bank_Table(item VARCHAR);");
        Cursor res =  db.rawQuery( "select * from Bank_Table", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String full_item = res.getString(res.getColumnIndex(COL_ITEM)) + " ";
            array_list.add(full_item);
            res.moveToNext();
        }
        return array_list;
    }


    public boolean insertItem (String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ITEM, item);

        db.insert(DB_TABLE, null, contentValues);
        return true;
    }

    public void deleteItem (String task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE,
                "item = ? ",
                new String[] { task });
    }

    public ArrayList<String> searchItems(String searchQuery) {
        ArrayList<String> result = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.query(DB_TABLE,
                new String[] {"item"},
                COL_ITEM + " LIKE '%" + searchQuery + "%' ", null, null, null,
                null);

        int iRow = c.getColumnIndex(COL_ITEM); //Cursor looking for column setting equal to these ints.

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result.add(c.getString(iRow));
        }

        return result;
    }


//    public ArrayList<String> sortItems() {
//        ArrayList<String> result = new ArrayList<String>();
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor c = db.query(DB_TABLE,
//                new String[] {"item"},
//                null, null, null, null,
//                "item ASC");
//
//        int iRow = c.getColumnIndex(COL_ITEM);
//
//        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
//            System.out.println("TABLE ITEM AFTER SORT: "+ c.getString(iRow));
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

