package com.bbudzowski.homeautoandroid.api;

import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.SimpleType;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;
import okhttp3.internal.http.HttpStatusCodesKt;

public class DeviceApi extends BaseApi<DeviceEntity>  {
    private final String base_url = host + "/device";
    private final JavaType listType;
    private final JavaType type;

    public DeviceApi() {
        listType = mapper.getTypeFactory().
                constructCollectionType(List.class, DeviceEntity.class);
        type = mapper.constructType(DeviceEntity.class);
    }

    public Timestamp getUpdateTime(){
        Response res = getResponse(base_url + "/updateTime");
        return getUpdateTime(res);
    }

    public List<DeviceEntity> getDevices() {
        Response res = getResponse(base_url + "/all");
        return getResultList(res, listType);
    }

    public DeviceEntity getDevice(String device_id) {
        Response res = getResponse(base_url + "?device_id=" + device_id);
        return getSingleResult(res, type);
    }

    private int updateDevice(DeviceEntity device) {
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
    }
}
