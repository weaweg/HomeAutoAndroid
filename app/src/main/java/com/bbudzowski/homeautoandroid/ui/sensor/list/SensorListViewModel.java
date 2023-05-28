package com.bbudzowski.homeautoandroid.ui.sensor.list;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbudzowski.homeautoandroid.ui.MainActivity;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;

import java.sql.Timestamp;
import java.util.List;

public class SensorListViewModel extends ViewModel {
    private final MutableLiveData<List<SensorEntity>> sensors;
    private Timestamp lastUpdateTime;

    public SensorListViewModel(MainActivity mainActivity) {
        sensors = new MutableLiveData<>();
        sensors.setValue(mainActivity.getSensors());
        lastUpdateTime = mainActivity.getLastUpdateTime();
    }

    public MutableLiveData<List<SensorEntity>> getSensors() {
        return sensors;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
