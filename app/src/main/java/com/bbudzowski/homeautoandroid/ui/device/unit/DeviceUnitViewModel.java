package com.bbudzowski.homeautoandroid.ui.device.unit;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.bbudzowski.homeautoandroid.ui.MainActivity;

public class DeviceUnitViewModel extends ViewModel {
    private final MutableLiveData<DeviceEntity> device;

    public DeviceUnitViewModel(MainActivity mainActivity, String device_id) {
        device = new MutableLiveData<>();
        device.setValue(mainActivity.getDevice(device_id));
    }

    public MutableLiveData<DeviceEntity> getDevice() {
        return device;
    }
}
