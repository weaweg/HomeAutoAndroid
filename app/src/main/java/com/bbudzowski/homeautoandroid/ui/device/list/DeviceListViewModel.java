package com.bbudzowski.homeautoandroid.ui.device.list;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbudzowski.homeautoandroid.MainActivity;
import com.bbudzowski.homeautoandroid.tables.DeviceEntity;

import java.sql.Timestamp;
import java.util.List;

public class DeviceListViewModel extends ViewModel {
    private MutableLiveData<List<DeviceEntity>> devices;
    private Timestamp lastUpdateTime;
    public MutableLiveData<List<DeviceEntity>> getDevices() {
        return devices;
    }

    public void setDevices(MainActivity mainActivity) {
        devices = new MutableLiveData<>();
        devices.setValue(mainActivity.getDevices());
        lastUpdateTime = mainActivity.getDevicesLastUpdate();
        if(lastUpdateTime == null)
            lastUpdateTime = new Timestamp(0);
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
