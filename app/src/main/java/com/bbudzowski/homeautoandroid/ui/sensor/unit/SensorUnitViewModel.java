package com.bbudzowski.homeautoandroid.ui.sensor.unit;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.bbudzowski.homeautoandroid.ui.MainActivity;

import java.sql.Timestamp;

public class SensorUnitViewModel extends ViewModel {
    private final MutableLiveData<SensorEntity> sensor;
    private Timestamp lastUpdateTime;

    public SensorUnitViewModel(MainActivity mainActivity, String device_id, String sensor_id) {
        sensor = new MutableLiveData<>();
        sensor.setValue(mainActivity.getSensor(device_id, sensor_id));
        lastUpdateTime = mainActivity.getDevicesLastUpdate();
    }

    public MutableLiveData<SensorEntity> getSensor() {
        return sensor;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
