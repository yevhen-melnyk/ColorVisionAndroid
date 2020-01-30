package com.example.myapplication;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;
import java.util.ArrayList;

import static com.example.myapplication.R.raw.cb_types;

public class SettingsActivity extends AppCompatActivity {

    Spinner cbTypeSpinner;
    TextView descText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        cbTypeSpinner = findViewById(R.id.cbTypeSpinner);
        descText = findViewById(R.id.descriptionText);

        ArrayList<CbType> CbTypes;


        //<editor-fold desc="Initial cbType spinner populating">
        //Read Colorblindness info from xml
        InputStream cbTypesInputStream = getResources().openRawResource(R.raw.cb_types);
        CbTypes = CbType.parseFromXml(cbTypesInputStream);

        //Passing received data to spinner through an adapter
        ArrayAdapter<CbType> cbTypeSpinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, CbTypes);
        cbTypeSpinner.setAdapter(cbTypeSpinnerAdapter);

        //if preferences exist - set appropriate selection in spinner on startup
        if(Preferences.appPreferences.getSelectedCbType() !=null){
            cbTypeSpinner.setSelection(Preferences.appPreferences.getSelectedCbType().id);
            descText.setText(Preferences.appPreferences.getSelectedCbType().description);
        }
        //</editor-fold>

        //Listener that will save selection to preferences and load descriptions when new cb type is selected
        cbTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Preferences.appPreferences.setSelectedCbType((CbType) cbTypeSpinner.getSelectedItem());
                Preferences.Save();
                descText.setText(Preferences.appPreferences.getSelectedCbType().description);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                descText.setText("");
            }
        });


    }
    @Override
    protected void onPause() {

        super.onPause();
       // Preferences.appPreferences.setSelectedCbType((CbType) cbTypeSpinner.getSelectedItem());
        Preferences.Save();
    }

    public void makeCbTypeSelection (View view){

    }



}
