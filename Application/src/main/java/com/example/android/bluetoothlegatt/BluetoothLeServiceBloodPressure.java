package com.example.android.bluetoothlegatt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.util.Log;
import java.util.UUID;


//Sub Class for measuring the Blood Pressure correctly


public class BluetoothLeServiceBloodPressure extends BluetoothLeService {

    private String BLOOD_PRESSURE_UNIT = "";
    private String BLOOD_PRESSURE_SYSTOLIC = "";
    private String BLOOD_PRESSURE_DIASTOLIC = "";
    private String BLOOD_PRESSURE_MAP = "";
    private String BLOOD_PRESSURE_PULSE = "";
    private String DATE = "";
    public final static UUID UUID_BLOOD_PRESSURE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.BLOOD_PRESSURE_MEASUREMENT);


    //Stock constructor
    public BluetoothLeServiceBloodPressure (){ }

    //Constructor
    public BluetoothLeServiceBloodPressure (String bloodunit, String systolic, String diastolic, String map, String pulse, String date){
        this.BLOOD_PRESSURE_UNIT = bloodunit;
        this.BLOOD_PRESSURE_SYSTOLIC = systolic;
        this.BLOOD_PRESSURE_DIASTOLIC = diastolic;
        this.BLOOD_PRESSURE_MAP = map;
        this.BLOOD_PRESSURE_PULSE = pulse;
        this.DATE = date;
    }

    //Setter methods
    public void setBP_UNIT(String bloodunit){
        this.BLOOD_PRESSURE_UNIT = bloodunit;
    }

    public void setBP_SYSTOLIC(String systolic) {
        this.BLOOD_PRESSURE_SYSTOLIC = systolic;
    }

    public void setBP_DIASTOLIC(String diastolic) {
        this.BLOOD_PRESSURE_DIASTOLIC = diastolic;
    }

    public void setBP_MAP(String map) {
        this.BLOOD_PRESSURE_MAP = map;
    }

    public void setBP_PULSE(String pulse) {
        this.BLOOD_PRESSURE_PULSE = pulse;
    }

    public void setDATE(String date){
        this.DATE = date;
    }


    //Getter methods
    public String getBP_UNIT(){
        return BLOOD_PRESSURE_UNIT;
    }

    public String getBP_SYSTOLIC(){
        return BLOOD_PRESSURE_SYSTOLIC;
    }

    public String getBP_DIASTOLIC(){
        return BLOOD_PRESSURE_DIASTOLIC;
    }

    public String getBP_MAP(){
        return BLOOD_PRESSURE_MAP;
    }

    public String getBP_PULSE(){
        return BLOOD_PRESSURE_PULSE;
    }

    public String getDate(){
        return DATE;
    }


    //read values of device
    public void readValues (final Intent intent, final BluetoothGattCharacteristic characteristic){

        //Data handling for blood pressure device:
        if (UUID_BLOOD_PRESSURE_MEASUREMENT.equals(characteristic.getUuid())) {

            int flag = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            String flagString = Integer.toBinaryString(flag);
            int offset=0;
            boolean mmhgunit = false;
            String systolic = "";
            String diastolic = "";
            String map = "";
            String pulse = "";
            double pulscheck = 0;

            for(int index = flagString.length(); 0 < index ; index--) {
                String key = flagString.substring(index-1 , index);

                if(index == flagString.length()) {
                    if(key.equals("0")) {
                        // mmHg
                        Log.d("SN", "mmHg");
                        intent.putExtra(BLOOD_PRESSURE_UNIT,"mmHg");
                        mmhgunit = true;
                        //this.setBP_UNIT("mmHg");
                    }
                    else {
                        // kPa
                        Log.d("SN", "kPa");
                        mmhgunit = false;
                        intent.putExtra(BLOOD_PRESSURE_UNIT,"kPa");
                        //this.setBP_UNIT("kPa");
                    }
                    // Unit converting
                    offset+=1;
                    systolic = Double.toString(Math.round(characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset)*100)/100.0);
                    Log.d("SN", "Systolic :"+String.format("%f", characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset)));
                    intent.putExtra(BLOOD_PRESSURE_SYSTOLIC, systolic);

                    offset+=2;
                    diastolic = Double.toString(Math.round(characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset)*100)/100.0);
                    Log.d("SN", "Diastolic :"+String.format("%f", characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset)));
                    intent.putExtra(BLOOD_PRESSURE_DIASTOLIC, diastolic);


                    offset+=2;
                    map = Double.toString(Math.round(characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset)*100)/100.0);
                    Log.d("SN", "Mean Arterial Pressure :"+String.format("%f", characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset)));
                    intent.putExtra(BLOOD_PRESSURE_MAP, map);

                    offset+=2;
                }
                else if(index == flagString.length()-1) {
                    if(key.equals("1")) {
                        // Time Stamp
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
                        // Pulse Rate
                        pulse = Double.toString(Math.round(characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset)*100)/100.0);
                        Log.d("SN", "Pulse Rate :"+String.format("%f", characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset)));
                        intent.putExtra(BLOOD_PRESSURE_PULSE, pulse);
                        pulscheck = (Math.round(characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset)*100)/100.0);
                        offset+=2;

                    }
                }
                else if(index == flagString.length()-3) {
                    // UserID
                }
              /*  else if(index == flagString.length()-4) {
                    // Measurement Status Flag
                    int statusFalg = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                    String statusFlagString = Integer.toBinaryString(statusFalg);
                    for(int i = statusFlagString.length(); 0 < i ; i--) {
                        String status = statusFlagString.substring(i-1 , i);
                        if(i == statusFlagString.length()) {
                            bundle.putInt(KEY_BODY_MOVEMENT_DETECTION, (status.endsWith("1"))? 1 : 0 );
                        }
                        else if(i == statusFlagString.length() - 1) {
                            bundle.putInt(KEY_CUFF_FIT_DETECTION, (status.endsWith("1"))? 1 : 0 );
                        }
                        else if(i == statusFlagString.length() - 2) {
                            bundle.putInt(KEY_IRREGULAR_PULSE_DETECTION, (status.endsWith("1"))? 1 : 0 );
                        }
                        else if(i == statusFlagString.length() - 3) {
                            i--;
                            String secondStatus = statusFlagString.substring(i-1 , i);
                            if(status.endsWith("1") && secondStatus.endsWith("0")) {
                                bundle.putInt(KEY_PULSE_RATE_RANGE_DETECTION, 1);
                            }
                            else if(status.endsWith("0") && secondStatus.endsWith("1")) {
                                bundle.putInt(KEY_PULSE_RATE_RANGE_DETECTION, 2);
                            }
                            else if(status.endsWith("1") && secondStatus.endsWith("1")) {
                                bundle.putInt(KEY_PULSE_RATE_RANGE_DETECTION, 3);
                            }
                            else {
                                bundle.putInt(KEY_PULSE_RATE_RANGE_DETECTION, 0);
                            }
                        }
                        else if(i == statusFlagString.length() - 5) {
                            bundle.putInt(KEY_MEASUREMENT_POSITION_DETECTION,(status.endsWith("1"))? 1 : 0);
                        }
                    }
                }*/

            }
            //Save last values (3 pair values per reading, the last one is the actual value to use)
            if (pulscheck < 500){
                if(mmhgunit){ this.setBP_UNIT("mmHg");}
                else {this.setBP_UNIT("kPa");}
                this.setBP_SYSTOLIC(systolic);
                this.setBP_DIASTOLIC(diastolic);
                this.setBP_MAP(map);
                this.setBP_PULSE(pulse);
            }


        }

    }

}
