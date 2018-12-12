/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothlegatt;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    //Save Values in Strings:
    public final static String WEIGHT_UNIT = "";
    public final static String WEIGHT_VALUE = "";
    public final static String BLOOD_PRESSURE_UNIT = "";
    public final static String BLOOD_PRESSURE_SYSTOLIC = "";
    public final static String BLOOD_PRESSURE_DIASTOLIC = "";
    public final static String BLOOD_PRESSURE_MAP = "";
    public final static String BLOOD_PRESSURE_PULSE = "";

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    public final static UUID UUID_WEIGHT_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.WEIGHT_MEASUREMENT);

    public final static UUID UUID_BLOOD_PRESSURE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.WEIGHT_MEASUREMENT);

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        Log.v(TAG, "broadcastUpdate" + characteristic.getUuid());
        Log.v(TAG, "data is " + characteristic.getValue().toString());

        //Call Method for getting the values of the characterstic and save them
        //intent.putExtra(EXTRA_DATA, readValues(characteristic));
        readValues(intent, characteristic);
        sendBroadcast(intent);

    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }

        //Special for Weight Measurement
        //Important to activate Indication value, otherwise you wont get data from it
        if (UUID_WEIGHT_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }

        //Special for Blood Pressure Measurement
        //Important to activate Indication value, otherwise you wont get data from it
        if (UUID_BLOOD_PRESSURE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }


    /**
     * Get the values of the specific device
     *
     *
     *
     */

    public void readValues (final Intent intent,
                              final BluetoothGattCharacteristic characteristic){
        //final Intent intent = new Intent(action);
        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        }

        //Data handling for weight scale:
        else if (UUID_WEIGHT_MEASUREMENT.equals(characteristic.getUuid())) {

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
                    }
                    else {
                        convertValue = 0.01f;
                        intent.putExtra(WEIGHT_UNIT,"LBS");
                    }
                    // Unit
                    offset+=1;

                    // Value
                    double value = (double)(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset)) * convertValue;
                    //Runden:
                    value = Math.round(value*100)/100.0;
                    intent.putExtra(EXTRA_DATA, Double.toString(value));
                    intent.putExtra(WEIGHT_VALUE, Double.toString(value));
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

        //Data handling for blood pressure device:
        else if (UUID_BLOOD_PRESSURE_MEASUREMENT.equals(characteristic.getUuid())) {
            //public final String[] WEIGHT_SCALE = {"Unit:", "", "Value:", ""}
            //"Unit:", "", "Systolic:", "", "Diastolic:", "", "Mean Arterial Pressure:", "", "Pulse:", ""};
            int flag = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            String flagString = Integer.toBinaryString(flag);
            int offset=0;
            for(int index = flagString.length(); 0 < index ; index--) {
                String key = flagString.substring(index-1 , index);

                if(index == flagString.length()) {
                    if(key.equals("0")) {
                        // mmHg
                        Log.d("SN", "mmHg");
                        intent.putExtra(BLOOD_PRESSURE_UNIT,"mmHg");
                    }
                    else {
                        // kPa
                        Log.d("SN", "kPa");
                        intent.putExtra(BLOOD_PRESSURE_UNIT,"kPa");
                    }
                    // Unit
                    offset+=1;
                    Log.d("SN", "Systolic :"+String.format("%f", characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset)));
                    intent.putExtra(BLOOD_PRESSURE_SYSTOLIC, Double.toString(Math.round(characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset)*100)/100.0));
                    offset+=2;
                    Log.d("SN", "Diastolic :"+String.format("%f", characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset)));
                    intent.putExtra(BLOOD_PRESSURE_DIASTOLIC, Double.toString(Math.round(characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset)*100)/100.0));
                    offset+=2;
                    Log.d("SN", "Mean Arterial Pressure :"+String.format("%f", characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset)));
                    intent.putExtra(BLOOD_PRESSURE_MAP, Double.toString(Math.round(characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset)*100)/100.0));
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
                   /* else {
                        // 日時が存在しない場合、現在日時を格納する。
                        Calendar calendar = Calendar.getInstance(Locale.getDefault());
                        bundle.putInt(KEY_YEAR, calendar.get(Calendar.YEAR));
                        bundle.putInt(KEY_MONTH, calendar.get(Calendar.MONTH)+1);
                        bundle.putInt(KEY_DAY, calendar.get(Calendar.DAY_OF_MONTH));
                        bundle.putInt(KEY_HOURS, calendar.get(Calendar.HOUR));
                        bundle.putInt(KEY_MINUTES, calendar.get(Calendar.MINUTE));
                        bundle.putInt(KEY_SECONDS, calendar.get(Calendar.SECOND));
                    }*/
                }
                else if(index == flagString.length()-2) {
                    if(key.equals("1")) {
                        // Pulse Rate
                        Log.d("SN", "Pulse Rate :"+String.format("%f", characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset)));
                        intent.putExtra(BLOOD_PRESSURE_PULSE, Double.toString(Math.round(characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset)*100)/100.0));
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
        }

        //Data handling for all devices, if not specified
        else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
    }

}
