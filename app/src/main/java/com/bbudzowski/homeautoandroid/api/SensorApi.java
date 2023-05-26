package com.bbudzowski.homeautoandroid.api;

import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.fasterxml.jackson.databind.JavaType;

import java.sql.Timestamp;
import java.util.List;

import okhttp3.Response;

public abstract class SensorApi extends BaseApi {
    private static final String base_url = host + "/sensor";
    private static final JavaType listType = mapper.getTypeFactory().
            constructCollectionType(List.class, SensorEntity.class);
    private static final JavaType type = mapper.constructType(SensorEntity.class);

    public static Timestamp getUpdateTime(){
        Response res = getResponse(base_url + "/update_time");
        return getUpdateTime(res);
    }

    public static List<SensorEntity> getSensors() {
        Response res = getResponse(base_url + "/all");
        return (List<SensorEntity>) getResultList(res, listType);
    }

    public static SensorEntity getSensor(String device_id, String sensor_id) {
        Response res = getResponse(
                base_url + "?device_id=" + device_id + "&sensor_id=" + sensor_id);
        return (SensorEntity) getSingleResult(res, type);
    }
}
