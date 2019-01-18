package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Quantity;
import static com.example.android.bluetoothlegatt.DBHelper.MHEALTH_COLUMN_BLOOD_PRESSURE_DIASTOLIC;
import static com.example.android.bluetoothlegatt.DBHelper.MHEALTH_COLUMN_BLOOD_PRESSURE_PULSE;
import static com.example.android.bluetoothlegatt.DBHelper.MHEALTH_COLUMN_BLOOD_PRESSURE_SYSTOLIC;
import static com.example.android.bluetoothlegatt.DBHelper.MHEALTH_COLUMN_BLOOD_PRESSURE_UNIT;
import static com.example.android.bluetoothlegatt.DBHelper.MHEALTH_COLUMN_EMAIL;
import static com.example.android.bluetoothlegatt.DBHelper.MHEALTH_COLUMN_LAST_READ_TIME_BPM;
import static com.example.android.bluetoothlegatt.DBHelper.MHEALTH_COLUMN_LAST_READ_TIME_WEIGHT;
import static com.example.android.bluetoothlegatt.DBHelper.MHEALTH_COLUMN_PATIENT_ID;
import static com.example.android.bluetoothlegatt.DBHelper.MHEALTH_COLUMN_WEIGHT_UNIT;
import static com.example.android.bluetoothlegatt.DBHelper.MHEALTH_COLUMN_WEIGHT_VALUE;

//Activity for patients screen, from here you can change patientid, server and upload data to fhir server

public class PatientActivity  extends Activity {

    private DBHelper mydb;

    private TextView patientid;
    private TextView server;


    private int id;
    private int countData;
    private String emailString;
    private String patientidString;
    private String weightunitString;
    private String weightvalueString;
    private String bpunitString;
    private String systolicString;
    private String diastolicString;
    private String pulseString;
    private String wdateString;
    private String bdateString;
    private String serverurl;
    private Patient patient;
    private Observation observationw;
    private Observation observationb1;
    private Observation observationb2;
    private Observation observationb3;
    private Observation observationb4;
    private PatientFhirHelper fhirHelper;
    private boolean weightset1;
    private boolean weightset2;

    //Gets called at opening the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_activity_fhir);
        mydb = new DBHelper(this);

        //Get last db-user-id which was created
        id = mydb.getLastUsersId();

        //FHIR components:
        //Define Patient
        patient = new Patient();
        fhirHelper = new PatientFhirHelper();
        // Create an Observation instance
        observationw = new Observation();
        observationb1 = new Observation();
        observationb2 = new Observation();
        observationb3 = new Observation();
        observationb4 = new Observation();

        //Get saved FhirServerURL:
        serverurl=fhirHelper.getServer();

        //Identify View-Fields and assign it
        patientid = findViewById(R.id.editTextPatientID);
        server = findViewById(R.id.editTextServer);

        //Set variables
        weightunitString = "";
        weightvalueString = "";
        wdateString = "";
        bpunitString = "";
        systolicString = "";
        diastolicString = "";
        pulseString = "";
        bdateString = "";
        patientidString = "";
        countData = 0;
        weightset1 = false;
        weightset2 = false;

        //Define buttons
        Button bpatientid = findViewById(R.id.buttonSavePatientID);
        bpatientid.setVisibility(View.INVISIBLE);
        Button bserver = findViewById(R.id.buttonSaveServer);
        bserver.setVisibility(View.INVISIBLE);
        Button bupload = findViewById(R.id.buttonUploadFHIR);
        bupload.setVisibility(View.INVISIBLE);

        //Read out the Arraylists from the db class
        //Get email and patient id
        for (int i = 0; i < mydb.getAllUsers().size(); i++){
            String temp = mydb.getAllUsers().get(i);

            if (( temp == MHEALTH_COLUMN_EMAIL)  && (mydb.getAllUsers().get(i+1) != null) && (mydb.getAllUsers().get(i+1).length() > 0)){
                emailString = mydb.getAllUsers().get(i+1);
            }
            else if (( temp == MHEALTH_COLUMN_PATIENT_ID) && (mydb.getAllUsers().get(i+1) != null) && (mydb.getAllUsers().get(i+1).length() > 0)){
                patientidString = mydb.getAllUsers().get(i+1);
                patient.addIdentifier().setSystem("urn:system").setValue(patientidString);
            }
        }

