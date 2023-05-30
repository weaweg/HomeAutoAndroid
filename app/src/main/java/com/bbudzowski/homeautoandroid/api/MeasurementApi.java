package com.bbudzowski.homeautoandroid.api;

import com.bbudzowski.homeautoandroid.tables.MeasurementEntity;
import com.fasterxml.jackson.databind.JavaType;

import java.util.ArrayList;
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

    public static List<MeasurementEntity> getMeasurementsForSensor(
            String device_id, String sensor_id, String start, String end) {
        Response res = getResponse(base_url + "/all?device_id=" + device_id + "&sensor_id=" + sensor_id +
                "&start_time=" + start + "&end_time=" + end);
        List<MeasurementEntity> measurements = (List<MeasurementEntity>) getResultList(res, listType);
        return measurements == null ? new ArrayList<>() : measurements;
    }


}
