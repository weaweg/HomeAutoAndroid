package com.bbudzowski.homeautoandroid.ui.sensor.list;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbudzowski.homeautoandroid.MainActivity;
import com.bbudzowski.homeautoandroid.api.DeviceApi;
import com.bbudzowski.homeautoandroid.api.MeasurementApi;
import com.bbudzowski.homeautoandroid.api.SensorApi;
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
        this.sensors = new MutableLiveData<>();
        List<SensorEntity> sensors = mainActivity.getSensors();
        for(SensorEntity sens : sensors) {
            sens.device = mainActivity.getDevice(sens.device_id);
            sens.lastMeasurement = MeasurementApi.
                    getLastMeasurementForSensor(sens.device_id, sens.sensor_id);
        }
        this.sensors.setValue(sensors);
        lastUpdateTime = mainActivity.getLastUpdateTime();
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
