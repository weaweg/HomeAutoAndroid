package com.bbudzowski.homeautoandroid.ui.sensor.list;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbudzowski.homeautoandroid.MainActivity;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;

import java.sql.Timestamp;
import java.util.List;

public class SensorListViewModel extends ViewModel {
    private MutableLiveData<List<SensorEntity>> sensors;
    private Timestamp lastUpdateTime;

    public MutableLiveData<List<SensorEntity>> getSensors() {
        return sensors;
    }

    public void setSensors(MainActivity mainActivity) {
        sensors = new MutableLiveData<>();
        sensors.setValue(genSensorsData(mainActivity));
        lastUpdateTime = mainActivity.getLastUpdateTime();
    }

    public List<SensorEntity> genSensorsData(MainActivity mainActivity) {
        List<SensorEntity> sensors = mainActivity.getSensors();
        for(SensorEntity sens : sensors)
            sens.device = mainActivity.getDevice(sens.device_id);
        return sensors;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
