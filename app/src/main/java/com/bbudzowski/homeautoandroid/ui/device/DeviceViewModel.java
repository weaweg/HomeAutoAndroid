package com.bbudzowski.homeautoandroid.ui.device;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbudzowski.homeautoandroid.api.DeviceApi;
import com.bbudzowski.homeautoandroid.tables.Device;

import java.util.List;

public class DeviceViewModel extends ViewModel {

    private final MutableLiveData<List<Device>> devices;
    private final DeviceApi dvApi;

    public DeviceViewModel() {
        devices = new MutableLiveData<>();
        dvApi = new DeviceApi();
        devices.setValue(dvApi.getDevices());
    }

    public LiveData<List<Device>> getDevices() {
        return devices;
    }
}