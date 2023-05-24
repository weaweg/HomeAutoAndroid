package com.bbudzowski.homeautoandroid.api;

import com.bbudzowski.homeautoandroid.tables.MeasurementEntity;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import java.sql.Timestamp;
import java.util.List;

import okhttp3.Response;

public class MeasurementApi extends BaseApi<MeasurementEntity> {
    private final String base_url = host + "/measurement";
    private final JavaType type;

    public MeasurementApi() {
        type = mapper.getTypeFactory().
                constructCollectionType(List.class, MeasurementEntity.class);
    }

    public Timestamp getUpdateTime(){
        Response res = getResponse(base_url + "/updateTime");
        return getUpdateTime(res);
    }

    public MeasurementEntity getLastMeasurementForSensor(String device_id, String sensor_id) {
        String args = "/last?device_id=" + device_id + "&sensor_id=" + sensor_id;
        Response res = getResponse(base_url + args);
        return getSingleResult(res, mapper.constructType(MeasurementEntity.class));
    }


}
