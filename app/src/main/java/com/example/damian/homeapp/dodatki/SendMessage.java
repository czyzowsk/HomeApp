package com.example.damian.homeapp.dodatki;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.damian.homeapp.TaskService;

/**
 * Created by Damian on 2017-05-06.
 */

public class SendMessage extends BroadcastReceiver {

    public static final String BRAMA = "BRAMA";
    public static final String GARAZ = "GARAZ";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (GARAZ.equals(action)) {
            new TaskService.WriteTask().execute("g");
        } else if (BRAMA.equals(action)) {
            new TaskService.WriteTask().execute("b");
        }
    }
}
