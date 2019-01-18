package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import static com.example.android.bluetoothlegatt.DBHelper.*;

//User Activity, accessed from the options menu
//Primary for displaying the values (grahpics and table) and changing username

public class UserActivity extends Activity{

    private DBHelper mydb;

    private TextView email;
    private TextView weightunit;
    private TextView weightvalue;
    private TextView bpunit;
    private TextView systolic;
    private TextView diastolic;
    private TextView map;
    private TextView pulse;
    private TextView datew;
    private TextView dateb;

    private TableLayout table;

    private Integer id;
    private String emailString;
    private String weightunitString;
    private String weightunitString2;
    private String weightvalueString;
    private String weightvalueString2;
    private String bpunitString;
    private String bpunitString2;
    private String systolicString;
    private String systolicString2;
    private String diastolicString;
    private String diastolicString2;
    private String mapString;
    private String mapString2;
    private String pulseString;
    private String pulseString2;
    private String genericUnitString;
    private String genericValueString;
    private String wdateString;
    private String bdateString;
    private String gdateString;
    private String wdateString2;
    private String bdateString2;
    private String gdateString2;
    private boolean weightunitkg;
    private boolean bpmunithmmhg;

    private int counter1;
    private int counter2;
    private int counter3;

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
        datew = findViewById(R.id.textViewWeightDate);
        dateb = findViewById(R.id.textViewbpmDate);
        GraphView graph1 = (GraphView) findViewById(R.id.graphweight);
        GraphView graph2 = (GraphView) findViewById(R.id.graphsystolic);
        GraphView graph3 = (GraphView) findViewById(R.id.graphdiastolic);
        GraphView graph4 = (GraphView) findViewById(R.id.graphpulse);
        GraphView graph5 = (GraphView) findViewById(R.id.graphgeneric);

        //Set variables
        weightunitString = "";
        weightunitString2 = "";
        weightvalueString = "";
        weightvalueString2 = "";
        bpunitString = "";
        systolicString = "";
        diastolicString = "";
        mapString = "";
        pulseString = "";
        genericUnitString = "";
        genericValueString = "";
        bpunitString2 = "";
        systolicString2 = "";
        diastolicString2 = "";
        mapString2 = "";
        pulseString2 = "";
        wdateString = "";
        bdateString = "";
        gdateString = "";
        wdateString2 = "";
        bdateString2 = "";
        gdateString2 = "";
        weightunitkg = false;
        bpmunithmmhg = false;
        counter1 = 0;
        counter2 = 0;
        counter3 = 0;

        //Get last db-user-id which was created
        id = mydb.getLastUsersId();


        //Set Graphs for the data
        BarGraphSeries<DataPoint> weightdata = new BarGraphSeries<>();
        BarGraphSeries<DataPoint> bpmdata1 = new BarGraphSeries<>();
        BarGraphSeries<DataPoint> bpmdata2 = new BarGraphSeries<>();
        BarGraphSeries<DataPoint> bpmdata3 = new BarGraphSeries<>();
        BarGraphSeries<DataPoint> genericdata = new BarGraphSeries<>();

        //Read out the Arraylists from the db class
        //Show Email
        for (int i = 0; i < mydb.getAllUsers().size(); i++){
            String temp = mydb.getAllUsers().get(i);

            if (( temp == MHEALTH_COLUMN_EMAIL)  && (mydb.getAllUsers().get(i+1) != null) && (mydb.getAllUsers().get(i+1).length() > 0)){
                emailString = mydb.getAllUsers().get(i+1);
            }
        }

