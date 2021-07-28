package com.baintex.everousample;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.baintex.everoulibrary.bluetooth.BeaconManagerObserver;
import com.baintex.everoulibrary.model.Device;
import com.baintex.everoulibrary.model.Trigger;

public class EverouSampleApp extends Application implements BeaconManagerObserver {


    public static boolean scannerStatus;
    private static EverouSampleApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static EverouSampleApp getInstance() {
        return instance;
    }


    @Override
    public void updateStatus(boolean running) {
        if (running)
            Log.i("BeaconManagerObserver", "Scan devices started");
        else
            Log.i("BeaconManagerObserver", "Scan devices stopped");
        scannerStatus = running;
    }

    @Override
    public void deviceDetected(String bluetoothId, int rssi) {
        Log.i("BeaconManagerObserver", "Device " + bluetoothId + " detected");
    }

    @Override
    public void error(Device device, String errorMessage) {

    }

    @Override
    public void triggerActionExecuted(Device device, Trigger trigger) {
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getApplicationContext(), "Device " + device.desc + " opened", Toast.LENGTH_SHORT).show());
    }
}
