package com.baintex.everousample.ui.main;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.baintex.everoulibrary.EverouManager;
import com.baintex.everoulibrary.bluetooth.BeaconManager;
import com.baintex.everoulibrary.bluetooth.BeaconManagerI;
import com.baintex.everoulibrary.bluetooth.BeaconManagerObserver;
import com.baintex.everoulibrary.exceptions.InitializationErrorException;
import com.baintex.everoulibrary.exceptions.InvalidAPIKeyException;
import com.baintex.everoulibrary.exceptions.ObtainingDevicesErrorException;
import com.baintex.everoulibrary.model.Device;
import com.baintex.everoulibrary.model.Trigger;
import com.baintex.everoulibrary.model.User;
import com.baintex.everousample.EverouSampleApp;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {

    private EverouManager everouManager;
    private BeaconManagerI beaconManager;
    private User user;
    private ArrayList<Device> devices;

    public void initEverouLibrary(Context context, String apiKey) throws InvalidAPIKeyException, InitializationErrorException {
        everouManager = EverouManager.getInstance(context);
        user = everouManager.init(apiKey);
    }

    public ArrayList<Device> getUserDevices() throws InvalidAPIKeyException, ObtainingDevicesErrorException, InitializationErrorException {
        devices = everouManager.getDevices();
        return devices;
    }

    public void startAutoMode(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            beaconManager = BeaconManager.getInstance(context);
            beaconManager.registerObserver(EverouSampleApp.getInstance());
            beaconManager.start(user, devices);
        }
    }

    public void stopAutoMode() {
        if (beaconManager != null)
            beaconManager.stop();
    }
}