        counter1 = 0;
        //Read out Arraylist for the WeightData & fill in the datapoint for the graphs
        for (int i = 0; i < mydb.getAllWeightDataFromUser(id).size(); i++){
            String temp = mydb.getAllWeightDataFromUser(id).get(i);

            if ((temp == MHEALTH_COLUMN_WEIGHT_UNIT) && (mydb.getAllWeightDataFromUser(id).get(i+1) != null)&& (mydb.getAllWeightDataFromUser(id).get(i+1).length() > 0)){
                weightunitString = mydb.getAllWeightDataFromUser(id).get(i+1);
                //for table:
                weightunitString2 += weightunitString +"\n";

                if (weightunitString.startsWith("K")){
                    weightunitkg = true;
                } else{ weightunitkg = false; }

            }
            else if ((temp == MHEALTH_COLUMN_WEIGHT_VALUE) && (mydb.getAllWeightDataFromUser(id).get(i+1) != null) && (mydb.getAllWeightDataFromUser(id).get(i+1).length() > 0)){
                counter1++;
                weightvalueString = mydb.getAllWeightDataFromUser(id).get(i+1);
                weightvalueString2 += weightvalueString +"\n";
                DataPoint point = new DataPoint(counter1, Double.parseDouble(weightvalueString));
                weightdata.appendData(point, true, counter1);

            }

            else if ((temp == MHEALTH_COLUMN_LAST_READ_TIME_WEIGHT) && (mydb.getAllWeightDataFromUser(id).get(i+1) != null) && (mydb.getAllWeightDataFromUser(id).get(i+1).length() > 0)){
                wdateString = mydb.getAllWeightDataFromUser(id).get(i+1);
                wdateString2 += wdateString +"\n";
                //DataPoint point = new DataPoint(i, Double.parseDouble(wdateString));
                //weightdata.appendData(point, true, i);

            }

        }

        counter1 = 0;
        counter2 = 0;
        counter3 = 0;
        //Read out Arraylist for the BPMData & fill in the datapoint for the graphs
        for (int i = 0; i < mydb.getAllBPMDataFromUser(id).size(); i++){
            String temp = mydb.getAllBPMDataFromUser(id).get(i);

            if ((temp == MHEALTH_COLUMN_BLOOD_PRESSURE_UNIT) && (mydb.getAllBPMDataFromUser(id).get(i+1) != null) && (mydb.getAllBPMDataFromUser(id).get(i+1).length() > 0)){
                bpunitString = mydb.getAllBPMDataFromUser(id).get(i+1);
                bpunitString2 += bpunitString +"\n";
                if (bpunitString.startsWith("m")){ bpmunithmmhg = true;
                }
                else{ bpmunithmmhg = false; }
            }
            else if ((temp == MHEALTH_COLUMN_BLOOD_PRESSURE_SYSTOLIC) && (mydb.getAllBPMDataFromUser(id).get(i+1) != null) && (mydb.getAllBPMDataFromUser(id).get(i+1).length() > 0)){
                counter1++;
                systolicString = mydb.getAllBPMDataFromUser(id).get(i+1);
                systolicString2 += systolicString +"\n";
                DataPoint point = new DataPoint(counter1, Double.parseDouble(systolicString));
                bpmdata1.appendData(point, true, counter1);

            }
            else if ((temp == MHEALTH_COLUMN_BLOOD_PRESSURE_DIASTOLIC) && (mydb.getAllBPMDataFromUser(id).get(i+1) != null) && (mydb.getAllBPMDataFromUser(id).get(i+1).length() > 0)){
                counter2++;
                diastolicString = mydb.getAllBPMDataFromUser(id).get(i+1);
                diastolicString2 += diastolicString +"\n";
                DataPoint point = new DataPoint(counter2, Double.parseDouble(diastolicString));
                bpmdata2.appendData(point, true, counter2);

            }
            /* //Not relevant
            else if ((temp == MHEALTH_COLUMN_BLOOD_PRESSURE_MAP) && (mydb.getAllBPMDataFromUser(id).get(i+1) != null) && (mydb.getAllBPMDataFromUser(id).get(i+1).length() > 0)){
                mapString = mydb.getAllDataFromUser(id).get(i+1);
                dp2[i] = new DataPoint(i, Integer.parseInt(mapString));
                DataPoint point = new DataPoint(i, Double.parseDouble(mapString));
                bpmdata4.appendData(point, true, i);
                //mapString += "\n";
            }
            */

            else if ((temp == MHEALTH_COLUMN_BLOOD_PRESSURE_PULSE) && (mydb.getAllBPMDataFromUser(id).get(i+1) != null) && (mydb.getAllBPMDataFromUser(id).get(i+1).length() > 0)){
                counter3++;
                pulseString = mydb.getAllBPMDataFromUser(id).get(i+1);
                pulseString2 += pulseString +"\n";
                DataPoint point = new DataPoint(counter3, Double.parseDouble(pulseString));
                bpmdata3.appendData(point, true, counter3);
            }

            else if ((temp == MHEALTH_COLUMN_LAST_READ_TIME_BPM) && (mydb.getAllBPMDataFromUser(id).get(i+1) != null) && (mydb.getAllBPMDataFromUser(id).get(i+1).length() > 0)){
                bdateString = mydb.getAllBPMDataFromUser(id).get(i+1);
                bdateString2 += bdateString +"\n";
                //DataPoint point = new DataPoint(i, Double.parseDouble(bdateString));
                //bpmdata1.appendData(point, true, i);
            }


        }

