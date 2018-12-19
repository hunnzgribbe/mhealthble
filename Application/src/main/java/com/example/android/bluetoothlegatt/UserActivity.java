package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserActivity extends Activity{

    DBHelper mydb;
    private int id;
    TextView email;
    TextView weightunit ;
    TextView weightvalue;
    TextView systolic;
    TextView diastolic;
    TextView map;
    TextView pulse;
    TextView date;
    TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting);
        //Identify View-Fields and assign it
        email = findViewById(R.id.editTextEmail2);
        weightunit = findViewById(R.id.textViewWeightUnit);
        weightvalue = findViewById(R.id.textViewWeightValue);
        systolic = findViewById(R.id.textViewSystolicValue);
        diastolic = findViewById(R.id.textViewDiastolicValue);
        map = findViewById(R.id.textViewMapValue);
        pulse = findViewById(R.id.textViewPulseValue);
        date = findViewById(R.id.textViewDateValue);

        mydb = new DBHelper(this);
        //Get last db-user-id which was created
        //id = mydb.getLastAddedRowId();
        id = mydb.getnumberOfRows();

        Cursor rs = mydb.getData(id);
        rs.moveToFirst();
        String email = rs.getString(rs.getColumnIndex(DBHelper.MHEALTHUSERS_COLUMN_EMAIL));
        String weightvalue = rs.getString(rs.getColumnIndex(Double.toString(DBHelper.MHEALTHUSERS_COLUMN_WEIGHT_VALUE)));
        String weightunit = rs.getString(rs.getColumnIndex(DBHelper.MHEALTHUSERS_COLUMN_WEIGHT_UNIT));
        String systolic = rs.getString(rs.getColumnIndex(Double.toString(DBHelper.MHEALTHUSERS_COLUMN_BLOOD_PRESSURE_SYSTOLIC)));
        String diastolic = rs.getString(rs.getColumnIndex(Double.toString(DBHelper.MHEALTHUSERS_COLUMN_BLOOD_PRESSURE_DIASTOLIC)));
        String map = rs.getString(rs.getColumnIndex(Double.toString(DBHelper.MHEALTHUSERS_COLUMN_BLOOD_PRESSURE_MAP)));
        String pulse = rs.getString(rs.getColumnIndex(Double.toString(DBHelper.MHEALTHUSERS_COLUMN_BLOOD_PRESSURE_PULSE)));
        String date = rs.getString(rs.getColumnIndex(DBHelper.MHEALTHUSERS_COLUMN_LAST_READ_TIME));

        if (!rs.isClosed()) {
            rs.close();
        }

            Button b = findViewById(R.id.buttonSaveUser2);
            b.setVisibility(View.INVISIBLE);

            table = findViewById(R.id.table_data);
            table.setVisibility(View.VISIBLE);

            this.email.setText(email);
            this.email.setFocusable(false);
            this.email.setClickable(false);

            this.weightvalue.setText(weightvalue);
            this.weightvalue.setFocusable(false);
            this.weightvalue.setClickable(false);

            this.weightunit.setText(weightunit);
            this.weightunit.setFocusable(false);
            this.weightunit.setClickable(false);

            this.systolic.setText(systolic);
            this.systolic.setFocusable(false);
            this.systolic.setClickable(false);

            this.diastolic.setText(diastolic);
            this.diastolic.setFocusable(false);
            this.diastolic.setClickable(false);

            this.map.setText(map);
            this.map.setFocusable(false);
            this.map.setClickable(false);

            this.pulse.setText(pulse);
            this.pulse.setFocusable(false);
            this.pulse.setClickable(false);

            this.date.setText(date);
            this.date.setFocusable(false);
            this.date.setClickable(false);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.usermenu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            //Edit user menu item clicked:
            case R.id.edit_user:
                Button b = findViewById(R.id.buttonSaveUser2);
                b.setVisibility(View.VISIBLE);

                email.setEnabled(true);
                email.setFocusableInTouchMode(true);
                email.setClickable(true);

                //Click listener for save button
                b.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        if (email.getText().toString() != null){
                            //save it and go to main screen
                            mydb.updateMhealthUserMail(id, email.getText().toString());
                            Toast.makeText(getApplicationContext(), "Updated E-Mail!",
                                    Toast.LENGTH_SHORT).show();

                        } else{
                            Toast.makeText(getApplicationContext(), "You have to enter an E-Mail",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                return true;

            //Delete user menu item clicked:
            case R.id.delete_user:

                //Show Warning message
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.delete_contact)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mydb.deleteUser(id);
                                Toast.makeText(getApplicationContext(), "User deleted successfully",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),UserAddActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                AlertDialog d = builder.create();
                d.setTitle("Are you sure?");
                d.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

  /*  public void run(View view) {
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            int Value = extras.getInt("id");
            if(Value>0){
                if(mydb.updateMhealthUserMail(id, email.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),DeviceScanActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(getApplicationContext(), "not Updated", Toast.LENGTH_SHORT).show();
                }
            } else{
                if(mydb.insertMhealthUserMail(email.getText().toString())){
                    Toast.makeText(getApplicationContext(), "done",
                            Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getApplicationContext(), "not done",
                            Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(),DeviceScanActivity.class);
                startActivity(intent);
            }
        }
    }
    */

}