        //Give the patient the name from the databaseusername
        patient.addName().addGiven(emailString);

        //Set observation for weight data
        observationw.setStatus(Observation.ObservationStatus.FINAL);
        //Read out Arraylist for the WeightData
        for (int i = 0; i < mydb.getLastWeightDataFromUser(id).size(); i++){
            String temp = mydb.getLastWeightDataFromUser(id).get(i);
            Quantity value = new Quantity();
            if ((temp == MHEALTH_COLUMN_WEIGHT_UNIT) && (mydb.getLastWeightDataFromUser(id).get(i+1) != null) && (mydb.getLastWeightDataFromUser(id).get(i+1).length() > 0)){
                weightunitString = mydb.getAllWeightDataFromUser(id).get(i+1);
                // Give the observation a code (what kind of observation is this)
                Coding coding = observationw.getCode().addCoding();
                coding.setCode("29463-7").setSystem("http://loinc.org").setDisplay("Body Weight");
                countData++;

            }
            else if ((temp == MHEALTH_COLUMN_WEIGHT_VALUE) && (mydb.getLastWeightDataFromUser(id).get(i+1) != null) && (mydb.getLastWeightDataFromUser(id).get(i+1).length() > 0) ){
                weightvalueString = mydb.getLastWeightDataFromUser(id).get(i+1);
                weightset1 = true;
            }

            else if ((temp == MHEALTH_COLUMN_LAST_READ_TIME_WEIGHT) && (mydb.getLastWeightDataFromUser(id).get(i+1) != null) && (mydb.getLastWeightDataFromUser(id).get(i+1).length() > 0)){
                wdateString = mydb.getLastWeightDataFromUser(id).get(i+1);
                weightset2 = true;
            }
            else if (weightset1 && weightset2){
                value.setValue(Double.parseDouble(weightvalueString)).setSystem("http://unitsofmeasure.org").setCode(weightunitString).setUnit(wdateString);
                observationw.setValue(value);
            }

        }


