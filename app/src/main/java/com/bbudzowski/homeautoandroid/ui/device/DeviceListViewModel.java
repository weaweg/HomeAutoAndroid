package com.bbudzowski.homeautoandroid.ui.device;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbudzowski.homeautoandroid.api.DeviceApi;
import com.bbudzowski.homeautoandroid.tables.DeviceEntity;

import java.io.InputStream;
import java.util.List;

public class DeviceListViewModel extends ViewModel {
    private final MutableLiveData<List<DeviceEntity>> devices = new MutableLiveData<>();

    public DeviceListViewModel() {
        devices.setValue(new DeviceApi().getDevices());
    }
    public LiveData<List<DeviceEntity>> getDevices() {
        return devices;
    }
}