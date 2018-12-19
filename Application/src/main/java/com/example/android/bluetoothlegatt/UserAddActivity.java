package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.content.Intent;
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
                    if ((1==1)){
                        //finish();

                        mydb.insertMhealthUser(email.getText().toString(), "0", 0, 0,0,0,0);
                        Toast.makeText(getApplicationContext(), Integer.toString(mydb.getnumberOfRows()),
                                Toast.LENGTH_SHORT).show();
//TODO 17.12.18 hier weiter machen, fehler: Userid weiterreichen an useractivity
                       // Toast.makeText(getApplicationContext(), mydb.deleteUser(mydb.getnumberOfRows()),
                        //        Toast.LENGTH_SHORT).show();

                        Toast.makeText(getApplicationContext(), mydb.getAllMhealthUsers().toString(),
                                Toast.LENGTH_SHORT).show();
                    }


                } else{
                    Toast.makeText(getApplicationContext(), "You have to enter an E-Mail",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



}
