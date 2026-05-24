package com.example.safeu2.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class dbhandler extends SQLiteOpenHelper {

    public dbhandler(Context context){
        super(context, parameters.DB_NAME, null, parameters.DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + parameters.TABLE_NAME + "("
                + parameters.KEY_ID + " INTEGER PRIMARY KEY," + parameters.KEY_NUMBER + " TEXT" + ")";
        Log.d("BDContacts", "Query being run is : "+create);
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public void addContact(Contact contact){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(parameters.KEY_NUMBER, contact.getNumber());

        db.insert(parameters.TABLE_NAME, null, values);
        Log.d("BDContacts", "successfully inserted");
        db.close();

    }

    public List<Contact> getAllContacts(){
        List<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // generate the query to read from database
        String select = "SELECT * FROM " + parameters.TABLE_NAME;
        Cursor cursor = db.rawQuery(select,null);
        //loop through now
        if(cursor.moveToFirst()){
            do{
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setNumber(cursor.getString(1));
                contactList.add(contact);
            }while(cursor.moveToNext());
        }
        return contactList;
    }

    public void deleteContactById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(parameters.TABLE_NAME, parameters.KEY_ID +"=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteContactByName(String temp){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(parameters.TABLE_NAME, parameters.KEY_NUMBER +"=?", new String[]{temp});
        db.close();
    }

}
