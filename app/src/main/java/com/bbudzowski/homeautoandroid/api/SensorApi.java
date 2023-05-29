package com.bbudzowski.homeautoandroid.api;

import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.Timestamp;
import java.util.ArrayList;
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
        List<SensorEntity> sensors = (List<SensorEntity>) getResultList(res, listType);
        return sensors == null ? new ArrayList<>() : sensors;
    }

    public static SensorEntity getSensor(String device_id, String sensor_id) {
        Response res = getResponse(
                base_url + "?device_id=" + device_id + "&sensor_id=" + sensor_id);
        return (SensorEntity) getSingleResult(res, type);
    }

    public static int updateSensor(SensorEntity sensor) {
        ObjectNode json = mapper.createObjectNode();
        json.put("device_id", sensor.device_id);
        json.put("sensor_id", sensor.sensor_id);
        if(sensor.name != null)
            json.put("name", sensor.name);
        if(sensor.json_desc != null)
            json.put("json_desc", sensor.json_desc.toString());
        String bodyString;
        try {
            bodyString = mapper.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            return -1;
        }
        try (Response res = putResponse(base_url + "/update", bodyString)) {
            return res.code();
        }
        catch (NullPointerException e) {
            return -1;
        }
    }
}