        observationb1.setStatus(Observation.ObservationStatus.FINAL);
        observationb2.setStatus(Observation.ObservationStatus.FINAL);
        observationb3.setStatus(Observation.ObservationStatus.FINAL);
        observationb4.setStatus(Observation.ObservationStatus.FINAL);
        //Read out Arraylist for the BPMData and set Observation for it
        for (int i = 0; i < mydb.getLastBPMDataFromUser(id).size(); i++){
            String temp = mydb.getLastBPMDataFromUser(id).get(i);

            if ((temp == MHEALTH_COLUMN_BLOOD_PRESSURE_UNIT) && (mydb.getLastBPMDataFromUser(id).get(i+1) != null) && (mydb.getLastBPMDataFromUser(id).get(i+1).length() > 0)){
                bpunitString = mydb.getLastBPMDataFromUser(id).get(i+1);
                countData++;
            }
            else if ((temp == MHEALTH_COLUMN_BLOOD_PRESSURE_SYSTOLIC) && (mydb.getLastBPMDataFromUser(id).get(i+1) != null) && (mydb.getLastBPMDataFromUser(id).get(i+1).length() > 0)){
                systolicString = mydb.getLastBPMDataFromUser(id).get(i+1);
                Coding coding = observationb1.getCode().addCoding();
                coding.setCode("8480-6").setSystem("http://loinc.org").setDisplay("Systolic blood pressure");
                Quantity value1 = new Quantity();
                value1.setValue(Double.parseDouble(systolicString)).setSystem("http://unitsofmeasure.org").setCode("mm[Hg]");
                observationb1.setValue(value1);
            }
            else if ((temp == MHEALTH_COLUMN_BLOOD_PRESSURE_DIASTOLIC) && (mydb.getLastBPMDataFromUser(id).get(i+1) != null) && (mydb.getLastBPMDataFromUser(id).get(i+1).length() > 0)){
                diastolicString = mydb.getLastBPMDataFromUser(id).get(i+1);
                Coding coding = observationb2.getCode().addCoding();
                coding.setCode("8462-4").setSystem("http://loinc.org").setDisplay("Diastolic blood pressure");
                Quantity value2 = new Quantity();
                value2.setValue(Double.parseDouble(diastolicString)).setSystem("http://unitsofmeasure.org").setCode("mm[Hg]");
                observationb2.setValue(value2);
            }

            else if ((temp == MHEALTH_COLUMN_BLOOD_PRESSURE_PULSE) && (mydb.getLastBPMDataFromUser(id).get(i+1) != null) && (mydb.getLastBPMDataFromUser(id).get(i+1).length() > 0)){
                pulseString = mydb.getLastBPMDataFromUser(id).get(i+1);
                Coding coding = observationb3.getCode().addCoding();
                coding.setCode("8867-4").setSystem("http://loinc.org").setDisplay("Heart rate");
                Quantity value3 = new Quantity();
                value3.setValue(Double.parseDouble(pulseString)).setSystem("http://unitsofmeasure.org").setCode("/min");
                observationb3.setValue(value3);
            }

            else if ((temp == MHEALTH_COLUMN_LAST_READ_TIME_BPM) && (mydb.getLastBPMDataFromUser(id).get(i+1) != null) && (mydb.getLastBPMDataFromUser(id).get(i+1).length() > 0)){
                bdateString = mydb.getLastBPMDataFromUser(id).get(i+1);
                Quantity value4 = new Quantity();
                value4.setUnit(bdateString);
                observationb4.setValue(value4);
            }

       }

        //Set appearence
        this.patientid.setEnabled(true);
        this.patientid.setText(patientidString);
        this.patientid.setFocusable(true);
        this.patientid.setClickable(true);
        this.patientid.setFocusableInTouchMode(true);
        bpatientid.setVisibility(View.VISIBLE);

        this.patientid.setEnabled(true);
        this.server.setText(serverurl);
        this.server.setFocusable(true);
        this.server.setClickable(true);
        this.server.setFocusableInTouchMode(true);
        bserver.setVisibility(View.VISIBLE);

        if ( (patientidString.length() > 0) && (serverurl.length() > 0) && (countData > 0)){
            bupload.setVisibility(View.VISIBLE);
        }


        //Click listener for Save Patientid button
        bpatientid.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (patientid.getText().toString().length() > 0){
                    //save it
                    mydb.updateMhealthUserPatientid(id, patientid.getText().toString());
                    patient.addIdentifier().setSystem("urn:system").setValue(patientid.getText().toString());
                    Toast.makeText(getApplicationContext(), "Updated Patient-ID!",
                            Toast.LENGTH_SHORT).show();

                } else{
                    Toast.makeText(getApplicationContext(), "You have to enter an valid Patient-ID",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Click listener for save server url button
        bserver.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (server.getText().toString().length() > 0){
                    //save it and go to main screen
                    serverurl = server.getText().toString();
                    Toast.makeText(getApplicationContext(), "Updated Server!",
                            Toast.LENGTH_SHORT).show();

                } else{
                    Toast.makeText(getApplicationContext(), "You have to enter a Server",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Click listener for fhir upload button
        bupload.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                fhirHelper.uploadPatientData(patient, observationw, serverurl);
                fhirHelper.uploadPatientData(patient, observationb1, serverurl);
                fhirHelper.uploadPatientData(patient, observationb2, serverurl);
                fhirHelper.uploadPatientData(patient, observationb3, serverurl);

                Toast.makeText(getApplicationContext(), "Uploaded!",
                            Toast.LENGTH_SHORT).show();

            }
        });

    }

}