        counter1 = 0;
        //Read out Arraylist for the GenericData & fill in the datapoint for the graphs
        for (int i = 0; i < mydb.getAllGenericDataFromUser(id).size(); i++){
            String temp = mydb.getAllGenericDataFromUser(id).get(i);

            if ((temp == MHEALTH_COLUMN_GENERIC_UNIT) && (mydb.getAllGenericDataFromUser(id).get(i+1) != null) && (mydb.getAllGenericDataFromUser(id).get(i+1).length() > 0)){
                genericUnitString = mydb.getAllGenericDataFromUser(id).get(i+1);
            }
            else if ((temp == MHEALTH_COLUMN_GENERIC_VALUE) && (mydb.getAllGenericDataFromUser(id).get(i+1) != null) && (mydb.getAllGenericDataFromUser(id).get(i+1).length() > 0)) {
                counter1++;
                genericValueString = mydb.getAllGenericDataFromUser(id).get(i + 1);
                DataPoint point = new DataPoint(counter1, Double.parseDouble(genericValueString));
                genericdata.appendData(point, true, counter1);
            }
            /*
            else if ((temp == MHEALTH_COLUMN_LAST_READ_TIME_GENERIC) && (mydb.getAllGenericDataFromUser(id).get(i+1) != null) && (mydb.getAllGenericDataFromUser(id).get(i+1).length() > 0)){
                gdateString = mydb.getAllGenericDataFromUser(id).get(i+1);
                DataPoint point = new DataPoint(i, Double.parseDouble(gdateString));
                genericdata.appendData(point, true, i);
                dp3[i] = new DataPoint(i, Integer.parseInt(gdateString));
                //dateString += "\n";
            }
            */

        }

        //Show Graph:
        //Graph titles:
        if (weightunitkg){
            weightdata.setTitle("Weight in KG");
        }else{weightdata.setTitle("Weight in LBS"); }

        if (bpmunithmmhg){
            bpmdata1.setTitle("Systolic in "+bpunitString);
            bpmdata2.setTitle("Diastolic in "+bpunitString);
            bpmdata3.setTitle("Pulse in BPM");
        }else{
            bpmdata1.setTitle("Systolic in kPa");
            bpmdata2.setTitle("Diastolic in kPa");
            bpmdata3.setTitle("Pulse in BPM");}

        genericdata.setTitle("Generic Device");

        //Add Data to graphs
        graph1.addSeries(weightdata);
        graph2.addSeries(bpmdata1);
        graph3.addSeries(bpmdata2);
        graph4.addSeries(bpmdata3);
        graph5.addSeries(genericdata);

        //Set graph parameters
        graph1.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph1.getLegendRenderer().setVisible(true);
        graph1.getLegendRenderer().setTextSize(25);
        graph1.getLegendRenderer().setTextColor(Color.BLACK);
        graph1.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph1.getViewport().setScalable(true);

