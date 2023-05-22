package com.bbudzowski.homeautoandroid.api;

import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;
import okhttp3.internal.http.HttpStatusCodesKt;

public class DeviceApi extends BaseApi<DeviceEntity>  {
    private final String base_url = host + "/device";
    private final JavaType type;

    public DeviceApi(InputStream keyFile) {
        super(keyFile);
        type = mapper.getTypeFactory().
                constructCollectionType(List.class, DeviceEntity.class);
    }

    public List<DeviceEntity> getDevices() {
        //Response res = getResponse(base_url + "/all");
        //return getResultList(res, type);
        List<DeviceEntity> devices = new ArrayList<>();
        for (int i = 0; i < 8; ++i) {
            DeviceEntity dev = new DeviceEntity();
            dev.device_id = "device_id " + i;
            dev.name = "name " + i;
            dev.location = "location " + i;
            devices.add(dev);
        }
        return devices;
    }

    public DeviceEntity getDevice(String device_id) {
        //Response res = getResponse(base_url + "?device_id = " + device_id);
        //return getSingleResult(res);
        DeviceEntity dev = new DeviceEntity();
        dev.device_id = device_id;
        dev.name = "name";
        dev.location = "location";
        return dev;
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
