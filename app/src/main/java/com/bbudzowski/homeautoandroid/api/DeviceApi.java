package com.bbudzowski.homeautoandroid.api;

import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.fasterxml.jackson.databind.JavaType;

import java.sql.Timestamp;
import java.util.List;

import okhttp3.Response;

public abstract class DeviceApi extends BaseApi {
    private static final String base_url = host + "/device";
    private static final JavaType listType = mapper.getTypeFactory().
            constructCollectionType(List.class, DeviceEntity.class);
    private static final JavaType type = mapper.constructType(DeviceEntity.class);

    public static Timestamp getUpdateTime(){
        Response res = getResponse(base_url + "/update_time");
        return getUpdateTime(res);
    }

    public static List<DeviceEntity> getDevices() {
        Response res = getResponse(base_url + "/all");
        return (List<DeviceEntity>) getResultList(res, listType);
    }

    public static DeviceEntity getDevice(String device_id) {
        Response res = getResponse(base_url + "?device_id=" + device_id);
        return (DeviceEntity) getSingleResult(res, type);
    }

    /*private int updateDevice(DeviceEntity device) {
        ObjectNode json = mapper.createObjectNode();
        json.put("device_id", device.device_id);
        json.put("name", device.name);
        json.put("location", device.location);
        String bodyString = null;
        try {
            bodyString = mapper.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            return HttpStatusCodesKt.HTTP_INTERNAL_SERVER_ERROR;
        }
        try (Response res = postResponse(base_url + "/update", bodyString)) {
            return res.code();
        }
    }*/
}
