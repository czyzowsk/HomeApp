package com.example.damian.homeapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.damian.homeapp.dodatki.InteractiveArrayAdapter;
import com.example.damian.homeapp.dodatki.MessageNotification;
import com.example.damian.homeapp.dodatki.Model;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    ArrayList<Model> list;
    SharedPref config;
    ArrayAdapter<Model> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        config = new SharedPref(this);

        final ListView listView = (ListView) findViewById(R.id.myListView);
        adapter = new InteractiveArrayAdapter(this, getModel());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this)
                                .create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Czy chcesz usunąc sparowane urządzenie");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        config.removeDefaultDevice();
                                        list.get(0).setInfo(config.getDefaultDeviceName());
                                        listView.setAdapter(adapter);

                                        Intent intent = new Intent(getApplicationContext(),
                                                TaskService.class);
                                        stopService(intent);

                                        new MessageNotification().notify(getApplicationContext(),
                                                "Sparuj swoje urządzenie Bluetooth ", 0);

                                        dialog.dismiss();
                                    }
                                });

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "ANULUJ",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        alertDialog.show();
                        break;
                }
            }
        });

    }


    private ArrayList<Model> getModel() {
        list = new ArrayList<Model>();
        list.add(get("Sparowane urządzenie"));
        // Initially select one of the items
        list.get(0).setInfo(config.getDefaultDeviceName());
        return list;
    }

    private Model get(String s) {
        return new Model(s);
    }

}