        graph2.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph2.getLegendRenderer().setVisible(true);
        graph2.getLegendRenderer().setTextSize(25);
        graph2.getLegendRenderer().setTextColor(Color.BLACK);
        graph2.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph2.getViewport().setScalable(true);

        graph3.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph3.getLegendRenderer().setVisible(true);
        graph3.getLegendRenderer().setTextSize(25);
        graph3.getLegendRenderer().setTextColor(Color.BLACK);
        graph3.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph3.getViewport().setScalable(true);

        graph4.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph4.getLegendRenderer().setVisible(true);
        graph4.getLegendRenderer().setTextSize(25);
        graph4.getLegendRenderer().setTextColor(Color.BLACK);
        graph4.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph4.getViewport().setScalable(true);

        graph5.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph5.getLegendRenderer().setVisible(true);
        graph5.getLegendRenderer().setTextSize(25);
        graph5.getLegendRenderer().setTextColor(Color.BLACK);
        graph5.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph5.getViewport().setScalable(true);


        weightdata.setSpacing(100);
        bpmdata1.setSpacing(100);
        bpmdata2.setSpacing(100);
        bpmdata3.setSpacing(100);
        genericdata.setSpacing(100);

        weightdata.setDrawValuesOnTop(true);
        bpmdata1.setDrawValuesOnTop(true);
        bpmdata2.setDrawValuesOnTop(true);
        bpmdata3.setDrawValuesOnTop(true);
        genericdata.setDrawValuesOnTop(true);

        weightdata.setValuesOnTopColor(Color.RED);
        bpmdata1.setValuesOnTopColor(Color.RED);
        bpmdata2.setValuesOnTopColor(Color.RED);
        bpmdata3.setValuesOnTopColor(Color.RED);
        genericdata.setValuesOnTopColor(Color.RED);


        //Other options: Save button and table
        Button b = findViewById(R.id.buttonSaveUser2);
        b.setVisibility(View.INVISIBLE);

        table = findViewById(R.id.table_data);
        table.setVisibility(View.VISIBLE);

        this.email.setText(emailString);
        this.email.setFocusable(false);
        this.email.setClickable(false);

        this.weightvalue.setText(weightvalueString2);
        this.weightvalue.setFocusable(false);
        this.weightvalue.setClickable(false);

        this.weightunit.setText(weightunitString2);
        this.weightunit.setFocusable(false);
        this.weightunit.setClickable(false);

        this.bpunit.setText(bpunitString2);
        this.bpunit.setFocusable(false);
        this.bpunit.setClickable(false);

        this.systolic.setText(systolicString2);
        this.systolic.setFocusable(false);
        this.systolic.setClickable(false);

        this.diastolic.setText(diastolicString2);
        this.diastolic.setFocusable(false);
        this.diastolic.setClickable(false);

        this.map.setText(mapString2);
        this.map.setFocusable(false);
        this.map.setClickable(false);

        this.pulse.setText(pulseString2);
        this.pulse.setFocusable(false);
        this.pulse.setClickable(false);

        this.datew.setText(wdateString2);
        this.datew.setFocusable(false);
        this.datew.setClickable(false);

        this.dateb.setText(bdateString2);
        this.dateb.setFocusable(false);
        this.dateb.setClickable(false);

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

            //FHIR entry clicked:
            case R.id.menu_fhir:
                Intent intent = new Intent(this, PatientActivity.class);
                this.startActivity(intent);
                return true;

            //Edit user menu entry clicked:
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
                        if (email.getText().toString().length() > 0){
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

            //Delete user menu entry clicked:
            //DB gets deleted as well as app data
            case R.id.delete_user:

                //Show Warning message
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage(R.string.delete_contact);
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int idd) {
                                //Delete DB
                                mydb.deleteUser(id);
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
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int idd) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


}
