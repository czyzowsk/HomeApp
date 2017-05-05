package com.example.damian.homeapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Damian on 2017-04-22.
 */

public class TaskService extends Service {


    //obiekty statyczne aby po zamknieciu aplikacji byly nadal w pamieci
    private static ServerThread serverThread;
    private static ConnectToServerThread connectToServerThread;

    static BluetoothDevice defaultBluetoothDevice;
    static BluetoothAdapter mBluetoothAdapter;

    SharedPref config;

    NotificationManager mNotifyMgr;
    NotificationCompat.Builder mBuilder;
    int mNotificationId = 001;

    //odbiorniki rozgloszen, informacja ze urzadzenie poszukuje zapisanego urzadzenia
    private BroadcastReceiver discoverDevicesReciver = null;
    private BroadcastReceiver discoveryFinishedReciver = null;

    MessageNotification notification;

    @Override
    public void onCreate() {
        super.onCreate();

        notification = new MessageNotification();
        initializeRecivers();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        config = new SharedPref(this);

        checkDevice();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//pamietaj aby wszystkie odbiorniki wyrejestrowac!
        unregisterReceiver(mReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    private void initializeRecivers() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        registerReceiver(mReceiver, filter);

    }

    private void checkDevice() {

        if (!mBluetoothAdapter.isEnabled())
            notification.notify(getApplicationContext(), "Włącz urządzenie Bluetootch", 1);

        else if (config.getDefaultDeviceName() == null) {
            notification.notify(getApplicationContext(), "Sparuj swoje urządzenie Bluetooth " +
                    config.getDefaultDeviceName(), 0);

        } else {
            searchDevice();
            notification.notify(getApplicationContext(), "Searching your reciver" +
                    config.getDefaultDeviceName(), 2);
        }
    }

    public void connectTo(BluetoothDevice device) {

        defaultBluetoothDevice = device;

        serverThread = new ServerThread(mBluetoothAdapter);
        serverThread.start();

        defaultBluetoothDevice = device;
        connectToServerThread = new ConnectToServerThread(device, mBluetoothAdapter);
        connectToServerThread.start();

    }

    public static class WriteTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... args) {
            try {
                connectToServerThread.commsThread.write(args[0]);
            } catch (Exception e) {
                Log.d("TaskService", e.getLocalizedMessage());
            }
            return null;
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                notification.notify(getApplicationContext(), "Połączono z " +
                        defaultBluetoothDevice.getName(), 3);

            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                searchDevice();
                notification.notify(getApplicationContext(), "Searching your reciver" +
                        config.getDefaultDeviceName(), 2);
                MainActivity.isConnected = false;
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                checkDevice();
            }
            if (!mBluetoothAdapter.isEnabled()) {
                notification.notify(getApplicationContext(), "Turn on Bluetooth Adapter", 1);
            }
        }

    };

    private void searchDevice() {

        discoverDevicesReciver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (device.getAddress().equals(config.getDefaultDevice())) {

                        connectTo(device);
                        try {
                            unregisterReceiver(discoverDevicesReciver);
                        }
                        catch (Exception e){
                            System.out.println(e.toString());
                        }
                        MainActivity.isConnected = true;
                    }
                }
            }
        };

        if (discoveryFinishedReciver == null) {
            discoveryFinishedReciver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        unregisterReceiver(discoveryFinishedReciver);
                    }
                    catch (Exception e){
                        System.out.println(e.toString());
                    }

                    if (!MainActivity.isConnected) {
                        searchDevice();
                    }
                }
            };
        }

        //odbiorniki rozgloszen

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(discoverDevicesReciver, filter1);
        registerReceiver(discoveryFinishedReciver, filter2);

        mBluetoothAdapter.startDiscovery();

    }
}
