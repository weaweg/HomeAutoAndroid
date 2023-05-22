package com.bbudzowski.homeautoandroid.ui.sensor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbudzowski.homeautoandroid.api.SensorApi;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;

import java.io.InputStream;
import java.util.List;

public class SensorViewModel extends ViewModel {
    private final MutableLiveData<List<SensorEntity>> sensors = new MutableLiveData<>();

    public SensorViewModel(InputStream keyFile) {
        sensors.setValue(new SensorApi(keyFile).getSensors());
    }
    public LiveData<List<SensorEntity>> getSensors() {
        return sensors;
    }
}
