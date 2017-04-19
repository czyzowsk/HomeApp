package com.example.damian.homeapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Set;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends AppCompatActivity {

    int REQUEST_LOCATION_SERVICE = 1;

    //obiekty statyczne aby po zamknieciu aplikacji byly nadal w pamieci
    private ServerThread serverThread;
    private static ConnectToServerThread connectToServerThread = null;
    private BluetoothAdapter bluetoothAdapter;

    BluetoothDevice defaultBluetoothDevice;

    static BluetoothAdapter mBluetoothAdapter;

    SharedPref config;

    private static boolean isConnected = false;

    public static Context baseContext;
    static Window window;

    //result of finished activity
    int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        checkDevice();

        baseContext = getBaseContext();
        window = getWindow();

        initializeRecivers();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(connectToServerThread != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                window.setStatusBarColor(ContextCompat.getColor(baseContext,
                        R.color.colorPrimaryDarkConnected));

            isConnected = true;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initializeRecivers() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        registerReceiver(mReceiver, filter);

    }


    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void checkDevice() {


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Nie mogę znaleźć urządzenia bluetooth. " +
                    "Twoje urządzenie nie jest kompatybilne z tą aplikacją");
            builder.setTitle("Uwaga!");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else {
                if (checkLocationPermission())
                    connectTo();
            }
        }
    }


    private boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION_SERVICE);
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_SERVICE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                connectTo();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Potrzebujesz uprawnień do poprawnego dzialania aplikacji." +
                        " Uruchom aplikacje ponownie i zakceptuj je.");
                builder.setTitle("Uwaga!");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.show();
            }
        }

    }


    public void connectTo() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        config = new SharedPref(getApplicationContext());
        boolean deviceFound = false;

        if (connectToServerThread == null) {

            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getAddress().equals(config.getDefaultDevice())) {
                        serverThread = new ServerThread(bluetoothAdapter);
                        serverThread.start();

                        defaultBluetoothDevice = device;
                        connectToServerThread = new ConnectToServerThread(device, bluetoothAdapter);
                        connectToServerThread.start();
                        Toast.makeText(this, "Connecting with " + device.getName(),
                                Toast.LENGTH_SHORT).show();
                        deviceFound = true;
                    }
                }
                if (deviceFound) {
                } else {
                    Intent i = new Intent(getApplicationContext(), FindDevicesActivity.class);
                    startActivity(i);
                }
            } else {
                Intent i = new Intent(getApplicationContext(), FindDevicesActivity.class);
                startActivity(i);
            }
        }
    }

    public void garageButton(View view) {
        if (isConnected)
            new WriteTask().execute("garage");
    }

    public void gateButton(View view) {
        if (isConnected)
            new WriteTask().execute("gate");
    }

    public void homeButton(View view) {
        if(isConnected){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        }

    }

    private class WriteTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... args) {
            try {
                connectToServerThread.commsThread.write(args[0]);
            } catch (Exception e) {
                Log.d("MainActivity", e.getLocalizedMessage());
            }
            return null;

        }
    }

    static Handler UIupdater = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            int numOfBytesRecived = msg.arg1;
            byte[] buffer = (byte[]) msg.obj;
            String strReceived = new String(buffer);
            strReceived = strReceived.substring(0, numOfBytesRecived);

            System.out.println(strReceived);

        }
    };


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    window.setStatusBarColor(ContextCompat.getColor(baseContext,
                            R.color.colorPrimaryDarkConnected));

                isConnected=true;
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    window.setStatusBarColor(ContextCompat.getColor(baseContext,
                            R.color.colorPrimaryDarkDisconnected));
                connectToServerThread.cancel();
                connectToServerThread = null;
                isConnected=false;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                if (checkLocationPermission())
                    connectTo();
            }
        }
    }

}