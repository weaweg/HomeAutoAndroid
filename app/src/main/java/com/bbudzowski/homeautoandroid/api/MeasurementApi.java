package com.bbudzowski.homeautoandroid.api;

import com.bbudzowski.homeautoandroid.tables.MeasurementEntity;
import com.fasterxml.jackson.databind.JavaType;

import java.util.List;

import okhttp3.Response;

public abstract class MeasurementApi extends BaseApi {
    private static final String base_url = host + "/measurement";
    private static final JavaType listType = mapper.getTypeFactory().
            constructCollectionType(List.class, MeasurementEntity.class);
    private static final JavaType type = mapper.constructType(MeasurementEntity.class);

    public static MeasurementEntity getLastMeasurementForSensor(String device_id, String sensor_id) {
        String args = "/last?device_id=" + device_id + "&sensor_id=" + sensor_id;
        Response res = getResponse(base_url + args);
        return (MeasurementEntity) getSingleResult(res, type);
    }


}
