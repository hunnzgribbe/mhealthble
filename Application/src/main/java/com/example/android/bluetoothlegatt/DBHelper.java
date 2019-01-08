package com.example.android.bluetoothlegatt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;


//Database class for storing the information

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "mHealth.db";
    public static final String MHEALTH_TABLE_USER = "mhealthuser";
    public static final String MHEALTH_TABLE_VALUES = "mhealthvalues";
    public static final String MHEALTH_COLUMN_ID_USER = "userid";
    public static final String MHEALTH_COLUMN_ID_VALUES = "valuesid";
    public static final String MHEALTH_COLUMN_EMAIL = "email";
    public static final String MHEALTH_COLUMN_WEIGHT_UNIT = "weightunit";
    public static final String MHEALTH_COLUMN_WEIGHT_VALUE = "weightvalue";
    public static final String MHEALTH_COLUMN_BLOOD_PRESSURE_UNIT = "bpunit";
    public static final String MHEALTH_COLUMN_BLOOD_PRESSURE_SYSTOLIC = "systolic";
    public static final String MHEALTH_COLUMN_BLOOD_PRESSURE_DIASTOLIC = "diastolic";
    public static final String MHEALTH_COLUMN_BLOOD_PRESSURE_MAP = "map";
    public static final String MHEALTH_COLUMN_BLOOD_PRESSURE_PULSE = "pulse";
    public static final String MHEALTH_COLUMN_LAST_READ_TIME = "date";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    //Create db
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table mhealthuser" +
                        "(userid integer primary key, email text)"
        );
        db.execSQL(
                "create table mhealthvalues" +
                        "(valueid integer primary key, userid integer, weightunit text, weightvalue text, bpunit text, systolic text, diastolic text, map text, pulse text, date text, foreign key (userid) references mhealthuser(userid))"
        );
    }

    //Drop table if exists
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS mhealthuser");
        db.execSQL("DROP TABLE IF EXISTS mhealtvalues");
        onCreate(db);
    }

    //Insert new user into db
    public boolean insertMhealthUser (String email, String weightunit, String weightvalue, String bpunit, String systolic, String diastolic, String map, String pulse, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues1 = new ContentValues();
        ContentValues contentValues2 = new ContentValues();
        contentValues1.put("email", email);
        contentValues2.put("weightunit", weightunit);
        contentValues2.put("weightvalue", weightvalue);
        contentValues2.put("bpunit", bpunit);
        contentValues2.put("systolic", systolic);
        contentValues2.put("diastolic", diastolic);
        contentValues2.put("map", map);
        contentValues2.put("pulse", pulse);
        contentValues2.put("date", date);
        db.insert("mhealthuser", null, contentValues1);
        db.insert("mhealthvalues", null, contentValues2);
        return true;
    }


    //Insert new values into db
    public boolean insertMhealthValues (int userid, String weightunit, String weightvalue, String bpunit, String systolic, String diastolic, String map, String pulse, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("userid", userid);
        contentValues.put("weightunit", weightunit);
        contentValues.put("weightvalue", weightvalue);
        contentValues.put("bpunit", bpunit);
        contentValues.put("systolic", systolic);
        contentValues.put("diastolic", diastolic);
        contentValues.put("map", map);
        contentValues.put("pulse", pulse);
        contentValues.put("date", date);

        db.insert("mhealthvalues", null, contentValues);
        return true;
    }

    //Insert new weightvalues into db
    public boolean insertMhealthValuesWeight (int userid, String weightunit, String weightvalue, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("userid", userid);
        contentValues.put("weightunit", weightunit);
        contentValues.put("weightvalue", weightvalue);
        contentValues.put("date", date);
        db.insert("mhealthvalues",null, contentValues);
        return true;
    }

    //Insert new bpmvalues into db
    public boolean insertMhealthValuesBpm (int userid, String bpunit, String systolic, String diastolic, String map, String pulse, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("userid", userid);
        contentValues.put("bpunit", bpunit);
        contentValues.put("systolic", systolic);
        contentValues.put("diastolic", diastolic);
        contentValues.put("map", map);
        contentValues.put("pulse", pulse);
        contentValues.put("date", date);
        db.insert("mhealthvalues",null, contentValues);
        return true;
    }

    //Cursor for getting data from a specific user
    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from mhealthuser inner join mhealthvalues on mhealthuser.userid=mhealthvalues.userid where mhealthuser.userid=? ", new String[] { Integer.toString(id) } );
        return res;
    }

    //Get the row numbers total
    public int getnumberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, MHEALTH_TABLE_USER);
        return numRows;
    }


    //Update methods: Update data for an existing user
    public boolean updateMhealthUser (Integer id, String email, String weightunit, String weightvalue, String bpunit, String systolic, String diastolic, String map, String pulse) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues1 = new ContentValues();
        ContentValues contentValues2 = new ContentValues();
        contentValues1.put("email", email);
        contentValues2.put("weightunit", weightunit);
        contentValues2.put("weightvalue", weightvalue);
        contentValues2.put("bpunit", bpunit);
        contentValues2.put("systolic", systolic);
        contentValues2.put("diastolic", diastolic);
        contentValues2.put("map", map);
        contentValues2.put("pulse", pulse);
        db.update("mhealthuser", contentValues1, "userid = ? ", new String[] { Integer.toString(id) } );
        db.update("mhealthvalues", contentValues2, "userid = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean updateMhealthUserMail (Integer id, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        db.update("mhealthuser", contentValues, "userid = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    //Delete User from db
    public Integer deleteUser (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int a = db.delete("mhelthuser",
                "userid = ? ",
                new String[] { Integer.toString(id) });

        int b = db.delete("mhealthvalues",
                "userid = ? ",
                new String[] { Integer.toString(id) });

        return a+b;

    }

    //Get an Arraylist for Email and ID
    public ArrayList<String> getAllUsers() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from mhealthuser", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(MHEALTH_COLUMN_ID_USER);
            array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_ID_USER)));
            array_list.add(MHEALTH_COLUMN_EMAIL);
            array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_EMAIL)));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    //Get an Arraylist for Data from specific user
    public ArrayList<String> getAllDataFromUser(int id) {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from mhealthuser inner join mhealthvalues on mhealthuser.userid=mhealthvalues.userid where mhealthvalues.userid=?", new String[] { Integer.toString(id) } );

        if (res.moveToFirst()) {
            do {

                array_list.add(MHEALTH_COLUMN_WEIGHT_UNIT);
                if((res.getString(res.getColumnIndex(MHEALTH_COLUMN_WEIGHT_UNIT)) != "") || (res.getString(res.getColumnIndex(MHEALTH_COLUMN_WEIGHT_UNIT)) != null)){
                array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_WEIGHT_UNIT)));
                }
                else{
                    array_list.add("N.a.");
                }

                array_list.add(MHEALTH_COLUMN_WEIGHT_VALUE);
                if((res.getString(res.getColumnIndex(MHEALTH_COLUMN_WEIGHT_VALUE)) != "") || (res.getString(res.getColumnIndex(MHEALTH_COLUMN_WEIGHT_VALUE)) != null)){
                array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_WEIGHT_VALUE)));
                }
                else{
                    array_list.add("N.a.");
                }

                array_list.add(MHEALTH_COLUMN_BLOOD_PRESSURE_UNIT);
                if((res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_UNIT)) != "") || (res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_UNIT)) != null)){
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_UNIT)));
                }
                else{
                    array_list.add("N.a.");
                }

                array_list.add(MHEALTH_COLUMN_BLOOD_PRESSURE_SYSTOLIC);
                if((res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_SYSTOLIC)) != "") || (res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_SYSTOLIC)) != null)){
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_SYSTOLIC)));
                }
                else{
                    array_list.add("N.a.");
                }

                array_list.add(MHEALTH_COLUMN_BLOOD_PRESSURE_DIASTOLIC);
                if((res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_DIASTOLIC)) != "") || (res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_DIASTOLIC)) != null)){
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_DIASTOLIC)));
                }
                else{
                    array_list.add("N.a.");
                }

                array_list.add(MHEALTH_COLUMN_BLOOD_PRESSURE_MAP);
                if((res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_MAP)) != "") || (res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_MAP)) != null)){
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_MAP)));
                }
                else{
                    array_list.add("N.a.");
                }

                array_list.add(MHEALTH_COLUMN_BLOOD_PRESSURE_PULSE);
                if((res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_PULSE)) != "") || (res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_PULSE)) != null)){
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_PULSE)));
               }
                else{
                    array_list.add("N.a.");
                }

                array_list.add(MHEALTH_COLUMN_LAST_READ_TIME);
                if((res.getString(res.getColumnIndex(MHEALTH_COLUMN_LAST_READ_TIME)) != "") || (res.getString(res.getColumnIndex(MHEALTH_COLUMN_LAST_READ_TIME)) != null)){
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_LAST_READ_TIME)));
                }
                else{
                    array_list.add("N.a.");
                }
            } while (res.moveToNext());
        }


        res.close();
        return array_list;
    }

    //Get the ID of the last inserted user
    public Integer getLastUsersId() {
        int tempid = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from mhealthuser", null );

        res.moveToLast();
        tempid = res.getInt(res.getColumnIndex(MHEALTH_COLUMN_ID_USER));
        return tempid;
    }


}
