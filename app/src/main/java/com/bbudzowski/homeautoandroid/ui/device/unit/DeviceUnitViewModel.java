package com.bbudzowski.homeautoandroid.ui.device.unit;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.bbudzowski.homeautoandroid.ui.MainActivity;

import java.sql.Timestamp;

public class DeviceUnitViewModel extends ViewModel {
    private final MutableLiveData<DeviceEntity> device;
    private Timestamp lastUpdateTime;

    public DeviceUnitViewModel(MainActivity mainActivity, String device_id) {
        device = new MutableLiveData<>();
        device.setValue(mainActivity.getDevice(device_id));
        lastUpdateTime = mainActivity.getDevicesLastUpdate();
    }

    public MutableLiveData<DeviceEntity> getDevice() {
        return device;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
