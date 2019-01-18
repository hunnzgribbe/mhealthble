package com.example.android.bluetoothlegatt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.util.Log;
import java.util.UUID;


//Sub Class for measuring the Weight of the scale correctly
public class BluetoothLeServiceWeight extends BluetoothLeService {

    private String WEIGHT_UNIT = "";
    private String WEIGHT_VALUE = "";
    private String DATE = "";
    public final static UUID UUID_WEIGHT_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.WEIGHT_MEASUREMENT);

    //Stock constructor
    public BluetoothLeServiceWeight (){ }

    //Constructor
    public BluetoothLeServiceWeight (String weightUnit, String weightValue, String date){
        this.WEIGHT_UNIT = weightUnit;
        this.WEIGHT_VALUE = weightValue;
        this.DATE = date;
    }

    //Setter methods
    public void setWEIGHT_UNIT(String weightUnit){
        this.WEIGHT_UNIT = weightUnit;
    }

    public void setWEIGHT_VALUE(String weightValue) {
        this.WEIGHT_VALUE = weightValue;
    }

    public void setDATE(String date){
        this.DATE = date;
    }

    //Getter methods
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
                    //Round:
                    value = Math.round(value*100)/100.0;
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
