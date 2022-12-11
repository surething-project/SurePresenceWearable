package pt.ulisboa.tecnico.surething.wearable.utils;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.bouncycastle.util.Pack;

import java.lang.reflect.Method;
import java.util.UUID;

import eu.surething_project.core.LocationClaim;
import eu.surething_project.core.SignedLocationClaim;
import pt.ulisboa.tecnico.surething.pose.Enc_Structure;
import pt.ulisboa.tecnico.surething.wearable.R;
import pt.ulisboa.tecnico.surething.wearable.activities.ScanActivity;
import pt.ulisboa.tecnico.surething.wearable.activities.VerifiedActivity;

public class BleHandler {

    private Context context;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.d("ADVERTISE", "STATE CHANGED");
        }
    };

    private BluetoothGattServer gattServer;
    private BluetoothGattService gattService;
    private BluetoothGattCharacteristic gattCharacteristic;
    private BluetoothGattDescriptor gattDescriptor;

    private static final String KIOSK = "DC:A6:32:68:67:46";

    private static final double MY_LOCATION_LAT = 38.73787545779024;
    private static final double MY_LOCATION_LNG = -9.137825390556634;

    private Location myLocation;

    private static final int REQUEST_ENABLE_BT = 0;

    private static final UUID SERVICE = UUID.fromString("0000ffff-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC = UUID.fromString("4b574c54-0000-1000-8000-00805f9b34fb");
    private static final UUID DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private Enc_Structure enc_structure;

    private PackageTask task;

    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;


    public BleHandler(Context context){
        this.context = context;
        initiateBLE();
    }

    private void initiateBLE(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ((ScanActivity) context).requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_FINE_LOCATION);
            }
            Log.d("1" , "1");

            //Starting the Manager
            bluetoothManager = context.getSystemService(BluetoothManager.class);

            if(bluetoothManager != null){
                Log.d("2" , "2");
                //Starting the adapter
                bluetoothAdapter = bluetoothManager.getAdapter();
            }

            if(bluetoothAdapter != null){
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    ((ScanActivity) context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                //Starting the advertiser
                bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
            }
        }
    }

    public void createGATTServer(){
        gattServer = bluetoothManager.openGattServer(context, new BluetoothGattServerCallback() {
            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                super.onConnectionStateChange(device, status, newState);
                Log.d("callback", "onConnectionStateChange");
                Log.d("callback22222", String.valueOf(newState));
                if(!device.getAddress().equals(KIOSK)){ //Only accept connections from the kiosk mac
                    try {
                        Method m = device.getClass().getMethod("removeBond", (Class[]) null);
                        m.invoke(device, (Object[]) null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{  //create config.txt file with key (MAC address) value (Location)
                    if(newState == BluetoothProfile.STATE_CONNECTED){ // new connection
                        Location location = new Location(LocationManager.GPS_PROVIDER);

                        location.setLatitude(MY_LOCATION_LAT);
                        location.setLongitude(MY_LOCATION_LNG);

                        myLocation = location;

                        task = new PackageTask();
                        task.execute();
                    }
                    else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                        Log.d("state", "state disconnected");
                        bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
                        gattServer.close();
                    }

                }
            }

            @Override
            public void onServiceAdded(int status, BluetoothGattService service) {
                Log.d("callback", "onServiceAdded");
                super.onServiceAdded(status, service);
            }


            @Override
            public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
                Log.d("callback", "onCharacteristicReadRequest");
                super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
                while (task.getStatus() != AsyncTask.Status.FINISHED) { // is there any other way?

                }
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, enc_structure.toByteArray());
                Intent intent = new Intent(context, VerifiedActivity.class);
                context.startActivity(intent);
            }


            @Override
            public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
                Log.d("callback", "onDescriptorReadRequest");
                super.onDescriptorReadRequest(device, requestId, offset, descriptor);
                while(task.getStatus() != AsyncTask.Status.FINISHED){ // is there any other way?

                }
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, enc_structure.toByteArray());
                Intent intent = new Intent(context, VerifiedActivity.class);
                context.startActivity(intent);
            }

            @Override
            public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
            }

            @Override
            public void onNotificationSent(BluetoothDevice device, int status) {
                Log.d("callback", "onNotificationSent");
                super.onNotificationSent(device, status);
            }
        });

        createService();
    }

    public class PackageTask extends AsyncTask<Void, Void, Enc_Structure> {
        @Override
        protected Enc_Structure  doInBackground(Void... voids) {
            Log.d("creating", "package");
            return createPackageToAdvertise();
        }

        @Override
        protected void onPostExecute(Enc_Structure result) {
            // nothing to do on the UI
            enc_structure = result;

        }
    }

    public class PackageTask_plaintext extends AsyncTask<Void, Void, SignedLocationClaim> {
        @Override
        protected SignedLocationClaim doInBackground(Void... voids) {
            return createPackageToAdvertise_plaintext();
        }

        @Override
        protected void onPostExecute(SignedLocationClaim result) {
            // nothing to do on the UI
            //enc_structure = result;

        }
    }

    public Enc_Structure createPackageToAdvertise(){

        LocationClaim locationClaim = CoreHandler.createLocationClaim(context, myLocation);

        byte[] signature = crypto.sign(context, locationClaim.toByteArray());

        SignedLocationClaim slc = CoreHandler.createSignedLocationClaim(locationClaim, CoreHandler.createSignature(signature));

        return POSEHandler.createEnc_Structure(context, slc.toByteArray()); // signed location claim

    }

    public SignedLocationClaim createPackageToAdvertise_plaintext(){

        LocationClaim locationClaim = CoreHandler.createLocationClaim(context, myLocation);

        byte[] signature = crypto.sign(context, locationClaim.toByteArray());

        SignedLocationClaim slc = CoreHandler.createSignedLocationClaim(locationClaim, CoreHandler.createSignature(signature));

        Log.d("SLC SIZE:", String.valueOf(slc.toByteArray().length));

        return slc;
    }

    public void startAdvertising(){

        if(bluetoothLeAdvertiser != null){
            Log.d("advertising", "advertising");
            AdvertiseSettings settings = new AdvertiseSettings.Builder().setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED).setConnectable(true).build();
            AdvertiseData data = new AdvertiseData.Builder().setIncludeDeviceName(true).build();
            bluetoothLeAdvertiser.startAdvertising(settings, data, advertiseCallback);
        }
    }

    public void createService(){

        BluetoothGattService service = new BluetoothGattService(SERVICE, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(CHARACTERISTIC,
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);

        characteristic.addDescriptor(new BluetoothGattDescriptor(DESCRIPTOR, BluetoothGattCharacteristic.PERMISSION_READ));

        //characteristic.setValue("Hello world");

        service.addCharacteristic(characteristic);

        gattServer.addService(service);
        Log.d("BLE", bluetoothAdapter.getAddress());

    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }


}
