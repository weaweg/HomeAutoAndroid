package com.bbudzowski.homeautoandroid.ui.device.list;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbudzowski.homeautoandroid.ui.MainActivity;
import com.bbudzowski.homeautoandroid.tables.DeviceEntity;

import java.sql.Timestamp;
import java.util.List;

public class DeviceListViewModel extends ViewModel {
    private final MutableLiveData<List<DeviceEntity>> devices;
    private Timestamp lastUpdateTime;

    public DeviceListViewModel(MainActivity mainActivity) {
        devices = new MutableLiveData<>();
        devices.setValue(mainActivity.getDevices());
        lastUpdateTime = mainActivity.getDevicesLastUpdate();
    }

    public MutableLiveData<List<DeviceEntity>> getDevices() {
        return devices;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
