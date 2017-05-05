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

    public void putDefaultDevice(String macAdress, String name) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("default_mac", macAdress);
        spEditor.putString("default_name", name);
        spEditor.commit();
    }

    public String getDefaultDevice(){
        return userLocalDatabase.getString("default_mac", null);
    }

    public String getDefaultDeviceName() {
        return userLocalDatabase.getString("default_name", null);
    }

    public void removeDefaultDevice() {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.remove("default_mac");
        spEditor.remove("default_name");
        spEditor.commit();
    }
}

