package com.komal.studentforum10;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Spinner mySpinner =(Spinner) findViewById(R.id.programSpinner);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(SettingsActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.program_name));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);


        Spinner mySpinner1 = (Spinner) findViewById(R.id.departmentSpinner);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(SettingsActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.department_name));
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner1.setAdapter(myAdapter2);

        Spinner mySpinner2 = (Spinner) findViewById(R.id.roleSpinner);
        ArrayAdapter<String> myAdapter3 = new ArrayAdapter<String>(SettingsActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.role_name));
        myAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner2.setAdapter(myAdapter3);



    }
}
