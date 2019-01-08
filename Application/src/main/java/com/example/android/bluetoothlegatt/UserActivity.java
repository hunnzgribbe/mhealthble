package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.android.bluetoothlegatt.DBHelper.*;

//User Activity, accessed from the options menu
//Primary for displaying the values and changing username
public class UserActivity extends Activity{

    private DBHelper mydb;
    private TextView email;
    private TextView weightunit ;
    private TextView weightvalue;
    private TextView bpunit;
    private TextView systolic;
    private TextView diastolic;
    private TextView map;
    private TextView pulse;
    private TextView date;
    private TableLayout table;
    private Integer id;
    private String emailString;
    private String weightunitString;
    private String weightvalueString;
    private String bpunitString;
    private String systolicString;
    private String diastolicString;
    private String mapString;
    private String pulseString;
    private String dateString;

    //Gets called at opening the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting);
        mydb = new DBHelper(this);
        //Identify View-Fields and assign it
        email = findViewById(R.id.editTextEmail2);
        weightunit = findViewById(R.id.textViewWeightUnit);
        weightvalue = findViewById(R.id.textViewWeightValue);
        bpunit = findViewById(R.id.textbpunit);
        systolic = findViewById(R.id.textViewSystolicValue);
        diastolic = findViewById(R.id.textViewDiastolicValue);
        map = findViewById(R.id.textViewMapValue);
        pulse = findViewById(R.id.textViewPulseValue);
        date = findViewById(R.id.textViewDateValue);

        weightunitString = "";
        weightvalueString = "";
        bpunitString = "";
        systolicString = "";
        diastolicString = "";
        mapString = "";
        pulseString = "";
        dateString = "";

        //Get last db-user-id which was created
        id = mydb.getLastUsersId();

        //Read out the Arraylists from the db class
        //Show Email
        for (int i = 0; i < mydb.getAllUsers().size(); i++){
            switch (mydb.getAllUsers().get(i)) {
                case MHEALTH_COLUMN_EMAIL:
                    emailString = mydb.getAllUsers().get(i+1);
                    break;
                default:
                    break;
            }
        }

        //Read out Arraylist for the Data
        for (int i = 0; i < mydb.getAllDataFromUser(id).size(); i++){
            String temp = mydb.getAllDataFromUser(id).get(i);

            if (temp == MHEALTH_COLUMN_WEIGHT_UNIT){
                weightunitString += mydb.getAllDataFromUser(id).get(i+1);
                weightunitString += "\n";
            }
            else if (temp == MHEALTH_COLUMN_WEIGHT_VALUE){
                weightvalueString += mydb.getAllDataFromUser(id).get(i+1);
                weightvalueString += "\n";
            }
            else if (temp == MHEALTH_COLUMN_BLOOD_PRESSURE_UNIT){
                bpunitString += mydb.getAllDataFromUser(id).get(i+1);
                bpunitString += "\n";
            }
            else if (temp == MHEALTH_COLUMN_BLOOD_PRESSURE_SYSTOLIC){
                systolicString += mydb.getAllDataFromUser(id).get(i+1);
                systolicString += "\n";
            }
            else if (temp == MHEALTH_COLUMN_BLOOD_PRESSURE_DIASTOLIC){
                diastolicString += mydb.getAllDataFromUser(id).get(i+1);
                diastolicString += "\n";
            }
            else if (temp == MHEALTH_COLUMN_BLOOD_PRESSURE_MAP){
                mapString += mydb.getAllDataFromUser(id).get(i+1);
                mapString += "\n";
            }
            else if (temp == MHEALTH_COLUMN_BLOOD_PRESSURE_PULSE){
                pulseString += mydb.getAllDataFromUser(id).get(i+1);
                pulseString += "\n";
            }
            else if (temp == MHEALTH_COLUMN_LAST_READ_TIME){
                dateString += mydb.getAllDataFromUser(id).get(i+1);
                dateString += "\n";
            }

        }

        Button b = findViewById(R.id.buttonSaveUser2);
        b.setVisibility(View.INVISIBLE);

        table = findViewById(R.id.table_data);
        table.setVisibility(View.VISIBLE);

        this.email.setText(emailString);
        this.email.setFocusable(false);
        this.email.setClickable(false);

        this.weightvalue.setText(weightvalueString);
        this.weightvalue.setFocusable(false);
        this.weightvalue.setClickable(false);

        this.weightunit.setText(weightunitString);
        this.weightunit.setFocusable(false);
        this.weightunit.setClickable(false);

        this.bpunit.setText(bpunitString);
        this.bpunit.setFocusable(false);
        this.bpunit.setClickable(false);

        this.systolic.setText(systolicString);
        this.systolic.setFocusable(false);
        this.systolic.setClickable(false);

        this.diastolic.setText(diastolicString);
        this.diastolic.setFocusable(false);
        this.diastolic.setClickable(false);

        this.map.setText(mapString);
        this.map.setFocusable(false);
        this.map.setClickable(false);

        this.pulse.setText(pulseString);
        this.pulse.setFocusable(false);
        this.pulse.setClickable(false);

        this.date.setText(dateString);
        this.date.setFocusable(false);
        this.date.setClickable(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Initialize Optionsmeu
        getMenuInflater().inflate(R.menu.usermenu, menu);

        return true;
    }

    //User opens options menu
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            //Edit user menu item clicked:
            case R.id.edit_user:
                Button b = findViewById(R.id.buttonSaveUser2);
                b.setVisibility(View.VISIBLE);

                //Set fields
                email.setEnabled(true);
                email.setFocusableInTouchMode(true);
                email.setClickable(true);
                table.setVisibility(View.INVISIBLE);

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
            //DB gets deleted as well as app data
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

                //Clear app data:
                try {
                    // clearing app data
                    if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                        ((ActivityManager)getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
                    } else {
                        String packageName = getApplicationContext().getPackageName();
                        Runtime runtime = Runtime.getRuntime();
                        runtime.exec("pm clear "+packageName);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


}
