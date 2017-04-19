package com.example.damian.homeapp;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class FindDevicesActivity extends ListActivity {

    final ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<>();
    final ArrayList<String> discoveredDevicesNames = new ArrayList<>();

    private BluetoothAdapter bluetoothAdapter;

    public final static String UUID = "00001101-0000-1000-8000-00805F9B34FB";

    SharedPref config;

    String parrentDevice;

    //odbiornik (uruchamia sie gdy nastapi jakas akcja np odnajdzie jakies urzadzenie)
    private BroadcastReceiver discoverDevicesReciver = null;
    private BroadcastReceiver discoveryFinishedReciver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_devices);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        config = new SharedPref(getApplicationContext());
        startDiscovering();

    }

    public void startDiscovering(){

        if (discoverDevicesReciver == null) {
            discoverDevicesReciver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE);
                        if (!discoveredDevices.contains(device)) {
                            discoveredDevices.add(device);
                            discoveredDevicesNames.add(device.getName());
                            setListAdapter(new ArrayAdapter<>(getBaseContext(),
                                    android.R.layout.simple_list_item_1, discoveredDevicesNames));
                        }
                    }
                }
            };
        }

        if (discoveryFinishedReciver == null) {
            discoveryFinishedReciver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Toast.makeText(getBaseContext(), "Discovery completed",
                            Toast.LENGTH_SHORT).show();
                    unregisterReceiver(discoveryFinishedReciver);
                }
            };
        }
        // --- Rejestrujemy odbiorniki rozgłoszeń

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);


        registerReceiver(discoverDevicesReciver, filter1);
        registerReceiver(discoveryFinishedReciver, filter2);

        Toast.makeText(getApplicationContext(), "Discovery in progress...",
                Toast.LENGTH_SHORT).show();
        bluetoothAdapter.startDiscovery();

    }

    public void onListItemClick(ListView parent, View v, int position, long id) {
        //jesli jestem z kims polaczony

        IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mPairReceiver, intent);

        BluetoothDevice deviceSelected = discoveredDevices.get(position);
        Boolean isBonded = false;
        try {
            isBonded = createBond(deviceSelected);
            if(isBonded)
            {
                //arrayListpaired.add(bdDevice.getName()+"\n"+bdDevice.getAddress());
                //adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        parrentDevice = deviceSelected.toString();
        config.putDefaultDevice(parrentDevice);
    }

    public boolean createBond(BluetoothDevice btDevice)
            throws Exception
    {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                        BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,
                        BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState ==
                        BluetoothDevice.BOND_BONDING) {
                    Toast.makeText(context ,"paired",Toast.LENGTH_LONG).show();
                    finish();

                } else if (state == BluetoothDevice.BOND_NONE && prevState ==
                        BluetoothDevice.BOND_BONDED){
                    Toast.makeText(context ,"Unpaired",Toast.LENGTH_LONG).show();
                }

            }
        }
    };
}
