package com.example.android.bluetoothlegatt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;


//Database helper class for storing the data

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "mHealth.db";
    public static final String MHEALTH_TABLE_USER = "mhealthuser";
    public static final String MHEALTH_TABLE_VALUES = "mhealthvalues";
    public static final String MHEALTH_COLUMN_ID_USER = "userid";
    public static final String MHEALTH_COLUMN_ID_PATIENT = "patientid";
    public static final String MHEALTH_COLUMN_ID_VALUES = "valuesid";
    public static final String MHEALTH_COLUMN_EMAIL = "email";
    public static final String MHEALTH_COLUMN_PATIENT_ID = "patientid";
    public static final String MHEALTH_COLUMN_WEIGHT_UNIT = "weightunit";
    public static final String MHEALTH_COLUMN_WEIGHT_VALUE = "weightvalue";
    public static final String MHEALTH_COLUMN_BLOOD_PRESSURE_UNIT = "bpunit";
    public static final String MHEALTH_COLUMN_BLOOD_PRESSURE_SYSTOLIC = "systolic";
    public static final String MHEALTH_COLUMN_BLOOD_PRESSURE_DIASTOLIC = "diastolic";
    public static final String MHEALTH_COLUMN_BLOOD_PRESSURE_MAP = "map";
    public static final String MHEALTH_COLUMN_BLOOD_PRESSURE_PULSE = "pulse";
    public static final String MHEALTH_COLUMN_GENERIC_UNIT = "genericunit";
    public static final String MHEALTH_COLUMN_GENERIC_VALUE = "genericvalue";
    public static final String MHEALTH_COLUMN_LAST_READ_TIME_WEIGHT = "wdate";
    public static final String MHEALTH_COLUMN_LAST_READ_TIME_BPM = "bdate";
    public static final String MHEALTH_COLUMN_LAST_READ_TIME_GENERIC = "gdate";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    //Create db with tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table mhealthuser" +
                        "(userid integer primary key, email text, patientid text)"
        );
        db.execSQL(
                "create table mhealthvaluesweight" +
                        "(valueid integer primary key, userid integer, weightunit text, weightvalue text, wdate text, foreign key (userid) references mhealthuser(userid))"
        );
        db.execSQL(
                "create table mhealthvaluesbpm" +
                        "(valueid integer primary key, userid integer, bpunit text, systolic text, diastolic text, map text, pulse text, bdate text, foreign key (userid) references mhealthuser(userid))"
        );
        db.execSQL(
                "create table mhealthvaluesgeneric" +
                        "(valueid integer primary key, userid integer, genericunit text, genericvalue text, gdate text, foreign key (userid) references mhealthuser(userid))"
        );
    }

    //Drop table if exists
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS mhealthuser");
        db.execSQL("DROP TABLE IF EXISTS mhealthvaluesweight");
        db.execSQL("DROP TABLE IF EXISTS mhealthvaluesbpm");
        db.execSQL("DROP TABLE IF EXISTS mhealthvaluesgeneric");
        onCreate(db);
    }

    //Insert new user into db
    public boolean insertMhealthUser (String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put("email", email);
        db.insert("mhealthuser", null, contentValues1);
        return true;
    }

    //Insert new values (weight and bpm) into db
    public boolean insertMhealthValues (int userid, String weightunit, String weightvalue, String bpunit, String systolic, String diastolic, String map, String pulse, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues1 = new ContentValues();
        ContentValues contentValues2 = new ContentValues();
        contentValues1.put("userid", userid);
        contentValues2.put("userid", userid);
        contentValues1.put("weightunit", weightunit);
        contentValues1.put("weightvalue", weightvalue);
        contentValues2.put("bpunit", bpunit);
        contentValues2.put("systolic", systolic);
        contentValues2.put("diastolic", diastolic);
        contentValues2.put("map", map);
        contentValues2.put("pulse", pulse);
        contentValues1.put("wdate", date);
        contentValues2.put("bdate", date);

        db.insert("mhealthvaluesweight", null, contentValues1);
        db.insert("mhealthvaluesbpm", null, contentValues2);
        return true;
    }

    //Insert new weightvalues into db
    public boolean insertMhealthValuesWeight (int userid, String weightunit, String weightvalue, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("userid", userid);
        contentValues.put("weightunit", weightunit);
        contentValues.put("weightvalue", weightvalue);
        contentValues.put("wdate", date);
        db.insert("mhealthvaluesweight",null, contentValues);
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
        contentValues.put("bdate", date);
        db.insert("mhealthvaluesbpm",null, contentValues);
        return true;
    }

    //Insert new generic values into db
    public boolean insertMhealthValuesGeneric (int userid, String genericunit, String genericvalue, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("userid", userid);
        contentValues.put("genericunit", genericunit);
        contentValues.put("genericvalue", genericvalue);
        contentValues.put("gdate", date);
        db.insert("mhealthvaluesgeneric",null, contentValues);
        return true;
    }

    //Cursor for getting data from a specific user
    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from mhealthuser inner join mhealthvaluesweight on mhealthuser.userid=mhealthvaluesweight.userid inner join mhealthvaluesbpm on mhealthuser.userid=mhealthvaluesbpm.userid inner join mhealthvaluesgeneric on mhealthuser.userid=mhealthvaluesgeneric.userid where mhealthuser.userid=? ", new String[] { Integer.toString(id) } );
        return res;
    }

    //Get the row numbers total
    public int getnumberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, MHEALTH_TABLE_USER);
        return numRows;
    }

    public boolean updateMhealthUserMail (int id, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        db.update("mhealthuser", contentValues, "userid = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean updateMhealthUserPatientid (int id, String patientid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("patientid", patientid);
        db.update("mhealthuser", contentValues, "userid = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    //Delete User from db
    public Integer deleteUser (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int a = db.delete("mhealthuser",
                "userid = ? ",
                new String[] { Integer.toString(id) });

        int b = db.delete("mhealthvaluesweight",
                "userid = ? ",
                new String[] { Integer.toString(id) });
        int c = db.delete("mhealthvaluesbpm",
                "userid = ? ",
                new String[] { Integer.toString(id) });
        int d = db.delete("mhealthvaluesgeneric",
                "userid = ? ",
                new String[] { Integer.toString(id) });

        return a+b+c+d;

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

            if((res.getString(res.getColumnIndex(MHEALTH_COLUMN_PATIENT_ID)) != "") || (res.getString(res.getColumnIndex(MHEALTH_COLUMN_PATIENT_ID)) != null)){
                array_list.add(MHEALTH_COLUMN_PATIENT_ID);
                array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_PATIENT_ID)));
            }
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    //Get an Arraylist for all specific Data (Weight) from specific user
    public ArrayList<String> getAllWeightDataFromUser(int id) {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from mhealthuser left join mhealthvaluesweight on mhealthuser.userid=mhealthvaluesweight.userid where mhealthuser.userid=?", new String[] { Integer.toString(id) } );

        if (res.moveToFirst()) {
            do {

                if(res.getColumnIndex(MHEALTH_COLUMN_WEIGHT_UNIT) > 0 ){
                    array_list.add(MHEALTH_COLUMN_WEIGHT_UNIT);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_WEIGHT_UNIT)));
                }
                if(res.getColumnIndex(MHEALTH_COLUMN_WEIGHT_VALUE) > 0 ){
                    array_list.add(MHEALTH_COLUMN_WEIGHT_VALUE);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_WEIGHT_VALUE)));
                }
                if(res.getColumnIndex(MHEALTH_COLUMN_LAST_READ_TIME_WEIGHT) > 0 ){
                    array_list.add(MHEALTH_COLUMN_LAST_READ_TIME_WEIGHT);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_LAST_READ_TIME_WEIGHT)));
                }

            } while (res.moveToNext());
        }

        res.close();
        return array_list;
    }

    //Get an Arraylist for last specific Data (Weight) from specific user, some methods are following
    public ArrayList<String> getLastWeightDataFromUser(int id) {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from mhealthuser left join mhealthvaluesweight on mhealthuser.userid=mhealthvaluesweight.userid where mhealthuser.userid=?", new String[] { Integer.toString(id) } );

        if (res.moveToLast()) {
            do {

                if(res.getColumnIndex(MHEALTH_COLUMN_WEIGHT_UNIT) > 0 ){
                    array_list.add(MHEALTH_COLUMN_WEIGHT_UNIT);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_WEIGHT_UNIT)));
                }
                if(res.getColumnIndex(MHEALTH_COLUMN_WEIGHT_VALUE) > 0 ){
                    array_list.add(MHEALTH_COLUMN_WEIGHT_VALUE);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_WEIGHT_VALUE)));
                }
                if(res.getColumnIndex(MHEALTH_COLUMN_LAST_READ_TIME_WEIGHT) > 0 ){
                    array_list.add(MHEALTH_COLUMN_LAST_READ_TIME_WEIGHT);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_LAST_READ_TIME_WEIGHT)));
                }

            } while (res.moveToNext());
        }

        res.close();
        return array_list;
    }

    //Get all BPM Data from user
    public ArrayList<String> getAllBPMDataFromUser(int id) {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from mhealthuser left join mhealthvaluesbpm on mhealthuser.userid=mhealthvaluesbpm.userid where mhealthuser.userid=?", new String[] { Integer.toString(id) } );

        if (res.moveToFirst()) {
            do {

                if(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_UNIT) > 0 ){
                    array_list.add(MHEALTH_COLUMN_BLOOD_PRESSURE_UNIT);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_UNIT)));
                }
                if(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_SYSTOLIC) > 0 ){
                    array_list.add(MHEALTH_COLUMN_BLOOD_PRESSURE_SYSTOLIC);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_SYSTOLIC)));
                }
                if(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_DIASTOLIC) > 0 ){
                    array_list.add(MHEALTH_COLUMN_BLOOD_PRESSURE_DIASTOLIC);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_DIASTOLIC)));
                }
                if(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_MAP) > 0 ){
                    array_list.add(MHEALTH_COLUMN_BLOOD_PRESSURE_MAP);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_MAP)));
                }
                if(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_PULSE) > 0 ){
                    array_list.add(MHEALTH_COLUMN_BLOOD_PRESSURE_PULSE);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_PULSE)));
                }
                if(res.getColumnIndex(MHEALTH_COLUMN_LAST_READ_TIME_BPM) > 0 ){
                    array_list.add(MHEALTH_COLUMN_LAST_READ_TIME_BPM);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_LAST_READ_TIME_BPM)));
                }

            } while (res.moveToNext());
        }

        res.close();
        return array_list;
    }

    //Get last BPM Data from user
    public ArrayList<String> getLastBPMDataFromUser(int id) {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from mhealthuser left join mhealthvaluesbpm on mhealthuser.userid=mhealthvaluesbpm.userid where mhealthuser.userid=?", new String[] { Integer.toString(id) } );

        if (res.moveToLast()) {
            do {

                if(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_UNIT) > 0 ){
                    array_list.add(MHEALTH_COLUMN_BLOOD_PRESSURE_UNIT);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_UNIT)));
                }
                if(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_SYSTOLIC) > 0 ){
                    array_list.add(MHEALTH_COLUMN_BLOOD_PRESSURE_SYSTOLIC);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_SYSTOLIC)));
                }
                if(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_DIASTOLIC) > 0 ){
                    array_list.add(MHEALTH_COLUMN_BLOOD_PRESSURE_DIASTOLIC);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_DIASTOLIC)));
                }
                if(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_MAP) > 0 ){
                    array_list.add(MHEALTH_COLUMN_BLOOD_PRESSURE_MAP);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_MAP)));
                }
                if(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_PULSE) > 0 ){
                    array_list.add(MHEALTH_COLUMN_BLOOD_PRESSURE_PULSE);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_BLOOD_PRESSURE_PULSE)));
                }
                if(res.getColumnIndex(MHEALTH_COLUMN_LAST_READ_TIME_BPM) > 0 ){
                    array_list.add(MHEALTH_COLUMN_LAST_READ_TIME_BPM);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_LAST_READ_TIME_BPM)));
                }

            } while (res.moveToNext());
        }

        res.close();
        return array_list;
    }

    //Get all generic data from user
    public ArrayList<String> getAllGenericDataFromUser(int id) {
        ArrayList<String> array_list = new ArrayList<String>();
//TODO weitermahen 10.01
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from mhealthuser left join mhealthvaluesgeneric on mhealthuser.userid=mhealthvaluesgeneric.userid where mhealthuser.userid=?", new String[] { Integer.toString(id) } );

        if (res.moveToFirst()) {
            do {

                if(res.getColumnIndex(MHEALTH_COLUMN_GENERIC_UNIT) > 0 ){
                    array_list.add(MHEALTH_COLUMN_GENERIC_UNIT);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_GENERIC_UNIT)));
                }
                if(res.getColumnIndex(MHEALTH_COLUMN_GENERIC_VALUE) > 0 ){
                    array_list.add(MHEALTH_COLUMN_GENERIC_VALUE);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_GENERIC_VALUE)));
                }
                if(res.getColumnIndex(MHEALTH_COLUMN_LAST_READ_TIME_GENERIC) > 0 ){
                    array_list.add(MHEALTH_COLUMN_LAST_READ_TIME_GENERIC);
                    array_list.add(res.getString(res.getColumnIndex(MHEALTH_COLUMN_LAST_READ_TIME_GENERIC)));
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
        res.close();
        return tempid;
    }


}
