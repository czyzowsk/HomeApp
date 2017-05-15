package com.example.damian.homeapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
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

import com.example.damian.homeapp.dodatki.MessageNotification;

import org.w3c.dom.Text;

import java.util.Set;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends AppCompatActivity {

    int REQUEST_LOCATION_SERVICE = 1;

    //obiekty statyczne aby po zamknieciu aplikacji byly nadal w pamieci


    static TextView wiadomosc;

    SharedPref config;

    public static boolean isConnected;

    public static Context baseContext;
    static Window window;

    //result of finished activity
    int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = new Intent(this, TaskService.class);
        startService(i);

        setContentView(R.layout.activity_main);

        checkLocationPermission();

        baseContext = getBaseContext();
        window = getWindow();

        wiadomosc = (TextView) findViewById(R.id.wiadomosc);


    }

    @Override
    protected void onResume() {
        super.onResume();
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

    public void garageButton(View view) {
        if (isConnected)
            new TaskService.WriteTask().execute("g");
    }

    public void gateButton(View view) {
        if (isConnected)
            new TaskService.WriteTask().execute("b");
    }

    public void homeButton(View view) {
        if(isConnected){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        }
    }

    public void onSettings(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    private static String strReceived = " = ";

    static Handler UIupdater = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            int numOfBytesRecived = msg.arg1;
            byte[] buffer = (byte[]) msg.obj;
            strReceived = new String(buffer );
            strReceived = strReceived.substring(0, numOfBytesRecived);

            wiadomosc.setText(strReceived);

        }
    };

     public static int getTemperatura1(){
            String temperatura;
            if (strReceived.contains("t1="))
                temperatura= "" + strReceived;

            return 1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                checkLocationPermission();
            }
        }
    }
}