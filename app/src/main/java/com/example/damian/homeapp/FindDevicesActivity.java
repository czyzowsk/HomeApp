package com.example.damian.homeapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class FindDevicesActivity extends ListActivity{

    final ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<>();
    final ArrayList<String> discoveredDevicesNames = new ArrayList<>();

    private BluetoothAdapter bluetoothAdapter;

    public final static String UUID = "00001101-0000-1000-8000-00805F9B34FB";

    SharedPref config;

    String parrentDevice;

    RelativeLayout buttonLayout;
    ProgressBar progressBar;

    //odbiornik (uruchamia sie gdy nastapi jakas akcja np odnajdzie jakies urzadzenie)
    private BroadcastReceiver discoverDevicesReciver = null;
    private BroadcastReceiver discoveryFinishedReciver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_devices);

        Intent intent = new Intent(getApplicationContext(),
                TaskService.class);
        stopService(intent);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        config = new SharedPref(getApplicationContext());
        startDiscovering();

        buttonLayout = (RelativeLayout) findViewById(R.id.button_refresh);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
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
                            if(device.getName() != null)
                                discoveredDevicesNames.add(device.getName());
                            else discoveredDevicesNames.add(device.getAddress());
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

                    progressBar.setVisibility(View.GONE);
                    buttonLayout.setVisibility(View.VISIBLE);

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

    public void onListItemClick(ListView parent, View v, final int position, long id) {
        //jesli jestem z kims polaczony

        final Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        final BluetoothDevice deviceSelected = discoveredDevices.get(position);
        AlertDialog alertDialog = new AlertDialog.Builder(FindDevicesActivity.this).create();
        alertDialog.setTitle("Połączenie");
        alertDialog.setCancelable(true);
        alertDialog.setMessage("Czy chcesz połączyć się z " + deviceSelected.getName());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "TAK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!pairedDevices.contains(discoveredDevices.get(position))) {
                            IntentFilter intent =
                                    new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                            registerReceiver(mPairReceiver, intent);

                            Boolean isBonded = false;
                            try {
                                isBonded = createBond(deviceSelected);
                                if (isBonded) {

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            parrentDevice = deviceSelected.toString();
                            config.putDefaultDevice(parrentDevice, deviceSelected.getName());
                        }
                        else {
                            config.putDefaultDevice(deviceSelected.toString(),
                                    deviceSelected.getName());
                            finish();
                        }
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Anuluj",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.show();


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
                    Toast.makeText(context ,"Paired",Toast.LENGTH_LONG).show();
                    unregisterReceiver(mPairReceiver);
                    finish();

                } else if (state == BluetoothDevice.BOND_NONE && prevState ==
                        BluetoothDevice.BOND_BONDED){
                    Toast.makeText(context ,"Unpaired",Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(discoverDevicesReciver);
        unregisterReceiver(discoveryFinishedReciver);

        Intent intent = new Intent(getApplicationContext(), TaskService.class);
        startService(intent);
    }

    public void onRefresh(View view){

        startDiscovering();
        buttonLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

    }

}
