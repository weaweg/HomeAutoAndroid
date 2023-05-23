package com.bbudzowski.homeautoandroid.ui.sensor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbudzowski.homeautoandroid.api.DeviceApi;
import com.bbudzowski.homeautoandroid.api.MeasurementApi;
import com.bbudzowski.homeautoandroid.api.SensorApi;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;

import java.util.List;

public class SensorListViewModel extends ViewModel {
    private final MutableLiveData<List<SensorEntity>> sensors = new MutableLiveData<>();

    public SensorListViewModel() {
        List<SensorEntity> sensors = new SensorApi().getSensors();
        for(SensorEntity sens : sensors) {
            sens.device = new DeviceApi().getDevice(sens.device_id);
            sens.lastMeasurement = new MeasurementApi()
                    .getLastMeasurementForSensor(sens.device_id, sens.sensor_id);
        }
        this.sensors.setValue(sensors);
    }
    public LiveData<List<SensorEntity>> getSensors() {
        return sensors;
    }
}
