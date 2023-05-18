package com.bbudzowski.homeautoandroid.api;

import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpStatusCodesKt;

public class DeviceApi extends BaseApi<DeviceEntity>  {
    private final String base_url = host + "/devices";

    public List<DeviceEntity> getDevices() {
        Response res = getResponse(base_url + "/all");
        return getResultList(res);
    }

    public DeviceEntity getDevice(String device_id) {
        Response res = getResponse(base_url);
        return getSingleResult(res);
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
