package com.bbudzowski.homeautoandroid.ui.device;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbudzowski.homeautoandroid.api.DeviceApi;
import com.bbudzowski.homeautoandroid.tables.DeviceEntity;

import java.io.InputStream;
import java.util.List;

public class DeviceUnitViewModel extends ViewModel {
    private final MutableLiveData<DeviceEntity> device = new MutableLiveData<>();

    public DeviceUnitViewModel(String device_id) {
        device.setValue(new DeviceApi().getDevice(device_id));
    }
    public LiveData<DeviceEntity> getDevice() {
        return device;
    }
}