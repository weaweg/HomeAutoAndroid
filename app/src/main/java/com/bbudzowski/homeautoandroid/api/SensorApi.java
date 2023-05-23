package com.bbudzowski.homeautoandroid.api;

import com.bbudzowski.homeautoandroid.tables.SensorEntity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SensorApi extends BaseApi<SensorEntity> {

    private final String url = host + "/sensor";
    public List<SensorEntity> getSensors() {
        /*Request request = new Request.Builder().get().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            return JSON.std.listOfFrom(SensorEntity.class, response.body().string());
        }
        catch (Exception e) {
            return null;
        }*/

        List<SensorEntity> sensors = new ArrayList<>();
        for (int i = 0; i < 8; ++i) {
            SensorEntity sens = new SensorEntity();
            sens.device_id = "device_id " + i%4;
            sens.sensor_id = "sensor_id " + i;
            sens.data_type = i%2;
            sens.current_state = i;
            sens.units = "u"+ i;
            sensors.add(sens);
        }
        return sensors;
    }
}
