package com.bulut.sleepbetter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.bulut.sleepbetter.R.layout.activity_sleep;

public class SleepActivity extends AppCompatActivity

{

    ArrayList<String> categories = new ArrayList<String>();
    TextView minuteTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_sleep);

        Spinner spinner = (Spinner) findViewById(R.id.spinner_minute);

        categories.add("1 minute");
        categories.add("2 minute");
        categories.add("3 minute");
        categories.add("4 minute");
        categories.add("5 minute");
        categories.add("7 minute");
        categories.add("8 minute");
        categories.add("9 minute");
        categories.add("10 minute");
        categories.add("11 minute");
        categories.add("12 minute");
        categories.add("13 minute");
        categories.add("14 minute");
        categories.add("15 minute");
        categories.add("16 minute");
        categories.add("17 minute");
        categories.add("18 minute");
        categories.add("19 minute");
        categories.add("20 minute");
        categories.add("21 minute");
        categories.add("22 minute");
        categories.add("23 minute");
        categories.add("24 minute");
        categories.add("25 minute");

        // Create the ArrayAdapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SleepActivity.this
                , android.R.layout.simple_spinner_item, categories);

        // Set the Adapter
        spinner.setAdapter(arrayAdapter);

        // Set the ClickListener for Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView,
                                       View view, int i, long l) {
                // TODO Auto-generated method stub
                Toast.makeText(SleepActivity.this, "You Selected : "
                        + categories.get(i) + " Level ", Toast.LENGTH_SHORT).show();

            }

            // If no option selected
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });


    }}