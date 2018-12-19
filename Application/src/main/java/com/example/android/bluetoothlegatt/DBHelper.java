package com.example.android.bluetoothlegatt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


//Database class for storing the informations

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "mHealth.db";
    public static final String MHEALTHUSERS_TABLE_NAME = "mhealthusers";
    public static final String MHEALTHUSERS_COLUMN_ID = "id";
    public static final String MHEALTHUSERS_COLUMN_EMAIL = "email";
    public static final String MHEALTHUSERS_COLUMN_WEIGHT_UNIT = "weightunit";
    public static final Double MHEALTHUSERS_COLUMN_WEIGHT_VALUE = 0.0;
    public static final Double MHEALTHUSERS_COLUMN_BLOOD_PRESSURE_SYSTOLIC = 0.0;
    public static final Double MHEALTHUSERS_COLUMN_BLOOD_PRESSURE_DIASTOLIC = 0.0;
    public static final Double MHEALTHUSERS_COLUMN_BLOOD_PRESSURE_MAP = 0.0;
    public static final Double MHEALTHUSERS_COLUMN_BLOOD_PRESSURE_PULSE = 0.0;
    public static final String MHEALTHUSERS_COLUMN_LAST_READ_TIME = "date";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table mhealthusers " +
                        "(id integer primary key, email text,weightunit text,weightvalue double, systolic double, diastolic double, map double, pulse double, date text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS healthuser");
        onCreate(db);
    }

    public boolean insertMhealthUser (String email, String weightunit, double weightvalue, double systolic, double diastolic, double map, double pulse) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("weightunit", weightunit);
        contentValues.put("weightvalue", weightvalue);
        contentValues.put("systolic", systolic);
        contentValues.put("diastolic", diastolic);
        contentValues.put("map", map);
        contentValues.put("pulse", pulse);

        db.insert("mhealthusers", null, contentValues);
        return true;
    }

    public boolean insertMhealthUserMail (String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        db.insert("mhealthusers", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from mhealthusers where id="+id+"", null );
        return res;
    }

    public int getnumberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, MHEALTHUSERS_TABLE_NAME);
        return numRows;
    }


    public boolean updateMhealthUser (Integer id, String email, String weightunit, double weightvalue, double systolic, double diastolic, double map, double pulse) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("weightunit", weightunit);
        contentValues.put("weightvalue", weightvalue);
        contentValues.put("systolic", systolic);
        contentValues.put("diastolic", diastolic);
        contentValues.put("map", map);
        contentValues.put("pulse", pulse);
        db.update("mhealthusers", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean updateMhealthUserMail (Integer id, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        db.update("mhealthusers", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean updateMhealthUserWeight (Integer id, String weightunit, Double weightvalue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("weightunit", weightunit);
        contentValues.put("weightvalue", weightvalue);
        db.update("mhealthusers", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean updateMhealthUserbpm (Integer id, double systolic, double diastolic, double map, double pulse) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("systolic", systolic);
        contentValues.put("diastolic", diastolic);
        contentValues.put("map", map);
        contentValues.put("pulse", pulse);
        db.update("mhealthusers", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean updateMhealthUserDate (Integer id, String datedata) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", datedata);
        db.update("mhealthusers", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }


    public Integer deleteUser (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllMhealthUsers() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from mhealthusers", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(MHEALTHUSERS_COLUMN_ID)));
            array_list.add(res.getString(res.getColumnIndex(MHEALTHUSERS_COLUMN_EMAIL)));
            res.moveToNext();
        }
        return array_list;
    }


}