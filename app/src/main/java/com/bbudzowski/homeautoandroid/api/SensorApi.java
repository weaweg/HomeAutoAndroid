package com.bbudzowski.homeautoandroid.api;

import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.fasterxml.jackson.databind.JavaType;

import java.sql.Timestamp;
import java.util.List;

import okhttp3.Response;

public class SensorApi extends BaseApi<SensorEntity> {

    private final String base_url = host + "/sensor";
    private final JavaType type;

    public SensorApi() {
        type = mapper.getTypeFactory().
                constructCollectionType(List.class, SensorEntity.class);
    }
    public Timestamp getUpdateTime(){
        Response res = getResponse(base_url + "/updateTime");
        return getUpdateTime(res);
    }

    public List<SensorEntity> getSensors() {
        Response res = getResponse(base_url + "/all");
        return getResultList(res, type);
    }
}
