package com.example.damian.homeapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Damian on 2017-04-15.
 */

public class SharedPref {


    public static final String SP_name = "userDetails";
    SharedPreferences userLocalDatabase;

    public SharedPref(Context context){
        userLocalDatabase = context.getSharedPreferences(SP_name, 0);
    }

    public void putDefaultDevice(String macAdress) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("default_mac", macAdress);
        spEditor.commit();
    }

    public String getDefaultDevice(){
        return userLocalDatabase.getString("default_mac", "00:00:00:00:00:00");
    }
}

