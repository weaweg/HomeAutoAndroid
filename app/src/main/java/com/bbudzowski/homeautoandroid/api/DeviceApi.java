package com.bbudzowski.homeautoandroid.api;

import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public abstract class DeviceApi extends BaseApi {
    private static final String base_url = host + "/device";
    private static final JavaType listType = mapper.getTypeFactory().
            constructCollectionType(List.class, DeviceEntity.class);
    private static final JavaType type = mapper.constructType(DeviceEntity.class);

    public static Timestamp getUpdateTime() {
        Response res = getResponse(base_url + "/update_time");
        return getUpdateTime(res);
    }

    public static List<DeviceEntity> getDevices() {
        Response res = getResponse(base_url + "/all");
        List<DeviceEntity> devices = (List<DeviceEntity>) getResultList(res, listType);
        return devices == null ? new ArrayList<>() : devices;
    }

    public static DeviceEntity getDevice(String device_id) {
        Response res = getResponse(base_url + "?device_id=" + device_id);
        return (DeviceEntity) getSingleResult(res, type);
    }

    public static int updateDevice(DeviceEntity device) {
        ObjectNode json = mapper.createObjectNode();
        json.put("device_id", device.device_id);
        json.put("name", device.name);
        json.put("location", device.location);
        String bodyString;
        try {
            bodyString = mapper.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            return -1;
        }
        try (Response res = putResponse(base_url + "/update", bodyString)) {
            return res.code();
        } catch (NullPointerException e) {
            return -1;
        }
    }

    public static int deleteDevice(String device_id) {
        try (Response res = deleteResponse(base_url + "/delete?device_id=" + device_id)) {
                return res.code();
        } catch (NullPointerException e) {
            return -1;
        }
    }
}
