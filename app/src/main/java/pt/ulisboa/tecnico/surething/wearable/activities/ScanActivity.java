package pt.ulisboa.tecnico.surething.wearable.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import eu.surething_project.core.LocationClaim;
import eu.surething_project.core.SignedLocationClaim;
import pt.ulisboa.tecnico.surething.pose.Enc_Structure;
import pt.ulisboa.tecnico.surething.wearable.R;
import pt.ulisboa.tecnico.surething.wearable.utils.BleHandler;
import pt.ulisboa.tecnico.surething.wearable.utils.CoreHandler;
import pt.ulisboa.tecnico.surething.wearable.utils.POSEHandler;
import pt.ulisboa.tecnico.surething.wearable.utils.crypto;

public class ScanActivity extends WearableActivity {

    private Handler mHandler;

    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;

    private BleHandler bleHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Enables Always-on
        setAmbientEnabled();

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Toast.makeText(ScanActivity.this, "Presence successfully verified!", Toast.LENGTH_LONG).show();
            }
        };
    }

    public class BleTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            initiateBLE();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            TextView mac = findViewById(R.id.textView_MAC);

            mac.setText("Your device is\n" + bleHandler.getBluetoothAdapter().getName()); //Only the name is public, Android will not allow to show the MAC address

        }
    }

    public void initiateBLE(){

        bleHandler = new BleHandler(this);
        bleHandler.createGATTServer();
        bleHandler.createService();
        bleHandler.startAdvertising();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("aiaiai", "aiaiaiai");
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("aiai", "Permissions granted");
                    initiateBLE();
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    @Override
    protected void onResume() {
        super.onResume();
        BleTask task = new BleTask();
        task.execute();

        Log.d("on resume", "on resume");
    }
}
