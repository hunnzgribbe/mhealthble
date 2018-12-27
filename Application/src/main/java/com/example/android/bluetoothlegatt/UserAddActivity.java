package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UserAddActivity extends Activity {

    DBHelper mydb;
    TextView email;
    Button savebutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_add);
        mydb = new DBHelper(this);
        email = findViewById(R.id.editTextEmail1);
        savebutton = findViewById(R.id.buttonSaveUser1);

        savebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!email.getText().toString().matches("")){
                    //save it and go to main screen

                    mydb.insertMhealthUser(email.getText().toString(),"N.A.","N.A.","N.A.","N.A.","N.A.","N.A.", "N.A.", "N.A.");

                    Toast.makeText(getApplicationContext(), "Done",
                            Toast.LENGTH_SHORT).show();
                    finish();


                } else{
                    Toast.makeText(getApplicationContext(), "You have to enter an E-Mail",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



}
