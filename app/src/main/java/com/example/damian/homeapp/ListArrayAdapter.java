package com.example.damian.homeapp;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

/**
 * Created by Damian on 2017-04-26.
 */

public class ListArrayAdapter extends ArrayAdapter<String> {


    public ListArrayAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }
}