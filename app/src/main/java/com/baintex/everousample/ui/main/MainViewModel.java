package com.baintex.everousample.ui.main;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.baintex.everoulibrary.EverouManager;
import com.baintex.everoulibrary.exceptions.InitializationErrorException;
import com.baintex.everoulibrary.exceptions.InvalidAPIKeyException;
import com.baintex.everoulibrary.exceptions.ObtainingDevicesErrorException;
import com.baintex.everoulibrary.model.Device;
import com.baintex.everoulibrary.model.User;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {

    private EverouManager everouManager;
    private User user;

    public void initEverouLibrary(Context context, String apiKey) throws InvalidAPIKeyException, InitializationErrorException {
        everouManager = EverouManager.getInstance(context);
        user = everouManager.init(apiKey);
    }

    public ArrayList<Device> getUserDevices() throws InvalidAPIKeyException, ObtainingDevicesErrorException, InitializationErrorException {
        return everouManager.getDevices();
    }
}