package com.example.android.bluetoothlegatt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;


//Class for measuring the Weight of the scale correctly
public class BluetoothLeServiceWeight extends BluetoothLeService {

    public String WEIGHT_UNIT = "";
    public String WEIGHT_VALUE = "";
    public String DATE = "";
    public final static UUID UUID_WEIGHT_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.WEIGHT_MEASUREMENT);
    private int from_Where_I_Am_Coming = 0;
    private DBHelper mydb ;
    private int id_To_Update = 0;

    //Standardkonstruktur
    public BluetoothLeServiceWeight (){
        mydb = new DBHelper(this);
    }

    //Konstruktor
    public BluetoothLeServiceWeight (String weightUnit, String weightValue, String date){
        this.WEIGHT_UNIT = weightUnit;
        this.WEIGHT_VALUE = weightValue;
        this.DATE = date;
        mydb = new DBHelper(this);
    }

    public void setWEIGHT_UNIT(String weightUnit){
        this.WEIGHT_UNIT = weightUnit;
    }

    public void setWEIGHT_VALUE(String weightValue) {
        this.WEIGHT_VALUE = weightValue;
    }

    public void setDATE(String date){
        this.DATE = date;
    }

    public String getWeightUnit(){
        return WEIGHT_UNIT;
    }

    public String getWeightValue(){
        return WEIGHT_VALUE;
    }

    public String getDate(){
        return DATE;
    }

    //read values of device
    public void readValues (final Intent intent, final BluetoothGattCharacteristic characteristic){

        if (UUID_WEIGHT_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            String flagString = Integer.toBinaryString(flag);
            int offset=0;
            for(int index = flagString.length(); 0 < index ; index--) {
                String key = flagString.substring(index-1 , index);

                if(index == flagString.length()) {
                    double convertValue = 0;
                    if(key.equals("0")) {
                        convertValue = 0.005f;
                        intent.putExtra(WEIGHT_UNIT,"KG");
                        this.setWEIGHT_UNIT("KG");
                    }
                    else {
                        convertValue = 0.01f;
                        intent.putExtra(WEIGHT_UNIT,"LBS");
                        this.setWEIGHT_UNIT("LBS");
                    }
                    // Unit
                    offset+=1;

                    // Value
                    double value = (double)(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset)) * convertValue;
                    //Runden:
                    value = Math.round(value*100)/100.0;
                    //test: intent.putExtra(EXTRA_DATA, Double.toString(value));
                    intent.putExtra(Double.toString(value), WEIGHT_VALUE);
                    this.setWEIGHT_VALUE(Double.toString(value));

                    Log.d("Weight", "V :" + value);
                    offset+=2;
                }
                else if(index == flagString.length()-1) {
                    if(key.equals("1")) {

                        Log.d("SN", "Y :"+String.format("%04d", characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset)));

                        offset+=2;
                        Log.d("SN", "M :"+String.format("%02d", characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset)));

                        offset+=1;
                        Log.d("SN", "D :"+String.format("%02d", characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset)));

                        offset+=1;

                        Log.d("SN", "H :"+String.format("%02d", characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset)));

                        offset+=1;
                        Log.d("SN", "M :"+String.format("%02d", characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset)));

                        offset+=1;
                        Log.d("SN", "S :"+String.format("%02d", characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset)));

                        offset+=1;
                    }

                }
                else if(index == flagString.length()-2) {
                    if(key.equals("1")) {
                        Log.d("SN", "ID :"+String.format("%02d", characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset)));
                        offset+=1;
                    }
                }
                else if(index == flagString.length()-3) {
                    if(key.equals("1")) {
                        // BMI and Height
                    }
                }
            }

        }

    }




}
